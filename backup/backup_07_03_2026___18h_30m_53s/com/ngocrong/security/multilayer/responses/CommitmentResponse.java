package com.ngocrong.security.multilayer.responses;

import java.math.BigInteger;

/**
 * Response cho lớp Commitment
 */
public class CommitmentResponse {
    public final BigInteger[] proofs; // Array of proof values
    
    public CommitmentResponse(BigInteger[] proofs) {
        this.proofs = proofs;
    }
}
