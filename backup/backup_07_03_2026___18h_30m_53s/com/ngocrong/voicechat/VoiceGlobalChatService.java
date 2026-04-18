package com.ngocrong.voicechat;

import com.ngocrong.network.Message;
import com.ngocrong.network.Service;
import com.ngocrong.server.SessionManager;
import com.ngocrong.user.Player;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Simple storage service for global voice chat messages. Maintains a ring
 * buffer of the most recent messages and provides utilities to notify clients
 * and retrieve audio data.
 */
public class VoiceGlobalChatService {

    private static final int MAX_MESSAGES = 20;
    private static final LinkedList<StoredMessage> MESSAGES = new LinkedList<>();
    private static int nextId = 1;

    private static String getTimeNow() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static synchronized int addMessage(Player sender, byte[] data,String timeStr) {
        StoredMessage msg = new StoredMessage();
        msg.id = nextId++;
        msg._char = sender;
        msg.timeStr = timeStr;
        msg.time = getTimeNow();
        msg.data = data;
        MESSAGES.addLast(msg);
        if (MESSAGES.size() > MAX_MESSAGES) {
            MESSAGES.removeFirst();
        }
        broadcastNotification(msg);
        return msg.id;
    }

    public static synchronized StoredMessage getMessage(int id) {
        for (StoredMessage m : MESSAGES) {
            if (m.id == id) {
                return m;
            }
        }
        return null;
    }

    private static void broadcastNotification(StoredMessage msg) {
        Message notify = null;
        try {
            notify = Service.messageSubCommand((byte) -99);
            var writer = notify.writer();
            writer.writeByte(13); // notification subtype
            writer.writeInt(msg.id);
            writer.writeInt(msg._char.id);
            writer.writeInt(msg._char.getHead());
            writer.writeInt(msg._char.getBody());
            writer.writeInt(msg._char.getLeg());
            writer.writeInt(msg._char.bag);
            writer.writeUTF(msg._char.name);
            writer.writeUTF(msg.time);
            writer.writeUTF(msg.timeStr);
            writer.flush();
            SessionManager.sendMessage(notify);
        } catch (IOException ignored) {
        } finally {
            if (notify != null) {
                try {
                    notify.cleanup();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
