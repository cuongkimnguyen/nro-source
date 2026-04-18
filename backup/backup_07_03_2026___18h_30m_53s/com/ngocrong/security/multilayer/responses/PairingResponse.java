package com.ngocrong.security.multilayer.responses;

import java.math.BigInteger;

/**
 * Response cho lớp Pairing
 */
public class PairingResponse {
    public final BigInteger pairingResult; // Result of pairing computation
    
    public PairingResponse(BigInteger pairingResult) {
        this.pairingResult = pairingResult;
    }
}
