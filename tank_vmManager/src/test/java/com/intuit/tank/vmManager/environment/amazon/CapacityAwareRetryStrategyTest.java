package com.intuit.tank.vmManager.environment.amazon;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.retry.AwsRetryStrategy;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.retries.api.AcquireInitialTokenRequest;
import software.amazon.awssdk.retries.api.RefreshRetryTokenRequest;
import software.amazon.awssdk.retries.api.RetryStrategy;
import software.amazon.awssdk.retries.api.RetryToken;
import software.amazon.awssdk.retries.api.TokenAcquisitionFailedException;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;

import static com.intuit.tank.vmManager.environment.amazon.AmazonInstance.INSUFFICIENT_INSTANCE_CAPACITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CapacityAwareRetryStrategyTest {

    private final RetryStrategy strategy =
            new CapacityAwareRetryStrategy(AwsRetryStrategy.forRetryMode(RetryMode.ADAPTIVE_V2));

    @Test
    public void doesNotRetryInsufficientInstanceCapacity() {
        RetryToken token = strategy.acquireInitialToken(AcquireInitialTokenRequest.create("ec2")).token();
        Ec2Exception ice = ec2Exception(500, INSUFFICIENT_INSTANCE_CAPACITY);

        assertThrows(TokenAcquisitionFailedException.class, () -> strategy.refreshRetryToken(
                RefreshRetryTokenRequest.builder().token(token).failure(ice).build()));
    }

    @Test
    public void stillRetriesOtherServerErrors() {
        RetryToken token = strategy.acquireInitialToken(AcquireInitialTokenRequest.create("ec2")).token();
        Ec2Exception internalError = ec2Exception(500, "InternalError");

        assertNotNull(strategy.refreshRetryToken(
                RefreshRetryTokenRequest.builder().token(token).failure(internalError).build()).token());
    }

    @Test
    public void toBuilderKeepsCapacityCheck() {
        RetryStrategy rebuilt = strategy.toBuilder().maxAttempts(5).build();

        assertInstanceOf(CapacityAwareRetryStrategy.class, rebuilt);
        assertEquals(5, rebuilt.maxAttempts());
        assertFalse(rebuilt.useClientDefaults());
    }

    private static Ec2Exception ec2Exception(int statusCode, String errorCode) {
        return (Ec2Exception) Ec2Exception.builder()
                .statusCode(statusCode)
                .awsErrorDetails(AwsErrorDetails.builder().errorCode(errorCode).build())
                .build();
    }
}
