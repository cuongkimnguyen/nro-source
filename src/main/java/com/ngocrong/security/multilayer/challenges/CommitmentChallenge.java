package com.ngocrong.security.multilayer.challenges;

import java.math.BigInteger;

/**
 * Challenge cho lớp Commitment
 */
public class CommitmentChallenge {
    public final BigInteger p;  // Prime modulus
    public final BigInteger q;  // (p-1)/2
    public final BigInteger g;  // Generator
    public final BigInteger[] challenges;  // Array of challenges
    public final BigInteger[] commitments; // Array of commitments
    
    public CommitmentChallenge(BigInteger p, BigInteger q, BigInteger g, 
                             BigInteger[] challenges, BigInteger[] commitments) {
        this.p = p;
        this.q = q;
        this.g = g;
        this.challenges = challenges;
        this.commitments = commitments;
    }
}
