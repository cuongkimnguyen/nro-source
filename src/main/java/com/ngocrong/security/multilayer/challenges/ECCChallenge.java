package com.ngocrong.security.multilayer.challenges;

import java.math.BigInteger;

/**
 * Challenge cho lớp ECC (Elliptic Curve Cryptography)
 */
public class ECCChallenge {
    public final BigInteger p;  // Prime modulus
    public final BigInteger a;  // Curve parameter a
    public final BigInteger b;  // Curve parameter b
    public final BigInteger gx; // Generator point x
    public final BigInteger gy; // Generator point y
    public final BigInteger[] scalars; // Array of scalars
    public final ECCPoint[] points;    // Array of points
    
    public ECCChallenge(BigInteger p, BigInteger a, BigInteger b, BigInteger gx, BigInteger gy,
                       BigInteger[] scalars, ECCPoint[] points) {
        this.p = p;
        this.a = a;
        this.b = b;
        this.gx = gx;
        this.gy = gy;
        this.scalars = scalars;
        this.points = points;
    }
}
