package com.ngocrong.security;

import com.ngocrong.data.SecurityData;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.repository.SecurityRepository;
import com.ngocrong.server.Server;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Random;

/**
 * Matrix Challenge for PC clients only Completely separate from Mobile
 * authentication
 */
public class MatrixChallengePC {

    public static final int SIZE = 5;
    public static long MOD_PC = 4294967291L;  // PC-specific MOD
    private static long[][] PC_SECRET = null;  // PC-specific secret
    private static PrintWriter logWriter;
    public static int CONFIG_ID = 1;

    private static SecurityRepository repo() {
        return GameRepository.getInstance().securityRepository;
    }

    static {
        try {
            logWriter = new PrintWriter(new FileWriter("matrix_pc_debug.txt", true));
            logToFile("=== Matrix Challenge PC Debug Log ===");
        } catch (IOException e) {
            //System.err.println("Failed to create PC log file: " + e.getMessage());
        }
        loadPCKey();
    }

    /**
     * Load PC key from database
     */
    public static synchronized void loadPCKey() {
        try {
            Optional<SecurityData> data = repo().findById(CONFIG_ID);
            if (data.isPresent()) {
                long oldMod = MOD_PC;
                MOD_PC = Optional.ofNullable(data.get().getModPC()).orElse(4294967291L);
                Server.VERSION_PC = Optional.ofNullable(data.get().getVersionPC()).orElse("0.0.4");

                // Regenerate secret if MOD changed
                if (oldMod != MOD_PC) {
                    regeneratePCSecret();
                    logToFile("PC Secret regenerated due to MOD change: " + oldMod + " -> " + MOD_PC);
                }

                //System.err.println("PC MOD loaded: " + MOD_PC);
                logToFile("PC MOD loaded: " + MOD_PC);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logToFile("Error loading PC key: " + e.getMessage());
        }
    }

    /**
     * Save PC key and regenerate secret
     */
    public static synchronized void savePCKey(long mod, String ver) {
        try {
            SecurityData cfg = repo().findById(CONFIG_ID).orElse(new SecurityData(CONFIG_ID, ver, mod));
            cfg.setModPC(mod);
            cfg.setVersionPC(ver);
            repo().save(cfg);

            long oldMod = MOD_PC;
            MOD_PC = mod;
            Server.VERSION_PC = ver;

            // Always regenerate secret when saving new MOD
            regeneratePCSecret();
            logToFile("PC Secret regenerated after save: " + oldMod + " -> " + MOD_PC);

            //System.err.println("PC MOD updated: " + MOD_PC);
            logToFile("PC MOD updated: " + MOD_PC);
        } catch (Exception e) {
            e.printStackTrace();
            logToFile("Error saving PC key: " + e.getMessage());
        }
    }

    /**
     * Get PC secret (generate if not exists)
     */
    public static synchronized long[][] getPCSecret() {
        if (PC_SECRET == null) {
            PC_SECRET = generatePCSecret();
        }
        return copyMatrix(PC_SECRET);
    }

    /**
     * Generate PC secret matrix
     */
    private static long[][] generatePCSecret() {
        logToFile("=== Generating PC Secret ===");
        PCRandom rnd = new PCRandom(0x5eedfaceL);
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

                java.math.BigInteger bigMod = java.math.BigInteger.valueOf(MOD_PC);
                secret[i][j] = bigVal.remainder(bigMod).longValue();

                logToFile(String.format("PC_SECRET[%d][%d]: hiInt=%d, loInt=%d, hi=%d, lo=%d, val=%d, final=%d (MOD=%d)",
                        i, j, hiInt, loInt, hi, lo, val, secret[i][j], MOD_PC));
            }
        }
        logToFile("=== PC Secret Generation Complete ===");
        return secret;
    }

    /**
     * Regenerate PC secret
     */
    public static synchronized void regeneratePCSecret() {
        logToFile("=== Regenerating PC Secret ===");
        PC_SECRET = generatePCSecret();
        logToFile("=== PC Secret Regeneration Complete ===");
    }

    /**
     * Generate random challenge matrix for PC
     */
    public static long[][] randomPCMatrix() {
        logToFile("=== Generating Random PC Challenge Matrix ===");
        Random rnd = new Random();
        long[][] m = new long[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                long val = Integer.toUnsignedLong(rnd.nextInt());
                m[i][j] = val % MOD_PC;
                logToFile(String.format("PC_Challenge[%d][%d] = %d (MOD=%d)", i, j, m[i][j], MOD_PC));
            }
        }
        logToFile("=== PC Challenge Matrix Generation Complete ===");
        return m;
    }

    /**
     * Matrix multiplication for PC
     */
    public static long[][] multiplyPC(long[][] a, long[][] b) {
        logToFile("=== PC Matrix Multiplication Start ===");
        long[][] r = new long[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int k = 0; k < SIZE; k++) {
                long aik = a[i][k];
                for (int j = 0; j < SIZE; j++) {
                    BigInteger bigAik = BigInteger.valueOf(aik);
                    BigInteger bigBkj = BigInteger.valueOf(b[k][j]);
                    BigInteger bigMOD = BigInteger.valueOf(MOD_PC);
                    BigInteger bigRij = BigInteger.valueOf(r[i][j]);

                    BigInteger product = bigAik.multiply(bigBkj).remainder(bigMOD);
                    BigInteger temp = bigRij.add(product);
                    BigInteger newVal = temp.remainder(bigMOD);

                    r[i][j] = newVal.longValue();

                    if (i < 2 && k < 2 && j < 2) {
                        logToFile(String.format("PC_r[%d][%d] += a[%d][%d] * b[%d][%d] = %d + (%d * %d %% %d) = %d",
                                i, j, i, k, k, j, bigRij.longValue(), aik, b[k][j], MOD_PC, r[i][j]));
                    }
                }
            }
        }
        logToFile("=== PC Matrix Multiplication End ===");
        return r;
    }

    /**
     * Compute PC response: S * C * S
     */
    public static long[][] computePCResponse(long[][] secret, long[][] challenge) {
        logToFile("=== PC Computing Response: S * C * S ===");

        // Log input matrices
        logToFile("PC Secret matrix:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("PC_S[%d][%d] = %d", i, j, secret[i][j]));
            }
        }

        logToFile("PC Challenge matrix:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("PC_C[%d][%d] = %d", i, j, challenge[i][j]));
            }
        }

        // First multiplication: S * C
        long[][] sc = multiplyPC(secret, challenge);
        logToFile("PC S * C completed:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("PC_SC[%d][%d] = %d", i, j, sc[i][j]));
            }
        }

        // Second multiplication: (S * C) * S
        long[][] result = multiplyPC(sc, secret);
        logToFile("PC (S * C) * S completed:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("PC_Final[%d][%d] = %d", i, j, result[i][j]));
            }
        }

        return result;
    }

    /**
     * Verify PC response
     */
    public static boolean verifyPC(long[][] secret, long[][] challenge, long[][] response) {
        long[][] expected = computePCResponse(secret, challenge);

        boolean isValid = true;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {

                if (expected[i][j] != response[i][j]) {
                    isValid = false;
                }
            }
        }

        return isValid;
    }


    /**
     * Log PC challenge being sent
     */
    public static void logPCChallengeSent(long[][] challenge) {
        logToFile("=== PC Challenge Sent to Client ===");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("PC_SentChallenge[%d][%d] = %d", i, j, challenge[i][j]));
            }
        }
        logToFile("=== End PC Challenge Sent ===");
    }

    /**
     * Get current PC MOD
     */
    public static long getPCMOD() {
        return MOD_PC;
    }

    /**
     * Test PC authentication
     */
    public static boolean testPCAuthentication() {
        logToFile("=== Testing PC Authentication ===");

        try {
            // Generate challenge
            long[][] challenge = randomPCMatrix();

            // Get secret
            long[][] secret = getPCSecret();

            // Compute response
            long[][] response = computePCResponse(secret, challenge);

            // Verify
            boolean isValid = verifyPC(secret, challenge, response);

            logToFile("PC Authentication Test: " + (isValid ? "PASS" : "FAIL"));
            return isValid;
        } catch (Exception e) {
            logToFile("PC Authentication Test FAILED: " + e.getMessage());
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
//            //System.err.println("[PC] " + message);
//        } catch (Exception e) {
//            //System.err.println("Failed to write to PC log file: " + e.getMessage());
//        }
    }

    public static void clearPCLogFile() {
        try {
            if (logWriter != null) {
                logWriter.close();
            }
            logWriter = new PrintWriter(new FileWriter("matrix_pc_debug.txt", false));
            logToFile("=== Matrix Challenge PC Debug Log (New Session) ===");
        } catch (IOException e) {
            //System.err.println("Failed to clear PC log file: " + e.getMessage());
        }
    }

    // PC-specific Random implementation
    private static class PCRandom {

        private long seed;

        public PCRandom(long seed) {
            this.seed = seed & ((1L << 48) - 1);
            logToFile(String.format("PC Random initialized with seed: %X, internal seed: %X", seed, this.seed));
        }

        public int nextInt() {
            long oldSeed = seed;
            seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
            int result = (int) ((seed >> 16) & 0xFFFFFFFFL);
            logToFile(String.format("PC NextInt: %X -> %X -> %d", oldSeed, seed, result));
            return result;
        }
    }
}
