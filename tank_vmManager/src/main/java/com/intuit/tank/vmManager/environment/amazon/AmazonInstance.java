package com.intuit.tank.vmManager.environment.amazon;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.AmazonEC2AsyncClientBuilder;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.AmazonEC2Exception;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.AttachVolumeRequest;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeAddressesRequest;
import com.amazonaws.services.ec2.model.DescribeAddressesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.IamInstanceProfileSpecification;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Tenancy;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.InstanceDescription;
import com.intuit.tank.vm.settings.InstanceTag;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmInstanceType;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMKillRequest;
import com.intuit.tank.vm.vmManager.VMRequest;
import com.intuit.tank.vmManager.environment.IEnvironmentInstance;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class AmazonInstance implements IEnvironmentInstance {

    protected static final long ASSOCIATE_IP_MAX_WAIT_MILIS = 1000 * 60 * 2;// 2 minutes
    private static final int MAX_INSTANCE_BATCH_SIZE = 10;
    private static Logger LOG = LogManager.getLogger(AmazonInstance.class);

    private AmazonEC2Async asynchEc2Client;
    private VMRegion vmRegion;

    private TankConfig config = new TankConfig();

    /**
     *
     * @param vmRegion
     */
    public AmazonInstance(@Nonnull VMRegion vmRegion) {
        this.vmRegion = vmRegion;
        try {
            CloudCredentials creds = config.getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
            ClientConfiguration clientConfig = new ClientConfiguration();
            clientConfig.setMaxConnections(2);
            if (StringUtils.isNotBlank(creds.getProxyHost())) {
                try {
                    clientConfig.setProxyHost(creds.getProxyHost());
                    if (StringUtils.isNotBlank(creds.getProxyPort())) {
                        clientConfig.setProxyPort(Integer.valueOf(creds.getProxyPort()));
                    }
                } catch (NumberFormatException e) {
                    LOG.error("invalid proxy setup.");
                }

            }
            if (StringUtils.isNotBlank(creds.getKey()) && StringUtils.isNotBlank(creds.getKeyId())) {
                AWSCredentials credentials = new BasicAWSCredentials(creds.getKeyId(), creds.getKey());
                asynchEc2Client = AmazonEC2AsyncClientBuilder
                        .standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withClientConfiguration(clientConfig)
                        .withRegion(vmRegion.getRegion())
                        .build();
            } else {
                asynchEc2Client = AmazonEC2AsyncClientBuilder
                        .standard()
                        .withClientConfiguration(clientConfig)
                        .withRegion(vmRegion.getRegion())
                        .build();
            }

        } catch (Exception ex) {
            LOG.error("Error initializing amazonclient: " + ex, ex);
            throw new RuntimeException(ex);
        }
    }

    public void attachVolume(String volumneId, String instanceId, String device) {
        AttachVolumeRequest attachVolumeRequest = new AttachVolumeRequest(volumneId, instanceId, device);
        asynchEc2Client.attachVolume(attachVolumeRequest);
        RebootInstancesRequest rebootInstancesRequest = new RebootInstancesRequest();
        rebootInstancesRequest.withInstanceIds(instanceId);
        asynchEc2Client.rebootInstances(rebootInstancesRequest);
    }

    /**
     * 
     * @inheritDoc
     */
    @Override
    public List<VMInformation> describeInstances(String... instanceIds) {
        try {
            HashSet<String> ids = new HashSet<String>(Arrays.asList(instanceIds));

            return asynchEc2Client.describeInstances().getReservations().stream()
                    .flatMap(reservationDescription -> reservationDescription.getInstances()
                            .stream()
                            .filter(instance -> ids.contains(instance.getInstanceId()))
                            .map(instance -> new AmazonDataConverter().instanceToVmInformation(reservationDescription, instance, vmRegion)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Failed to retrieve instance from Amazon: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @inheritDoc
     */
    @Override
    public List<VMInformation> create(VMRequest request) {
        List<VMInformation> result = new ArrayList<>();
        try {
            VMInstanceRequest instanceRequest = (VMInstanceRequest) request;
            InstanceDescription instanceDescription = instanceRequest.getInstanceDescription();
            if (instanceDescription == null) {
                instanceDescription = config.getVmManagerConfig().getInstanceForRegionAndType(vmRegion, instanceRequest.getImage());
            }

            // Get the required data
            if (instanceRequest.getReuseStoppedInstance()) {
                List<VMInformation> instances = findAllInstancesOfType(this.vmRegion, instanceRequest.getImage());
                LOG.info("looking for stopped instance with ami-id of " + instanceRequest.getImage());
                for (VMInformation vmInfo : instances) {
                    LOG.info("found instance with id " + vmInfo.getInstanceId() + " with state of " + vmInfo.getState());
                    if ("stopped".equalsIgnoreCase(vmInfo.getState())) {
                        StartInstancesRequest startInstancesRequest = new StartInstancesRequest();
                        startInstancesRequest.withInstanceIds(vmInfo.getInstanceId());
                        // restart this instance
                        StartInstancesResult startInstances = asynchEc2Client.startInstances(startInstancesRequest);
                        result.addAll(new AmazonDataConverter().processStateChange(startInstances.getStartingInstances()));
                        break;
                    }
                }
            }
            if (result.isEmpty()) {
                InstanceType size = AmazonInstance.getInstanceType(instanceRequest.getSize());
                VmInstanceType vmType = config.getVmManagerConfig().getInstanceType(instanceRequest.getSize());
                String keyPair = instanceDescription.getKeypair();
                if (instanceRequest.getJobId() != null) {
                    instanceRequest.addUserData(TankConstants.KEY_JOB_ID, instanceRequest.getJobId());
                }
                if (instanceRequest.getReportingMode() != null) {
                    LOG.info("Setting reporting mode to " + instanceRequest.getReportingMode());
                    instanceRequest.addUserData(TankConstants.KEY_REPORTING_MODE, instanceRequest.getReportingMode());
                } else {
                    LOG.warn("Reporting mode not set.");
                }
                if (vmType.getJvmArgs() != null) {
                    instanceRequest.addUserData(TankConstants.KEY_JVM_ARGS, vmType.getJvmArgs());
                }
                CloudCredentials cloudCredentials = config.getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
                if (StringUtils.isNotBlank(cloudCredentials.getKeyId()) && StringUtils.isNotBlank(cloudCredentials.getKey())) {
                    instanceRequest.addUserData(TankConstants.KEY_AWS_SECRET_KEY_ID, cloudCredentials.getKeyId());
                    instanceRequest.addUserData(TankConstants.KEY_AWS_SECRET_KEY, cloudCredentials.getKey());
                }

                instanceRequest.addUserData(TankConstants.KEY_CONTROLLER_URL, config.getControllerBase());
                instanceRequest.addUserData(TankConstants.KEY_NUM_USERS_PER_AGENT, Integer.toString(instanceRequest.getNumUsersPerAgent()));

                if (instanceRequest.isUseEips()) {
                    instanceRequest.addUserData(TankConstants.KEY_USING_BIND_EIP, Boolean.TRUE.toString());
                }
                if (instanceRequest.getLoggingProfile() != null) {
                    LOG.info("Setting loggingProfile to " + instanceRequest.getLoggingProfile());
                    instanceRequest.addUserData(TankConstants.KEY_LOGGING_PROFILE, instanceRequest.getLoggingProfile());
                } else {
                    LOG.warn("Logging  profile not set.");
                }
                if (instanceRequest.getStopBehavior() != null) {
                    LOG.info("Setting stopBehavior to " + instanceRequest.getStopBehavior());
                    instanceRequest.addUserData(TankConstants.KEY_STOP_BEHAVIOR, instanceRequest.getStopBehavior());
                } else {
                    LOG.warn("stop Behavior not set.");
                }

                String userData = buildUserData(instanceRequest.getUserData());

                int remaining = instanceRequest.getNumberOfInstances();
                String image = getAMI(instanceDescription);
                LOG.info("Requesting " + remaining + " instances in " + vmRegion.getName() + " with AMI=" + image);

                RunInstancesRequest runInstancesRequestTemplate = new RunInstancesRequest();
                Tenancy tenancy = StringUtils.isEmpty(instanceDescription.getTenancy()) ? Tenancy.Default : Tenancy.fromValue(instanceDescription.getTenancy());
                runInstancesRequestTemplate.withImageId(image)
                                    .withInstanceType(size.toString())
                					.withKeyName(keyPair)
                					.withPlacement(new Placement().withTenancy(tenancy))
                					.withMonitoring(true)
                					.withUserData(userData);

                Collection<String> c = instanceDescription.getSecurityGroupIds();
                if (!c.isEmpty()) {
                	LOG.info("Security Group IDs " + c.toString());
                    runInstancesRequestTemplate.withSecurityGroupIds(c);
                } else {
                    runInstancesRequestTemplate.withSecurityGroups(instanceDescription.getSecurityGroup());
                }
                if (StringUtils.isNotBlank(instanceDescription.getIamRole())) {
                    IamInstanceProfileSpecification iamInstanceProfile = new IamInstanceProfileSpecification().withName(instanceDescription.getIamRole());
                    runInstancesRequestTemplate.withIamInstanceProfile(iamInstanceProfile);
                }
                // add zone info if specified
                if (!StringUtils.isEmpty(instanceDescription.getZone())) {
                    runInstancesRequestTemplate.withPlacement(new Placement().withAvailabilityZone(instanceDescription.getZone()));
                }
                // Loop to request agent instances
                List<String> subnetIds = instanceDescription.getSubnetIds();
                int position = ThreadLocalRandom.current().nextInt(subnetIds.size());
                while ( !subnetIds.isEmpty() && remaining > 0 ) {
                    RunInstancesRequest runInstancesRequest = runInstancesRequestTemplate.clone();
                    runInstancesRequest.withSubnetId(subnetIds.get(position >= subnetIds.size() ? 0 : position++));
                    int requestCount = Math.min(remaining, MAX_INSTANCE_BATCH_SIZE);
                    try {
                        RunInstancesResult results =
                                asynchEc2Client.runInstances(
                                        runInstancesRequest.withMinCount(1).withMaxCount(requestCount));
                        result.addAll(new AmazonDataConverter().processReservation(results.getReservation(), vmRegion));
                        remaining -= results.getReservation().getInstances().size();
                    } catch (AmazonEC2Exception ae) {
                        LOG.error("Amazon issue starting instances: count="
                                + remaining + " : subnets=" + subnetIds.size() + " : " + ae.getMessage());
                        if(StringUtils.contains(ae.getMessage(), "InsufficientInstanceCapacity")) {
                            subnetIds.remove(runInstancesRequest.getSubnetId());
                        }
                    }
                }

                if (instanceRequest.isUseEips()) {
                    Set<Address> availableEips = new HashSet<Address>();
                    synchronized (instanceRequest.getRegion()) {
                        DescribeAddressesResult describeAddresses = asynchEc2Client.describeAddresses(new DescribeAddressesRequest());
                        Set<String> reserved = config.getVmManagerConfig().getReservedElasticIps();
                        for (Address address : describeAddresses.getAddresses()) {
                            String elasticIPType = instanceDescription.isVPC() ? "vpc" : "standard";
                            if (elasticIPType.equalsIgnoreCase(address.getDomain()) && StringUtils.isBlank(address.getInstanceId())) {
                                String ip = address.getPublicIp();
                                if (!reserved.contains(ip)) {
                                    availableEips.add(address);
                                }
                            }
                        }
                    }
                    List<Address> randomizedIps = new ArrayList<Address>(availableEips);
                    Collections.shuffle(randomizedIps);

                    synchronized (instanceRequest.getRegion()) {
                        Iterator<Address> iter = randomizedIps.iterator();
                        List<AssociateContainer> bindIps = new ArrayList<AssociateContainer>();
                        for (VMInformation info : result) {
                            if (!iter.hasNext()) {
                                break;
                            }
                            bindIps.add(new AssociateContainer(info.getInstanceId(), iter.next()));
                        }
                        CountDownLatch latch = new CountDownLatch(bindIps.size());
                        for (AssociateContainer container : bindIps) {
                            associateAddress(container.instanceId, container.address, latch);
                        }
                        latch.await();
                    }
                }
            }
            if (instanceDescription.getPublicIp() != null && result.size() == 1) {
                // bind to the public ip
                String instanceId = result.get(0).getInstanceId();
                // wait for instance to be in running state
                associateAddress(instanceId, new Address().withPublicIp(instanceDescription.getPublicIp()), null);
                // reboot(result);
            }
            if (!result.isEmpty()) {
                List<String> ids = result.stream().map(VMInformation::getInstanceId).collect(Collectors.toList());
                LOG.info("Setting tags for instances " + ids);
                tagInstance(ids, buildTags(instanceRequest));
            }

        } catch (AmazonEC2Exception ae) {
            LOG.error("Amazon issue starting instances: " + ae.getMessage(), ae);
            throw new RuntimeException(ae);
        } catch (Exception ex) {
            LOG.error("Error starting instances: " + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     *
     * @inheritDoc
     */
    @Override
    public void tagInstance(final List<String> instanceIds, KeyValuePair... tag) {
        if (tag.length != 0) {
            final List<Tag> tags = Arrays.stream(tag)
                    .map(pair -> new Tag(pair.getKey(), pair.getValue())).collect(Collectors.toList());

            new Thread( () -> {
                int count = 0;
                try {
                    while (++count <= 5 && !instanceIds.isEmpty()) {
                        Thread.sleep(5000);
                        CreateTagsRequest createTagsRequest
                                = new CreateTagsRequest().withResources(instanceIds).withTags(tags);
                        asynchEc2Client.createTagsAsync(createTagsRequest);
                        Thread.sleep(1000);

                        Future<DescribeInstancesResult> describeInstances
                                = asynchEc2Client.describeInstancesAsync(
                                        new DescribeInstancesRequest().withInstanceIds(instanceIds));
                        for (Reservation r : describeInstances.get().getReservations()) {
                            for (Instance i : r.getInstances()) {
                                if (i.getTags() != null && !i.getTags().isEmpty()) {
                                    instanceIds.remove(i.getInstanceId());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Error tagging instances: " + e, e);
                }
            }).start();
        }
    }

    /**
     * @param instanceRequest
     * @return
     */
    private KeyValuePair[] buildTags(VMInstanceRequest instanceRequest) {
        List<KeyValuePair> pairs = new ArrayList<>();
        pairs.add(new KeyValuePair("Name", buildNameTag(instanceRequest)));
        pairs.add(new KeyValuePair("Controller", config.getInstanceName()));

        if (instanceRequest.getJobId() != null) {
            instanceRequest.addUserData(TankConstants.KEY_JOB_ID, instanceRequest.getJobId());
            instanceRequest.addUserData(TankConstants.KEY_CONTROLLER_URL, config.getControllerBase());
            pairs.add(new KeyValuePair("JobId", instanceRequest.getJobId()));

            if (NumberUtils.isCreatable(instanceRequest.getJobId())) {
                JobInstance jobInstance = new JobInstanceDao().findById(Integer.valueOf(instanceRequest.getJobId()));
                if (jobInstance != null) {
                    pairs.add(new KeyValuePair("JobName", jobInstance.getName()));
                }
            }
        }
        List<InstanceTag> tags = config.getVmManagerConfig().getTags();
        for (InstanceTag tag : tags) {
            pairs.add(new KeyValuePair(tag.getName(), tag.getValue()));
        }
        return pairs.toArray(new KeyValuePair[0]);
    }

    /**
     * @param instanceRequest
     * @return
     */
    private String buildNameTag(VMInstanceRequest instanceRequest) {
        StringBuilder sb = new StringBuilder(instanceRequest.getImage().getConfigName());
        if (StringUtils.isNoneEmpty(instanceRequest.getJobId())) {
            sb.append(" job(" + instanceRequest.getJobId() + ")");
        }
        if (StringUtils.isNoneEmpty(config.getInstanceName())) {
            sb.insert(0, config.getInstanceName() + " ");
        }
        return sb.toString();
    }

    @Override
    public List<VMInformation> kill(VMRequest request) {
        VMKillRequest killRequest = (VMKillRequest) request;
        TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest(killRequest.getInstances());
        TerminateInstancesResult terminateInstances = asynchEc2Client.terminateInstances(terminateInstancesRequest);
        return new AmazonDataConverter().processStateChange(terminateInstances.getTerminatingInstances());
    }

    @Override
    public void killInstances(List<String> instanceIds) {

        // Filter instanceId list to only instances in the client defined region.
        List<VMInformation> instancesInRegion = describeInstances(instanceIds.toArray(new String[instanceIds.size()]));
        List<String> instanceIdsInRegion = instancesInRegion.stream().map(VMInformation::getInstanceId).collect(Collectors.toList());

        if (!instanceIdsInRegion.isEmpty()) {
            // asynchronous AWS SDK terminate instances.
            asynchEc2Client.terminateInstancesAsync(
                    new TerminateInstancesRequest(instanceIdsInRegion),
                    new AsyncHandler<TerminateInstancesRequest, TerminateInstancesResult>() {
                        @Override
                        public void onError(Exception exception) {
                            LOG.warn("something went wrong killing the instances : {}",
                                    exception.getLocalizedMessage());
                        }

                        @Override
                        public void onSuccess(TerminateInstancesRequest request, TerminateInstancesResult result) {
                            LOG.trace("instances killed successfully : {}",
                                    result.getTerminatingInstances());
                        }
                    });
            //return new AmazonDataConverter().processStateChange(terminateInstances.getTerminatingInstances());
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<VMInformation> findInstancesOfType(VMRegion region, VMImageType type) {
        List<VMInformation> ret = new ArrayList<>();
        try {
            DescribeInstancesResult instances = asynchEc2Client.describeInstances();
            InstanceDescription instanceForRegionAndType = config.getVmManagerConfig().getInstanceForRegionAndType(region, type);
            for (Reservation res : instances.getReservations()) {
                if (res.getInstances() != null) {
                    for (com.amazonaws.services.ec2.model.Instance inst : res.getInstances()) {
                        if ((inst.getState().getName().equalsIgnoreCase("running")
                                || inst.getState().getName().equalsIgnoreCase("pending"))
                                && inst.getImageId().equals(instanceForRegionAndType.getAmi())) {
                            ret.add(new AmazonDataConverter().instanceToVmInformation(res, inst, region));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting instances: " + e.toString(), e);
            throw new RuntimeException(e);
        }
        return ret;
    }

    /**
     * @inheritDoc
     */
    private List<VMInformation> findAllInstancesOfType(VMRegion region, VMImageType type) {
        List<VMInformation> ret = new ArrayList<VMInformation>();
        try {
            DescribeInstancesResult instances = asynchEc2Client.describeInstances();
            InstanceDescription instanceForRegionAndType = config.getVmManagerConfig().getInstanceForRegionAndType(region, type);
            for (Reservation res : instances.getReservations()) {
                if (res.getInstances() != null) {
                    for (com.amazonaws.services.ec2.model.Instance inst : res.getInstances()) {
                        if (inst.getImageId().equals(instanceForRegionAndType.getAmi())) {
                            ret.add(new AmazonDataConverter().instanceToVmInformation(res, inst, region));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting instances: " + e.toString(), e);
            throw new RuntimeException(e);
        }
        return ret;
    }

    /**
     * Get the size of the instance
     * 
     * @param size
     *            The size requested
     * @return The size assigned
     */
    private static InstanceType getInstanceType(String size) {
        try {
            return InstanceType.fromValue(size);
        } catch (Exception e) {
            LOG.warn("Error parsing vminstanceType " + size, e);
        }
        return InstanceType.C52xlarge;
    }

    /**
     * Request the ami parameter from SSM
     * @param instanceDescription
     * @return The ami assigned
     */
    private String getAMI(InstanceDescription instanceDescription) {
        String name = instanceDescription.getSSMAmi();
        if (StringUtils.isNotEmpty(name)) {
            try {
                final AWSSimpleSystemsManagement client = AWSSimpleSystemsManagementClientBuilder.defaultClient();
                GetParameterResult result = client.getParameter(new GetParameterRequest().withName(name));
                return result.getParameter().getValue();
            } catch (Exception e) {
                LOG.error("Error retriveing AMI from SSM with name " + name + ", default to InstanceRequest", e);
            }
        }
        return instanceDescription.getAmi();
    }

    @Override
    public void associateAddress(final String instanceId, final Address address, final CountDownLatch latch) {

        new Thread( () -> {
            boolean associated = false;

            try {
                long start = System.currentTimeMillis();
                int count = 0;
                LOG.info("Setting ip for instance " + instanceId + " to " + address.getPublicIp());
                while ((System.currentTimeMillis() - start) < ASSOCIATE_IP_MAX_WAIT_MILIS && !associated) {
                    count++;
                    try {
                       if (address.getAllocationId() == null) {
                            asynchEc2Client.associateAddressAsync(new AssociateAddressRequest()
                                                                .withInstanceId(instanceId)
                                                                .withPublicIp(address.getPublicIp()));
                        } else {
                            asynchEc2Client.associateAddressAsync(new AssociateAddressRequest()
                                                                .withInstanceId(instanceId)
                                                                .withAllocationId(address.getAllocationId()));
                        }
                        Thread.sleep((new Random().nextInt(10) + 10) * 100L);
                        Future<DescribeInstancesResult> describeInstances
                                = asynchEc2Client.describeInstancesAsync(new DescribeInstancesRequest().withInstanceIds(instanceId));
                        for (Reservation r : describeInstances.get().getReservations()) {
                            for (Instance i : r.getInstances()) {
                                if (address.getPublicIp().equals(i.getPublicIpAddress())) {
                                    associated = true;
                                }
                            }
                        }
                        if (associated) {
                            LOG.info(instanceId + " associated with " + address.getPublicIp());
                        } else if (count % 5 == 0) {
                            LOG.info(instanceId + " not associated yet " + address.getPublicIp() + ". Retrying... count = " + count);
                        }
                    } catch (Exception e) {
                        if (count < 5) {
                            LOG.warn("Error associating ip address: " + e + " Will retry.");
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("Error setting elastic ip: " + e, e);
            } finally {
                LOG.info("exiting associated = " + associated);
                if (latch != null) {
                    latch.countDown();
                }
            }
        }).start();

    }

    @Override
    public void reboot(List<VMInformation> instances) {
        List<String> instanceIds = instances.stream()
                .map(VMInformation::getInstanceId).collect(Collectors.toCollection(() -> new ArrayList<>(instances.size())));
        asynchEc2Client.rebootInstancesAsync(new RebootInstancesRequest(instanceIds));
        // ec2Interface.rebootInstances(instanceIds);
    }

    /**
     * @param instanceIds
     */
    public void stopInstances(List<String> instanceIds) {

        // Filter instanceId list to only instances in the client defined region.
        List<VMInformation> instancesInRegion = describeInstances(instanceIds.toArray(new String[instanceIds.size()]));
        List<String> instanceIdsInRegion = instancesInRegion.stream().map(VMInformation::getInstanceId).collect(Collectors.toList());

        if (!instanceIdsInRegion.isEmpty()) {
            // asynchronous AWS SDK stop instances.
            asynchEc2Client.stopInstancesAsync(
                    new StopInstancesRequest(instanceIdsInRegion),
                    new AsyncHandler<StopInstancesRequest, StopInstancesResult>() {
                        @Override
                        public void onError(Exception exception) {
                            LOG.warn("something went wrong stopping the instances {}",
                                    exception.getLocalizedMessage());
                        }

                        @Override
                        public void onSuccess(StopInstancesRequest request, StopInstancesResult result) {
                            LOG.trace("instances stopped successfully {}",
                                    result.getStoppingInstances());
                        }
                    });
            //return new AmazonDataConverter().processStateChange(stoppingInstances);
        }
    }

    /**
     * @param userDataMap
     * @return
     */
    private String buildUserData(@Nonnull Map<String, String> userDataMap) {
        String sb = userDataMap.entrySet().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
        return Base64.encodeBase64String(sb.getBytes());
    }

    private static class AssociateContainer {
        private String instanceId;
        private Address address;

        private AssociateContainer(String instanceId, Address address) {
            super();
            this.instanceId = instanceId;
            this.address = address;
        }

    }

    public String findPublicName(String instanceId) {
        try {
            DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instanceId);
            DescribeInstancesResult result = asynchEc2Client.describeInstances(describeInstancesRequest);
            if (result.getReservations() != null && result.getReservations().size() == 1) {
                return result.getReservations().get(0).getInstances().get(0).getPublicDnsName();
            }
        } catch (Exception e) {
            LOG.error("Error getting public dns: " + e, e);
        }
        return null;
    }

}
