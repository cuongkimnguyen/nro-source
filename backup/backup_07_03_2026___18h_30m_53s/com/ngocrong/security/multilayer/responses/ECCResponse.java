package com.ngocrong.security.multilayer.responses;

import com.ngocrong.security.multilayer.challenges.ECCPoint;

/**
 * Response cho lớp ECC
 */
public class ECCResponse {
    public final ECCPoint[] results; // Array of ECC operation results
    
    public ECCResponse(ECCPoint[] results) {
        this.results = results;
    }
}
