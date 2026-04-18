package com.ngocrong.security.multilayer;

import com.ngocrong.security.multilayer.responses.*;

/**
 * Response từ client chứa kết quả của tất cả 5 lớp
 */
public class ClientResponse {
    public final String challengeId;
    public final VRFResponse vrfResponse;
    public final CommitmentResponse commitmentResponse;
    public final ECCResponse eccResponse;
    public final PairingResponse pairingResponse;
    public final ZKResponse zkResponse;
    
    public ClientResponse(String challengeId, VRFResponse vrfResponse, 
                        CommitmentResponse commitmentResponse, ECCResponse eccResponse,
                        PairingResponse pairingResponse, ZKResponse zkResponse) {
        this.challengeId = challengeId;
        this.vrfResponse = vrfResponse;
        this.commitmentResponse = commitmentResponse;
        this.eccResponse = eccResponse;
        this.pairingResponse = pairingResponse;
        this.zkResponse = zkResponse;
    }
}
