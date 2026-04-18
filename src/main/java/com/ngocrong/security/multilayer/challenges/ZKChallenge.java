package com.ngocrong.security.multilayer.challenges;

import java.math.BigInteger;

/**
 * Challenge cho lớp Zero-Knowledge
 */
public class ZKChallenge {
    public final BigInteger p;  // Prime modulus
    public final BigInteger q;  // (p-1)/2
    public final BigInteger g;  // Generator 1
    public final BigInteger h;  // Generator 2
    public final BigInteger commitment; // g^secret mod p
    public final BigInteger secret;     // Secret value
    
    public ZKChallenge(BigInteger p, BigInteger q, BigInteger g, BigInteger h, 
                      BigInteger commitment, BigInteger secret) {
        this.p = p;
        this.q = q;
        this.g = g;
        this.h = h;
        this.commitment = commitment;
        this.secret = secret;
    }
}
