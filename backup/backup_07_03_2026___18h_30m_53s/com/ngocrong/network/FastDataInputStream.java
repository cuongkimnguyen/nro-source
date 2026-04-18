/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Administrator
 */
public class FastDataInputStream implements AutoCloseable {

    // Sử dụng bộ đệm lớn hơn với kích thước mặc định tối ưu
    private static final int DEFAULT_BUFFER_SIZE = 2048;

    private final InputStream in;
    private byte[] buffer;
    private int position;
    private int count;

    public FastDataInputStream(InputStream in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    public FastDataInputStream(InputStream in, int bufferSize) {
        this.in = in;
        this.buffer = new byte[bufferSize];
        this.position = 0;
        this.count = 0;
    }

    // Phương thức đọc byte tối ưu
    public byte readByte() throws IOException {
        if (position >= count) {
            fill();
            if (position >= count) {
                throw new EOFException();
            }
        }
        return buffer[position++];
    }

    // Phương thức đọc short tối ưu
    public short readShort() throws IOException {
        // Đảm bảo rằng có ít nhất 2 byte trong bộ đệm
        if (position + 1 >= count) {
            return (short) (((readByte() & 0xFF) << 8) | (readByte() & 0xFF));
        }

        int value = ((buffer[position++] & 0xFF) << 8) | (buffer[position++] & 0xFF);
        return (short) value;
    }

    // Phương thức đọc int tối ưu
    public int readInt() throws IOException {
        // Đảm bảo rằng có ít nhất 4 byte trong bộ đệm
        if (position + 3 >= count) {
            return ((readByte() & 0xFF) << 24)
                    | ((readByte() & 0xFF) << 16)
                    | ((readByte() & 0xFF) << 8)
                    | (readByte() & 0xFF);
        }

        int value = ((buffer[position++] & 0xFF) << 24)
                | ((buffer[position++] & 0xFF) << 16)
                | ((buffer[position++] & 0xFF) << 8)
                | (buffer[position++] & 0xFF);
        return value;
    }

    // Phương thức đọc long tối ưu
    public long readLong() throws IOException {
        // Đọc 8 byte, kết hợp thành long
        return ((long) (readInt()) << 32) | (readInt() & 0xFFFFFFFFL);
    }

    // Phương thức đọc chuỗi UTF tối ưu
    public String readUTF() throws IOException {
        // Đọc độ dài
        int length = readShort() & 0xFFFF;

        // Đọc các byte cho chuỗi
        byte[] bytes = new byte[length];
        readFully(bytes);

        // Chuyển đổi byte thành chuỗi
        return new String(bytes, StandardCharsets.UTF_8);
    }

    // Đọc toàn bộ mảng byte
    public void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    // Đọc toàn bộ mảng byte với offset và length
    public void readFully(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }

        // Số byte có sẵn trong bộ đệm
        int available = count - position;

        // Nếu tất cả dữ liệu có trong bộ đệm
        if (len <= available) {
            System.arraycopy(buffer, position, b, off, len);
            position += len;
            return;
        }

        // Sao chép phần có sẵn trong bộ đệm
        if (available > 0) {
            System.arraycopy(buffer, position, b, off, available);
            off += available;
            len -= available;
            position = count; // Bộ đệm đã được đọc hết
        }

        // Đọc phần còn lại trực tiếp từ luồng input nếu lớn hơn kích thước bộ đệm
        if (len > buffer.length) {
            while (len > 0) {
                int bytesRead = in.read(b, off, len);
                if (bytesRead <= 0) {
                    throw new EOFException();
                }
                off += bytesRead;
                len -= bytesRead;
            }
            return;
        }

        // Nạp lại bộ đệm và đọc phần còn lại
        fill();
        if (len > count - position) {
            throw new EOFException();
        }
        System.arraycopy(buffer, position, b, off, len);
        position += len;
    }

    // Nạp lại bộ đệm
    private void fill() throws IOException {
        position = 0;
        count = 0;
        int bytesRead = in.read(buffer, 0, buffer.length);
        if (bytesRead > 0) {
            count = bytesRead;
        }
    }

    // Kiểm tra số byte có sẵn
    public int available() throws IOException {
        return count - position + in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    public boolean readBoolean() throws IOException {
        return readByte() != 0;
    }

// Phương thức đọc một byte từ luồng
    public int read() throws IOException {
        try {
            return readByte() & 0xFF;
        } catch (EOFException e) {
            return -1;
        }
    }

// Phương thức đọc một phần của mảng byte
    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }

        try {
            readFully(b, off, len);
            return len;
        } catch (EOFException e) {
            // Nếu đã đọc một phần dữ liệu trước khi gặp EOF
            if (position < count) {
                int available = count - position;
                if (available > len) {
                    available = len;
                }
                System.arraycopy(buffer, position, b, off, available);
                position += available;
                return available;
            }
            return -1;
        }
    }

// Phương thức đọc vào mảng byte
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

// Phương thức đọc float - IEEE 754
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

// Phương thức đọc double - IEEE 754
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

// Phương thức đọc char (2 byte)
    public char readChar() throws IOException {
        return (char) readShort();
    }

// Phương thức bỏ qua n byte
    public long skip(long n) throws IOException {
        long remaining = n;

        // Bỏ qua bytes có sẵn trong bộ đệm
        int available = count - position;
        if (available > 0) {
            long skipped = Math.min(available, remaining);
            position += skipped;
            remaining -= skipped;
        }

        // Nếu cần bỏ qua thêm
        if (remaining > 0) {
            // Nếu số byte cần bỏ qua lớn hơn kích thước bộ đệm, bỏ qua trực tiếp
            if (remaining > buffer.length) {
                long actuallySkipped = in.skip(remaining);
                return n - remaining + actuallySkipped;
            }

            // Nạp lại bộ đệm và bỏ qua phần còn lại nếu có thể
            fill();
            available = count - position;
            if (available > 0) {
                long skipped = Math.min(available, remaining);
                position += skipped;
                remaining -= skipped;
            }
        }

        return n - remaining;
    }

    public int readUnsignedByte() throws IOException {
        return readByte() & 0xFF;
    }

    /**
     * Đọc một short dưới dạng unsigned, trả về giá trị từ 0 đến 65535.
     *
     * @return Giá trị short unsigned từ 0 đến 65535
     * @throws IOException nếu có lỗi I/O xảy ra
     */
    public int readUnsignedShort() throws IOException {
        return readShort() & 0xFFFF;
    }

    /**
     * Đọc một mảng int từ luồng đầu vào.
     *
     * @return Mảng int đã đọc, hoặc null nếu mảng là null
     * @throws IOException nếu có lỗi I/O xảy ra
     */
    public int[] readInts() throws IOException {
        // Đọc độ dài mảng
        int length = readInt();

        // Kiểm tra mảng null
        if (length == -1) {
            return null;
        }

        // Kiểm tra độ dài hợp lệ
        if (length < 0 || length > 1000000) { // Giới hạn hợp lý
            throw new IOException("Invalid array length: " + length);
        }

        int[] result = new int[length];

        // Tối ưu đọc nếu có đủ dữ liệu trong bộ đệm
        if (position + length * 4 <= count) {
            // Đọc trực tiếp từ bộ đệm
            for (int i = 0; i < length; i++) {
                result[i] = ((buffer[position++] & 0xFF) << 24)
                        | ((buffer[position++] & 0xFF) << 16)
                        | ((buffer[position++] & 0xFF) << 8)
                        | (buffer[position++] & 0xFF);
            }
        } else {
            // Đọc từng phần tử
            for (int i = 0; i < length; i++) {
                result[i] = readInt();
            }
        }

        return result;
    }

    /**
     * Đọc một mảng long từ luồng đầu vào.
     *
     * @return Mảng long đã đọc, hoặc null nếu mảng là null
     * @throws IOException nếu có lỗi I/O xảy ra
     */
    public long[] readLongs() throws IOException {
        // Đọc độ dài mảng
        int length = readInt();

        // Kiểm tra mảng null
        if (length == -1) {
            return null;
        }

        // Kiểm tra độ dài hợp lệ
        if (length < 0 || length > 1000000) { // Giới hạn hợp lý
            throw new IOException("Invalid array length: " + length);
        }

        long[] result = new long[length];

        // Đọc từng phần tử
        for (int i = 0; i < length; i++) {
            result[i] = readLong();
        }

        return result;
    }

    /**
     * Đọc một mảng String từ luồng đầu vào.
     *
     * @return Mảng String đã đọc, hoặc null nếu mảng là null
     * @throws IOException nếu có lỗi I/O xảy ra
     */
    public String[] readUTFs() throws IOException {
        // Đọc độ dài mảng
        int length = readInt();

        // Kiểm tra mảng null
        if (length == -1) {
            return null;
        }

        // Kiểm tra độ dài hợp lệ
        if (length < 0 || length > 1000000) { // Giới hạn hợp lý
            throw new IOException("Invalid array length: " + length);
        }

        String[] result = new String[length];

        // Đọc từng chuỗi
        for (int i = 0; i < length; i++) {
            short strLength = readShort();
            if (strLength == -1) {
                result[i] = null; // String null
            } else {
                result[i] = readUTF();
            }
        }

        return result;
    }

    /**
     * Đọc một mảng boolean từ luồng đầu vào.
     *
     * @return Mảng boolean đã đọc, hoặc null nếu mảng là null
     * @throws IOException nếu có lỗi I/O xảy ra
     */
    public boolean[] readBooleans() throws IOException {
        // Đọc độ dài mảng
        int length = readInt();

        // Kiểm tra mảng null
        if (length == -1) {
            return null;
        }

        // Kiểm tra độ dài hợp lệ
        if (length < 0 || length > 1000000) { // Giới hạn hợp lý
            throw new IOException("Invalid array length: " + length);
        }

        boolean[] result = new boolean[length];

        // Với mảng nhỏ, đọc theo cách đơn giản
        if (length <= 32) {
            for (int i = 0; i < length; i++) {
                result[i] = readBoolean();
            }
            return result;
        }

        // Với mảng lớn, giải nén các bit
        int byteCount = (length + 7) / 8;
        int byteIndex = 0;
        int bitPosition = 0;
        byte currentByte = 0;

        for (int i = 0; i < length; i++) {
            if (bitPosition == 0) {
                currentByte = readByte();
            }

            result[i] = ((currentByte >> bitPosition) & 1) == 1;

            bitPosition = (bitPosition + 1) % 8;
        }

        return result;
    }

}
