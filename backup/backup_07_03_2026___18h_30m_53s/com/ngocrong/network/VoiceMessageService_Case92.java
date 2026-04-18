package com.ngocrong.network;

import _HunrProvision.HoangAnhDz;
import com.ngocrong.model.VoiceMessage;
import com.ngocrong.model.VoiceMessageType;
import com.ngocrong.user.Player;
import com.ngocrong.server.SessionManager;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class VoiceMessageService_Case92 {
    
    private static VoiceMessageService_Case92 instance;
    
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
    
    public static VoiceMessageService_Case92 gI() {
        if (instance == null) {
            instance = new VoiceMessageService_Case92();
        }
        return instance;
    }
    
    private VoiceMessageService_Case92() {
        System.err.println("VoiceMessageService_Case92 initialized");
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
            
            // Send via case 92 message system
            sendVoiceMessageViaCase92(voiceMsg, null, true);
            
            System.err.println(String.format("World chat voice message processed: %s", voiceMsg));
            
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
            
            // Send via case 92 message system
            sendVoiceMessageViaCase92(voiceMsg, receiver, false);
            
            System.err.println(String.format("Private chat voice message processed: %s", voiceMsg));
            
        } catch (Exception e) {
            // HoangAnhDz.logError(e);
            sendErrorMessage(sender, "Failed to process voice message");
        }
    }
    
    private void sendVoiceMessageViaCase92(VoiceMessage voiceMsg, Player specificReceiver, boolean isWorldChat) {
        try {
            // Create unique voice ID for Panel access
            String voiceId = voiceMsg.getSenderName() + "_" + voiceMsg.getTimestamp();
            
            // Send case 92 message (visible chat message)
            Message chatMsg = new Message((byte) 92);
            
            // Character name (sender)
            chatMsg.writer().writeUTF(voiceMsg.getSenderName());
            
            // Message content with voice indicator and ID
            String voiceText;
            if (isWorldChat) {
                voiceText = String.format("🎤 [Voice %.1fs] Click to play", voiceMsg.getDuration());
            } else {
                voiceText = String.format("🎤 [Private Voice %.1fs] Click to play", voiceMsg.getDuration());
            }
            // Embed voice ID in the message text for client recognition
            voiceText = "VOICE_MSG_ID:" + voiceId + "|" + voiceText;
            chatMsg.writer().writeUTF(voiceText);
            
            // Character appearance data - use special ID for voice messages
            chatMsg.writer().writeInt(-999); // Special charID for voice messages
            chatMsg.writer().writeShort((short) 0); // head
            chatMsg.writer().writeShort((short) 0); // body
            chatMsg.writer().writeShort((short) 0); // bag
            chatMsg.writer().writeShort((short) 0); // leg
            chatMsg.writer().writeByte(isWorldChat ? (byte) 1 : (byte) 2); // voice message type flag
            
            // Send chat message
            if (isWorldChat) {
                SessionManager.sendMessage(chatMsg);
            } else {
                specificReceiver.getSession().sendMessage(chatMsg);
            }
            
            chatMsg.cleanup();
            
            // Send voice data storage command (case -62)
            Message voiceDataMsg = new Message((byte) -62); // CMD_VOICE_DATA_STORE
            voiceDataMsg.writer().writeUTF(voiceId); // Unique ID
            voiceDataMsg.writer().writeByte(voiceMsg.getMessageType().getValue());
            voiceDataMsg.writer().writeUTF(voiceMsg.getSenderName());
            voiceDataMsg.writer().writeInt(voiceMsg.getSenderId());
            voiceDataMsg.writer().writeUTF(voiceMsg.getReceiverName() != null ? voiceMsg.getReceiverName() : "");
            voiceDataMsg.writer().writeFloat(voiceMsg.getDuration());
            voiceDataMsg.writer().writeLong(voiceMsg.getTimestamp());
            voiceDataMsg.writer().writeInt(voiceMsg.getDataSize());
            voiceDataMsg.writer().write(voiceMsg.getAudioData());
            
            // Send voice data
            if (isWorldChat) {
                SessionManager.sendMessage(voiceDataMsg);
            } else {
                specificReceiver.getSession().sendMessage(voiceDataMsg);
            }
            
            voiceDataMsg.cleanup();
            
        } catch (Exception e) {
            // HoangAnhDz.logError(e);
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
            
            System.err.println(String.format("Parsing voice message: type=%s, sender=%s, receiver=%s, duration=%.1f, audioSize=%d",
                    type, senderName, receiverName, duration, audioDataLength));
            
            if (audioDataLength <= 0 || audioDataLength > MAX_AUDIO_DATA_SIZE) {
                // HoangAnhDz.logError("Invalid audio data length: " + audioDataLength);
                return null;
            }
            
            byte[] audioData = new byte[audioDataLength];
            message.reader().readFully(audioData);
            
            VoiceMessage voiceMsg = new VoiceMessage(audioData, 0, senderName, receiverName, duration, type);
            voiceMsg.setTimestamp(timestamp);
            
            return voiceMsg;
            
        } catch (IOException e) {
            System.err.println("Error at parseVoiceMessage: " + e.getMessage());
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
    
    private void sendErrorMessage(Player player, String errorMsg) {
        try {
            // Send error via case 92 system
            Message msg = new Message((byte) 92);
            msg.writer().writeUTF("System");
            msg.writer().writeUTF("[Voice Error] " + errorMsg);
            msg.writer().writeInt(-1); // No character data
            msg.writer().writeShort((short) 0);
            msg.writer().writeShort((short) 0);
            msg.writer().writeShort((short) 0);
            msg.writer().writeShort((short) 0);
            msg.writer().writeByte((byte) 0);
            
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
    
    // Cleanup and statistics methods remain the same...
    public void cleanup() {
        long currentTime = System.currentTimeMillis();
        
        lastVoiceMessageTime.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > 60000);
        
        voiceMessageCount.entrySet().removeIf(entry -> 
            currentTime - lastVoiceMessageTime.getOrDefault(entry.getKey(), 0L) > 60000);
        
        recentVoiceMessages.removeIf(msg -> msg.getAgeInSeconds() > 300);
    }
    
    public long getTotalVoiceMessagesSent() {
        return totalVoiceMessagesSent.get();
    }
    
    public String getStatistics() {
        return String.format(
                "Voice Message Statistics (Case92):\n" +
                "Total Messages: %d\n" +
                "World Chat: %d\n" +
                "Private Chat: %d\n" +
                "Audio Data Processed: %d bytes\n" +
                "Recent Messages: %d",
                getTotalVoiceMessagesSent(),
                totalWorldChatMessages.get(),
                totalPrivateChatMessages.get(),
                totalAudioDataProcessed.get(),
                recentVoiceMessages.size()
        );
    }
}