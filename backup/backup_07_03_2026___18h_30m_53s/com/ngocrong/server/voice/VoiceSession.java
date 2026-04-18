package com.ngocrong.server.voice;

import com.ngocrong.network.Message;
import com.ngocrong.network.VoiceMessageService;
import com.ngocrong.server.SessionManager;
import com.ngocrong.user.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class VoiceSession implements Runnable {

    private final Socket socket;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private Thread thread;
    private Player player;

    public VoiceSession(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.thread = new Thread(this, "VoiceSession");
        this.thread.start();
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                MessageData msgData = readMessageDirect();
                if (msgData == null) {
                    break;
                }
                try {
                    handleMessageDirect(msgData);
                } catch (Exception e) {
                    System.err.println("Error handling message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Voice session error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close();
        }
    }

    // Class để lưu message data trực tiếp
    private static class MessageData {
        byte command;
        byte[] data;
        
        MessageData(byte command, byte[] data) {
            this.command = command;
            this.data = data;
        }
    }

    private MessageData readMessageDirect() throws IOException {
        byte cmd;
        try {
            cmd = dis.readByte();
        } catch (IOException e) {
            System.err.println("Failed to read command byte: " + e.getMessage());
            return null;
        }
        
        try {
            int size = (dis.readByte() & 0xff) << 16;
            size |= (dis.readByte() & 0xff) << 8;
            size |= (dis.readByte() & 0xff);
            
            System.err.println("Reading message - Command: " + cmd + ", Size: " + size);
            
            if (size < 0 || size > 1024 * 1024) { // 1MB limit
                System.err.println("Invalid message size: " + size);
                return null;
            }
            
            byte[] data = new byte[size];
            if (size > 0) {
                dis.readFully(data);
                System.err.println("Successfully read " + data.length + " bytes of data");
            }
            
            return new MessageData(cmd, data);
        } catch (IOException e) {
            System.err.println("Failed to read message data: " + e.getMessage());
            throw e;
        }
    }

    private void handleMessageDirect(MessageData msgData) throws IOException {
        try {
            System.err.println("Handling message command: " + msgData.command);
            
            if (msgData.command == -100) { // handshake
                // Tạo Message object cho handshake
                Message msg = new Message(msgData.command, msgData.data);
                int id = msg.reader().readInt();
                System.err.println("Handshake for playerId: " + id);
                this.player = SessionManager.findChar(id);
                if (player != null) {
                    VoiceSessionManager.addSession(player.name, this);
                    System.err.println("Player session added successfully: " + player.name);
                } else {
                    System.err.println("Player not found: " + id);
                }
                msg.cleanup();
                return;
            }
            
            if (player == null) {
                System.err.println("Player is null, ignoring message command: " + msgData.command);
                return;
            }
            
            if (msgData.command == -58) { // voice chat message
                System.err.println("Started get voiceChat");

                byte[] data = msgData.data;
                System.err.println("Message data length: " + (data != null ? data.length : "null"));
                
                if (data == null || data.length == 0) {
                    System.err.println("No data in message");
                    return;
                }
                
                if (data.length < 1) {
                    System.err.println("Data too short, expected at least 1 byte");
                    return;
                }
                
                byte type = data[0];
                System.err.println("Data type: " + type);
                
                if (data.length < 2) {
                    System.err.println("No payload data after type byte");
                    return;
                }
                
                byte[] payload = java.util.Arrays.copyOfRange(data, 1, data.length);
                System.err.println("Payload length: " + payload.length);
                
                // Tạo message copy với payload
                Message copy = new Message(msgData.command, payload);
                System.err.println("Created copy message, processing type: " + type);
                
                switch (type) {
                    case 0:
                        System.err.println("Processing world chat voice message");
                        VoiceMessageService.gI().processWorldChatVoiceMessage(player, copy);
                        System.err.println("World chat voice message processed");
                        break;
                    case 1:
                        System.err.println("Processing private chat voice message");
                        VoiceMessageService.gI().processPrivateChatVoiceMessage(player, copy);
                        System.err.println("Private chat voice message processed");
                        break;
                    case 2:
                        System.err.println("Processing map chat voice message");
                        VoiceMessageService.gI().processMapChatVoiceMessage(player, copy);
                        System.err.println("Map chat voice message processed");
                        break;
                    default:
                        System.err.println("Unknown voice message type: " + type);
                        break;
                }
                
                copy.cleanup();
                System.err.println("Voice message handling completed");
            } else {
                System.err.println("Unknown command: " + msgData.command);
            }
        } catch (IOException e) {
            System.err.println("IOException in handleMessage: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected exception in handleMessage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void sendMessage(Message msg) throws IOException {
        try {
            byte[] data = msg.getData();
            int size = data != null ? data.length : 0;
            
            System.err.println("Sending message - Command: " + msg.getCommand() + ", Size: " + size);
            
            dos.writeByte(msg.getCommand());
            dos.writeByte((size >> 16) & 0xff);
            dos.writeByte((size >> 8) & 0xff);
            dos.writeByte(size & 0xff);
            if (size > 0) {
                dos.write(data);
            }
            dos.flush();
            
            System.err.println("Message sent successfully");
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
            throw e;
        }
    }

    public void close() {
        System.err.println("Closing voice session for player: " + (player != null ? player.name : "unknown"));
        VoiceSessionManager.removeSession(this);
        try {
            socket.close();
        } catch (Exception e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }
}