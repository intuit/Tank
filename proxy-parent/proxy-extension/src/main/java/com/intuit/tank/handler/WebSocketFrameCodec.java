package com.intuit.tank.handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Codec for reading and writing WebSocket frames per RFC 6455.
 * 
 * This handles the low-level byte parsing:
 * 1. Read first 2 bytes to get FIN, opcode, MASK, and initial length
 * 2. Read extended length if needed (2 or 8 more bytes)
 * 3. Read masking key if MASK bit is set (4 bytes)
 * 4. Read payload and unmask if needed
 * 
 * For writing, we do the reverse - apply mask if needed, encode length, write.
 */
public class WebSocketFrameCodec {

    /**
     * Read a single WebSocket frame from the input stream.
     * Blocks until a complete frame is received.
     * 
     * @param in Input stream to read from
     * @return Parsed WebSocket frame (payload is unmasked)
     * @throws IOException If read fails or stream closes
     */
    public WebSocketFrame readFrame(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);

        // Byte 1: FIN (1 bit) + RSV1-3 (3 bits) + Opcode (4 bits)
        int byte1 = dis.readUnsignedByte();
        boolean fin = (byte1 & 0x80) != 0;      // Check bit 7
        int opcodeValue = byte1 & 0x0F;         // Bits 0-3
        WebSocketFrame.Opcode opcode = WebSocketFrame.Opcode.fromCode(opcodeValue);

        // Byte 2: MASK (1 bit) + Payload length (7 bits)
        int byte2 = dis.readUnsignedByte();
        boolean masked = (byte2 & 0x80) != 0;   // Check bit 7
        int payloadLen = byte2 & 0x7F;          // Bits 0-6

        // Extended payload length
        // If 126: next 2 bytes are the actual length (16-bit)
        // If 127: next 8 bytes are the actual length (64-bit)
        long actualLength;
        if (payloadLen == 126) {
            actualLength = dis.readUnsignedShort();
        } else if (payloadLen == 127) {
            actualLength = dis.readLong();
            // Sanity check - don't allow absurdly large frames
            if (actualLength > Integer.MAX_VALUE) {
                throw new IOException("Frame too large: " + actualLength);
            }
        } else {
            actualLength = payloadLen;
        }

        // Masking key (4 bytes, only if MASK=1)
        byte[] maskingKey = null;
        if (masked) {
            maskingKey = new byte[4];
            dis.readFully(maskingKey);
        }

        // Payload
        byte[] payload = new byte[(int) actualLength];
        if (actualLength > 0) {
            dis.readFully(payload);

            // Unmask the payload if masked
            // XOR each byte with maskingKey[i % 4]
            if (masked) {
                for (int i = 0; i < payload.length; i++) {
                    payload[i] = (byte) (payload[i] ^ maskingKey[i % 4]);
                }
            }
        }

        return new WebSocketFrame(fin, opcode, masked, payload, maskingKey);
    }

    /**
     * Write a WebSocket frame to the output stream.
     * 
     * @param out Output stream to write to
     * @param frame Frame to write
     * @param applyMask Whether to mask the payload (true for client→server)
     * @throws IOException If write fails
     */
    public void writeFrame(OutputStream out, WebSocketFrame frame, boolean applyMask) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);

        byte[] payload = frame.getPayload();
        int payloadLen = payload.length;

        // Byte 1: FIN + Opcode
        int byte1 = frame.getOpcode().getCode();
        if (frame.isFin()) {
            byte1 |= 0x80;  // Set FIN bit
        }
        dos.writeByte(byte1);

        // Byte 2: MASK + Payload length
        int byte2 = applyMask ? 0x80 : 0x00;
        if (payloadLen <= 125) {
            byte2 |= payloadLen;
            dos.writeByte(byte2);
        } else if (payloadLen <= 65535) {
            byte2 |= 126;
            dos.writeByte(byte2);
            dos.writeShort(payloadLen);
        } else {
            byte2 |= 127;
            dos.writeByte(byte2);
            dos.writeLong(payloadLen);
        }

        // Masking key + payload
        if (applyMask) {
            // Generate random masking key
            byte[] maskingKey = generateMaskingKey();
            dos.write(maskingKey);

            // Write masked payload
            for (int i = 0; i < payloadLen; i++) {
                dos.writeByte(payload[i] ^ maskingKey[i % 4]);
            }
        } else {
            // Write unmasked payload
            dos.write(payload);
        }

        dos.flush();
    }

    /**
     * Write a frame preserving its original masking state.
     * Used when relaying frames unchanged.
     */
    public void writeFramePreserveMask(OutputStream out, WebSocketFrame frame) throws IOException {
        writeFrame(out, frame, frame.isMasked());
    }

    /**
     * Generate a random 4-byte masking key
     */
    private byte[] generateMaskingKey() {
        byte[] key = new byte[4];
        java.util.concurrent.ThreadLocalRandom.current().nextBytes(key);
        return key;
    }
}
