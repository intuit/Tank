package com.intuit.tank.handler;

/**
 * Represents a single WebSocket frame as defined in RFC 6455.
 * 
 * Frame structure:
 * - FIN: 1 bit - indicates final fragment
 * - RSV1-3: 3 bits - reserved for extensions (we ignore)
 * - Opcode: 4 bits - frame type
 * - MASK: 1 bit - whether payload is masked
 * - Payload length: 7 bits (+ extended if needed)
 * - Masking key: 4 bytes (only if MASK=1)
 * - Payload: the actual data
 */
public class WebSocketFrame {

    /**
     * WebSocket opcodes as defined in RFC 6455 Section 5.2
     */
    public enum Opcode {
        CONTINUATION(0x0),  // Continuation of a fragmented message
        TEXT(0x1),          // Text frame (UTF-8)
        BINARY(0x2),        // Binary frame
        // 0x3-0x7 reserved for further non-control frames
        CLOSE(0x8),         // Connection close
        PING(0x9),          // Ping (keep-alive)
        PONG(0xA);          // Pong (response to ping)
        // 0xB-0xF reserved for further control frames

        private final int code;

        Opcode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static Opcode fromCode(int code) {
            for (Opcode op : values()) {
                if (op.code == code) {
                    return op;
                }
            }
            throw new IllegalArgumentException("Unknown opcode: " + code);
        }

        public boolean isControl() {
            return code >= 0x8;
        }
    }

    private final boolean fin;
    private final Opcode opcode;
    private final boolean masked;
    private final byte[] payload;
    private final byte[] maskingKey;

    /**
     * Create a WebSocket frame
     * 
     * @param fin True if this is the final fragment
     * @param opcode Frame type
     * @param masked True if payload is masked (client→server)
     * @param payload Frame payload (already unmasked if was masked)
     * @param maskingKey Original masking key (null if not masked)
     */
    public WebSocketFrame(boolean fin, Opcode opcode, boolean masked, byte[] payload, byte[] maskingKey) {
        this.fin = fin;
        this.opcode = opcode;
        this.masked = masked;
        this.payload = payload != null ? payload : new byte[0];
        this.maskingKey = maskingKey;
    }

    public boolean isFin() {
        return fin;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public boolean isMasked() {
        return masked;
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte[] getMaskingKey() {
        return maskingKey;
    }

    /**
     * Get payload as UTF-8 string (for TEXT frames)
     */
    public String getPayloadAsText() {
        return new String(payload, java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Check if this is a TEXT frame
     */
    public boolean isText() {
        return opcode == Opcode.TEXT || 
               (opcode == Opcode.CONTINUATION && payload.length > 0);
    }

    /**
     * Check if this is a control frame (CLOSE, PING, PONG)
     */
    public boolean isControl() {
        return opcode.isControl();
    }

    @Override
    public String toString() {
        return String.format("WebSocketFrame[fin=%s, opcode=%s, masked=%s, payloadLen=%d]",
                fin, opcode, masked, payload.length);
    }
}
