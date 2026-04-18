package com.ngocrong.network;

import _HunrProvision.HoangAnhDz;
import com.ngocrong.model.VoiceMessage;
import com.ngocrong.model.VoiceMessageType;
import com.ngocrong.user.Player;
import com.ngocrong.server.voice.VoiceSessionManager;
import com.ngocrong.server.SessionManager;
import com.ngocrong.util.Utils;
import com.ngocrong.map.tzone.Zone;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class VoiceMessageService {

    private static VoiceMessageService instance;

    // Statistics
    private AtomicLong totalVoiceMessagesSent = new AtomicLong(0);
    private AtomicLong totalWorldChatMessages = new AtomicLong(0);
    private AtomicLong totalPrivateChatMessages = new AtomicLong(0);
    private AtomicLong totalAudioDataProcessed = new AtomicLong(0);

    // Rate limiting
    private ConcurrentHashMap<String, Long> lastVoiceMessageTime = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> voiceMessageCount = new ConcurrentHashMap<>();
    private static final long VOICE_MESSAGE_COOLDOWN = 2000; // 2 seconds
    private static final int MAX_VOICE_MESSAGES_PER_MINUTE = 10;

    // Voice message history for recent messages
    private ConcurrentLinkedQueue<VoiceMessage> recentVoiceMessages = new ConcurrentLinkedQueue<>();
    private static final int MAX_RECENT_MESSAGES = 100;

    // Limits
    private static final int MAX_AUDIO_DATA_SIZE = 1024 * 1024; // 1MB
    private static final float MAX_DURATION = 30.0f; // 30 seconds
    private static final int MIN_AUDIO_DATA_SIZE = 100; // 100 bytes minimum

    public static VoiceMessageService gI() {
        if (instance == null) {
            instance = new VoiceMessageService();
        }
        return instance;
    }

    private VoiceMessageService() {
        // Initialize service
        //System.err.println("VoiceMessageService initialized");
    }

    public void processWorldChatVoiceMessage(Player sender, Message message) {
        try {
            VoiceMessage voiceMsg = parseVoiceMessage(message, VoiceMessageType.WORLD_CHAT);
            if (voiceMsg == null) {
                return;
            }

            voiceMsg.setSenderName(sender.name);
            voiceMsg.setSenderId(sender.id);

            if (!validateVoiceMessage(voiceMsg, sender)) {
                return;
            }

            // Update statistics
            totalVoiceMessagesSent.incrementAndGet();
            totalWorldChatMessages.incrementAndGet();
            totalAudioDataProcessed.addAndGet(voiceMsg.getDataSize());

            // Add to recent messages
            addToRecentMessages(voiceMsg);

            // Broadcast to all players in the server
            broadcastWorldChatVoiceMessage(voiceMsg);

            //System.err.println(String.format("World chat voice message processed: %s", voiceMsg));

        } catch (Exception e) {
            // HoangAnhDz.logError(e);
            sendErrorMessage(sender, "Failed to process voice message");
        }
    }

    public void processPrivateChatVoiceMessage(Player sender, Message message) {
        try {
            VoiceMessage voiceMsg = parseVoiceMessage(message, VoiceMessageType.PRIVATE_CHAT);
            if (voiceMsg == null) {
                return;
            }

            voiceMsg.setSenderName(sender.name);
            voiceMsg.setSenderId(sender.id);

            if (!validateVoiceMessage(voiceMsg, sender)) {
                return;
            }

            // Find receiver
            Player receiver = SessionManager._findPlayer(voiceMsg.getReceiverName());
            if (receiver == null) {
                sendErrorMessage(sender, "Player not found: " + voiceMsg.getReceiverName());
                return;
            }

            if (receiver.equals(sender)) {
                sendErrorMessage(sender, "Cannot send voice message to yourself");
                return;
            }

            // Update statistics
            totalVoiceMessagesSent.incrementAndGet();
            totalPrivateChatMessages.incrementAndGet();
            totalAudioDataProcessed.addAndGet(voiceMsg.getDataSize());

            // Add to recent messages
            addToRecentMessages(voiceMsg);

            // Send to receiver
            sendPrivateChatVoiceMessage(voiceMsg, sender, receiver);

            //System.err.println(String.format("Private chat voice message processed: %s", voiceMsg));

        } catch (Exception e) {
            // HoangAnhDz.logError(e);
            sendErrorMessage(sender, "Failed to process voice message");
        }
    }

    public void processMapChatVoiceMessage(Player sender, Message message) {
        try {
            VoiceMessage voiceMsg = parseVoiceMessage(message, VoiceMessageType.MAP_CHAT);
            if (voiceMsg == null) {
                return;
            }

            voiceMsg.setSenderName(sender.name);
            voiceMsg.setSenderId(sender.id);

            if (!validateVoiceMessage(voiceMsg, sender)) {
                return;
            }

            totalVoiceMessagesSent.incrementAndGet();
            totalAudioDataProcessed.addAndGet(voiceMsg.getDataSize());
            addToRecentMessages(voiceMsg);

            broadcastMapVoiceMessage(voiceMsg, sender.zone);

            //System.err.println(String.format("Map chat voice message processed: %s", voiceMsg));

        } catch (Exception e) {
            // HoangAnhDz.logError(e);
            sendErrorMessage(sender, "Failed to process voice message");
        }
    }

    private VoiceMessage parseVoiceMessage(Message message, VoiceMessageType type) {
        try {
            String receiverName = null;
            if (type == VoiceMessageType.PRIVATE_CHAT) {
                receiverName = message.reader().readUTF();
            }

            String senderName = message.reader().readUTF();
            float duration = message.reader().readFloat();
            long timestamp = message.reader().readLong();
            int audioDataLength = message.reader().readInt();

            if (audioDataLength <= 0 || audioDataLength > MAX_AUDIO_DATA_SIZE) {
                // HoangAnhDz.logError("Invalid audio data length: " + audioDataLength);
                return null;
            }

            byte[] audioData = new byte[audioDataLength];
            message.reader().readFully(audioData);
            // Filter noise on the server side as an extra safeguard
            audioData = Utils.applyNoiseGate(audioData, 0.02f);

            VoiceMessage voiceMsg = new VoiceMessage(audioData, 0, senderName, receiverName, duration, type);
            voiceMsg.setTimestamp(timestamp);

            return voiceMsg;

        } catch (IOException e) {
            // HoangAnhDz.logError(e);
            return null;
        }
    }

    private boolean validateVoiceMessage(VoiceMessage voiceMsg, Player sender) {
        // Basic validation
        if (!voiceMsg.isValid()) {
            sendErrorMessage(sender, "Invalid voice message data");
            return false;
        }

        // Size validation
        if (voiceMsg.getDataSize() > MAX_AUDIO_DATA_SIZE) {
            sendErrorMessage(sender, "Voice message too large");
            return false;
        }

        if (voiceMsg.getDataSize() < MIN_AUDIO_DATA_SIZE) {
            sendErrorMessage(sender, "Voice message too small");
            return false;
        }

        // Duration validation
        if (voiceMsg.getDuration() > MAX_DURATION) {
            sendErrorMessage(sender, "Voice message too long");
            return false;
        }

        if (voiceMsg.getDuration() < 0.1f) {
            sendErrorMessage(sender, "Voice message too short");
            return false;
        }

        // Rate limiting
        String playerName = sender.name;
        long currentTime = System.currentTimeMillis();

        // Check cooldown
        Long lastTime = lastVoiceMessageTime.get(playerName);
        if (lastTime != null && (currentTime - lastTime) < VOICE_MESSAGE_COOLDOWN) {
            sendErrorMessage(sender, "Please wait before sending another voice message");
            return false;
        }

        // Check rate limit (messages per minute)
        Integer count = voiceMessageCount.get(playerName);
        if (count != null && count >= MAX_VOICE_MESSAGES_PER_MINUTE) {
            sendErrorMessage(sender, "Voice message limit reached. Please wait.");
            return false;
        }

        // Update rate limiting data
        lastVoiceMessageTime.put(playerName, currentTime);
        voiceMessageCount.put(playerName, (count != null ? count : 0) + 1);

        return true;
    }

    private void broadcastWorldChatVoiceMessage(VoiceMessage voiceMsg) {
        try {
            Message msg = new Message(-58); // CMD_VOICE_RECEIVE
            writeVoiceMessageToMessage(msg, voiceMsg);

            // Broadcast to all players via voice channel
            com.ngocrong.server.voice.VoiceSessionManager.sendMessage(msg);

            msg.cleanup();

        } catch (Exception e) {
            // HoangAnhDz.logError(e);
        }
    }

    private void broadcastMapVoiceMessage(VoiceMessage voiceMsg, Zone zone) {
        try {
            Message msg = new Message(-58); // CMD_VOICE_RECEIVE
            writeVoiceMessageToMessage(msg, voiceMsg);

            for (Player p : zone.getPlayers()) {
                com.ngocrong.server.voice.VoiceSessionManager.sendMessage(p, msg);
            }

            msg.cleanup();
        } catch (Exception e) {
            // HoangAnhDz.logError(e);
        }
    }

    private void sendPrivateChatVoiceMessage(VoiceMessage voiceMsg, Player sender, Player receiver) {
        try {
            Message msg = new Message(-58); // CMD_VOICE_RECEIVE
            writeVoiceMessageToMessage(msg, voiceMsg);

            com.ngocrong.server.voice.VoiceSessionManager.sendMessage(sender, msg);
            com.ngocrong.server.voice.VoiceSessionManager.sendMessage(receiver, msg);

            msg.cleanup();

        } catch (Exception e) {
            // HoangAnhDz.logError(e);
        }
    }

    private void writeVoiceMessageToMessage(Message msg, VoiceMessage voiceMsg) throws IOException {
        msg.writer().writeByte(voiceMsg.getMessageType().getValue());
        //System.err.println("getMessageType :" + voiceMsg.getMessageType().getValue());
        msg.writer().writeUTF(voiceMsg.getSenderName());
        //System.err.println("getSenderName " + voiceMsg.getSenderName());
        msg.writer().writeInt(voiceMsg.getSenderId());
        //System.err.println("voiceMsg.getSenderId() " + voiceMsg.getSenderId());
        if (voiceMsg.getReceiverName() != null) {
            msg.writer().writeUTF(voiceMsg.getReceiverName());
            //System.err.println("voiceMsg.getReceiverName()" + voiceMsg.getReceiverName());
        } else {
            msg.writer().writeUTF("");
            //System.err.println();
        }

        msg.writer().writeFloat(voiceMsg.getDuration());
        //System.err.println("voiceMsg.getDuration() " + voiceMsg.getDuration());
        msg.writer().writeLong(voiceMsg.getTimestamp());
        //System.err.println("voiceMsg.getTimestamp()" + voiceMsg.getTimestamp());
        msg.writer().writeInt(voiceMsg.getDataSize());
        //System.err.println("voiceMsg.getDataSize()" + voiceMsg.getDataSize());
        msg.writer().write(voiceMsg.getAudioData());
        //System.err.println("voiceMsg.getAudioData()" + voiceMsg.getAudioData().length);
        msg.writer().flush();
        //System.err.println("FullData" + msg.getData().length);
    }

    private void sendErrorMessage(Player player, String errorMsg) {
        try {
            // Send error as a regular chat message
            Message msg = new Message(-41); // CMD_CHAT_MESSAGE
            msg.writer().writeUTF("System");
            msg.writer().writeUTF("[Voice Error] " + errorMsg);
            player.getSession().sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            // HoangAnhDz.logError(e);
        }
    }

    private void addToRecentMessages(VoiceMessage voiceMsg) {
        // Add metadata only (without audio data) to save memory
        recentVoiceMessages.offer(voiceMsg.createMetadataOnly());

        // Remove old messages
        while (recentVoiceMessages.size() > MAX_RECENT_MESSAGES) {
            recentVoiceMessages.poll();
        }
    }

    // Periodic cleanup method
    public void cleanup() {
        long currentTime = System.currentTimeMillis();

        // Clean up rate limiting data (older than 1 minute)
        lastVoiceMessageTime.entrySet().removeIf(entry
                -> currentTime - entry.getValue() > 60000);

        voiceMessageCount.entrySet().removeIf(entry
                -> currentTime - lastVoiceMessageTime.getOrDefault(entry.getKey(), 0L) > 60000);

        // Clean up old recent messages (older than 5 minutes)
        recentVoiceMessages.removeIf(msg -> msg.getAgeInSeconds() > 300);
    }

    // Statistics methods
    public long getTotalVoiceMessagesSent() {
        return totalVoiceMessagesSent.get();
    }

    public long getTotalWorldChatMessages() {
        return totalWorldChatMessages.get();
    }

    public long getTotalPrivateChatMessages() {
        return totalPrivateChatMessages.get();
    }

    public long getTotalAudioDataProcessed() {
        return totalAudioDataProcessed.get();
    }

    public int getRecentMessagesCount() {
        return recentVoiceMessages.size();
    }

    public String getStatistics() {
        return String.format(
                "Voice Message Statistics:\n"
                + "Total Messages: %d\n"
                + "World Chat: %d\n"
                + "Private Chat: %d\n"
                + "Audio Data Processed: %d bytes\n"
                + "Recent Messages: %d",
                getTotalVoiceMessagesSent(),
                getTotalWorldChatMessages(),
                getTotalPrivateChatMessages(),
                getTotalAudioDataProcessed(),
                getRecentMessagesCount()
        );
    }
}
