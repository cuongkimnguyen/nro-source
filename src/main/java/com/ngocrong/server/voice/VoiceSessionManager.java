package com.ngocrong.server.voice;

import com.ngocrong.network.Message;
import com.ngocrong.user.Player;

import java.util.concurrent.ConcurrentHashMap;

public class VoiceSessionManager {
    private static final ConcurrentHashMap<String, VoiceSession> sessions = new ConcurrentHashMap<>();

    static void addSession(String playerName, VoiceSession session) {
        sessions.put(playerName, session);
    }

    static void removeSession(VoiceSession session) {
        sessions.values().removeIf(v -> v == session);
    }

    public static VoiceSession getSession(Player player) {
        return player != null ? sessions.get(player.name) : null;
    }

    public static void sendMessage(Message msg) {
        for (VoiceSession vs : sessions.values()) {
            try {
                vs.sendMessage(msg);
            } catch (Exception ignored) {
            }
        }
    }

    public static void sendMessage(Player player, Message msg) {
        VoiceSession vs = getSession(player);
        if (vs != null) {
            try {
                vs.sendMessage(msg);
            } catch (Exception ignored) {
            }
        }
    }
}
