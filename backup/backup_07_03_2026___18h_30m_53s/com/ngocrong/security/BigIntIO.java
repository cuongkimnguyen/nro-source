package com.ngocrong.security;

import com.ngocrong.network.FastDataInputStream;
import com.ngocrong.network.FastDataOutputStream;

import java.io.IOException;
import java.math.BigInteger;

public class BigIntIO {
    public static BigInteger readBigInt(FastDataInputStream in) throws IOException {
        int len = in.readInt();
        if (len <= 0) {
            return BigInteger.ZERO;
        }
        byte[] be = new byte[len];
        in.readFully(be);
        return new BigInteger(1, be);
    }

    public static void writeBigInt(FastDataOutputStream out, BigInteger value) throws IOException {
        byte[] be = value.toByteArray();
        if (be[0] == 0) {
            byte[] tmp = new byte[be.length - 1];
            System.arraycopy(be, 1, tmp, 0, tmp.length);
            be = tmp;
        }
        out.writeInt(be.length);
        out.write(be);
    }
}
