package com.ngocrong.security.multilayer.challenges;

import java.math.BigInteger;

/**
 * Challenge cho lớp VRF (Verifiable Random Function)
 */
public class VRFChallenge {
    public final BigInteger p;  // Prime modulus
    public final BigInteger q;  // (p-1)/2
    public final BigInteger g;  // Generator
    public final BigInteger y;  // g^x mod p
    public final BigInteger x;  // Secret challenge
    
    public VRFChallenge(BigInteger p, BigInteger q, BigInteger g, BigInteger y, BigInteger x) {
        this.p = p;
        this.q = q;
        this.g = g;
        this.y = y;
        this.x = x;
    }
}
