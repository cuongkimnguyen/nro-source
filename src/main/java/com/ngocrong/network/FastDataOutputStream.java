/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.network;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Administrator
 */
public class FastDataOutputStream implements AutoCloseable {

    // Sử dụng bộ đệm lớn hơn với kích thước mặc định tối ưu
    private static final int DEFAULT_BUFFER_SIZE = 2048;

    private final OutputStream out;
    private final byte[] buffer;
    private int position;

    public FastDataOutputStream(OutputStream out) {
        this(out, DEFAULT_BUFFER_SIZE);
    }

    public FastDataOutputStream(OutputStream out, int bufferSize) {
        this.out = out;
        this.buffer = new byte[bufferSize];
        this.position = 0;
    }

    // Phương thức ghi byte (không đồng bộ hóa)
    public void writeByte(int v) throws IOException {
        ensureCapacity(1);
        buffer[position++] = (byte) v;
    }

    // Phương thức ghi short sử dụng bit shifting trực tiếp
    public void writeShort(int v) throws IOException {
        ensureCapacity(2);
        buffer[position++] = (byte) (v >> 8);
        buffer[position++] = (byte) v;
    }

    // Phương thức ghi int tối ưu
    public void writeInt(int v) throws IOException {
        ensureCapacity(4);
        buffer[position++] = (byte) (v >> 24);
        buffer[position++] = (byte) (v >> 16);
        buffer[position++] = (byte) (v >> 8);
        buffer[position++] = (byte) v;
    }

    // Phương thức ghi long tối ưu
    public void writeLong(long v) throws IOException {
        ensureCapacity(8);
        buffer[position++] = (byte) (v >> 56);
        buffer[position++] = (byte) (v >> 48);
        buffer[position++] = (byte) (v >> 40);
        buffer[position++] = (byte) (v >> 32);
        buffer[position++] = (byte) (v >> 24);
        buffer[position++] = (byte) (v >> 16);
        buffer[position++] = (byte) (v >> 8);
        buffer[position++] = (byte) v;
    }

    // Phương thức ghi chuỗi tối ưu
    public void writeUTF(String str) throws IOException {
        // Tính toán kích thước byte của chuỗi UTF-8
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

        // Ghi độ dài trước
        writeShort(bytes.length);

        // Ghi dữ liệu chuỗi
        write(bytes, 0, bytes.length);
    }

    // Viết mảng byte với một lần gọi
    public void write(byte[] b, int off, int len) throws IOException {
        // Nếu dữ liệu quá lớn so với bộ đệm, xả bộ đệm trước
        if (len > buffer.length - position) {
            // Xả dữ liệu hiện tại
            flush();

            // Nếu dữ liệu lớn hơn bộ đệm, ghi trực tiếp
            if (len > buffer.length) {
                out.write(b, off, len);
                return;
            }
        }

        // Sao chép vào bộ đệm
        System.arraycopy(b, off, buffer, position, len);
        position += len;
    }

    // Đảm bảo bộ đệm có đủ dung lượng
    private void ensureCapacity(int required) throws IOException {
        if (position + required > buffer.length) {
            flush();
        }
    }

    // Xả dữ liệu từ bộ đệm xuống luồng
    public void flush() throws IOException {
        if (position > 0) {
            out.write(buffer, 0, position);
            position = 0;
        }
        out.flush();
    }

    public void writeBoolean(boolean v) throws IOException {
        ensureCapacity(1);
        buffer[position++] = (byte) (v ? 1 : 0);
    }

// Phương thức ghi mảng byte đầy đủ
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

// Phương thức ghi một byte
    public void write(int b) throws IOException {
        writeByte(b);
    }

// Phương thức ghi float - IEEE 754
    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

// Phương thức ghi double - IEEE 754
    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

// Phương thức ghi char (2 byte)
    public void writeChar(int v) throws IOException {
        writeShort(v);
    }

    @Override
    public void close() throws IOException {
        flush();
        out.close();
    }

    public void writeInts(short[] values) throws IOException {
        if (values == null) {
            writeInt(-1); // Đánh dấu null
            return;
        }

        // Ghi độ dài mảng
        int length = values.length;
        writeInt(length);

        // Đảm bảo có đủ không gian trong bộ đệm
        // Nếu mảng lớn, xử lý theo từng đoạn
        if (length * 4 > buffer.length - position) {
            // Nếu không đủ không gian, ghi theo từng phần tử
            for (int i = 0; i < length; i++) {
                writeInt(values[i]);
            }
        } else {
            // Nếu đủ không gian, viết trực tiếp tất cả vào bộ đệm
            ensureCapacity(length * 4);
            for (int i = 0; i < length; i++) {
                int v = values[i];
                buffer[position++] = (byte) (v >> 24);
                buffer[position++] = (byte) (v >> 16);
                buffer[position++] = (byte) (v >> 8);
                buffer[position++] = (byte) v;
            }
        }
    }

    /**
     * Ghi một mảng int vào luồng đầu ra.
     *
     * @param values Mảng int cần ghi
     * @throws IOException nếu có lỗi I/O xảy ra
     */
    public void writeInts(int[] values) throws IOException {
        if (values == null) {
            writeInt(-1); // Đánh dấu null
            return;
        }

        // Ghi độ dài mảng
        int length = values.length;
        writeInt(length);

        // Đảm bảo có đủ không gian trong bộ đệm
        // Nếu mảng lớn, xử lý theo từng đoạn
        if (length * 4 > buffer.length - position) {
            // Nếu không đủ không gian, ghi theo từng phần tử
            for (int i = 0; i < length; i++) {
                writeInt(values[i]);
            }
        } else {
            // Nếu đủ không gian, viết trực tiếp tất cả vào bộ đệm
            ensureCapacity(length * 4);
            for (int i = 0; i < length; i++) {
                int v = values[i];
                buffer[position++] = (byte) (v >> 24);
                buffer[position++] = (byte) (v >> 16);
                buffer[position++] = (byte) (v >> 8);
                buffer[position++] = (byte) v;
            }
        }
    }

    /**
     * Ghi một mảng long vào luồng đầu ra.
     *
     * @param values Mảng long cần ghi
     * @throws IOException nếu có lỗi I/O xảy ra
     */
    public void writeLongs(long[] values) throws IOException {
        if (values == null) {
            writeInt(-1); // Đánh dấu null
            return;
        }

        // Ghi độ dài mảng
        int length = values.length;
        writeInt(length);

        // Xử lý từng phần tử
        for (int i = 0; i < length; i++) {
            writeLong(values[i]);
        }
    }

    /**
     * Ghi một mảng String vào luồng đầu ra dưới dạng UTF-8.
     *
     * @param values Mảng String cần ghi
     * @throws IOException nếu có lỗi I/O xảy ra
     */
    public void writeUTFs(String[] values) throws IOException {
        if (values == null) {
            writeInt(-1); // Đánh dấu null
            return;
        }

        // Ghi độ dài mảng
        int length = values.length;
        writeInt(length);

        // Xử lý từng phần tử
        for (int i = 0; i < length; i++) {
            String str = values[i];
            if (str == null) {
                writeShort(-1); // Đánh dấu String null
            } else {
                writeUTF(str);
            }
        }
    }

    /**
     * Ghi một mảng boolean vào luồng đầu ra.
     *
     * @param values Mảng boolean cần ghi
     * @throws IOException nếu có lỗi I/O xảy ra
     */
    public void writeBooleans(boolean[] values) throws IOException {
        if (values == null) {
            writeInt(-1); // Đánh dấu null
            return;
        }

        // Ghi độ dài mảng
        int length = values.length;
        writeInt(length);

        // Tối ưu: Đóng gói 8 giá trị boolean vào mỗi byte
        int byteCount = (length + 7) / 8; // Số byte cần thiết

        // Nếu mảng nhỏ, xử lý theo cách đơn giản
        if (length <= 32) {
            for (int i = 0; i < length; i++) {
                writeBoolean(values[i]);
            }
            return;
        }

        // Đối với mảng lớn, đóng gói nhiều giá trị boolean vào một byte
        ensureCapacity(byteCount);
        int byteValue = 0;
        int bitPosition = 0;

        for (int i = 0; i < length; i++) {
            if (values[i]) {
                byteValue |= (1 << bitPosition);
            }

            bitPosition++;

            if (bitPosition == 8 || i == length - 1) {
                buffer[position++] = (byte) byteValue;
                byteValue = 0;
                bitPosition = 0;
            }
        }
    }

}
