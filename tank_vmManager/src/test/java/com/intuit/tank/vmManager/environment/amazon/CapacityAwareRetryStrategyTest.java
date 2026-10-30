package com.intuit.tank.vmManager.environment.amazon;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.retry.AwsRetryStrategy;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.retries.api.AcquireInitialTokenRequest;
import software.amazon.awssdk.retries.api.BackoffStrategy;
import software.amazon.awssdk.retries.api.RecordSuccessRequest;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    public void recordSuccessDelegates() {
        RetryToken token = strategy.acquireInitialToken(AcquireInitialTokenRequest.create("ec2")).token();

        assertNotNull(strategy.recordSuccess(RecordSuccessRequest.create(token)).token());
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void builderDelegatesEverySetting() {
        RetryStrategy.Builder delegate = mock(RetryStrategy.Builder.class);
        RetryStrategy built = mock(RetryStrategy.class);
        when(delegate.build()).thenReturn(built);
        Predicate<Throwable> retryOn = failure -> true;
        Predicate<Throwable> throttling = failure -> false;
        BackoffStrategy backoff = BackoffStrategy.retryImmediately();
        BackoffStrategy throttlingBackoff = BackoffStrategy.retryImmediately();

        CapacityAwareRetryStrategy.Builder builder = new CapacityAwareRetryStrategy.Builder(delegate);
        CapacityAwareRetryStrategy rebuilt = builder.retryOnException(retryOn)
                .maxAttempts(2)
                .backoffStrategy(backoff)
                .throttlingBackoffStrategy(throttlingBackoff)
                .treatAsThrottling(throttling)
                .build();

        verify(delegate).retryOnException(retryOn);
        verify(delegate).maxAttempts(2);
        verify(delegate).backoffStrategy(backoff);
        verify(delegate).throttlingBackoffStrategy(throttlingBackoff);
        verify(delegate).treatAsThrottling(throttling);
        when(built.maxAttempts()).thenReturn(2);
        assertEquals(2, rebuilt.maxAttempts());
    }
}
