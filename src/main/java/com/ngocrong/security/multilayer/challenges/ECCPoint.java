package com.ngocrong.security.multilayer.challenges;

import java.math.BigInteger;

/**
 * Điểm trên đường cong elliptic
 */
public class ECCPoint {
    public final BigInteger x;
    public final BigInteger y;
    
    public ECCPoint(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ECCPoint other = (ECCPoint) obj;
        return x.equals(other.x) && y.equals(other.y);
    }
    
    @Override
    public int hashCode() {
        return x.hashCode() * 31 + y.hashCode();
    }
}
