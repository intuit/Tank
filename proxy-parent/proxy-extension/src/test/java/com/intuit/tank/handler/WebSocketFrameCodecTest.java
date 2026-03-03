package com.intuit.tank.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WebSocket frame codec.
 * Validates RFC 6455 frame parsing and generation.
 */
class WebSocketFrameCodecTest {

    private WebSocketFrameCodec codec;

    @BeforeEach
    void setUp() {
        codec = new WebSocketFrameCodec();
    }

    @Test
    void testReadUnmaskedTextFrame() throws IOException {
        // Simple unmasked TEXT frame with "Hello"
        // Byte 1: 0x81 = FIN=1, opcode=1 (TEXT)
        // Byte 2: 0x05 = MASK=0, length=5
        // Payload: "Hello"
        byte[] frameBytes = new byte[] {
            (byte) 0x81,  // FIN=1, opcode=TEXT
            0x05,         // MASK=0, length=5
            'H', 'e', 'l', 'l', 'o'
        };

        WebSocketFrame frame = codec.readFrame(new ByteArrayInputStream(frameBytes));

        assertTrue(frame.isFin());
        assertEquals(WebSocketFrame.Opcode.TEXT, frame.getOpcode());
        assertFalse(frame.isMasked());
        assertEquals("Hello", frame.getPayloadAsText());
    }

    @Test
    void testReadMaskedTextFrame() throws IOException {
        // Masked TEXT frame with "Hello" from client
        // Masking key: 0x37, 0xFA, 0x21, 0x3D
        // "Hello" = 0x48, 0x65, 0x6C, 0x6C, 0x6F
        // Masked:   0x48^0x37, 0x65^0xFA, 0x6C^0x21, 0x6C^0x3D, 0x6F^0x37
        //         = 0x7F,     0x9F,      0x4D,      0x51,      0x58
        byte[] frameBytes = new byte[] {
            (byte) 0x81,  // FIN=1, opcode=TEXT
            (byte) 0x85,  // MASK=1, length=5
            0x37, (byte) 0xFA, 0x21, 0x3D,  // Masking key
            0x7F, (byte) 0x9F, 0x4D, 0x51, 0x58  // Masked payload
        };

        WebSocketFrame frame = codec.readFrame(new ByteArrayInputStream(frameBytes));

        assertTrue(frame.isFin());
        assertEquals(WebSocketFrame.Opcode.TEXT, frame.getOpcode());
        assertTrue(frame.isMasked());
        assertEquals("Hello", frame.getPayloadAsText());  // Should be unmasked
    }

    @Test
    void testReadBinaryFrame() throws IOException {
        // Binary frame with 3 bytes
        byte[] frameBytes = new byte[] {
            (byte) 0x82,  // FIN=1, opcode=BINARY
            0x03,         // MASK=0, length=3
            0x01, 0x02, 0x03
        };

        WebSocketFrame frame = codec.readFrame(new ByteArrayInputStream(frameBytes));

        assertTrue(frame.isFin());
        assertEquals(WebSocketFrame.Opcode.BINARY, frame.getOpcode());
        assertArrayEquals(new byte[] {0x01, 0x02, 0x03}, frame.getPayload());
    }

    @Test
    void testReadCloseFrame() throws IOException {
        // Close frame with status code 1000 (normal closure)
        byte[] frameBytes = new byte[] {
            (byte) 0x88,  // FIN=1, opcode=CLOSE
            0x02,         // MASK=0, length=2
            0x03, (byte) 0xE8  // Status code 1000 in big-endian
        };

        WebSocketFrame frame = codec.readFrame(new ByteArrayInputStream(frameBytes));

        assertTrue(frame.isFin());
        assertEquals(WebSocketFrame.Opcode.CLOSE, frame.getOpcode());
        assertTrue(frame.isControl());
    }

    @Test
    void testReadPingFrame() throws IOException {
        byte[] frameBytes = new byte[] {
            (byte) 0x89,  // FIN=1, opcode=PING
            0x04,         // MASK=0, length=4
            'p', 'i', 'n', 'g'
        };

        WebSocketFrame frame = codec.readFrame(new ByteArrayInputStream(frameBytes));

        assertEquals(WebSocketFrame.Opcode.PING, frame.getOpcode());
        assertTrue(frame.isControl());
        assertEquals("ping", frame.getPayloadAsText());
    }

    @Test
    void testReadPongFrame() throws IOException {
        byte[] frameBytes = new byte[] {
            (byte) 0x8A,  // FIN=1, opcode=PONG
            0x04,         // MASK=0, length=4
            'p', 'o', 'n', 'g'
        };

        WebSocketFrame frame = codec.readFrame(new ByteArrayInputStream(frameBytes));

        assertEquals(WebSocketFrame.Opcode.PONG, frame.getOpcode());
        assertTrue(frame.isControl());
    }

    @Test
    void testRead16BitLength() throws IOException {
        // Frame with 126 bytes payload (requires 16-bit extended length)
        byte[] payload = new byte[126];
        for (int i = 0; i < 126; i++) {
            payload[i] = (byte) (i % 256);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(0x82);  // FIN=1, opcode=BINARY
        baos.write(126);   // length=126 means "read next 2 bytes for actual length"
        baos.write(0);     // High byte of 126
        baos.write(126);   // Low byte of 126
        baos.write(payload);

        WebSocketFrame frame = codec.readFrame(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(126, frame.getPayload().length);
        assertArrayEquals(payload, frame.getPayload());
    }

    @Test
    void testWriteUnmaskedTextFrame() throws IOException {
        WebSocketFrame frame = new WebSocketFrame(
            true,
            WebSocketFrame.Opcode.TEXT,
            false,
            "Hello".getBytes(),
            null
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        codec.writeFrame(baos, frame, false);

        byte[] written = baos.toByteArray();
        assertEquals(0x81, written[0] & 0xFF);  // FIN=1, TEXT
        assertEquals(0x05, written[1] & 0xFF);  // MASK=0, length=5
        assertEquals("Hello", new String(written, 2, 5));
    }

    @Test
    void testWriteMaskedTextFrame() throws IOException {
        WebSocketFrame frame = new WebSocketFrame(
            true,
            WebSocketFrame.Opcode.TEXT,
            false,
            "Hello".getBytes(),
            null
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        codec.writeFrame(baos, frame, true);

        byte[] written = baos.toByteArray();
        assertEquals(0x81, written[0] & 0xFF);  // FIN=1, TEXT
        assertEquals(0x85, written[1] & 0xFF);  // MASK=1, length=5
        // Bytes 2-5 are masking key, 6-10 are masked payload

        // Read it back and verify
        WebSocketFrame readBack = codec.readFrame(new ByteArrayInputStream(written));
        assertEquals("Hello", readBack.getPayloadAsText());
    }

    @Test
    void testRoundTrip() throws IOException {
        // Write a frame, read it back, verify identical
        String originalText = "WebSocket round-trip test!";
        WebSocketFrame original = new WebSocketFrame(
            true,
            WebSocketFrame.Opcode.TEXT,
            false,
            originalText.getBytes(),
            null
        );

        // Write
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        codec.writeFrame(baos, original, false);

        // Read back
        WebSocketFrame readBack = codec.readFrame(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(original.isFin(), readBack.isFin());
        assertEquals(original.getOpcode(), readBack.getOpcode());
        assertEquals(originalText, readBack.getPayloadAsText());
    }

    @Test
    void testFragmentedFrame() throws IOException {
        // First fragment (FIN=0, opcode=TEXT)
        byte[] frag1 = new byte[] {
            0x01,  // FIN=0, opcode=TEXT
            0x03,  // length=3
            'H', 'e', 'l'
        };

        // Continuation fragment (FIN=1, opcode=CONTINUATION)
        byte[] frag2 = new byte[] {
            (byte) 0x80,  // FIN=1, opcode=CONTINUATION
            0x02,         // length=2
            'l', 'o'
        };

        WebSocketFrame frame1 = codec.readFrame(new ByteArrayInputStream(frag1));
        assertFalse(frame1.isFin());
        assertEquals(WebSocketFrame.Opcode.TEXT, frame1.getOpcode());
        assertEquals("Hel", frame1.getPayloadAsText());

        WebSocketFrame frame2 = codec.readFrame(new ByteArrayInputStream(frag2));
        assertTrue(frame2.isFin());
        assertEquals(WebSocketFrame.Opcode.CONTINUATION, frame2.getOpcode());
        assertEquals("lo", frame2.getPayloadAsText());
    }

    @Test
    void testEmptyFrame() throws IOException {
        byte[] frameBytes = new byte[] {
            (byte) 0x81,  // FIN=1, opcode=TEXT
            0x00          // MASK=0, length=0
        };

        WebSocketFrame frame = codec.readFrame(new ByteArrayInputStream(frameBytes));

        assertTrue(frame.isFin());
        assertEquals(0, frame.getPayload().length);
    }
}
