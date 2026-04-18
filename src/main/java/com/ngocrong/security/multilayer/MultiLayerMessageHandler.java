package com.ngocrong.security.multilayer;

import com.ngocrong.network.Message;
import com.ngocrong.network.FastDataInputStream;
import com.ngocrong.network.FastDataOutputStream;
import com.ngocrong.network.Session;
import com.ngocrong.consts.Cmd;
import com.ngocrong.security.multilayer.challenges.CommitmentChallenge;
import com.ngocrong.security.multilayer.challenges.ECCChallenge;
import com.ngocrong.security.multilayer.challenges.ECCPoint;
import com.ngocrong.security.multilayer.challenges.PairingChallenge;
import com.ngocrong.security.multilayer.challenges.VRFChallenge;
import com.ngocrong.security.multilayer.challenges.ZKChallenge;
import com.ngocrong.security.multilayer.responses.CommitmentResponse;
import com.ngocrong.security.multilayer.responses.ECCResponse;
import com.ngocrong.security.multilayer.responses.PairingResponse;
import com.ngocrong.security.multilayer.responses.VRFResponse;
import com.ngocrong.security.multilayer.responses.ZKResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.math.BigInteger;

/**
 * Message Handler cho hệ thống xác thực đa lớp
 */
public class MultiLayerMessageHandler {

    private static final Logger logger = Logger.getLogger(MultiLayerMessageHandler.class.getName());

    // Command constants
    public static final byte MULTI_LAYER_CHALLENGE = 59;
    public static final byte MULTI_LAYER_RESPONSE = 60;

    /**
     * Xử lý message từ client
     */
    public static void handleMessage(Session session, Message message) {
        try {
            byte command = message.getCommand();

            switch (command) {
                case MULTI_LAYER_CHALLENGE:
                    //System.err.println("clientRequest");
                    handleChallengeRequest(session);
                    break;

                case MULTI_LAYER_RESPONSE:
                    //System.err.println("clientResponse");
                    handleResponse(session, message);
                    break;

                default:
                    logger.warning("Unknown multi-layer command: " + command);
                    break;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error handling multi-layer message", e);
        }
    }

    /**
     * Xử lý yêu cầu challenge từ client
     */
    private static void handleChallengeRequest(Session session) {
        try {
            //System.err.println("req1");
            long startTime = System.currentTimeMillis();
            //System.err.println("req2");

            // Lấy challenge đã được server khởi tạo sẵn
            ChallengeResponse challenge = MultiLayerCryptoSystem.getChallenge();
            // MultiLayerLogger.log("sendChallenge: id=" + challenge.challengeId + ", ts=" + challenge.timestamp);
            //System.err.println("req3");

            // Gửi challenge về client
            sendChallenge(session, challenge);
            //System.err.println("req4");

            long endTime = System.currentTimeMillis();
           // logger.info("Challenge created and sent in " + (endTime - startTime) + "ms");
            // MultiLayerLogger.log("sendChallenge: done in " + (endTime - startTime) + "ms");
            //System.err.println("req5");

        } catch (Exception e) {
            e.printStackTrace();
        //    logger.log(Level.SEVERE, "Error creating challenge", e);
            sendError(session, "Failed to create challenge");
        }
    }

    /**
     * Xử lý response từ client
     */
    private static void handleResponse(Session session, Message message) {
        try {
            long startTime = System.currentTimeMillis();

            // Đọc response từ client
            ClientResponse response = readClientResponse(message);
            // MultiLayerLogger.log("handleResponse: recv for id=" + response.challengeId);

            // Xác minh response
            VerificationResult result = MultiLayerCryptoSystem.verifyResponse(
                    response.challengeId, response);
            session.resultKey = result;
            // MultiLayerLogger.log("verifyResult: success=" + result.success + ", msg=" + result.message + ", time=" + result.verificationTime + "ms");
            // Gửi kết quả về client
            //sendVerificationResult(session, result);

            long endTime = System.currentTimeMillis();
       //     logger.info("Response verified in " + (endTime - startTime) + "ms. Success: " + result.success);

        } catch (Exception e) {
          //  logger.log(Level.SEVERE, "Error handling response", e);
            sendError(session, "Failed to verify response");
        }
    }

    /**
     * Gửi challenge về client
     */
    private static void sendChallenge(Session session, ChallengeResponse challenge) {
        try {
         //   //System.err.println("Send challenge");
            Message response = new Message(MULTI_LAYER_CHALLENGE);
            FastDataOutputStream dos = response.writer();

            // Gửi challenge ID
            dos.writeUTF(challenge.challengeId);
            dos.writeLong(challenge.timestamp);

            // Gửi VRF challenge
            writeVRFChallenge(dos, challenge.vrfChallenge);

            // Gửi Commitment challenge
            writeCommitmentChallenge(dos, challenge.commitmentChallenge);

            // Gửi ECC challenge
            writeECCChallenge(dos, challenge.eccChallenge);

            // Gửi Pairing challenge
            writePairingChallenge(dos, challenge.pairingChallenge);

            // Gửi ZK challenge
            writeZKChallenge(dos, challenge.zkChallenge);

            dos.flush();
            session.sendMessage(response);
            response.cleanup();
        //    //System.err.println("Send challenge success");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending challenge", e);
        }
    }

    /**
     * Gửi kết quả xác minh về client
     */
    private static void sendVerificationResult(Session session, VerificationResult result) {
        try {
            Message response = new Message(MULTI_LAYER_RESPONSE);
            FastDataOutputStream dos = response.writer();

            dos.writeBoolean(result.success);
            dos.writeUTF(result.message);
            dos.writeLong(result.verificationTime);

            dos.flush();
            session.sendMessage(response);
            response.cleanup();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending verification result", e);
        }
    }

    /**
     * Gửi thông báo lỗi về client
     */
    private static void sendError(Session session, String errorMessage) {
        try {
            Message response = new Message(MULTI_LAYER_RESPONSE);
            FastDataOutputStream dos = response.writer();

            dos.writeBoolean(false);
            dos.writeUTF(errorMessage);
            dos.writeLong(System.currentTimeMillis());

            dos.flush();
            session.sendMessage(response);
            response.cleanup();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending error message", e);
        }
    }

    // ==================== WRITE METHODS ====================
    private static void writeVRFChallenge(FastDataOutputStream dos, VRFChallenge challenge) throws IOException {
        dos.writeUTF(challenge.p.toString());
        dos.writeUTF(challenge.q.toString());
        dos.writeUTF(challenge.g.toString());
        dos.writeUTF(challenge.y.toString());
        // Gửi thêm secret x để client tính VRF proof
        dos.writeUTF(challenge.x.toString());
    }

    private static void writeCommitmentChallenge(FastDataOutputStream dos, CommitmentChallenge challenge) throws IOException {
        dos.writeUTF(challenge.p.toString());
        dos.writeUTF(challenge.q.toString());
        dos.writeUTF(challenge.g.toString());
        dos.writeInt(challenge.challenges.length);
        for (int i = 0; i < challenge.challenges.length; i++) {
            dos.writeUTF(challenge.challenges[i].toString());
        }
        for (int i = 0; i < challenge.commitments.length; i++) {
            dos.writeUTF(challenge.commitments[i].toString());
        }
    }

    private static void writeECCChallenge(FastDataOutputStream dos, ECCChallenge challenge) throws IOException {
        dos.writeUTF(challenge.p.toString());
        dos.writeUTF(challenge.a.toString());
        dos.writeUTF(challenge.b.toString());
        dos.writeUTF(challenge.gx.toString());
        dos.writeUTF(challenge.gy.toString());
        dos.writeInt(challenge.scalars.length);
        for (int i = 0; i < challenge.scalars.length; i++) {
            dos.writeUTF(challenge.scalars[i].toString());
        }
        for (int i = 0; i < challenge.points.length; i++) {
            dos.writeUTF(challenge.points[i].x.toString());
            dos.writeUTF(challenge.points[i].y.toString());
        }
    }

    private static void writePairingChallenge(FastDataOutputStream dos, PairingChallenge challenge) throws IOException {
        dos.writeInt(challenge.group1Elements.length);
        for (int i = 0; i < challenge.group1Elements.length; i++) {
            dos.writeUTF(challenge.group1Elements[i].toString());
        }
        dos.writeInt(challenge.group2Elements.length);
        for (int i = 0; i < challenge.group2Elements.length; i++) {
            dos.writeUTF(challenge.group2Elements[i].toString());
        }
    }

    private static void writeZKChallenge(FastDataOutputStream dos, ZKChallenge challenge) throws IOException {
        dos.writeUTF(challenge.p.toString());
        dos.writeUTF(challenge.q.toString());
        dos.writeUTF(challenge.g.toString());
        dos.writeUTF(challenge.h.toString());
        dos.writeUTF(challenge.commitment.toString());
        // Gửi thêm secret để client tính ZK proof nhanh
        dos.writeUTF(challenge.secret.toString());
    }

    // ==================== READ METHODS ====================
    private static ClientResponse readClientResponse(Message message) throws IOException {
        FastDataInputStream dis = message.reader();

        String challengeId = dis.readUTF();

        // Đọc VRF response
        VRFResponse vrfResponse = readVRFResponse(dis);

        // Đọc Commitment response
        CommitmentResponse commitmentResponse = readCommitmentResponse(dis);

        // Đọc ECC response
        ECCResponse eccResponse = readECCResponse(dis);

        // Đọc Pairing response
        PairingResponse pairingResponse = readPairingResponse(dis);

        // Đọc ZK response
        ZKResponse zkResponse = readZKResponse(dis);

        return new ClientResponse(challengeId, vrfResponse, commitmentResponse,
                eccResponse, pairingResponse, zkResponse);
    }

    private static VRFResponse readVRFResponse(FastDataInputStream dis) throws IOException {
        String proofStr = dis.readUTF();
        BigInteger proof = new BigInteger(proofStr);
        return new VRFResponse(proof);
    }

    private static CommitmentResponse readCommitmentResponse(FastDataInputStream dis) throws IOException {
        int count = dis.readInt();
        BigInteger[] proofs = new BigInteger[count];
        for (int i = 0; i < count; i++) {
            String proofStr = dis.readUTF();
            proofs[i] = new BigInteger(proofStr);
        }
        return new CommitmentResponse(proofs);
    }

    private static ECCResponse readECCResponse(FastDataInputStream dis) throws IOException {
        int count = dis.readInt();
        ECCPoint[] results = new ECCPoint[count];
        for (int i = 0; i < count; i++) {
            String xStr = dis.readUTF();
            String yStr = dis.readUTF();
            BigInteger x = new BigInteger(xStr);
            BigInteger y = new BigInteger(yStr);
            results[i] = new ECCPoint(x, y);
        }
        return new ECCResponse(results);
    }

    private static PairingResponse readPairingResponse(FastDataInputStream dis) throws IOException {
        String resultStr = dis.readUTF();
        BigInteger result = new BigInteger(resultStr);
        return new PairingResponse(result);
    }

    private static ZKResponse readZKResponse(FastDataInputStream dis) throws IOException {
        String proofStr = dis.readUTF();
        String challengeStr = dis.readUTF();
        BigInteger proof = new BigInteger(proofStr);
        BigInteger challenge = new BigInteger(challengeStr);
        return new ZKResponse(proof, challenge);
    }
}
