package com.intuit.tank.persistence.databases;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.PagedDatabaseResult;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.settings.TankConfig;

public class GraphiteDatasource implements IDatabase {
    private static final Logger LOG = Logger.getLogger(GraphiteDatasource.class);
	
	private String enviornemnt = "qa";
	private String graphiteHost = "10.20.1.13";
	private int graphitePort = 2003;
	Date send = new Date();
	Calendar c = Calendar.getInstance();
	List<TankResult> outgoing = new ArrayList<TankResult>();
	
    private static TankConfig config = new TankConfig();

	@Override
	public void createTable(String tableName) {
        HierarchicalConfiguration resultsProviderConfig = config.getVmManagerConfig().getResultsProviderConfig();
        if (resultsProviderConfig != null) {
            try {
            	enviornemnt = resultsProviderConfig.getString("enviornemnt");
            	graphiteHost = resultsProviderConfig.getString("graphiteHost");
            	String s = resultsProviderConfig.getString("graphitePort");
                if (NumberUtils.isDigits(s)) {
                	graphitePort = Integer.parseInt(s);
                }
            } catch (Exception e) {
                LOG.error(e.toString());
            }
        }

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
		outgoing.addAll(results);
		if (send.after(results.get(0).getTimeStamp())) {
			Collections.sort(outgoing);
			try {
				Socket socket = new Socket(graphiteHost, graphitePort);
				OutputStream s = socket.getOutputStream();
				PrintWriter out = new PrintWriter(s, true);
				String requestName = "";
				String jobId = "";
				long sum = 0;
				int count = 0;
				for (TankResult metric: outgoing) {
					if (metric.getRequestName().equalsIgnoreCase(requestName)) {
						sum += metric.getResponseTime();
						count++;
					} else if (count != 0) {
						long average = sum / count;
						long l = send.getTime() / 1000;
						out.printf("tank.%s.%s.%s.ResponseTime.AVG %d %d%n", enviornemnt, metric.getJobId(), requestName, average, l );	
						requestName = metric.getRequestName();
						jobId = metric.getJobId();
						sum = metric.getResponseTime();
						count = 1;
					} else { // Handles the first time through //
						requestName = metric.getRequestName();
						jobId = metric.getJobId();
						sum = metric.getResponseTime();
						count = 1;
					}
				}
				long average = sum / count;
				long l = send.getTime() / 1000;
				out.printf("tank.%s.%s.%s.ResponseTime.AVG %d %d%n", enviornemnt, jobId, requestName, average, l );	
				out.close();
				socket.close();
			} catch (UnknownHostException e) {
				LOG.error("Unknown host: " + graphiteHost);
			} catch (IOException e) {
				LOG.error("Error while writing data to graphite: " + e.getMessage(), e);
			} catch (Exception e) {
				LOG.error("Error: " + e.getMessage(), e);
			}
			c.setTime(send);
			c.add(Calendar.SECOND, 15);
			send = new Date(c.getTime().getTime());
			outgoing.clear();
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
		return graphiteHost;
	}

}
