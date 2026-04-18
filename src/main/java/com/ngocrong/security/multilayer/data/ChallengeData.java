package com.ngocrong.security.multilayer.data;

import com.ngocrong.security.multilayer.challenges.*;

/**
 * Lưu trữ dữ liệu challenge hoàn chỉnh
 */
public class ChallengeData {
    public final String challengeId;
    public final long timestamp;
    public final VRFChallenge vrfChallenge;
    public final CommitmentChallenge commitmentChallenge;
    public final ECCChallenge eccChallenge;
    public final PairingChallenge pairingChallenge;
    public final ZKChallenge zkChallenge;
    
    public ChallengeData(String challengeId, long timestamp, 
                        VRFChallenge vrfChallenge, CommitmentChallenge commitmentChallenge,
                        ECCChallenge eccChallenge, PairingChallenge pairingChallenge, 
                        ZKChallenge zkChallenge) {
        this.challengeId = challengeId;
        this.timestamp = timestamp;
        this.vrfChallenge = vrfChallenge;
        this.commitmentChallenge = commitmentChallenge;
        this.eccChallenge = eccChallenge;
        this.pairingChallenge = pairingChallenge;
        this.zkChallenge = zkChallenge;
    }
}
