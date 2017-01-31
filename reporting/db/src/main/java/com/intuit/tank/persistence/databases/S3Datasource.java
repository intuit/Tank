package com.intuit.tank.persistence.databases;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.GetUserResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.PagedDatabaseResult;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * S3Datasource
 * 
 * @author Kevin McGoldrick
 * 
 */
public class S3Datasource implements IDatabase {
    private static final Logger LOG = LogManager.getLogger(S3Datasource.class);
	
    InetAddress ip;
    private String hostname = "fail";
	private String enviornemnt = "qa";
	private String metricString = "ctg.tank.transaction";
	private String bucketName = "tank-test";
    
    private TankConfig config = new TankConfig();
	private HierarchicalConfiguration resultsProviderConfig = config.getVmManagerConfig().getResultsProviderConfig();

	@Override
	public void createTable(String tableName) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteTable(String tableName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteForJob(String tableName, String jobId, boolean asynch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasTable(String tableName) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean hasJobData(String tableName, String jobId) {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addTimingResults(String tableName, List<TankResult> results, boolean asynch) {
        if (resultsProviderConfig != null) {
            try {
            	enviornemnt = config.getInstanceName();
            	if (StringUtils.equalsIgnoreCase(enviornemnt, "prod")) {
            		enviornemnt = "prd";
            	} else if (StringUtils.equalsIgnoreCase(enviornemnt, "qa")) {
            		enviornemnt = "qal";
            	} else if (StringUtils.equalsIgnoreCase(enviornemnt, "canada")) {
            		enviornemnt = "can";
            	} else {
            		enviornemnt = "prf";
            	}
            	metricString = resultsProviderConfig.getString("metricString");
            	bucketName = resultsProviderConfig.getString("bucket");
            	hostname = ip.getHostName();
            } catch (Exception e) {
                LOG.error("Failed to get S3 Datasource parameters " + e.toString(), e);
            }
        }
		String jobId = results.get(0).getJobId();
		String instance = results.get(0).getInstanceId();
		long l = results.get(results.size()-1).getTimeStamp().getTime() / 1000;
		Collections.sort(results);
		
		try {
			AmazonIdentityManagement iamClient = AmazonIdentityManagementClientBuilder.defaultClient();
			String arn = iamClient.getUser().getUser().getArn();
			int start = arn.lastIndexOf("arn:aws:iam::");
			String account = arn.substring(start, arn.indexOf(":", start+1));
			
			AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
			
			String Region = s3Client.getRegionName();
			StringBuilder sb = new StringBuilder();
			List<Long> groupResults = new ArrayList<Long>();
			String requestName = "";
			long sum = 0;
			for (TankResult metric: results) {
				if (metric.getRequestName().equalsIgnoreCase(requestName)) {
					groupResults.add(Long.valueOf(metric.getResponseTime()));
					sum += metric.getResponseTime();
				} else if (!groupResults.isEmpty()) { // Handles the last time through of the group//
					int size = groupResults.size();
					Collections.sort(groupResults);
					Long[] sortedList = groupResults.toArray(new Long[size]);
					long average = sum / size;
					int fiftieth = (size/2);
					if (fiftieth >= 1) fiftieth--;
					int ninetieth =(size*(9/10));
					if (ninetieth >= 1) ninetieth--;
					sb.append(metricString + ".resp_time.min " + sortedList[0].longValue() + " " + l)
						.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName + " location=" + Region + " accountid=" + account)
						.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
						.append(metricString + ".resp_time.avg " + average + " " + l)
						.append(" source=" + hostname + " instanceid=" + instance + " transaction= " + requestName + " location=" + Region + " accountid=" + account)
						.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
						.append(metricString + ".resp_time.max " + sortedList[size-1].longValue() + " " + l)
						.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName + " location=" + Region + " accountid=" + account)
						.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
						.append(metricString + ".resp_time.tp_50 " + sortedList[fiftieth].longValue() + " " + l)
						.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName + " location=" + Region + " accountid=" + account)
						.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
						.append(metricString + ".resp_time.tp_90 " + sortedList[ninetieth].longValue() + " " + l)
						.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName + " location=" + Region + " accountid=" + account)
						.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
						.append(metricString + ".rpi " + size + " " + l)
						.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName + " location=" + Region + " accountid=" + account)
						.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"));
					requestName = metric.getRequestName();
					groupResults.clear();
					groupResults.add(Long.valueOf(metric.getResponseTime()));
					sum = 0;
				} else { // Handles the first time through //
					requestName = metric.getRequestName();
					groupResults.add(Long.valueOf(metric.getResponseTime()));
					sum = metric.getResponseTime();
				}
			} // Get that last one //
			int size = groupResults.size();
			Collections.sort(groupResults);
			Long[] sortedList = groupResults.toArray(new Long[size]);
			long average = sum / size;
			int fiftieth = (size/2);
			if (fiftieth >= 1) fiftieth--;
			int ninetieth =(size*(9/10));
			if (ninetieth >= 1) ninetieth--;
			sb.append(metricString + ".resp_time.min " + sortedList[0].longValue() + " " + l)
				.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName + " location=" + Region + " accountid=" + account)
				.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
				.append(metricString + ".resp_time.avg " + average + " " + l)
				.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName + " location=" + Region + " accountid=" + account)
				.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
				.append(metricString + ".resp_time.max " + sortedList[size-1].longValue() + " " + l)
				.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName + " location=" + Region + " accountid=" + account)
				.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
				.append(metricString + ".resp_time.tp_50 " + sortedList[fiftieth].longValue() + " " + l)
				.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName + " location=" + Region + " accountid=" + account)
				.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
				.append(metricString + ".resp_time.tp_90 " + sortedList[ninetieth].longValue() + " " + l)
				.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName + " location=" + Region + " accountid=" + account)
				.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
				.append(metricString + ".rpi " + size + " " + l)
				.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName + " location=" + Region + " accountid=" + account)
				.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"));
			InputStream is =  new ByteArrayInputStream(sb.toString().getBytes());
			ObjectMetadata metaData = new ObjectMetadata();
			metaData.setContentLength(sb.length());
			s3Client.putObject(new PutObjectRequest(bucketName, "TANK-AgentData-" + UUID.randomUUID(), is, metaData));
		} catch (AmazonServiceException ase) {
			LOG.error("AmazonServiceException: which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason." + ase.getMessage(), ase);
		} catch (AmazonClientException ace) {
			LOG.error("AmazonClientException: which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network." + ace.getMessage(), ace);
		} catch (Exception e) {
			LOG.error("Error: " + e.getMessage(), e);
		}
	}

	@Override
	public Set<String> getTables(String regex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addItems(String tableName, List<Item> items, boolean asynch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Item> getItems(String tableName, String minRange, String maxRange, String instanceId, String... jobId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedDatabaseResult getPagedItems(String tableName,
			Object nextToken, String minRange, String maxRange,
			String instanceId, String jobId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatabaseName(TankDatabaseType type, String jobId) {
		// TODO Auto-generated method stub
		return bucketName;
	}

}
