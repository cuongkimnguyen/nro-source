package com.ngocrong.security.multilayer;

/**
 * Kết quả xác minh từ server
 */
public class VerificationResult {
    public final boolean success;
    public final String message;
    public final long verificationTime;
    
    public VerificationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.verificationTime = System.currentTimeMillis();
    }
}
