package com.ngocrong.security;

/**
 * Manager class to route matrix challenge operations to appropriate handler
 * Decides between PC and Mobile based on device type
 */
public class MatrixChallengeManager {

    // Device constants
    public static final int DEVICE_PC = 0;
    public static final int DEVICE_APK = 1;
    public static final int DEVICE_IOS = 2;

    /**
     * Determine if device is PC or Mobile
     * @param device 0=PC, 1=APK, 2=iOS
     * @return true if PC, false if Mobile
     */
    public static boolean isPC(int device) {
        return device == DEVICE_PC;
    }

    /**
     * Get device name for logging
     */
    public static String getDeviceName(int device) {
        switch (device) {
            case DEVICE_PC: return "PC";
            case DEVICE_APK: return "APK";
            case DEVICE_IOS: return "iOS";
            default: return "Unknown(" + device + ")";
        }
    }

    // ===== CHALLENGE GENERATION =====
    
    /**
     * Generate challenge matrix for specific device
     */
    public static long[][] generateChallenge(int device) {
        if (isPC(device)) {
            return MatrixChallengePC.randomPCMatrix();
        } else {
            return MatrixChallengeMobile.randomMobileMatrix();
        }
    }

    // ===== SECRET MANAGEMENT =====
    
    /**
     * Get secret for specific device
     */
    public static long[][] getSecret(int device) {
        if (isPC(device)) {
            return MatrixChallengePC.getPCSecret();
        } else {
            return MatrixChallengeMobile.getMobileSecret();
        }
    }

    /**
     * Regenerate secret for specific device
     */
    public static void regenerateSecret(int device) {
        if (isPC(device)) {
            MatrixChallengePC.regeneratePCSecret();
        } else {
            MatrixChallengeMobile.regenerateMobileSecret();
        }
    }

    // ===== COMPUTATION =====
    
    /**
     * Compute response for specific device
     */
    public static long[][] computeResponse(long[][] secret, long[][] challenge, int device) {
        if (isPC(device)) {
            return MatrixChallengePC.computePCResponse(secret, challenge);
        } else {
            return MatrixChallengeMobile.computeMobileResponse(secret, challenge);
        }
    }

    // ===== VERIFICATION =====
    
    /**
     * Verify response for specific device
     */
    public static boolean verify(long[][] secret, long[][] challenge, long[][] response, int device) {
        if (isPC(device)) {
            return MatrixChallengePC.verifyPC(secret, challenge, response);
        } else {
            return MatrixChallengeMobile.verifyMobile(secret, challenge, response);
        }
    }

    // ===== LOGGING =====
    
    /**
     * Print secret for specific device
     */
    

    /**
     * Log challenge sent for specific device
     */
    public static void logChallengeSent(long[][] challenge, int device) {
        if (isPC(device)) {
            MatrixChallengePC.logPCChallengeSent(challenge);
        } else {
            MatrixChallengeMobile.logMobileChallengeSent(challenge);
        }
    }

    /**
     * Clear log file for specific device
     */
    public static void clearLogFile(int device) {
        if (isPC(device)) {
            MatrixChallengePC.clearPCLogFile();
        } else {
            MatrixChallengeMobile.clearMobileLogFile();
        }
    }

    // ===== MOD MANAGEMENT =====
    
    /**
     * Get MOD for specific device
     */
    public static long getMOD(int device) {
        if (isPC(device)) {
            return MatrixChallengePC.getPCMOD();
        } else {
            return MatrixChallengeMobile.getMobileMOD();
        }
    }

    

    // ===== TESTING =====
    
    /**
     * Test authentication for specific device
     */
    public static boolean testAuthentication(int device) {
        if (isPC(device)) {
            return MatrixChallengePC.testPCAuthentication();
        } else {
            return MatrixChallengeMobile.testMobileAuthentication();
        }
    }

    /**
     * Test all device types
     */
    public static void testAllDevices() {
        System.out.println("=== Testing All Device Types ===");
        
        boolean pcTest = testAuthentication(DEVICE_PC);
        boolean apkTest = testAuthentication(DEVICE_APK);
        boolean iosTest = testAuthentication(DEVICE_IOS);
        
        System.out.println("\n=== Test Results ===");
        System.out.println("PC Test: " + (pcTest ? "✅ PASS" : "❌ FAIL"));
        System.out.println("APK Test: " + (apkTest ? "✅ PASS" : "❌ FAIL"));
        System.out.println("iOS Test: " + (iosTest ? "✅ PASS" : "❌ FAIL"));
        
        if (pcTest && apkTest && iosTest) {
            System.out.println("🎉 ALL TESTS PASSED!");
        } else {
            System.out.println("💥 SOME TESTS FAILED!");
        }
    }

    /**
     * Test separation between PC and Mobile
     */
    public static void testSeparation() {
        System.out.println("=== Testing PC vs Mobile Separation ===");
        
        // Same challenge for both
        long[][] testChallenge = {
            {1000, 2000, 3000, 4000, 5000},
            {6000, 7000, 8000, 9000, 10000},
            {11000, 12000, 13000, 14000, 15000},
            {16000, 17000, 18000, 19000, 20000},
            {21000, 22000, 23000, 24000, 25000}
        };
        
        // Get secrets
        long[][] pcSecret = getSecret(DEVICE_PC);
        long[][] mobileSecret = getSecret(DEVICE_APK);
        
        // Compute responses
        long[][] pcResponse = computeResponse(pcSecret, testChallenge, DEVICE_PC);
        long[][] mobileResponse = computeResponse(mobileSecret, testChallenge, DEVICE_APK);
        
        // Check if different
        boolean responsesDifferent = false;
        for (int i = 0; i < MatrixChallengePC.SIZE && !responsesDifferent; i++) {
            for (int j = 0; j < MatrixChallengePC.SIZE && !responsesDifferent; j++) {
                if (pcResponse[i][j] != mobileResponse[i][j]) {
                    responsesDifferent = true;
                }
            }
        }
        
        System.out.println("PC MOD: " + getMOD(DEVICE_PC));
        System.out.println("Mobile MOD: " + getMOD(DEVICE_APK));
        System.out.println("Responses different: " + responsesDifferent);
        
        // Self verification
        boolean pcValid = verify(pcSecret, testChallenge, pcResponse, DEVICE_PC);
        boolean mobileValid = verify(mobileSecret, testChallenge, mobileResponse, DEVICE_APK);
        
        // Cross verification (should fail)
        boolean pcCrossValid = verify(mobileSecret, testChallenge, pcResponse, DEVICE_APK);
        boolean mobileCrossValid = verify(pcSecret, testChallenge, mobileResponse, DEVICE_PC);
        
        System.out.println("PC self-verify: " + pcValid);
        System.out.println("Mobile self-verify: " + mobileValid);
        System.out.println("PC cross-verify: " + pcCrossValid);
        System.out.println("Mobile cross-verify: " + mobileCrossValid);
        
        if (pcValid && mobileValid && responsesDifferent && (!pcCrossValid || !mobileCrossValid)) {
            System.out.println("✅ SEPARATION WORKING: PC and Mobile are completely separate!");
        } else {
            System.out.println("❌ SEPARATION FAILED: PC and Mobile not properly separated!");
        }
    }

    /**
     * Print current status for all devices
     */
    public static void printStatus() {
       
    }

    /**
     * Simulate complete authentication flow
     */
    public static void simulateAuthentication(int device) {
        String deviceName = getDeviceName(device);
        System.out.println("=== Simulating " + deviceName + " Authentication ===");
        
        try {
            // 1. Server generates challenge
            long[][] challenge = generateChallenge(device);
            long[][] secret = getSecret(device);
            
            System.out.println("1. Server generated challenge for " + deviceName);
            
            // 2. Client computes response (simulated)
            long[][] response = computeResponse(secret, challenge, device);
            
            System.out.println("2. Client computed response");
            
            // 3. Server verifies response
            boolean verified = verify(secret, challenge, response, device);
            
            System.out.println("3. Server verification: " + (verified ? "SUCCESS" : "FAILED"));
            
            if (verified) {
                System.out.println("✅ " + deviceName + " authentication SUCCESSFUL!");
            } else {
                System.out.println("❌ " + deviceName + " authentication FAILED!");
            }
            
        } catch (Exception e) {
            System.out.println("❌ " + deviceName + " authentication simulation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize with different MODs for testing
     */
}