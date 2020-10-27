package com.intuit.tank.vmManager.environment.amazon;

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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.ProxyConfiguration;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.Ec2AsyncClientBuilder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.Address;
import software.amazon.awssdk.services.ec2.model.AssociateAddressRequest;
import software.amazon.awssdk.services.ec2.model.AttachVolumeRequest;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeAddressesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeAddressesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.IamInstanceProfileSpecification;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.Placement;
import software.amazon.awssdk.services.ec2.model.RebootInstancesRequest;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.RunInstancesMonitoringEnabled;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StartInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.Tenancy;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesResponse;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class AmazonInstance implements IEnvironmentInstance {

    protected static final long ASSOCIATE_IP_MAX_WAIT_MILIS = 1000 * 60 * 2;// 2 minutes
    private static final int MAX_INSTANCE_BATCH_SIZE = 10;
    private static Logger LOG = LogManager.getLogger(AmazonInstance.class);

    private Ec2AsyncClient ec2AsyncClient;
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
            Ec2AsyncClientBuilder ec2AsyncClientBuilder = Ec2AsyncClient.builder();
            if (creds != null && StringUtils.isNotBlank(creds.getProxyHost())) {
                try {
                    ProxyConfiguration.Builder proxyConfig = ProxyConfiguration.builder().host(creds.getProxyHost());
                    if (StringUtils.isNotBlank(creds.getProxyPort())) {
                        proxyConfig.port(Integer.parseInt(System.getProperty(creds.getProxyPort())));
                    }
                    NettyNioAsyncHttpClient.Builder httpClientBuilder =
                            NettyNioAsyncHttpClient.builder().proxyConfiguration(proxyConfig.build());
                    ec2AsyncClientBuilder.httpClientBuilder(httpClientBuilder);
                } catch (NumberFormatException e) {
                    LOG.error("invalid proxy setup.");
                }
            }
            if (creds != null && StringUtils.isNotBlank(creds.getKey()) && StringUtils.isNotBlank(creds.getKeyId())) {
                AwsCredentials credentials = AwsBasicCredentials.create(creds.getKeyId(), creds.getKey());
                ec2AsyncClientBuilder.credentialsProvider(StaticCredentialsProvider.create(credentials));
            }
            ec2AsyncClient = ec2AsyncClientBuilder
                    .region(Region.of(vmRegion.getRegion()))
                    .build();
        } catch (Exception ex) {
            LOG.error("Error initializing amazonclient: " + ex, ex);
            throw new RuntimeException(ex);
        }
    }

    public void attachVolume(String volumneId, String instanceId, String device) {
        AttachVolumeRequest attachVolumeRequest =
                AttachVolumeRequest.builder().volumeId(volumneId).instanceId(instanceId).device(device).build();
        ec2AsyncClient.attachVolume(attachVolumeRequest);
        RebootInstancesRequest rebootInstancesRequest =
                RebootInstancesRequest.builder().instanceIds(instanceId).build();
        ec2AsyncClient.rebootInstances(rebootInstancesRequest);
    }

    /**
     * 
     * @inheritDoc
     */
    @Override
    public List<VMInformation> describeInstances(String... instanceIds) {
        HashSet<String> ids = new HashSet<String>(Arrays.asList(instanceIds));

        return ec2AsyncClient.describeInstances().join().reservations().stream()
                .flatMap(reservationDescription -> reservationDescription.instances()
                        .stream()
                        .filter(instance -> ids.contains(instance.instanceId()))
                        .map(instance -> new AmazonDataConverter().instanceToVmInformation(reservationDescription.requesterId(), instance, vmRegion)))
                .collect(Collectors.toList());
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
                        StartInstancesRequest startInstancesRequest = StartInstancesRequest.builder().instanceIds(vmInfo.getInstanceId()).build();
                        // restart this instance
                        StartInstancesResponse startInstances = ec2AsyncClient.startInstances(startInstancesRequest).join();
                        result.addAll(new AmazonDataConverter().processStateChange(startInstances.startingInstances()));
                        break;
                    }
                }
            }
            if (result.isEmpty()) {
                InstanceType instanceType = AmazonInstance.getInstanceType(instanceRequest.getSize());
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

                RunInstancesRequest.Builder runInstancesRequestTemplate = RunInstancesRequest.builder();
                Tenancy tenancy = StringUtils.isEmpty(instanceDescription.getTenancy()) ? Tenancy.DEFAULT : Tenancy.fromValue(instanceDescription.getTenancy());
                runInstancesRequestTemplate.imageId(image)
                        .instanceType(instanceType.toString())
                        .keyName(keyPair)
                        .placement(Placement.builder().tenancy(tenancy).build())
                        .monitoring(RunInstancesMonitoringEnabled.builder().build())
                        .userData(userData);

                Collection<String> c = instanceDescription.getSecurityGroupIds();
                if (!c.isEmpty()) {
                	LOG.info("Security Group IDs " + c.toString());
                    runInstancesRequestTemplate.securityGroupIds(c);
                } else {
                    runInstancesRequestTemplate.securityGroups(instanceDescription.getSecurityGroup());
                }
                if (StringUtils.isNotBlank(instanceDescription.getIamRole())) {
                    runInstancesRequestTemplate.iamInstanceProfile(
                            IamInstanceProfileSpecification.builder().name(instanceDescription.getIamRole()).build());
                }
                // add zone info if specified
                if (!StringUtils.isEmpty(instanceDescription.getZone())) {
                    runInstancesRequestTemplate.placement(Placement.builder().availabilityZone(instanceDescription.getZone()).build());
                }
                // Loop to request agent instances
                List<String> subnetIds = instanceDescription.getSubnetIds();
                int position = ThreadLocalRandom.current().nextInt(subnetIds.size());
                while ( !subnetIds.isEmpty() && remaining > 0 ) {
                    RunInstancesRequest.Builder runInstancesRequest = runInstancesRequestTemplate.copy();
                    runInstancesRequest.subnetId(subnetIds.get(position >= subnetIds.size() ? 0 : position++));
                    int requestCount = Math.min(remaining, MAX_INSTANCE_BATCH_SIZE);
                    try {
                        RunInstancesResponse response = ec2AsyncClient.runInstances(
                                runInstancesRequest.minCount(1).maxCount(requestCount).build()).join();
                        result.addAll(new AmazonDataConverter().processReservation(
                                response.requesterId(), response.instances(), vmRegion));
                        remaining -= response.instances().size();
                    } catch (Ec2Exception ae) {
                        LOG.error("Amazon issue starting instances: count="
                                + remaining + " : subnets=" + subnetIds.size() + " : " + ae.getMessage());
                        if(StringUtils.contains(ae.getMessage(), "InsufficientInstanceCapacity")) {
                            subnetIds.remove(runInstancesRequest.build().subnetId());
                        }
                    }
                }

                if (instanceRequest.isUseEips()) {
                    Set<Address> availableEips = new HashSet<Address>();
                    synchronized (instanceRequest.getRegion()) {
                        DescribeAddressesResponse describeAddresses =
                                ec2AsyncClient.describeAddresses(DescribeAddressesRequest.builder().build()).join();
                        Set<String> reserved = config.getVmManagerConfig().getReservedElasticIps();
                        for (Address address : describeAddresses.addresses()) {
                            String elasticIPType = instanceDescription.isVPC() ? "vpc" : "standard";
                            if (elasticIPType.equalsIgnoreCase(address.domainAsString()) && StringUtils.isBlank(address.instanceId())) {
                                String ip = address.publicIp();
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
                associateAddress(instanceId, Address.builder().publicIp(instanceDescription.getPublicIp()).build(), null);
                // reboot(result);
            }
            if (!result.isEmpty()) {
                List<String> ids = result.stream().map(VMInformation::getInstanceId).collect(Collectors.toList());
                LOG.info("Setting tags for instances " + ids);
                tagInstance(ids, buildTags(instanceRequest));
            }

        } catch (Ec2Exception ae) {
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
                    .map(pair -> Tag.builder().key(pair.getKey()).value(pair.getValue()).build()).collect(Collectors.toList());

            new Thread( () -> {
                int count = 0;
                try {
                    while (++count <= 5 && !instanceIds.isEmpty()) {
                        CreateTagsRequest createTagsRequest = CreateTagsRequest.builder().resources(instanceIds).tags(tags).build();
                        ec2AsyncClient.createTags(createTagsRequest);
                        DescribeInstancesResponse response =
                                ec2AsyncClient.describeInstances(
                                        DescribeInstancesRequest.builder().instanceIds(instanceIds).build()).join();
                        for (Reservation reservation : response.reservations()) {
                            for (Instance instance : reservation.instances()) {
                                if (instance.tags() != null && !instance.tags().isEmpty()) {
                                    instanceIds.remove(instance.instanceId());
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
            sb.append("-job[" + instanceRequest.getJobId() + "]");
        }
        if (StringUtils.isNoneEmpty(config.getInstanceName())) {
            sb.insert(0, config.getInstanceName() + "-");
        }
        return sb.toString();
    }

    @Override
    public List<VMInformation> kill(VMRequest request) {
        VMKillRequest killRequest = (VMKillRequest) request;
        TerminateInstancesRequest terminateInstancesRequest =
                TerminateInstancesRequest.builder().instanceIds(killRequest.getInstances()).build();
        TerminateInstancesResponse response = ec2AsyncClient.terminateInstances(terminateInstancesRequest).join();
        return new AmazonDataConverter().processStateChange(response.terminatingInstances());
    }

    @Override
    public void killInstances(List<String> instanceIds) {
        // Filter instanceId list to only instances in the client defined region.
        List<VMInformation> instancesInRegion = describeInstances(instanceIds.toArray(new String[instanceIds.size()]));
        List<String> instanceIdsInRegion = instancesInRegion.stream().map(VMInformation::getInstanceId).collect(Collectors.toList());

        if (!instanceIdsInRegion.isEmpty()) {
            ec2AsyncClient.terminateInstances(TerminateInstancesRequest.builder().instanceIds(instanceIdsInRegion).build());
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<VMInformation> findInstancesOfType(VMRegion region, VMImageType type) {
        List<VMInformation> ret = new ArrayList<>();
        try {
            DescribeInstancesResponse response = ec2AsyncClient.describeInstances().join();
            InstanceDescription instanceForRegionAndType = config.getVmManagerConfig().getInstanceForRegionAndType(region, type);
            for (Reservation reservation : response.reservations()) {
                if (reservation.instances() != null) {
                    for (Instance instance : reservation.instances()) {
                        if ((instance.state().nameAsString().equalsIgnoreCase("running")
                                || instance.state().nameAsString().equalsIgnoreCase("pending"))
                                && instance.imageId().equals(instanceForRegionAndType.getAmi())) {
                            ret.add(new AmazonDataConverter().instanceToVmInformation(reservation.requesterId(), instance, region));
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
        List<VMInformation> ret = new ArrayList<>();
        try {
            DescribeInstancesResponse response = ec2AsyncClient.describeInstances().join();
            InstanceDescription instanceForRegionAndType = config.getVmManagerConfig().getInstanceForRegionAndType(region, type);
            for (Reservation reservation : response.reservations()) {
                if (reservation.instances() != null) {
                    for (Instance instance : reservation.instances()) {
                        if (instance.imageId().equals(instanceForRegionAndType.getAmi())) {
                            ret.add(new AmazonDataConverter().instanceToVmInformation(reservation.requesterId(), instance, region));
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
        return InstanceType.C5_LARGE;
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
                final SsmClient ssmClient = SsmClient.builder().build();
                GetParameterResponse response = ssmClient.getParameter(GetParameterRequest.builder().name(name).build());
                return response.parameter().value();
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
                LOG.info("Setting ip for instance " + instanceId + " to " + address.publicIp());
                while ((System.currentTimeMillis() - start) < ASSOCIATE_IP_MAX_WAIT_MILIS && !associated) {
                    count++;
                    try {
                        if (address.allocationId() == null) {
                            ec2AsyncClient.associateAddress(
                                    AssociateAddressRequest.builder().instanceId(instanceId).publicIp(address.publicIp()).build());
                        } else {
                            ec2AsyncClient.associateAddress(
                                    AssociateAddressRequest.builder().instanceId(instanceId).allocationId(address.allocationId()).build());
                        }
                        Thread.sleep((new Random().nextInt(10) + 10) * 100L);
                        DescribeInstancesResponse describeInstances =
                                ec2AsyncClient.describeInstances(
                                        DescribeInstancesRequest.builder().instanceIds(instanceId).build()).join();
                        for (Reservation reservation : describeInstances.reservations()) {
                            for (Instance instance : reservation.instances()) {
                                if (address.publicIp().equals(instance.publicIpAddress())) {
                                    associated = true;
                                }
                            }
                        }
                        if (associated) {
                            LOG.info(instanceId + " associated with " + address.publicIp());
                        } else if (count % 5 == 0) {
                            LOG.info(instanceId + " not associated yet " + address.publicIp() + ". Retrying... count = " + count);
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
        ec2AsyncClient.rebootInstances(RebootInstancesRequest.builder().instanceIds(instanceIds).build());
    }

    /**
     * @param instanceIds
     */
    public void stopInstances(List<String> instanceIds) {

        // Filter instanceId list to only instances in the client defined region.
        List<VMInformation> instancesInRegion = describeInstances(instanceIds.toArray(new String[instanceIds.size()]));
        List<String> instanceIdsInRegion = instancesInRegion.stream().map(VMInformation::getInstanceId).collect(Collectors.toList());

        if (!instanceIdsInRegion.isEmpty()) {
            ec2AsyncClient.stopInstances(StopInstancesRequest.builder().instanceIds(instanceIdsInRegion).build());
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
            DescribeInstancesRequest describeInstancesRequest =
                    DescribeInstancesRequest.builder().instanceIds(instanceId).build();
            DescribeInstancesResponse response = ec2AsyncClient.describeInstances(describeInstancesRequest).join();
            if (response.reservations() != null && response.reservations().size() == 1) {
                Reservation reservation = response.reservations().get(0);
                return reservation.instances().get(0).publicDnsName();
            }
        } catch (Exception e) {
            LOG.error("Error getting public dns: " + e, e);
        }
        return null;
    }

}
