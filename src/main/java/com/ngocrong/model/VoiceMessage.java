package com.ngocrong.model;

import java.util.Arrays;

public class VoiceMessage {
    
    private byte[] audioData;
    private String senderName;
    private String receiverName; // null for world chat
    private float duration;
    private long timestamp;
    private int senderId;
    private VoiceMessageType messageType;
    
    public VoiceMessage() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public VoiceMessage(byte[] audioData, int senderId, String senderName, String receiverName,
                       float duration, VoiceMessageType messageType) {
        this.audioData = audioData;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.duration = duration;
        this.messageType = messageType;
        this.timestamp = System.currentTimeMillis();
    }
    
    public byte[] getAudioData() {
        return audioData;
    }
    
    public void setAudioData(byte[] audioData) {
        this.audioData = audioData;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getReceiverName() {
        return receiverName;
    }
    
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
    
    public float getDuration() {
        return duration;
    }
    
    public void setDuration(float duration) {
        this.duration = duration;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
    
    public VoiceMessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(VoiceMessageType messageType) {
        this.messageType = messageType;
    }
    
    public boolean isWorldChat() {
        return messageType == VoiceMessageType.WORLD_CHAT;
    }
    
    public boolean isPrivateChat() {
        return messageType == VoiceMessageType.PRIVATE_CHAT;
    }
    
    public int getDataSize() {
        return audioData != null ? audioData.length : 0;
    }
    
    public long getAgeInSeconds() {
        return (System.currentTimeMillis() - timestamp) / 1000;
    }
    
    public boolean isExpired(int maxAgeSeconds) {
        return getAgeInSeconds() > maxAgeSeconds;
    }
    
    public boolean isValid() {
        return audioData != null && audioData.length > 0 && 
               senderName != null && !senderName.trim().isEmpty() &&
               duration > 0 && duration <= 30; // Max 30 seconds
    }
    
    @Override
    public String toString() {
        return String.format("VoiceMessage[%s] from %s to %s - %.1fs (%d bytes)", 
                           messageType, senderName, 
                           receiverName != null ? receiverName : "ALL", 
                           duration, getDataSize());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        VoiceMessage that = (VoiceMessage) obj;
        return Float.compare(that.duration, duration) == 0 &&
               timestamp == that.timestamp &&
               Arrays.equals(audioData, that.audioData) &&
               Objects.equals(senderName, that.senderName) &&
               Objects.equals(receiverName, that.receiverName) &&
               messageType == that.messageType;
    }
    
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(audioData);
        result = 31 * result + Objects.hash(senderName, receiverName, duration, timestamp, messageType);
        return result;
    }
    
    // Helper method to create a copy without audio data (for logging)
    public VoiceMessage createMetadataOnly() {
        VoiceMessage copy = new VoiceMessage();
        copy.senderName = this.senderName;
        copy.receiverName = this.receiverName;
        copy.duration = this.duration;
        copy.timestamp = this.timestamp;
        copy.messageType = this.messageType;
        // Don't copy audioData to save memory
        return copy;
    }
}

class Objects {
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
    
    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }
}