package com.intuit.tank.perfManager.workLoads;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ControllerInitiatedAgentWsClientTest {

    @Test
    public void sparseBoundaryAckRestoresWholeWindow() throws Exception {
        ControllerInitiatedAgentWsClient.ActiveTransfer transfer =
                new ControllerInitiatedAgentWsClient.ActiveTransfer("file-1");

        queueChunks(transfer, 32);

        assertEquals(0, transfer.availableCredits());
        transfer.confirmThrough(31);
        assertEquals(32, transfer.availableCredits());
    }

    @Test
    public void legacyPerChunkAckRestoresOnlyOneCredit() throws Exception {
        ControllerInitiatedAgentWsClient.ActiveTransfer transfer =
                new ControllerInitiatedAgentWsClient.ActiveTransfer("file-1");

        queueChunks(transfer, 32);

        assertEquals(0, transfer.availableCredits());
        transfer.confirmThrough(0);
        assertEquals(1, transfer.availableCredits());
    }

    @Test
    public void finalPartialWindowRestoresOnlyOutstandingCredits() throws Exception {
        ControllerInitiatedAgentWsClient.ActiveTransfer transfer =
                new ControllerInitiatedAgentWsClient.ActiveTransfer("file-1");

        queueChunks(transfer, 5);

        assertEquals(27, transfer.availableCredits());
        transfer.confirmThrough(4);
        assertEquals(32, transfer.availableCredits());
    }

    @Test
    public void exactBoundaryCompleteDoesNotDoubleRelease() throws Exception {
        ControllerInitiatedAgentWsClient.ActiveTransfer transfer =
                new ControllerInitiatedAgentWsClient.ActiveTransfer("file-1");

        queueChunks(transfer, 32);

        transfer.confirmThrough(31);
        transfer.confirmThrough(31);

        assertEquals(32, transfer.availableCredits());
    }

    @Test
    public void duplicateAckDoesNotOverCredit() throws Exception {
        ControllerInitiatedAgentWsClient.ActiveTransfer transfer =
                new ControllerInitiatedAgentWsClient.ActiveTransfer("file-1");

        queueChunks(transfer, 10);

        transfer.confirmThrough(9);
        transfer.confirmThrough(5);

        assertEquals(32, transfer.availableCredits());
    }

    @Test
    public void nullAckDoesNotReleaseCredit() throws Exception {
        ControllerInitiatedAgentWsClient.ActiveTransfer transfer =
                new ControllerInitiatedAgentWsClient.ActiveTransfer("file-1");

        queueChunks(transfer, 1);

        assertEquals(31, transfer.availableCredits());
        transfer.confirmThrough(null);
        assertEquals(31, transfer.availableCredits());
    }

    @Test
    public void failedTransferStopsFutureAcquire() {
        ControllerInitiatedAgentWsClient.ActiveTransfer transfer =
                new ControllerInitiatedAgentWsClient.ActiveTransfer("file-1");

        transfer.fail("agent rejected chunk");

        IOException failure = assertThrows(IOException.class,
                () -> transfer.acquire(1));
        assertTrue(failure.getMessage().contains("agent rejected chunk"));
    }

    @Test
    public void failedTransferWakesBlockedAcquireAndFailsIt() throws Exception {
        ControllerInitiatedAgentWsClient.ActiveTransfer transfer =
                new ControllerInitiatedAgentWsClient.ActiveTransfer("file-1");
        queueChunks(transfer, 32);

        CompletableFuture<IOException> blockedAcquire = CompletableFuture.supplyAsync(() -> {
            try {
                transfer.acquire(TimeUnit.SECONDS.toMillis(5));
                return null;
            } catch (IOException e) {
                return e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new IOException(e);
            }
        });

        Thread.sleep(50L);
        transfer.fail("connection closed");

        IOException failure = blockedAcquire.get(1, TimeUnit.SECONDS);
        assertNotNull(failure);
        assertTrue(failure.getMessage().contains("connection closed"));
    }

    private static void queueChunks(ControllerInitiatedAgentWsClient.ActiveTransfer transfer, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            assertTrue(transfer.acquire(1));
        }
    }
}
