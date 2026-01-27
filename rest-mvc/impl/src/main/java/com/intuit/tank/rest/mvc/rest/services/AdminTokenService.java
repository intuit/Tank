package com.intuit.tank.rest.mvc.rest.services;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import java.time.Instant;

import jakarta.annotation.PostConstruct;

@Service
public class AdminTokenService {
    
    private static final Logger LOG = LogManager.getLogger(AdminTokenService.class);
    
    private TankConfig tankConfig;
    private UserDao userDao;
    
    @PostConstruct
    public void init() {
        tankConfig = new TankConfig();
        userDao = new UserDao();
        LOG.info("AdminTokenService initialized for environment: {}", tankConfig.getInstanceName());
    }
    
    /**
     * Validates if the provided token is both a valid user token and matches the admin token from SSM
     * 
     * @param token The token to validate
     * @return true if token is valid admin token, false otherwise
     */
    public boolean isValidAdminToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            LOG.warn("Admin token validation failed: token is null or empty");
            return false;
        }
        
        // First check if token corresponds to a valid user
        User user = userDao.findByApiToken(token);
        if (user == null) {
            LOG.warn("Admin token validation failed: no user found for token {}", maskToken(token));
            return false;
        }
        
        // Then check if token matches the admin token from SSM
        String ssmAdminToken = getAdminTokenFromSSM();
        if (ssmAdminToken == null) {
            LOG.error("Admin token validation failed: could not retrieve admin token from SSM");
            return false;
        }
        
        boolean tokensMatch = token.equals(ssmAdminToken);
        if (tokensMatch) {
            // Update last login timestamp for successful API token authentication
            user.setLastLoginTs(Instant.now());
            userDao.saveOrUpdate(user);
            LOG.info("Admin token validation successful for user: {} with token: {}", user.getName(), maskToken(token));
        } else {
            LOG.warn("Admin token validation failed: token does not match SSM admin token");
        }
        
        return tokensMatch;
    }
    
    /**
     * Retrieves the admin token from SSM parameter store
     * 
     * @return The admin token from SSM, or null if not found/error
     */
    private String getAdminTokenFromSSM() {
        String environment = tankConfig.getInstanceName();
        String parameterName = String.format("/Tank/%s/admin_token", environment);
        
        try (SsmClient ssmClient = SsmClient.builder().build()) {
            GetParameterRequest request = GetParameterRequest.builder()
                    .name(parameterName)
                    .withDecryption(true)
                    .build();
            
            GetParameterResponse response = ssmClient.getParameter(request);
            String token = response.parameter().value();
            
            LOG.info("Successfully retrieved admin token from SSM parameter: {}", parameterName);
            return token;
            
        } catch (Exception e) {
            LOG.error("Failed to retrieve admin token from SSM parameter: {} - Error: {}", 
                     parameterName, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Masks a token for safe logging by showing only first 4 and last 4 characters
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "****";
        }
        return token.substring(0, 4) + "****" + token.substring(token.length() - 4);
    }
}
