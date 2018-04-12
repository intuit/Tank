/*
 * This file is part of the OWASP Proxy, a free intercepting proxy library.
 * Copyright (C) 2008-2010 Rogan Dawes <rogan@dawes.za.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to:
 * The Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.owasp.proxy.ajp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.owasp.proxy.util.AsciiString;

public class AJPMessage {

    static final byte[] AJP_CLIENT = { (byte) 0x12, (byte) 0x34 };
    static final byte[] AJP_SERVER = { (byte) 0x41, (byte) 0x42 };

    /**
     * Fixed size buffer.
     */
    protected byte buf[];

    /**
     * The current read or write position in the buffer.
     */
    protected int pos;

    /**
     * This actually means different things depending on whether the packet is read or write. For read, it's the length
     * of the payload (excluding the header). For write, it's the length of the packet as a whole (counting the header).
     * Oh, well.
     */
    protected int len;

    public AJPMessage(int packetSize) {
        buf = new byte[packetSize];
    }

    /**
     * Prepare this packet for accumulating a message to be written. Set the write position to just after the header
     * (but leave the length unwritten, because it is as yet unknown).
     */
    public void reset() {
        len = 4;
        pos = 4;
        Arrays.fill(buf, (byte) 0);
    }

    public void endClientMessage() {
        end(AJP_CLIENT);
    }

    public void endServerMessage() {
        end(AJP_SERVER);
    }

    /**
     * For a packet to be sent, finish the process of accumulating data and write the packet signature and the length of
     * the data payload into the header.
     */
    private void end(byte[] signature) {
        len = pos;
        int dLen = len - 4;

        buf[0] = signature[0];
        buf[1] = signature[1];
        buf[2] = (byte) ((dLen >>> 8) & 0xFF);
        buf[3] = (byte) (dLen & 0xFF);
    }

    public byte[] toByteArray() {
        byte[] arr = new byte[getLen()];
        System.arraycopy(buf, 0, arr, 0, getLen());
        return arr;
    }

    /**
     * Return the current message length. For read, it's the length of the payload (excluding the header). For write,
     * it's the length of the packet as a whole (counting the header).
     */
    public int getLen() {
        return len;
    }

    /**
     * Add a short integer (2 bytes) to the message.
     */
    public void appendInt(int val) {
        buf[pos++] = (byte) ((val >>> 8) & 0xFF);
        buf[pos++] = (byte) (val & 0xFF);
    }

    /**
     * Append a byte (1 byte) to the message.
     */
    public void appendByte(int val) {
        buf[pos++] = (byte) val;
    }

    /**
     * Append a boolean value to the message.
     */
    public void appendBoolean(boolean val) {
        buf[pos++] = (byte) (val ? 1 : 0);
    }

    /**
     * Append an int (4 bytes) to the message.
     */
    public void appendLongInt(int val) {
        buf[pos++] = (byte) ((val >>> 24) & 0xFF);
        buf[pos++] = (byte) ((val >>> 16) & 0xFF);
        buf[pos++] = (byte) ((val >>> 8) & 0xFF);
        buf[pos++] = (byte) (val & 0xFF);
    }

    /**
     * Write a String out at the current write position. Strings are encoded with the length in two bytes first, then
     * the string, and then a terminating \0 (which is <B>not</B> included in the encoded length). The terminator is for
     * the convenience of the C code, where it saves a round of copying. A null string is encoded as a string with
     * length 0.
     */
    public void appendString(String str) {
        int len = str.length();
        appendInt(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            // Note: This is clearly incorrect for many strings,
            // but is the only consistent approach within the current
            // servlet framework. It must suffice until servlet output
            // streams properly encode their output.
            if ((c <= 31) && (c != 9) && (c != 10) && (c != 13)) {
                c = ' ';
            } else if (c == 127) {
                c = ' ';
            }
            appendByte(c);
        }
        appendByte(0);
    }

    /**
     * Copy a chunk of bytes into the packet, starting at the current write position. The chunk of bytes is encoded with
     * the length in two bytes first, then the data itself, and finally a terminating \0 (which is <B>not</B> included
     * in the encoded length).
     * 
     * @param b
     *            The array from which to copy bytes.
     * @param off
     *            The offset into the array at which to start copying
     * @param len
     *            The number of bytes to copy.
     */
    public void appendBytes(byte[] b, int off, int len) {
        if (pos + len + 3 > buf.length) {
            System.err.println("ajpmessage.overflow " + len + " " + pos);
            return;
        }
        appendInt(len);
        System.arraycopy(b, off, buf, pos, len);
        pos += len;
        appendByte(0);
    }

    /**
     * Utility method to read as many bytes as will fit into the buffer from an InputStream, using the AJPMessage buffer
     * as the read destination. This eliminates an extra buffer, as well as a copy.
     * 
     * @param in
     * @return the number of bytes read from the InputStream
     * @throws IOException
     */
    public int appendBytes(InputStream in, int max) throws IOException {
        max = Math.min(max, buf.length - (pos + 2 + 1));
        int read = 0, got;
        while (read < max
                && (got = in.read(buf, pos + 2 + read, max - read)) > -1) {
            read += got;
        }
        if (read > 0) {
            appendInt(read);
            pos += read;
            appendByte(0);
        }
        return read;
    }

    /**
     * Read an integer from packet, and advance the read position past it. Integers are encoded as two unsigned bytes
     * with the high-order byte first, and, as far as I can tell, in little-endian order within each byte.
     */
    public int getInt() {
        int b1 = buf[pos++] & 0xFF;
        int b2 = buf[pos++] & 0xFF;
        return (b1 << 8) + b2;
    }

    public int peekInt() {
        int b1 = buf[pos] & 0xFF;
        int b2 = buf[pos + 1] & 0xFF;
        return (b1 << 8) + b2;
    }

    public byte getByte() {
        return buf[pos++];
    }

    public byte peekByte() {
        return buf[pos];
    }

    public boolean getBoolean() {
        byte res = buf[pos++];
        return res == 0 ? false : true;
    }

    /**
     * Copy a chunk of bytes from the packet into an array and advance the read position past the chunk. See
     * appendBytes() for details on the encoding.
     * 
     * @return The number of bytes copied.
     */
    public int getBytes(byte[] dest) {
        int length = getInt();
        if (pos + length > buf.length) {
            System.err.println("ajpmessage.read" + length);
            return 0;
        }

        if ((length == 0xFFFF) || (length == -1)) {
            return 0;
        }

        System.arraycopy(buf, pos, dest, 0, length);
        pos += length;
        pos++; // Skip terminating \0
        return length;
    }

    /**
     * Read a 32 bits integer from packet, and advance the read position past it. Integers are encoded as four unsigned
     * bytes with the high-order byte first, and, as far as I can tell, in little-endian order within each byte.
     */
    public int getLongInt() {
        int b1 = buf[pos++] & 0xFF; // No swap, Java order
        b1 <<= 8;
        b1 |= (buf[pos++] & 0xFF);
        b1 <<= 8;
        b1 |= (buf[pos++] & 0xFF);
        b1 <<= 8;
        b1 |= (buf[pos++] & 0xFF);
        return b1;
    }

    /**
     * Write a String out at the current write position. Strings are encoded with the length in two bytes first, then
     * the string, and then a terminating \0 (which is <B>not</B> included in the encoded length). The terminator is for
     * the convenience of the C code, where it saves a round of copying. A null string is encoded as a string with
     * length 0.
     */
    public String getString() {
        int len = getInt();
        if (len == 0xFFFF || len == -1)
            return null;
        StringBuilder buff = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = (char) getByte();
            buff.append(c);
        }
        byte b;
        if ((b = getByte()) != 0)
            throw new IllegalStateException(
                    "Unexpected read! Expected null, got " + b);
        return buff.toString();
    }

    public int getHeaderLength() {
        return 4;
    }

    public int getPacketSize() {
        return buf.length;
    }

    public int processHeader() {
        pos = 0;
        int mark = getInt();
        len = getInt();
        // Verify message signature
        if ((mark != 0x1234) && (mark != 0x4142)) {
            System.err.println("ajpmessage.invalid " + mark);
            if (true) {
                dump("In: ");
            }
            return -1;
        }
        if (false) {
            System.err.println("Received " + len + " " + buf[0]);
        }
        return len;
    }

    /**
     * Dump the contents of the message, prefixed with the given String.
     */
    public void dump(String msg) {
        if (true) {
            System.err.println(msg + ": " + AsciiString.create(buf) + " " + pos
                    + "/" + (len + 4));
        }
        int max = pos;
        if (len + 4 > pos)
            max = len + 4;
        if (max > 1000)
            max = 1000;
        if (true) {
            for (int j = 0; j < max; j += 16) {
                System.err.println(hexLine(buf, j, len));
            }
        }
    }

    protected static String hexLine(byte buf[], int start, int len) {
        StringBuffer sb = new StringBuffer();
        for (int i = start; i < start + 16; i++) {
            if (i < len + 4) {
                sb.append(hex(buf[i]) + " ");
            } else {
                sb.append("   ");
            }
        }
        sb.append(" | ");
        for (int i = start; i < start + 16 && i < len + 4; i++) {
            if (!Character.isISOControl((char) buf[i])) {
                sb.append(Character.valueOf((char) buf[i]));
            } else {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    protected static String hex(int x) {
        String h = Integer.toHexString(x);
        if (h.length() == 1) {
            h = "0" + h;
        }
        return h.substring(h.length() - 2);
    }

    /**
     * Read an AJP message.
     * 
     * @throws IOException
     *             any failure, including incomplete reads
     */
    public void readMessage(InputStream in) throws IOException {
        reset();
        read(in, buf, 0, getHeaderLength());
        processHeader();
        read(in, buf, getHeaderLength(), getLen());
    }

    /**
     * Read at least the specified amount of bytes, and place them in the input buffer.
     */
    private static void read(InputStream in, byte[] buf, int pos, int n)
            throws IOException {

        int read = 0;
        int res = 0;
        while (read < n) {
            res = in.read(buf, read + pos, n - read);
            if (res > 0) {
                read += res;
            } else {
                throw new IOException("Read failed, got " + read + " of " + n);
            }
        }
    }

    public void write(OutputStream out) throws IOException {
        out.write(buf, 0, getLen());
        out.flush();
    }

}
