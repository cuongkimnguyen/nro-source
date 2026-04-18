package com.ngocrong.security.multilayer.responses;

import java.math.BigInteger;

/**
 * Response cho lớp Zero-Knowledge
 */
public class ZKResponse {
    public final BigInteger proof;    // Proof value
    public final BigInteger challenge; // Challenge value
    
    public ZKResponse(BigInteger proof, BigInteger challenge) {
        this.proof = proof;
        this.challenge = challenge;
    }
}
