package com.ngocrong.security.multilayer;

import com.ngocrong.security.multilayer.challenges.CommitmentChallenge;
import com.ngocrong.security.multilayer.challenges.ECCChallenge;
import com.ngocrong.security.multilayer.challenges.ECCPoint;
import com.ngocrong.security.multilayer.challenges.PairingChallenge;
import com.ngocrong.security.multilayer.challenges.VRFChallenge;
import com.ngocrong.security.multilayer.challenges.ZKChallenge;
import com.ngocrong.security.multilayer.data.ChallengeData;
import com.ngocrong.security.multilayer.responses.CommitmentResponse;
import com.ngocrong.security.multilayer.responses.ECCResponse;
import com.ngocrong.security.multilayer.responses.PairingResponse;
import com.ngocrong.security.multilayer.responses.VRFResponse;
import com.ngocrong.security.multilayer.responses.ZKResponse;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

/**
 * Hệ thống xác thực mật mã học đa lớp bảo mật cao Triển khai 5 lớp xác thực
 * tuần tự với độ phức tạp mật mã học cao
 */
public class MultiLayerCryptoSystem {

    // Constants
    private static final int PRIME_BITS = 512;
    private static final int VRF_PROOF_BITS = 256;
    private static final int COMMITMENT_LAYERS = 1;
    // Bit-size riêng cho Commitment để giảm kích thước/thời gian
    private static final int COMMITMENT_BITS = 160;
    private static final int ECC_OPERATIONS = 3;

    // Secure random generator
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    // Challenge duy nhất của server
    private static ChallengeData SERVER_CHALLENGE_DATA;
    private static ChallengeResponse SERVER_CHALLENGE_RESPONSE;

    // ECC curve parameters (NIST P-256 equivalent)
    private static final BigInteger ECC_P = new BigInteger("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF", 16);
    private static final BigInteger ECC_A = new BigInteger("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", 16);
    private static final BigInteger ECC_B = new BigInteger("5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B", 16);
    private static final BigInteger ECC_GX = new BigInteger("6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296", 16);
    private static final BigInteger ECC_GY = new BigInteger("4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5", 16);

    public static void init() {
        SERVER_CHALLENGE_DATA = generateChallengeData();
        SERVER_CHALLENGE_RESPONSE = new ChallengeResponse(
                SERVER_CHALLENGE_DATA.challengeId,
                SERVER_CHALLENGE_DATA.timestamp,
                SERVER_CHALLENGE_DATA.vrfChallenge,
                SERVER_CHALLENGE_DATA.commitmentChallenge,
                SERVER_CHALLENGE_DATA.eccChallenge,
                SERVER_CHALLENGE_DATA.pairingChallenge,
                SERVER_CHALLENGE_DATA.zkChallenge
        );
    }

    /**
     * Lấy challenge của server đã khởi tạo sẵn
     */
    public static ChallengeResponse getChallenge() {
        return SERVER_CHALLENGE_RESPONSE;
    }

    /**
     * Khởi tạo challenge duy nhất cho server
     */
    private static ChallengeData generateChallengeData() {
        try {
            String challengeId = UUID.randomUUID().toString();
            long timestamp = System.currentTimeMillis();

            VRFChallenge vrfChallenge = createVRFChallenge();
            CommitmentChallenge commitmentChallenge = createCommitmentChallenge();
            ECCChallenge eccChallenge = createECCChallenge();
            PairingChallenge pairingChallenge = createPairingChallenge();
            ZKChallenge zkChallenge = createZKChallenge();

            return new ChallengeData(
                    challengeId, timestamp, vrfChallenge, commitmentChallenge,
                    eccChallenge, pairingChallenge, zkChallenge
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create server challenge", e);
        }
    }

    /**
     * Xác minh response từ client
     */
    public static VerificationResult verifyResponse(String challengeId, ClientResponse response) {
        try {
            if (!SERVER_CHALLENGE_DATA.challengeId.equals(challengeId)) {
                return new VerificationResult(false, "Challenge not found or expired");
            }

            // MultiLayerLogger.log("verify: start challengeId=" + challengeId);
            // Xác minh từng lớp
            if (!verifyVRFLayer(SERVER_CHALLENGE_DATA.vrfChallenge, response.vrfResponse)) {
                // MultiLayerLogger.log("verify: VRF failed");
                return new VerificationResult(false, "VRF verification failed");
            }

            if (!verifyCommitmentLayer(SERVER_CHALLENGE_DATA.commitmentChallenge, response.commitmentResponse)) {
                // MultiLayerLogger.log("verify: Commitment failed");
                return new VerificationResult(false, "Commitment verification failed");
            }

            if (!verifyECCLayer(SERVER_CHALLENGE_DATA.eccChallenge, response.eccResponse)) {
                // MultiLayerLogger.log("verify: ECC failed");
                return new VerificationResult(false, "ECC verification failed");
            }

            if (!verifyPairingLayer(SERVER_CHALLENGE_DATA.pairingChallenge, response.pairingResponse)) {
                // MultiLayerLogger.log("verify: Pairing failed");
                return new VerificationResult(false, "Pairing verification failed");
            }

            if (!verifyZKLayer(SERVER_CHALLENGE_DATA.zkChallenge, response.zkResponse)) {
                // MultiLayerLogger.log("verify: ZK failed");
                return new VerificationResult(false, "ZK verification failed");
            }

            // MultiLayerLogger.log("verify: all layers OK");
            return new VerificationResult(true, "All layers verified successfully");

        } catch (Exception e) {
            // MultiLayerLogger.log("verify: error " + e);
            return new VerificationResult(false, "Verification error: " + e.getMessage());
        }
    }

    // ==================== LỚP 1: VRF (Verifiable Random Function) ====================
    private static VRFChallenge createVRFChallenge() {
        // Sinh số nguyên tố an toàn 2048-bit
        //System.err.println("createChallenge2 - 1");
        BigInteger p = generateSafePrime(PRIME_BITS);
        //System.err.println("createChallenge2 - 2");

        BigInteger q = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);
        //System.err.println("createChallenge2 - 3");

        // Sinh generator g và challenge x
        BigInteger g = findGenerator(p, q);
        //System.err.println("createChallenge2 - 4");

        BigInteger x = new BigInteger(VRF_PROOF_BITS, SECURE_RANDOM).mod(q);
        //System.err.println("createChallenge2 - 5");

        // Tính y = g^x mod p
        BigInteger y = g.modPow(x, p);
        //System.err.println("createChallenge2 - 6");

        return new VRFChallenge(p, q, g, y, x);
    }

    private static boolean verifyVRFLayer(VRFChallenge challenge, VRFResponse response) {
        try {
            BigInteger p = challenge.p;
            BigInteger q = challenge.q;
            BigInteger g = challenge.g;
            BigInteger y = challenge.y;

            // MultiLayerLogger.log("VRF_verify: p=" + p);
            // MultiLayerLogger.log("VRF_verify: q=" + q);
            // MultiLayerLogger.log("VRF_verify: g=" + g);
            // MultiLayerLogger.log("VRF_verify: y=" + y);
            // MultiLayerLogger.log("VRF_verify: proof=" + response.proof);

            // Xác minh đơn giản: proof phải bằng x, do y = g^x mod p
            BigInteger leftSide = g.modPow(response.proof, p);
            // MultiLayerLogger.log("VRF_verify: g^proof mod p = " + leftSide);
            // MultiLayerLogger.log("VRF_verify: y = " + y);
            // MultiLayerLogger.log("VRF_verify: equals = " + leftSide.equals(y));

            return leftSide.equals(y);

        } catch (Exception e) {
            // MultiLayerLogger.log("VRF_verify: error " + e);
            return false;
        }
    }

    // ==================== LỚP 2: COMMITMENT ====================
    private static CommitmentChallenge createCommitmentChallenge() {
        BigInteger p = generateSafePrime(COMMITMENT_BITS);
        BigInteger q = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);
        BigInteger g = findGenerator(p, q);

        BigInteger[] challenges = new BigInteger[COMMITMENT_LAYERS];
        BigInteger[] commitments = new BigInteger[COMMITMENT_LAYERS];

        for (int i = 0; i < COMMITMENT_LAYERS; i++) {
            challenges[i] = new BigInteger(PRIME_BITS, SECURE_RANDOM).mod(q);
            commitments[i] = g.modPow(challenges[i], p);
        }

        return new CommitmentChallenge(p, q, g, challenges, commitments);
    }

    private static boolean verifyCommitmentLayer(CommitmentChallenge challenge, CommitmentResponse response) {
        try {
            BigInteger p = challenge.p;
            BigInteger g = challenge.g;

            // MultiLayerLogger.log("Commitment_verify: p=" + p);
            // MultiLayerLogger.log("Commitment_verify: g=" + g);
            // MultiLayerLogger.log("Commitment_verify: proof[0]=" + response.proofs[0]);
            // MultiLayerLogger.log("Commitment_verify: commitment[0]=" + challenge.commitments[0]);

            for (int i = 0; i < COMMITMENT_LAYERS; i++) {
                BigInteger expectedCommitment = g.modPow(response.proofs[i], p);
                // MultiLayerLogger.log("Commitment_verify: g^proof[" + i + "] mod p = " + expectedCommitment);
                // MultiLayerLogger.log("Commitment_verify: commitment[" + i + "] = " + challenge.commitments[i]);
                // MultiLayerLogger.log("Commitment_verify: equals[" + i + "] = " + expectedCommitment.equals(challenge.commitments[i]));
                if (!expectedCommitment.equals(challenge.commitments[i])) {
                    // MultiLayerLogger.log("Commitment_verify: failed at index " + i);
                    return false;
                }
            }

            // MultiLayerLogger.log("Commitment_verify: success");
            return true;

        } catch (Exception e) {
            // MultiLayerLogger.log("Commitment_verify: error " + e);
            return false;
        }
    }

    // ==================== LỚP 3: ECC ====================
    private static ECCChallenge createECCChallenge() {
        BigInteger[] scalars = new BigInteger[ECC_OPERATIONS];
        ECCPoint[] points = new ECCPoint[ECC_OPERATIONS];

        for (int i = 0; i < ECC_OPERATIONS; i++) {
            scalars[i] = new BigInteger(256, SECURE_RANDOM).mod(ECC_P);
            points[i] = new ECCPoint(ECC_GX, ECC_GY);
        }

        return new ECCChallenge(ECC_P, ECC_A, ECC_B, ECC_GX, ECC_GY, scalars, points);
    }

    private static boolean verifyECCLayer(ECCChallenge challenge, ECCResponse response) {
        try {
            // MultiLayerLogger.log("ECC_verify: p=" + challenge.p);
            // MultiLayerLogger.log("ECC_verify: a=" + challenge.a);
            // MultiLayerLogger.log("ECC_verify: b=" + challenge.b);
            // MultiLayerLogger.log("ECC_verify: gx=" + challenge.gx);
            // MultiLayerLogger.log("ECC_verify: gy=" + challenge.gy);
            // MultiLayerLogger.log("ECC_verify: scalars[0]=" + challenge.scalars[0]);
            // MultiLayerLogger.log("ECC_verify: points[0].x=" + challenge.points[0].x);
            // MultiLayerLogger.log("ECC_verify: points[0].y=" + challenge.points[0].y);
            // MultiLayerLogger.log("ECC_verify: response[0].x=" + response.results[0].x);
            // MultiLayerLogger.log("ECC_verify: response[0].y=" + response.results[0].y);

            // Verify ECC operations
            for (int i = 0; i < ECC_OPERATIONS; i++) {
                ECCPoint expected = scalarMultiply(challenge.points[i], challenge.scalars[i], challenge.p, challenge.a, challenge.b);
                // MultiLayerLogger.log("ECC_verify: expected[" + i + "].x=" + expected.x);
                // MultiLayerLogger.log("ECC_verify: expected[" + i + "].y=" + expected.y);
                // MultiLayerLogger.log("ECC_verify: equals[" + i + "]=" + expected.equals(response.results[i]));
                if (!expected.equals(response.results[i])) {
                    // MultiLayerLogger.log("ECC_verify: failed at index " + i);
                    return false;
                }
            }

            // MultiLayerLogger.log("ECC_verify: success");
            return true;

        } catch (Exception e) {
            // MultiLayerLogger.log("ECC_verify: error " + e);
            return false;
        }
    }

    // ==================== LỚP 4: PAIRING (Mô phỏng) ====================
    private static PairingChallenge createPairingChallenge() {
        BigInteger[] group1Elements = new BigInteger[2];
        BigInteger[] group2Elements = new BigInteger[2];

        for (int i = 0; i < 2; i++) {
            group1Elements[i] = new BigInteger(256, SECURE_RANDOM);
            group2Elements[i] = new BigInteger(256, SECURE_RANDOM);
        }

        return new PairingChallenge(group1Elements, group2Elements);
    }

    private static boolean verifyPairingLayer(PairingChallenge challenge, PairingResponse response) {
        try {
            // Mô phỏng pairing verification
            // MultiLayerLogger.log("Pairing_verify: group1[0]=" + challenge.group1Elements[0]);
            // MultiLayerLogger.log("Pairing_verify: group1[1]=" + challenge.group1Elements[1]);
            // MultiLayerLogger.log("Pairing_verify: group2[0]=" + challenge.group2Elements[0]);
            // MultiLayerLogger.log("Pairing_verify: group2[1]=" + challenge.group2Elements[1]);
            // MultiLayerLogger.log("Pairing_verify: response=" + response.pairingResult);

            BigInteger result1 = challenge.group1Elements[0].multiply(challenge.group1Elements[1]);
            BigInteger result2 = challenge.group2Elements[0].multiply(challenge.group2Elements[1]);
            BigInteger expected = result1.add(result2);

            // MultiLayerLogger.log("Pairing_verify: result1=" + result1);
            // MultiLayerLogger.log("Pairing_verify: result2=" + result2);
            // MultiLayerLogger.log("Pairing_verify: expected=" + expected);
            // MultiLayerLogger.log("Pairing_verify: equals=" + response.pairingResult.equals(expected));

            return response.pairingResult.equals(expected);

        } catch (Exception e) {
            // MultiLayerLogger.log("Pairing_verify: error " + e);
            return false;
        }
    }

    // ==================== LỚP 5: ZERO-KNOWLEDGE ====================
    private static ZKChallenge createZKChallenge() {
        BigInteger p = generateSafePrime(PRIME_BITS);
        BigInteger q = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);
        BigInteger g = findGenerator(p, q);
        BigInteger h = findGenerator(p, q);

        BigInteger secret = new BigInteger(PRIME_BITS, SECURE_RANDOM).mod(q);
        BigInteger commitment = g.modPow(secret, p);

        return new ZKChallenge(p, q, g, h, commitment, secret);
    }

    private static boolean verifyZKLayer(ZKChallenge challenge, ZKResponse response) {
        try {
            BigInteger p = challenge.p;
            BigInteger g = challenge.g;
            BigInteger h = challenge.h;

            // MultiLayerLogger.log("ZK_verify: p=" + p);
            // MultiLayerLogger.log("ZK_verify: g=" + g);
            // MultiLayerLogger.log("ZK_verify: h=" + h);
            // MultiLayerLogger.log("ZK_verify: commitment=" + challenge.commitment);
            // MultiLayerLogger.log("ZK_verify: secret=" + challenge.secret);
            // MultiLayerLogger.log("ZK_verify: proof=" + response.proof);
            // MultiLayerLogger.log("ZK_verify: challenge=" + response.challenge);

            // Verify ZK proof
            BigInteger leftSide = g.modPow(response.proof, p);
            BigInteger rightSide = challenge.commitment.multiply(h.modPow(response.challenge, p)).mod(p);

            // MultiLayerLogger.log("ZK_verify: leftSide=" + leftSide);
            // MultiLayerLogger.log("ZK_verify: rightSide=" + rightSide);
            // MultiLayerLogger.log("ZK_verify: equals=" + leftSide.equals(rightSide));

            return leftSide.equals(rightSide);

        } catch (Exception e) {
            // MultiLayerLogger.log("ZK_verify: error " + e);
            return false;
        }
    }

    // ==================== HELPER METHODS ====================
    private static BigInteger generateSafePrime(int bits) {
        BigInteger p;
        do {
            p = BigInteger.probablePrime(bits, SECURE_RANDOM);
        } while (!p.subtract(BigInteger.ONE).divide(BigInteger.TWO).isProbablePrime(100));
        return p;
    }

    private static BigInteger findGenerator(BigInteger p, BigInteger q) {
        BigInteger g;
        do {
            g = new BigInteger(p.bitLength(), SECURE_RANDOM).mod(p);
        } while (g.equals(BigInteger.ZERO) || g.equals(BigInteger.ONE)
                || !g.modPow(q, p).equals(BigInteger.ONE));
        return g;
    }

    private static ECCPoint scalarMultiply(ECCPoint point, BigInteger scalar, BigInteger p, BigInteger a, BigInteger b) {
        // Simplified ECC scalar multiplication
        BigInteger x = point.x.modPow(scalar, p);
        BigInteger y = point.y.modPow(scalar, p);
        return new ECCPoint(x, y);
    }

}
