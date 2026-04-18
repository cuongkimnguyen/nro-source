package com.ngocrong.security;

import com.ngocrong.server.Server;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Random;

/**
 * Matrix Challenge for Mobile clients only (APK/iOS)
 * Completely separate from PC authentication
 */
public class MatrixChallengeMobile {

    public static final int SIZE = 5;
    public static long MOD_MOBILE = 4294967291L;  // Mobile-specific MOD (different from PC)
    private static long[][] MOBILE_SECRET = null;  // Mobile-specific secret
    private static PrintWriter logWriter;

    static {
        try {
            logWriter = new PrintWriter(new FileWriter("matrix_mobile_debug.txt", true));
            logToFile("=== Matrix Challenge Mobile Debug Log ===");
        } catch (IOException e) {
            //System.err.println("Failed to create Mobile log file: " + e.getMessage());
        }
        // Initialize mobile secret on startup
        getMobileSecret();
    }

    /**
     * Save Mobile key and regenerate secret
     */


    /**
     * Get Mobile secret (generate if not exists)
     */
    public static synchronized long[][] getMobileSecret() {
        if (MOBILE_SECRET == null) {
            MOBILE_SECRET = generateMobileSecret();
        }
        return copyMatrix(MOBILE_SECRET);
    }

    /**
     * Generate Mobile secret matrix
     */
    private static long[][] generateMobileSecret() {
        logToFile("=== Generating Mobile Secret ===");
        MobileRandom rnd = new MobileRandom(0x5eedfaceL);
        long[][] secret = new long[SIZE][SIZE];
        
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int hiInt = rnd.nextInt();
                int loInt = rnd.nextInt();

                long hi = Integer.toUnsignedLong(hiInt);
                long lo = Integer.toUnsignedLong(loInt);
                long val = (hi << 32) | lo;

                java.math.BigInteger bigVal = java.math.BigInteger.valueOf(val);
                if (val < 0) {
                    bigVal = java.math.BigInteger.valueOf(2).pow(64).add(java.math.BigInteger.valueOf(val));
                }

                java.math.BigInteger bigMod = java.math.BigInteger.valueOf(MOD_MOBILE);
                secret[i][j] = bigVal.remainder(bigMod).longValue();

                logToFile(String.format("MOBILE_SECRET[%d][%d]: hiInt=%d, loInt=%d, hi=%d, lo=%d, val=%d, final=%d (MOD=%d)",
                        i, j, hiInt, loInt, hi, lo, val, secret[i][j], MOD_MOBILE));
            }
        }
        logToFile("=== Mobile Secret Generation Complete ===");
        return secret;
    }

    /**
     * Regenerate Mobile secret
     */
    public static synchronized void regenerateMobileSecret() {
        logToFile("=== Regenerating Mobile Secret ===");
        MOBILE_SECRET = generateMobileSecret();
        logToFile("=== Mobile Secret Regeneration Complete ===");
    }

    /**
     * Generate random challenge matrix for Mobile
     */
    public static long[][] randomMobileMatrix() {
        logToFile("=== Generating Random Mobile Challenge Matrix ===");
        Random rnd = new Random();
        long[][] m = new long[SIZE][SIZE];
        
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                long val = Integer.toUnsignedLong(rnd.nextInt());
                m[i][j] = val % MOD_MOBILE;
                logToFile(String.format("Mobile_Challenge[%d][%d] = %d (MOD=%d)", i, j, m[i][j], MOD_MOBILE));
            }
        }
        logToFile("=== Mobile Challenge Matrix Generation Complete ===");
        return m;
    }

    /**
     * Matrix multiplication for Mobile
     */
    public static long[][] multiplyMobile(long[][] a, long[][] b) {
        logToFile("=== Mobile Matrix Multiplication Start ===");
        long[][] r = new long[SIZE][SIZE];
        
        for (int i = 0; i < SIZE; i++) {
            for (int k = 0; k < SIZE; k++) {
                long aik = a[i][k];
                for (int j = 0; j < SIZE; j++) {
                    BigInteger bigAik = BigInteger.valueOf(aik);
                    BigInteger bigBkj = BigInteger.valueOf(b[k][j]);
                    BigInteger bigMOD = BigInteger.valueOf(MOD_MOBILE);
                    BigInteger bigRij = BigInteger.valueOf(r[i][j]);

                    BigInteger product = bigAik.multiply(bigBkj).remainder(bigMOD);
                    BigInteger temp = bigRij.add(product);
                    BigInteger newVal = temp.remainder(bigMOD);

                    r[i][j] = newVal.longValue();

                    if (i < 2 && k < 2 && j < 2) {
                        logToFile(String.format("Mobile_r[%d][%d] += a[%d][%d] * b[%d][%d] = %d + (%d * %d %% %d) = %d",
                                i, j, i, k, k, j, bigRij.longValue(), aik, b[k][j], MOD_MOBILE, r[i][j]));
                    }
                }
            }
        }
        logToFile("=== Mobile Matrix Multiplication End ===");
        return r;
    }

    /**
     * Compute Mobile response: S * C * S
     */
    public static long[][] computeMobileResponse(long[][] secret, long[][] challenge) {
        logToFile("=== Mobile Computing Response: S * C * S ===");

        // Log input matrices
        logToFile("Mobile Secret matrix:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("Mobile_S[%d][%d] = %d", i, j, secret[i][j]));
            }
        }

        logToFile("Mobile Challenge matrix:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("Mobile_C[%d][%d] = %d", i, j, challenge[i][j]));
            }
        }

        // First multiplication: S * C
        long[][] sc = multiplyMobile(secret, challenge);
        logToFile("Mobile S * C completed:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("Mobile_SC[%d][%d] = %d", i, j, sc[i][j]));
            }
        }

        // Second multiplication: (S * C) * S
        long[][] result = multiplyMobile(sc, secret);
        logToFile("Mobile (S * C) * S completed:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("Mobile_Final[%d][%d] = %d", i, j, result[i][j]));
            }
        }

        return result;
    }

    /**
     * Verify Mobile response
     */
    public static boolean verifyMobile(long[][] secret, long[][] challenge, long[][] response) {
        logToFile("=== Mobile Verification Start ===");

        // Log received response
        logToFile("Received Mobile Response from Client:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("Mobile_ClientResponse[%d][%d] = %d", i, j, response[i][j]));
            }
        }

        long[][] expected = computeMobileResponse(secret, challenge);

        boolean isValid = true;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("Mobile_expected[%d][%d] : %d", i, j, expected[i][j]));
                logToFile(String.format("Mobile_response[%d][%d] : %d", i, j, response[i][j]));
                if (expected[i][j] != response[i][j]) {
                    logToFile(String.format("Mobile_MISMATCH at [%d][%d]: expected=%d, got=%d",
                            i, j, expected[i][j], response[i][j]));
                    isValid = false;
                }
            }
        }

        logToFile(String.format("=== Mobile Verification Result: %s ===", (isValid ? "PASS" : "FAIL")));
        return isValid;
    }

    /**
     * Print Mobile secret
     */
    public static void printMobileSecret() {
        logToFile("=== Mobile Secret (MOD=" + MOD_MOBILE + ") ===");
        long[][] secret = getMobileSecret();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("Mobile[%d][%d] = %d", i, j, secret[i][j]));
            }
        }
        logToFile("=== End Mobile Secret ===");
    }

    /**
     * Log Mobile challenge being sent
     */
    public static void logMobileChallengeSent(long[][] challenge) {
        logToFile("=== Mobile Challenge Sent to Client ===");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("Mobile_SentChallenge[%d][%d] = %d", i, j, challenge[i][j]));
            }
        }
        logToFile("=== End Mobile Challenge Sent ===");
    }

    /**
     * Get current Mobile MOD
     */
    public static long getMobileMOD() {
        return MOD_MOBILE;
    }

    /**
     * Test Mobile authentication
     */
    public static boolean testMobileAuthentication() {
        logToFile("=== Testing Mobile Authentication ===");
        
        try {
            // Generate challenge
            long[][] challenge = randomMobileMatrix();
            
            // Get secret
            long[][] secret = getMobileSecret();
            
            // Compute response
            long[][] response = computeMobileResponse(secret, challenge);
            
            // Verify
            boolean isValid = verifyMobile(secret, challenge, response);
            
            logToFile("Mobile Authentication Test: " + (isValid ? "PASS" : "FAIL"));
            return isValid;
        } catch (Exception e) {
            logToFile("Mobile Authentication Test FAILED: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Helper methods
    private static long[][] copyMatrix(long[][] matrix) {
        long[][] copy = new long[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }

    public static void logToFile(String message) {
//        try {
//            if (logWriter != null) {
//                logWriter.println(message);
//                logWriter.flush();
//            }
//            //System.err.println("[Mobile] " + message);
//        } catch (Exception e) {
//            //System.err.println("Failed to write to Mobile log file: " + e.getMessage());
//        }
    }

    public static void clearMobileLogFile() {
        try {
            if (logWriter != null) {
                logWriter.close();
            }
            logWriter = new PrintWriter(new FileWriter("matrix_mobile_debug.txt", false));
            logToFile("=== Matrix Challenge Mobile Debug Log (New Session) ===");
        } catch (IOException e) {
            //System.err.println("Failed to clear Mobile log file: " + e.getMessage());
        }
    }

    // Mobile-specific Random implementation
    private static class MobileRandom {
        private long seed;

        public MobileRandom(long seed) {
            this.seed = seed & ((1L << 48) - 1);
            logToFile(String.format("Mobile Random initialized with seed: %X, internal seed: %X", seed, this.seed));
        }

        public int nextInt() {
            long oldSeed = seed;
            seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
            int result = (int) ((seed >> 16) & 0xFFFFFFFFL);
            logToFile(String.format("Mobile NextInt: %X -> %X -> %d", oldSeed, seed, result));
            return result;
        }
    }
}