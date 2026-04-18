package com.ngocrong.security.multilayer;

import com.ngocrong.security.multilayer.challenges.*;

/**
 * Response chứa challenge cho client
 */
public class ChallengeResponse {
    public final String challengeId;
    public final long timestamp;
    public final VRFChallenge vrfChallenge;
    public final CommitmentChallenge commitmentChallenge;
    public final ECCChallenge eccChallenge;
    public final PairingChallenge pairingChallenge;
    public final ZKChallenge zkChallenge;
    
    public ChallengeResponse(String challengeId, long timestamp,
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
