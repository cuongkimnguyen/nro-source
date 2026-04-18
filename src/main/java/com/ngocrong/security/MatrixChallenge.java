package com.ngocrong.security;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class MatrixChallenge {
    public static final int SIZE = 5;
    public static final long MOD = 4294967291L; // Prime near 2^32
    private static final long[][] DEFAULT_SECRET = generateDefaultSecret();
    private static PrintWriter logWriter;
    
    static {
        try {
            logWriter = new PrintWriter(new FileWriter("matrix_server_debug.txt", true));
            logToFile("=== Matrix Challenge Server Debug Log ===");
        } catch (IOException e) {
            //System.err.println("Failed to create log file: " + e.getMessage());
        }
    }
    
    public static void logToFile(String message) {
//        try {
//            if (logWriter != null) {
//                logWriter.println(message);
//                logWriter.flush();
//            }
//            //System.err.println(message);
//        } catch (Exception e) {
//            //System.err.println("Failed to write to log file: " + e.getMessage());
//        }
    }

    // Custom Random implementation matching Java's algorithm exactly
    private static class JavaRandom {
        private long seed;

        public JavaRandom(long seed) {
            this.seed = seed & ((1L << 48) - 1);
            logToFile(String.format("JavaRandom initialized with seed: %X, internal seed: %X", seed, this.seed));
        }

        public int nextInt() {
            long oldSeed = seed;
            seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
            int result = (int)((seed >> 16) & 0xFFFFFFFFL);
            logToFile(String.format("NextInt: %X -> %X -> %d", oldSeed, seed, result));
            return result;
        }
    }

    private static long[][] generateDefaultSecret() {
        logToFile("=== Generating Java DEFAULT_SECRET (FINAL FIX) ===");
        JavaRandom rnd = new JavaRandom(0x5eedfaceL);
        long[][] secret = new long[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int hiInt = rnd.nextInt();
                int loInt = rnd.nextInt();
                
                // CRITICAL FIX: Sử dụng chính xác cách C# xử lý
                // Convert int to uint first, then to ulong - giống C#
                long hi = Integer.toUnsignedLong(hiInt);  // Equivalent to C#: (ulong)((uint)hiInt)
                long lo = Integer.toUnsignedLong(loInt);  // Equivalent to C#: (ulong)((uint)loInt)
                
                long val = (hi << 32) | lo;
                
                // CRITICAL: Sử dụng math tương đương C# BigInteger
                java.math.BigInteger bigVal = java.math.BigInteger.valueOf(val);
                if (val < 0) {
                    // Handle overflow case - convert to unsigned equivalent
                    bigVal = java.math.BigInteger.valueOf(2).pow(64).add(java.math.BigInteger.valueOf(val));
                }
                
                java.math.BigInteger bigMod = java.math.BigInteger.valueOf(MOD);
                secret[i][j] = bigVal.remainder(bigMod).longValue();
                
                logToFile(String.format("SECRET[%d][%d]: hiInt=%d, loInt=%d, hi=%d, lo=%d, val=%d, bigVal=%s, final=%d", 
                    i, j, hiInt, loInt, hi, lo, val, bigVal.toString(), secret[i][j]));
            }
        }
        logToFile("=== Java DEFAULT_SECRET Generation Complete (FINAL FIX) ===");
        return secret;
    }

    public static long[][] defaultSecret() {
        long[][] copy = new long[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(DEFAULT_SECRET[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }

    public static long[][] randomMatrix() {
        logToFile("=== Generating Random Challenge Matrix ===");
        Random rnd = new Random();
        long[][] m = new long[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                // Generate 32-bit unsigned values to match network protocol
                long val = Integer.toUnsignedLong(rnd.nextInt());
                m[i][j] = val % MOD;
                logToFile(String.format("Challenge[%d][%d] = %d", i, j, m[i][j]));
            }
        }
        logToFile("=== Random Challenge Matrix Generation Complete ===");
        return m;
    }

    public static long[][] multiply(long[][] a, long[][] b) {
        logToFile("=== Java Matrix Multiplication Start (FINAL FIX) ===");
        long[][] r = new long[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int k = 0; k < SIZE; k++) {
                long aik = a[i][k];
                for (int j = 0; j < SIZE; j++) {
                    // CRITICAL: Sử dụng BigInteger để match chính xác C#
                    java.math.BigInteger bigAik = java.math.BigInteger.valueOf(aik);
                    java.math.BigInteger bigBkj = java.math.BigInteger.valueOf(b[k][j]);
                    java.math.BigInteger bigMOD = java.math.BigInteger.valueOf(MOD);
                    java.math.BigInteger bigRij = java.math.BigInteger.valueOf(r[i][j]);
                    
                    // Exact same logic as C#: temp = r[i][j] + (aik * b[k][j]) % MOD
                    java.math.BigInteger product = bigAik.multiply(bigBkj).remainder(bigMOD);
                    java.math.BigInteger temp = bigRij.add(product);
                    java.math.BigInteger newVal = temp.remainder(bigMOD);
                    
                    r[i][j] = newVal.longValue();
                    
                    // Log first few calculations for debugging
                    if (i < 2 && k < 2 && j < 2) {
                        logToFile(String.format("r[%d][%d] += a[%d][%d] * b[%d][%d] = %d + (%d * %d %% %d) = %d", 
                            i, j, i, k, k, j, bigRij.longValue(), aik, b[k][j], MOD, r[i][j]));
                    }
                }
            }
        }
        logToFile("=== Java Matrix Multiplication End (FINAL FIX) ===");
        return r;
    }

    public static long[][] computeResponse(long[][] secret, long[][] challenge) {
        logToFile("=== Java Computing Response: S * C * S (FINAL FIX) ===");
        
        // Log input matrices
        logToFile("Complete Java Secret matrix (FINAL FIX):");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("S[%d][%d] = %d", i, j, secret[i][j]));
            }
        }
        
        logToFile("Complete Java Challenge matrix:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("C[%d][%d] = %d", i, j, challenge[i][j]));
            }
        }
        
        // First multiplication: S * C
        long[][] sc = multiply(secret, challenge);
        logToFile("Java S * C completed - Full result (FINAL FIX):");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("SC[%d][%d] = %d", i, j, sc[i][j]));
            }
        }
        
        // Second multiplication: (S * C) * S
        long[][] result = multiply(sc, secret);
        logToFile("Java (S * C) * S completed - Full result (FINAL FIX):");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("Final[%d][%d] = %d", i, j, result[i][j]));
            }
        }
        
        return result;
    }

    public static boolean verify(long[][] secret, long[][] challenge, long[][] response) {
        logToFile("=== Java Verification Start (FINAL FIX) ===");
        
        // Log received response
        logToFile("Received Response from Client:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("ClientResponse[%d][%d] = %d", i, j, response[i][j]));
            }
        }
        
        long[][] expected = computeResponse(secret, challenge);
        
        boolean isValid = true;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("expected[%d][%d] : %d", i, j, expected[i][j]));
                logToFile(String.format("response[%d][%d] : %d", i, j, response[i][j]));
                if (expected[i][j] != response[i][j]) {
                    logToFile(String.format("MISMATCH at [%d][%d]: expected=%d, got=%d", 
                        i, j, expected[i][j], response[i][j]));
                    isValid = false;
                }
            }
        }
        
        logToFile(String.format("=== Java Verification Result: %s ===", (isValid ? "PASS" : "FAIL")));
        return isValid;
    }

    // Clear log file at start
    public static void clearLogFile() {
        try {
            if (logWriter != null) {
                logWriter.close();
            }
            logWriter = new PrintWriter(new FileWriter("matrix_server_debug.txt", false));
            logToFile("=== Matrix Challenge Server Debug Log (New Session - FINAL FIX) ===");
        } catch (IOException e) {
            //System.err.println("Failed to clear log file: " + e.getMessage());
        }
    }

    // Print all default secret values
    public static void printDefaultSecret() {
        logToFile("=== Java DEFAULT_SECRET (FINAL FIX) ===");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("[%d][%d] = %d", i, j, DEFAULT_SECRET[i][j]));
            }
        }
        logToFile("=== End Java DEFAULT_SECRET (FINAL FIX) ===");
    }

    // Log challenge being sent
    public static void logChallengeSent(long[][] challenge) {
        logToFile("=== Challenge Sent to Client ===");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                logToFile(String.format("SentChallenge[%d][%d] = %d", i, j, challenge[i][j]));
            }
        }
        logToFile("=== End Challenge Sent ===");
    }
}