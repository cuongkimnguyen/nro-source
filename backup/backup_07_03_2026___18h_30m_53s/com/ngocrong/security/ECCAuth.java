package com.ngocrong.security;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ECCAuth {

    private final SimpleECC ecc;
    private final BigInteger secret;
    private final SimpleECC.Point challengePoint;

    public ECCAuth(SimpleECC ecc) {
        this.ecc = ecc;
        ecc.testGeneratorPoint();
        SecureRandom rnd = new SecureRandom();

        // Use at least 256 bits of randomness for the secret
        this.secret = new BigInteger(256, rnd).mod(ecc.getP());
        this.challengePoint = ecc.multiply(ecc.g, secret);

        // DEBUG: In thông tin khởi tạo
    //System.err.println("=== ECC AUTH INITIALIZATION ===");
    //System.err.println("Curve parameters:");
    //System.err.println("p: " + ecc.getP());
    //System.err.println("a: " + ecc.getA());
    //System.err.println("b: " + ecc.getB());
    //System.err.println("Generator point G:");
    //System.err.println("gx: " + ecc.g.x);
    //System.err.println("gy: " + ecc.g.y);
    //System.err.println("Server secret key: " + secret);
    //System.err.println("Challenge point (secret * G):");
    //System.err.println("cx: " + challengePoint.x);
    //System.err.println("cy: " + challengePoint.y);
    //System.err.println("=== END INITIALIZATION ===\n");
    }

    public SimpleECC getCurve() {
        return ecc;
    }

    public SimpleECC.Point getChallengePoint() {
        return challengePoint;
    }

    public boolean verify(SimpleECC.Point R, SimpleECC.Point S) {
    //System.err.println("=== VERIFICATION PROCESS ===");
    //System.err.println("Received from client:");
    //System.err.println("R point:");
    //System.err.println("rx: " + R.x);
    //System.err.println("ry: " + R.y);
    //System.err.println("S point:");
    //System.err.println("sx: " + S.x);
    //System.err.println("sy: " + S.y);

        // Kiểm tra điểm có trên đường cong không
        boolean rOnCurve = ecc.isOnCurve(R);
        boolean sOnCurve = ecc.isOnCurve(S);
    //System.err.println("R on curve: " + rOnCurve);
    //System.err.println("S on curve: " + sOnCurve);

        if (!rOnCurve || !sOnCurve) {
        //System.err.println("VERIFICATION FAILED: Points not on curve");
        //System.err.println("=== END VERIFICATION ===\n");
            return false;
        }

        // Tính expected = secret * R
        SimpleECC.Point expected = ecc.multiply(R, secret);
    //System.err.println("Expected point (secret * R):");
    //System.err.println("expected_x: " + expected.x);
    //System.err.println("expected_y: " + expected.y);

        // So sánh
        boolean xMatch = expected.x.equals(S.x);
        boolean yMatch = expected.y.equals(S.y);
        boolean verified = xMatch && yMatch;

    //System.err.println("X coordinates match: " + xMatch);
    //System.err.println("Y coordinates match: " + yMatch);
    //System.err.println("VERIFICATION RESULT: " + verified);

        if (!verified) {
        //System.err.println("Expected vs Received:");
        //System.err.println("Expected X: " + expected.x);
        //System.err.println("Received X: " + S.x);
        //System.err.println("Expected Y: " + expected.y);
        //System.err.println("Received Y: " + S.y);
        }

    //System.err.println("=== END VERIFICATION ===\n");
        return verified;
    }
}
