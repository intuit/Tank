package com.intuit.tank.vmManager.environment.amazon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2AsyncClient;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.AttachVolumeRequest;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeAddressesRequest;
import com.amazonaws.services.ec2.model.DescribeAddressesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.IamInstanceProfileSpecification;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
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
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
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

public class AmazonInstance implements IEnvironmentInstance {

    protected static final long ASSOCIATE_IP_MAX_WAIT_MILIS = 1000 * 60 * 2;// 2
                                                                            // minutes
    private static Logger logger = Logger.getLogger(AmazonInstance.class);

    private AmazonEC2AsyncClient asynchEc2Client;
    // private TypicaInterface ec2Interface;
    private VMRegion vmRegion;
    private VMRequest request;

    private TankConfig config = new TankConfig();

    // private static final Set<String> reservedEipSet =
    // Collections.synchronizedSet(new HashSet<String>());

    /**
     * 
     * @param request
     * @param vmRegion
     */
    public AmazonInstance(VMRequest request, VMRegion vmRegion) {
        // In case vmRegion is passed as null, use default region from settings
        // file
        if (vmRegion == null) {
            // vmRegion = VMRegion.US_EAST;
            vmRegion = config.getVmManagerConfig().getDefaultRegion();
        }
        this.vmRegion = vmRegion;
        this.request = request;
        try {
            CloudCredentials creds = config.getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
            AWSCredentials credentials = null;
            if (StringUtils.isNotBlank(creds.getKey()) && StringUtils.isNotBlank(creds.getKeyId())) {
                credentials = new BasicAWSCredentials(creds.getKeyId(), creds.getKey());
            }
            ClientConfiguration clientConfig = new ClientConfiguration();
            if (StringUtils.isNotBlank(creds.getProxyHost())) {
                try {
                    clientConfig.setProxyHost(creds.getProxyHost());
                    if (StringUtils.isNotBlank(creds.getProxyPort())) {
                        clientConfig.setProxyPort(Integer.valueOf(creds.getProxyPort()));
                    }
                } catch (NumberFormatException e) {
                    logger.error("invalid proxy setup.");
                }

            }
            if (credentials != null) {
                asynchEc2Client = new AmazonEC2AsyncClient(credentials, clientConfig, Executors.newFixedThreadPool(2));
            } else {
                asynchEc2Client = new AmazonEC2AsyncClient(clientConfig);
            }
            // asynchEc2Client = new AmazonEC2AsyncClient(new
            // BasicAWSCredentials(creds.getKeyId(), creds.getKey()));
            asynchEc2Client.setEndpoint(vmRegion.getEndpoint());

        } catch (Exception ex) {
            logger.error("Error initializing amazonclient: " + ex, ex);
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
     * @{inheritDoc
     */
    @Override
    public List<VMInformation> describeInstances(String... instanceIds) {
        List<VMInformation> result = new ArrayList<VMInformation>();
        try {

            DescribeInstancesResult results = asynchEc2Client.describeInstances();
            HashSet<String> ids = new HashSet<String>(Arrays.asList(instanceIds));
            for (Reservation reservationDescription : results.getReservations()) {
                for (com.amazonaws.services.ec2.model.Instance instance : reservationDescription.getInstances()) {
                    if (ids.contains(instance.getInstanceId())) {
                        result.add(new AmazonDataConverter().instanceToVmInformation(reservationDescription, instance, vmRegion));
                    }
                }
                // result.addAll(TypicaDataConverter.processReservationDescription(reservationDescription));
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve instance from Amazon: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public List<VMInformation> create() {
        List<VMInformation> result = null;
        try {
            VMInstanceRequest instanceRequest = (VMInstanceRequest) request;
            InstanceDescription instanceDescription = instanceRequest.getInstanceDescription();
            if (instanceDescription == null) {
                instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(vmRegion, instanceRequest.getImage());
            }

            // Get the required data
            int number = instanceRequest.getNumberOfInstances();
            String image = instanceDescription.getAmi();
            if (instanceRequest.getReuseStoppedInstance()) {
                List<VMInformation> instances = findAllInstancesOfType(this.vmRegion, instanceRequest.getImage());
                logger.info("looking for stopped instance with ami-id of " + instanceRequest.getImage());
                for (VMInformation vmInfo : instances) {
                    logger.info("found instance with id " + vmInfo.getInstanceId() + " with state of " + vmInfo.getState());
                    if ("stopped".equalsIgnoreCase(vmInfo.getState())) {
                        StartInstancesRequest startInstancesRequest = new StartInstancesRequest();
                        startInstancesRequest.withInstanceIds(vmInfo.getInstanceId());
                        // restart this instance
                        StartInstancesResult startInstances = asynchEc2Client.startInstances(startInstancesRequest);
                        result = new AmazonDataConverter().processStateChange(startInstances.getStartingInstances());
                        break;
                    }
                }
            }
            if (result == null) {
                InstanceType size = AmazonInstance.getInstanceType(instanceRequest.getSize());
                VmInstanceType vmType = config.getVmManagerConfig().getInstanceType(instanceRequest.getSize());
                String keyPair = instanceDescription.getKeypair();
                if (instanceRequest.getJobId() != null) {
                    instanceRequest.addUserData(TankConstants.KEY_JOB_ID, instanceRequest.getJobId());
                }
                if (instanceRequest.getReportingMode() != null) {
                    logger.info("Setting reporting mode to " + instanceRequest.getReportingMode());
                    instanceRequest.addUserData(TankConstants.KEY_REPORTING_MODE, instanceRequest.getReportingMode());
                } else {
                    logger.warn("Reporting mode not set.");
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
                    logger.info("Setting loggingProfile to " + instanceRequest.getLoggingProfile());
                    instanceRequest.addUserData(TankConstants.KEY_LOGGING_PROFILE, instanceRequest.getLoggingProfile());
                } else {
                    logger.warn("Logging  profile not set.");
                }
                if (instanceRequest.getStopBehavior() != null) {
                    logger.info("Setting stopBehavior to " + instanceRequest.getStopBehavior());
                    instanceRequest.addUserData(TankConstants.KEY_STOP_BEHAVIOR, instanceRequest.getStopBehavior());
                } else {
                    logger.warn("stop Behavior not set.");
                }

                String userData = buildUserData(instanceRequest.getUserData());
                Set<String> availableEips = new HashSet<String>();
                if (instanceRequest.isUseEips()) {
                    synchronized (instanceRequest.getRegion()) {
                        DescribeAddressesResult describeAddresses = asynchEc2Client.describeAddresses(new DescribeAddressesRequest());
                        Set<String> reserved = config.getVmManagerConfig().getReservedElasticIps();
                        for (Address address : describeAddresses.getAddresses()) {

                            if ("standard".equalsIgnoreCase(address.getDomain()) && StringUtils.isBlank(address.getInstanceId())) {
                                String ip = address.getPublicIp();
                                if (!reserved.contains(ip)) {
                                    availableEips.add(ip);
                                }
                            }
                        }

                    }
                }
                List<String> randomizedIps = new ArrayList<String>(availableEips);
                Collections.shuffle(randomizedIps);
                RunInstancesRequest runInstancesRequest = new RunInstancesRequest(image, number, number);
                runInstancesRequest.withInstanceType(size.toString()).withKeyName(keyPair).withMonitoring(true).withUserData(userData);
                runInstancesRequest.withMonitoring(true);

                // add subnet if defined
                if (!StringUtils.isEmpty(instanceDescription.getSubnetId())) {
                    runInstancesRequest.withSubnetId(instanceDescription.getSubnetId());
                }
                Collection<String> c = getStringCollection(instanceDescription.getSecurityGroupIds());
                if (!c.isEmpty()) {
                    runInstancesRequest.withSecurityGroupIds(c);
                } else {
                    runInstancesRequest.withSecurityGroups(instanceDescription.getSecurityGroup());
                }
                if (StringUtils.isNotBlank(instanceDescription.getIamRole())) {
                    IamInstanceProfileSpecification iamInstanceProfile = new IamInstanceProfileSpecification().withName(instanceDescription.getIamRole());
                    runInstancesRequest.withIamInstanceProfile(iamInstanceProfile);
                }
                // add zone info if specified
                if (!StringUtils.isEmpty(instanceDescription.getZone())) {
                    runInstancesRequest.withPlacement(new Placement().withAvailabilityZone(instanceDescription.getZone()));
                }
                RunInstancesResult results = asynchEc2Client.runInstances(runInstancesRequest);
                result = new AmazonDataConverter().processReservation(results.getReservation(), vmRegion);
                if (instanceRequest.isUseEips()) {
                    synchronized (instanceRequest.getRegion()) {
                        Iterator<String> iter = randomizedIps.iterator();
                        List<AssociateContainer> bindIps = new ArrayList<AssociateContainer>();
                        for (VMInformation info : result) {
                            if (!iter.hasNext()) {
                                break;
                            }
                            String availableIp = iter.next();
                            bindIps.add(new AssociateContainer(info.getInstanceId(), availableIp));
                        }
                        CountDownLatch latch = new CountDownLatch(bindIps.size());
                        for (AssociateContainer container : bindIps) {
                            associateAddress(container.instanceId, container.publicIp, latch);
                        }
                        latch.await();
                    }
                }
            }
            if (instanceDescription.getPublicIp() != null && result.size() == 1) {
                // bind to the public ip
                String instanceId = result.get(0).getInstanceId();
                // wait for instance to be in running state
                associateAddress(instanceId, instanceDescription.getPublicIp(), null);
                // reboot(result);
            }
            if (result.size() > 0) {
                List<String> ids = new ArrayList<String>();
                for (VMInformation inst : result) {
                    ids.add(inst.getInstanceId());
                }
                logger.info("Setting tags for instances " + ids);
                tagInstance(ids, buildTags(instanceRequest));
            }

        } catch (Exception ex) {
            logger.error("Error starting instancs: " + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

    private Collection<String> getStringCollection(String s) {
        Set<String> ret = new HashSet<String>();
        if (StringUtils.isNotBlank(s)) {
            String[] strings = StringUtils.split(s, ",");
            for (String str : strings) {
                ret.add(str.trim());
            }
        }
        return ret;
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public void tagInstance(final List<String> instanceIds, KeyValuePair... tag) {
        if (tag.length != 0) {
            final List<Tag> tags = new ArrayList<Tag>();
            for (KeyValuePair pair : tag) {
                tags.add(new Tag(pair.getKey(), pair.getValue()));
            }

            new Thread(new Runnable() {

                @Override
                public void run() {
                    int count = 0;
                    try {
                        while (++count <= 5 && !instanceIds.isEmpty()) {
                            Thread.sleep(5000);
                            CreateTagsRequest createTagsRequest = new CreateTagsRequest().withResources(instanceIds).withTags(tags);
                            asynchEc2Client.createTagsAsync(createTagsRequest);
                            Thread.sleep(1000);

                            Future<DescribeInstancesResult> describeInstances = asynchEc2Client.describeInstancesAsync(new DescribeInstancesRequest().withInstanceIds(instanceIds));
                            for (Reservation r : describeInstances.get().getReservations()) {
                                for (Instance i : r.getInstances()) {
                                    if (i.getTags() != null && !i.getTags().isEmpty()) {
                                        instanceIds.remove(i.getInstanceId());
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error tagging instances: " + e, e);
                    }
                };
            }).start();
        }
    }

    /**
     * @param instanceDescription
     * @param instance
     * @return
     */
    private KeyValuePair[] buildTags(VMInstanceRequest instanceRequest) {
        List<KeyValuePair> pairs = new ArrayList<KeyValuePair>();
        pairs.add(new KeyValuePair("Name", buildNameTag(instanceRequest)));

        if (instanceRequest.getJobId() != null) {
            instanceRequest.addUserData(TankConstants.KEY_JOB_ID, instanceRequest.getJobId());
            instanceRequest.addUserData(TankConstants.KEY_CONTROLLER_URL, config.getControllerBase());

            if (NumberUtils.isNumber(instanceRequest.getJobId())) {
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
        return pairs.toArray(new KeyValuePair[pairs.size()]);
    }

    /**
     * @param instanceDescription
     * @param instance
     * @return
     */
    private String buildNameTag(VMInstanceRequest instanceRequest) {
        StringBuilder sb = new StringBuilder(instanceRequest.getImage().getConfigName());
        if (instanceRequest.getJobId() != null) {
            sb.append(" job(" + instanceRequest.getJobId() + ")");
        }
        return sb.toString();
    }

    @Override
    public List<VMInformation> kill() {
        List<VMInformation> result = null;
        try {
            VMKillRequest killRequest = (VMKillRequest) request;
            TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest(killRequest.getInstances());
            TerminateInstancesResult terminateInstances = asynchEc2Client.terminateInstances(terminateInstancesRequest);
            result = new AmazonDataConverter().processStateChange(terminateInstances.getTerminatingInstances());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
        return result;
    }

    public void killInstances(List<VMInformation> instances) {
        List<String> instanceIds = new ArrayList<String>(instances.size());
        for (VMInformation instance : instances) {
            instanceIds.add(instance.getInstanceId());
        }
        asynchEc2Client.terminateInstances(new TerminateInstancesRequest(instanceIds));
    }

    @Override
    public List<VMInformation> kill(List<String> instanceIds) {
        List<VMInformation> result = new ArrayList<VMInformation>();
        try {
            for (VMRegion region : config.getVmManagerConfig().getRegions()) {
                result.addAll(killForRegion(region, instanceIds));
            }
            asynchEc2Client.setEndpoint(vmRegion.getEndpoint());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
        return result;
    }

    private List<VMInformation> killForRegion(VMRegion region, List<String> instanceIds) {
        List<VMInformation> result = new ArrayList<VMInformation>();
        try {
            asynchEc2Client.setEndpoint(region.getEndpoint());
            List<VMInformation> instances = describeInstances(instanceIds.toArray(new String[instanceIds.size()]));
            List<String> ids = new ArrayList<String>();
            for (VMInformation info : instances) {
                ids.add(info.getInstanceId());
            }
            if (ids.size() > 0) {
                TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest(ids);
                TerminateInstancesResult terminateInstances = asynchEc2Client.terminateInstances(terminateInstancesRequest);
                result = new AmazonDataConverter().processStateChange(terminateInstances.getTerminatingInstances());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

    private List<VMInformation> stopForRegion(VMRegion region, List<String> instanceIds) {
        List<VMInformation> result = new ArrayList<VMInformation>();
        try {
            asynchEc2Client.setEndpoint(region.getEndpoint());
            List<VMInformation> instances = describeInstances(instanceIds.toArray(new String[instanceIds.size()]));
            List<String> ids = new ArrayList<String>();
            for (VMInformation info : instances) {
                ids.add(info.getInstanceId());
            }
            if (ids.size() > 0) {
                StopInstancesRequest stopInstancesRequest = new StopInstancesRequest(ids);
                StopInstancesResult stopResult = asynchEc2Client.stopInstances(stopInstancesRequest);
                List<InstanceStateChange> stoppingInstances = stopResult.getStoppingInstances();
                result = new AmazonDataConverter().processStateChange(stoppingInstances);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public List<VMInformation> findInstancesOfType(VMRegion region, VMImageType type) {
        List<VMInformation> ret = new ArrayList<VMInformation>();
        try {
            DescribeInstancesResult instances = asynchEc2Client.describeInstances();
            InstanceDescription instanceForRegionAndType = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(region, type);
            for (Reservation res : instances.getReservations()) {
                if (res.getInstances() != null) {
                    for (com.amazonaws.services.ec2.model.Instance inst : res.getInstances()) {
                        if ((inst.getState().getName().equalsIgnoreCase("running") || inst.getState().getName().equalsIgnoreCase("pending"))
                                && inst.getImageId().equals(instanceForRegionAndType.getAmi())) {
                            ret.add(new AmazonDataConverter().instanceToVmInformation(res, inst, region));
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error getting instances: " + e.toString(), e);
            throw new RuntimeException(e);
        }
        return ret;
    }

    // /**
    // * @{inheritDoc
    // */
    // public List<VMInformation> getTagInfo(String... instanceIds) {
    // List<VMInformation> ret = new ArrayList<VMInformation>();
    // try {
    // DescribeTagsResult tags = asynchEc2Client.describeTags();
    // for (TagDescription tag : tags.getTags()) {
    // tag.g
    // }
    // InstanceDescription instanceForRegionAndType = new
    // TankConfig().getVmManagerConfig()
    // .getInstanceForRegionAndType(region, type);
    // for (Reservation res : instances.getReservations()) {
    // if (res.getInstances() != null) {
    // for (com.amazonaws.services.ec2.model.Instance inst : res.getInstances())
    // {
    // if ((inst.getState().getName().equalsIgnoreCase("running") ||
    // inst.getState().getName()
    // .equalsIgnoreCase("pending"))
    // && inst.getImageId().equals(instanceForRegionAndType.getAmi())) {
    // ret.add(new AmazonDataConverter().instanceToVmInformation(res, inst));
    // }
    // }
    // }
    // }
    // } catch (Exception e) {
    // logger.error("Error getting instances: " + e.toString(), e);
    // throw new RuntimeException(e);
    // }
    // return ret;
    // }

    /**
     * @{inheritDoc
     */
    private List<VMInformation> findAllInstancesOfType(VMRegion region, VMImageType type) {
        List<VMInformation> ret = new ArrayList<VMInformation>();
        try {
            DescribeInstancesResult instances = asynchEc2Client.describeInstances();
            InstanceDescription instanceForRegionAndType = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(region, type);
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
            logger.error("Error getting instances: " + e.toString(), e);
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
        InstanceType output = InstanceType.C32xlarge;
        try {
            output = InstanceType.fromValue(size);
        } catch (Exception e) {
            logger.warn("Error parsing vminstanceType " + size, e);
        }
        return output;
    }

    @Override
    public void associateAddress(final String instanceId, final String publicIp, final CountDownLatch latch) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                boolean associated = false;
                try {
                    long start = System.currentTimeMillis();
                    int count = 0;
                    logger.info("Setting ip for instance " + instanceId + " to " + publicIp);
                    while ((System.currentTimeMillis() - start) < ASSOCIATE_IP_MAX_WAIT_MILIS && !associated) {
                        count++;
                        try {
                            long sleep = (new Random().nextInt(10) + 10) * 100L;
                            Thread.sleep(sleep);
                            asynchEc2Client.associateAddressAsync(new AssociateAddressRequest(instanceId, publicIp));
                            Thread.sleep(sleep);
                            Future<DescribeInstancesResult> describeInstances = asynchEc2Client.describeInstancesAsync(new DescribeInstancesRequest().withInstanceIds(instanceId));
                            for (Reservation r : describeInstances.get().getReservations()) {
                                for (Instance i : r.getInstances()) {
                                    if (publicIp.equals(i.getPublicIpAddress())) {
                                        associated = true;
                                    }
                                }
                            }
                            if (associated) {
                                logger.info(instanceId + " associated with " + publicIp);
                            } else if (count % 5 == 0) {
                                logger.info(instanceId + " not associated yet" + publicIp + ". Retrying... count = " + count);
                            }
                        } catch (Exception e) {
                            if (count < 5) {
                                logger.warn("Error associating ip address: " + e + " Will retry.");
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error setting elastic ip: " + e, e);
                } finally {
                    logger.info("exiting associated = " + associated);
                    if (latch != null) {
                        latch.countDown();
                    }
                }
            };
        }).start();

    }

    @Override
    public void reboot(List<VMInformation> instances) {
        List<String> instanceIds = new ArrayList<String>(instances.size());
        for (VMInformation instance : instances) {
            instanceIds.add(instance.getInstanceId());
        }
        asynchEc2Client.rebootInstancesAsync(new RebootInstancesRequest(instanceIds));
        // ec2Interface.rebootInstances(instanceIds);
    }

    /**
     * @param instanceIds
     */
    public List<VMInformation> stopInstances(List<String> instanceIds) {
        List<VMInformation> result = new ArrayList<VMInformation>();
        try {
            for (VMRegion region : config.getVmManagerConfig().getRegions()) {
                result.addAll(killForRegion(region, instanceIds));
            }
            asynchEc2Client.setEndpoint(vmRegion.getEndpoint());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * @param jobId
     * @return
     */
    private String buildUserData(@Nonnull Map<String, String> userDataMap) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : userDataMap.entrySet()) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(entry.getKey() + "=" + entry.getValue());
        }

        return Base64.encodeBase64String(sb.toString().getBytes());
    }

    private static class AssociateContainer {
        private String instanceId;
        private String publicIp;

        private AssociateContainer(String instanceId, String publicIp) {
            super();
            this.instanceId = instanceId;
            this.publicIp = publicIp;
        }

    }

    public String findPublicName(String instanceId) {
        String ret = null;
        try {
            DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instanceId);
            DescribeInstancesResult result = asynchEc2Client.describeInstances(describeInstancesRequest);
            if (result.getReservations() != null && result.getReservations().size() == 1) {
                Reservation reservation = result.getReservations().get(0);
                String publicDnsName = reservation.getInstances().get(0).getPublicDnsName();
                ret = publicDnsName;
            }
        } catch (Exception e) {
            logger.error("Error getting public dns: " + e, e);
        }
        return ret;
    }

}
