package com.ngocrong.security.multilayer.challenges;

import java.math.BigInteger;

/**
 * Challenge cho lớp Pairing (BLS-style pairing simulation)
 */
public class PairingChallenge {
    public final BigInteger[] group1Elements; // Elements from group G1
    public final BigInteger[] group2Elements; // Elements from group G2
    
    public PairingChallenge(BigInteger[] group1Elements, BigInteger[] group2Elements) {
        this.group1Elements = group1Elements;
        this.group2Elements = group2Elements;
    }
}
