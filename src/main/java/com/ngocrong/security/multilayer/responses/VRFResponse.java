package com.ngocrong.security.multilayer.responses;

import java.math.BigInteger;

/**
 * Response cho lớp VRF
 */
public class VRFResponse {
    public final BigInteger proof; // Proof value
    
    public VRFResponse(BigInteger proof) {
        this.proof = proof;
    }
}
