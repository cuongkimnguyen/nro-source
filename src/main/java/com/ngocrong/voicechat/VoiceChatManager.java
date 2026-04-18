/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.voicechat;

import com.ngocrong.network.Message;
import com.ngocrong.network.Service;
import com.ngocrong.user.Player;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class VoiceChatManager {

    private static final Logger logger = Logger.getLogger(VoiceChatManager.class.getName());

    public static final byte VOICE_CLAN = 1;
    public static final byte VOICE_ZONE = 2;
    public static final byte VOICE_ALL = 3;

    public static VoiceChatManager instance;

    public static VoiceChatManager gI() {
        instance = instance == null ? new VoiceChatManager() : instance;
        return instance;
    }

    public void action(byte type, Player sender, byte[] voiceData) {
        if (sender == null || voiceData == null || voiceData.length == 0) {
            return;
        }

        Set<Player> recipients = getRecipients(type, sender);

        if (!recipients.isEmpty()) {
            sendVoiceToPlayers(recipients, type, sender, voiceData);

            // Log for monitoring
            logger.info(String.format("Voice chat: Player %d sent %d bytes to %d recipients (type=%d)",
                    sender.id, voiceData.length, recipients.size(), type));
        }
    }

    private void sendVoiceToPlayers(Set<Player> recipients, byte type, Player sender, byte[] voiceData) {
        if (recipients.isEmpty()) {
            return;
        }

        Message msg = null;
        try {
            // Create message
            msg = Service.messageSubCommand((byte) -99);// Voice chat message ID           
            var writer = msg.writer();
            writer.writeByte(10); // Type Message VoiceChat ID
            // Write sender ID
            writer.writeInt(sender.id);
            writer.writeUTF(sender.name);
            writer.writeByte(type);
            // Write voice data length and data
            writer.writeInt(voiceData.length);
            writer.write(voiceData);
            writer.flush();

            // Send to all recipients
            int successCount = 0;
            int errorCount = 0;

            for (Player recipient : recipients) {
                try {
                    if (recipient != null && recipient.service != null && canSendVoice(sender, recipient, type)) {
                        recipient.service.sendMessage(msg);
                        successCount++;
                    }
                } catch (Exception e) {
                    errorCount++;
                    logger.log(Level.WARNING, String.format("Failed to send voice to player %d",
                            recipient != null ? recipient.id : -1), e);
                }
            }

            // Log results
            if (errorCount > 0) {
                logger.warning(String.format("Voice chat delivery: %d success, %d errors",
                        successCount, errorCount));
            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error creating voice chat message", ex);
        } finally {
            // Always cleanup message
            if (msg != null) {
                try {
                    msg.cleanup();
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error cleaning up message", e);
                }
            }
        }
    }

    private Set<Player> getRecipients(byte type, Player sender) {
        Set<Player> recipients = new HashSet<>();

        switch (type) {
            case VOICE_CLAN:
                addClanMembers(recipients, sender);
                break;

            case VOICE_ZONE:
                addZoneMembers(recipients, sender);
                break;

            case VOICE_ALL:
                addClanMembers(recipients, sender);
                addZoneMembers(recipients, sender);
                break;

            default:
                logger.warning("Unknown voice chat type: " + type);
                break;
        }

        // Remove sender from recipients
        recipients.remove(sender);
        return recipients;
    }

    /**
     * Add clan members to recipients
     */
    private void addClanMembers(Set<Player> recipients, Player sender) {
        if (sender.clan != null) {
            try {
                List<Player> clanMembers = sender.clan.getPlayersOnline();
                if (clanMembers != null) {
                    recipients.addAll(clanMembers);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error getting clan members for voice chat", e);
            }
        }
    }

    /**
     * Add zone members to recipients
     */
    private void addZoneMembers(Set<Player> recipients, Player sender) {
        if (sender.zone != null) {
            try {
                List<Player> zonePlayers = sender.zone.getPlayers();
                if (zonePlayers != null) {
                    recipients.addAll(zonePlayers);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error getting zone players for voice chat", e);
            }
        }
    }

    private boolean canSendVoice(Player playerSend, Player playerReceive, byte type) {
        if (playerSend == null || playerReceive == null) {
            return false;
        }
        if (playerSend.voiceSetting == null || playerReceive.voiceSetting == null) {
            return false;
        }
        switch (type) {
            case VOICE_CLAN:
                return playerSend.clan != null
                        && playerReceive.clan != null
                        && playerSend.clan.equals(playerReceive.clan)
                        && playerReceive.voiceSetting.enableSpeakClan;

            case VOICE_ZONE:
                return playerSend.zone != null
                        && playerReceive.zone != null
                        && playerSend.zone.equals(playerReceive.zone)
                        && playerReceive.voiceSetting.enableSpeakZone;

            case VOICE_ALL:
                return canSendVoice(playerSend, playerReceive, VOICE_CLAN)
                        || canSendVoice(playerSend, playerReceive, VOICE_ZONE);

            default:
                return false;
        }
    }
}
