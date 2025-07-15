package com.intuit.tank.rest.mvc.rest.services.user;

import com.intuit.tank.dao.UserDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of UserService for user data operations
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger LOG = LogManager.getLogger(UserServiceImpl.class);
    
    private UserDao userDao = new UserDao();
    
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private final ConcurrentHashMap<String, Map<String, Object>> jobResults = new ConcurrentHashMap<>();
    
    @Override
    public String processExportRequest(String userIdentifier) {
        String jobId = UUID.randomUUID().toString();
        
        Map<String, Object> jobStatus = new HashMap<>();
        jobStatus.put("jobId", jobId);
        jobStatus.put("status", "ACKNOWLEDGED");
        jobStatus.put("operation", "EXPORT");
        jobStatus.put("userIdentifier", userIdentifier);
        
        jobResults.put(jobId, jobStatus);
        
        // Process asynchronously
        executorService.submit(() -> processExportAsync(jobId, userIdentifier));
        
        LOG.info("Export request acknowledged for user: {} with jobId: {}", userIdentifier, jobId);
        return jobId;
    }
    
    @Override
    public String processDeleteRequest(String userIdentifier) {
        String jobId = UUID.randomUUID().toString();
        
        Map<String, Object> jobStatus = new HashMap<>();
        jobStatus.put("jobId", jobId);
        jobStatus.put("status", "ACKNOWLEDGED");
        jobStatus.put("operation", "DELETE");
        jobStatus.put("userIdentifier", userIdentifier);
        
        jobResults.put(jobId, jobStatus);
        
        // Process asynchronously
        executorService.submit(() -> processDeleteAsync(jobId, userIdentifier));
        
        LOG.info("Delete request acknowledged for user: {} with jobId: {}", userIdentifier, jobId);
        return jobId;
    }
    
    @Override
    public Map<String, Object> getJobStatus(String jobId) {
        return jobResults.get(jobId);
    }
    
    private void processExportAsync(String jobId, String userIdentifier) {
        Map<String, Object> jobStatus = jobResults.get(jobId);
        
        try {
            jobStatus.put("status", "IN_PROGRESS");
            LOG.info("Starting export for user: {} (jobId: {})", userIdentifier, jobId);
            
            Map<String, Object> userData = userDao.exportUserData(userIdentifier);
            
            if (userData.isEmpty()) {
                jobStatus.put("status", "NO_DATA");
                jobStatus.put("message", "No data found for user");
            } else {
                jobStatus.put("status", "SUCCESS");
                jobStatus.put("data", userData);
                jobStatus.put("recordCount", userData.size());
            }
            
            LOG.info("Export completed for user: {} (jobId: {})", userIdentifier, jobId);
            
        } catch (Exception e) {
            LOG.error("Export failed for user: {} (jobId: {})", userIdentifier, jobId, e);
            jobStatus.put("status", "FAILED");
            jobStatus.put("error", e.getMessage());
        }
        
        // Schedule cleanup after 1 hour
        executorService.schedule(() -> jobResults.remove(jobId), 1, TimeUnit.HOURS);
    }
    
    private void processDeleteAsync(String jobId, String userIdentifier) {
        Map<String, Object> jobStatus = jobResults.get(jobId);
        
        try {
            jobStatus.put("status", "IN_PROGRESS");
            LOG.info("Starting deletion for user: {} (jobId: {})", userIdentifier, jobId);
            
            // Perform deletion directly
            long deletedCount = userDao.deleteUserData(userIdentifier);
            
            if (deletedCount == 0) {
                jobStatus.put("status", "NO_DATA");
                jobStatus.put("message", "No data found for user");
            } else {
                jobStatus.put("status", "SUCCESS");
                jobStatus.put("recordsAffected", deletedCount);
                jobStatus.put("message", "User data successfully anonymized");
            }
            
            LOG.info("Deletion completed for user: {} (jobId: {})", userIdentifier, jobId);
            
        } catch (Exception e) {
            LOG.error("Deletion failed for user: {} (jobId: {})", userIdentifier, jobId, e);
            jobStatus.put("status", "FAILED");
            jobStatus.put("error", e.getMessage());
        }
        
        // Schedule cleanup after 1 hour
        executorService.schedule(() -> jobResults.remove(jobId), 1, TimeUnit.HOURS);
    }
} 