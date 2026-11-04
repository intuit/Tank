package com.intuit.tank.vmManager.environment.amazon;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.retries.api.AcquireInitialTokenRequest;
import software.amazon.awssdk.retries.api.AcquireInitialTokenResponse;
import software.amazon.awssdk.retries.api.BackoffStrategy;
import software.amazon.awssdk.retries.api.RecordSuccessRequest;
import software.amazon.awssdk.retries.api.RecordSuccessResponse;
import software.amazon.awssdk.retries.api.RefreshRetryTokenRequest;
import software.amazon.awssdk.retries.api.RefreshRetryTokenResponse;
import software.amazon.awssdk.retries.api.RetryStrategy;
import software.amazon.awssdk.retries.api.TokenAcquisitionFailedException;

import java.util.function.Predicate;

/**
 * Delegates to a standard SDK retry strategy but never retries InsufficientInstanceCapacity.
 * EC2 returns that error as a 500, which the SDK treats as transient; retrying the same
 * instance type in the same AZ only delays the fallback to the next instance type.
 */
class CapacityAwareRetryStrategy implements RetryStrategy {

    private final RetryStrategy delegate;

    CapacityAwareRetryStrategy(RetryStrategy delegate) {
        this.delegate = delegate;
    }

    static boolean isInsufficientCapacity(Throwable failure) {
        return failure instanceof AwsServiceException ase
                && ase.awsErrorDetails() != null
                && AmazonInstance.INSUFFICIENT_INSTANCE_CAPACITY.equals(ase.awsErrorDetails().errorCode());
    }

    @Override
    public AcquireInitialTokenResponse acquireInitialToken(AcquireInitialTokenRequest request) {
        return delegate.acquireInitialToken(request);
    }

    @Override
    public RefreshRetryTokenResponse refreshRetryToken(RefreshRetryTokenRequest request) {
        if (isInsufficientCapacity(request.failure())) {
            throw new TokenAcquisitionFailedException("Not retrying insufficient instance capacity",
                    request.token(), request.failure());
        }
        return delegate.refreshRetryToken(request);
    }

    @Override
    public RecordSuccessResponse recordSuccess(RecordSuccessRequest request) {
        return delegate.recordSuccess(request);
    }

    @Override
    public int maxAttempts() {
        return delegate.maxAttempts();
    }

    /**
     * The delegate is already fully configured; stop the client builder from layering defaults on top.
     */
    @Override
    public boolean useClientDefaults() {
        return false;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(delegate.toBuilder());
    }

    static class Builder implements RetryStrategy.Builder<Builder, CapacityAwareRetryStrategy> {
        private final RetryStrategy.Builder<?, ?> delegate;

        Builder(RetryStrategy.Builder<?, ?> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Builder retryOnException(Predicate<Throwable> shouldRetry) {
            delegate.retryOnException(shouldRetry);
            return this;
        }

        @Override
        public Builder maxAttempts(int maxAttempts) {
            delegate.maxAttempts(maxAttempts);
            return this;
        }

        @Override
        public Builder backoffStrategy(BackoffStrategy backoffStrategy) {
            delegate.backoffStrategy(backoffStrategy);
            return this;
        }

        @Override
        public Builder throttlingBackoffStrategy(BackoffStrategy throttlingBackoffStrategy) {
            delegate.throttlingBackoffStrategy(throttlingBackoffStrategy);
            return this;
        }

        @Override
        public Builder treatAsThrottling(Predicate<Throwable> treatAsThrottling) {
            delegate.treatAsThrottling(treatAsThrottling);
            return this;
        }

        @Override
        public CapacityAwareRetryStrategy build() {
            return new CapacityAwareRetryStrategy(delegate.build());
        }
    }
}
