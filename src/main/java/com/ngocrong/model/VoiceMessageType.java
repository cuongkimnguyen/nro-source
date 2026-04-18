package com.ngocrong.model;

public enum VoiceMessageType {
    WORLD_CHAT(0),
    PRIVATE_CHAT(1),
    MAP_CHAT(2);
    
    private final int value;
    
    VoiceMessageType(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static VoiceMessageType fromValue(int value) {
        for (VoiceMessageType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return WORLD_CHAT; // Default
    }
    
    @Override
    public String toString() {
        return name() + "(" + value + ")";
    }
}