package com.ngocrong.security;

import java.math.BigInteger;

public class SimpleECC {

    public static class Point {

        public final BigInteger x;
        public final BigInteger y;
        public final boolean infinity;

        public Point() {
            this.x = BigInteger.ZERO;
            this.y = BigInteger.ZERO;
            this.infinity = true;
        }

        public Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
            this.infinity = false;
        }
    }

    private final BigInteger p;
    private final BigInteger a;
    private final BigInteger b;
    public final Point g;

    public SimpleECC(BigInteger p, BigInteger a, BigInteger b, Point g) {
        this.p = p;
        this.a = a;
        this.b = b;
        this.g = g;
    }

    // Helper method to ensure positive modular result (like C#)
    private BigInteger positiveMod(BigInteger value) {
        BigInteger result = value.mod(p);
        if (result.signum() < 0) {
            result = result.add(p);
        }
        return result;
    }

    public boolean isOnCurve(Point P) {
        if (P.infinity) {
            return true;
        }

    //System.err.println("=== CHECKING POINT ON CURVE ===");
    //System.err.println("Point: (" + P.x + ", " + P.y + ")");
    //System.err.println("Curve: y² = x³ + " + a + "x + " + b + " (mod " + p + ")");

        // Left side: y²
        BigInteger left = P.y.multiply(P.y).mod(p);
    //System.err.println("Left side (y²): " + left);

        // Right side: x³ + ax + b
        BigInteger x3 = P.x.multiply(P.x).multiply(P.x);
        BigInteger ax = a.multiply(P.x);
        BigInteger right = x3.add(ax).add(b);

    //System.err.println("x³: " + x3);
    //System.err.println("ax: " + ax);
    //System.err.println("b: " + b);
    //System.err.println("Right side before mod: " + right);

        // Apply modular arithmetic
        right = right.mod(p);
    //System.err.println("Right side after mod: " + right);

        // Check if negative and fix (like C#)
        if (right.signum() < 0) {
            right = right.add(p);
        //System.err.println("Right side after fixing negative: " + right);
        }

        boolean result = left.equals(right);
    //System.err.println("Comparison result: " + result);
    //System.err.println("=== END CURVE CHECK ===\n");

        return result;
    }

    public void testGeneratorPoint() {
    //System.err.println("=== TESTING GENERATOR POINT ===");
        boolean gOnCurve = isOnCurve(g);
    //System.err.println("Generator point on curve: " + gOnCurve);
    //System.err.println("=== END GENERATOR TEST ===\n");
    }

    public Point add(Point P, Point Q) {
        if (P.infinity) {
            return Q;
        }
        if (Q.infinity) {
            return P;
        }

        if (P.x.equals(Q.x)) {
            if (P.y.add(Q.y).mod(p).equals(BigInteger.ZERO)) {
                return new Point(); // Point at infinity
            }

            // Point doubling - match C# exactly
            BigInteger m = P.x.multiply(P.x).multiply(BigInteger.valueOf(3)).add(a);
            m = positiveMod(m);

            BigInteger denominator = P.y.multiply(BigInteger.valueOf(2));
            denominator = positiveMod(denominator);

            m = m.multiply(denominator.modInverse(p));
            m = positiveMod(m);

            BigInteger rx = m.multiply(m).subtract(P.x.multiply(BigInteger.valueOf(2)));
            BigInteger ry = m.multiply(P.x.subtract(rx)).subtract(P.y);

            // Ensure positive results like C#
            return new Point(positiveMod(rx), positiveMod(ry));
        } else {
            // Point addition
            BigInteger numerator = Q.y.subtract(P.y);
            BigInteger denominator = Q.x.subtract(P.x);

            numerator = positiveMod(numerator);
            denominator = positiveMod(denominator);

            BigInteger m = numerator.multiply(denominator.modInverse(p));
            m = positiveMod(m);

            BigInteger rx = m.multiply(m).subtract(P.x).subtract(Q.x);
            BigInteger ry = m.multiply(P.x.subtract(rx)).subtract(P.y);

            // Ensure positive results like C#
            return new Point(positiveMod(rx), positiveMod(ry));
        }
    }

    public Point multiply(Point P, BigInteger k) {
        Point result = new Point(); // Point at infinity
        Point addend = P;
        BigInteger n = k;

        while (n.compareTo(BigInteger.ZERO) > 0) {
            if (n.and(BigInteger.ONE).equals(BigInteger.ONE)) {
                result = add(result, addend);
            }
            addend = add(addend, addend);
            n = n.shiftRight(1);
        }
        return result;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getA() {
        return a;
    }

    public BigInteger getB() {
        return b;
    }
}
