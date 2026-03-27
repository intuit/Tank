package com.intuit.tank.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketRelayTest {

    /**
     * Helper to create a WebSocket frame byte array for testing.
     */
    private byte[] buildFrame(boolean fin, int opcode, boolean masked, byte[] payload) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        int byte1 = opcode;
        if (fin) byte1 |= 0x80;
        dos.writeByte(byte1);

        int byte2 = masked ? 0x80 : 0x00;
        if (payload.length <= 125) {
            byte2 |= payload.length;
            dos.writeByte(byte2);
        } else if (payload.length <= 65535) {
            byte2 |= 126;
            dos.writeByte(byte2);
            dos.writeShort(payload.length);
        } else {
            byte2 |= 127;
            dos.writeByte(byte2);
            dos.writeLong(payload.length);
        }

        if (masked) {
            byte[] mask = {0x01, 0x02, 0x03, 0x04};
            dos.write(mask);
            for (int i = 0; i < payload.length; i++) {
                dos.writeByte(payload[i] ^ mask[i % 4]);
            }
        } else {
            dos.write(payload);
        }

        dos.flush();
        return baos.toByteArray();
    }

    /**
     * Build a CLOSE frame with status code and reason.
     */
    private byte[] buildCloseFrame(boolean masked, int statusCode, String reason) throws IOException {
        byte[] reasonBytes = reason.getBytes(StandardCharsets.UTF_8);
        byte[] payload = new byte[2 + reasonBytes.length];
        payload[0] = (byte) ((statusCode >> 8) & 0xFF);
        payload[1] = (byte) (statusCode & 0xFF);
        System.arraycopy(reasonBytes, 0, payload, 2, reasonBytes.length);
        return buildFrame(true, 0x8, masked, payload);
    }

    @Test
    @DisplayName("P1 #25 -- CLOSE frame is echoed back to sender before relay stops")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void closeFrameIsEchoed() throws Exception {
        // Client sends a CLOSE frame (masked, as per RFC 6455)
        byte[] closeFrame = buildCloseFrame(true, 1000, "normal");

        // Client socket: reads from serverWriteToClient, writes to clientWriteCapture
        ByteArrayInputStream clientInput = new ByteArrayInputStream(new byte[0]); // nothing from server
        ByteArrayOutputStream clientOutputCapture = new ByteArrayOutputStream(); // captures echo back to client

        // Server socket: reads nothing, writes captured
        // The client→server direction reads from clientInput to serverOutput
        ByteArrayInputStream serverInput = new ByteArrayInputStream(new byte[0]);
        ByteArrayOutputStream serverOutputCapture = new ByteArrayOutputStream();

        // The relay reads from client socket input (closeFrame) and writes to server socket output
        // Then echoes CLOSE back to client socket output

        // Client socket input = the CLOSE frame; output = where echo goes
        TestSocket clientSocket = new TestSocket(
                new ByteArrayInputStream(closeFrame),  // relay reads CLOSE from here
                clientOutputCapture                     // echo CLOSE written here
        );

        // Server socket input = empty (server sends nothing); output = forwarded frames go here
        TestSocket serverSocket = new TestSocket(
                serverInput,           // relay reads from here (nothing)
                serverOutputCapture    // forwarded CLOSE goes here
        );

        WebSocketSession session = new WebSocketSession("ws://example.com/ws", Collections.emptyMap());
        WebSocketRelay relay = new WebSocketRelay(clientSocket, serverSocket, session);

        relay.start();
        relay.awaitCompletion(5000);

        // The relay should have forwarded the CLOSE to the server
        byte[] serverReceived = serverOutputCapture.toByteArray();
        assertTrue(serverReceived.length > 0,
                "Server should have received forwarded CLOSE frame");

        // The relay should have echoed the CLOSE back to the client
        byte[] clientReceived = clientOutputCapture.toByteArray();
        assertTrue(clientReceived.length > 0,
                "Client should have received echoed CLOSE frame, but got 0 bytes");

        // Verify the echoed frame is a CLOSE frame (FIN=1, opcode=0x8 → byte = 0x88)
        int firstByte = clientReceived[0] & 0xFF;
        assertEquals(0x88, firstByte, "Echoed frame should be CLOSE (FIN=1, opcode=0x8)");
    }

    /**
     * Minimal test socket that wraps byte streams.
     */
    private static class TestSocket extends Socket {
        private final InputStream in;
        private final OutputStream out;
        private volatile boolean closed = false;

        TestSocket(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        @Override
        public InputStream getInputStream() {
            return in;
        }

        @Override
        public OutputStream getOutputStream() {
            return out;
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public synchronized void setSoTimeout(int timeout) {
            // no-op
        }
    }
}
