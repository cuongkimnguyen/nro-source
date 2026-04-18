/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.voicechat;

import com.ngocrong.network.Message;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class VoiceSetting {

    public boolean enableSpeakZone;
    public boolean enableSpeakClan;
    public boolean enableMICMap;
    public boolean enableMICBang;

    public void setSetting(Message msg) {
        try {
            enableSpeakClan = msg.reader().readBoolean();
            enableSpeakZone = msg.reader().readBoolean();
            enableMICBang = msg.reader().readBoolean();
            enableMICMap = msg.reader().readBoolean();
        } catch (IOException ex) {
            Logger.getLogger(VoiceSetting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
