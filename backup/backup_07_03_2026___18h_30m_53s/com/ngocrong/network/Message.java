package com.ngocrong.network;

import _HunrProvision.HoangAnhDz;
import java.io.*;

public class Message {

    private byte command;
    private ByteArrayOutputStream os;
    private FastDataOutputStream dos;
    private ByteArrayInputStream is;
    public FastDataInputStream dis;

    public Message(int command) {
        this((byte) command);
    }

    public Message(byte command) {
        this.command = command;
        os = new ByteArrayOutputStream();
        dos = new FastDataOutputStream(os);
    }

    public Message(byte command, byte[] data) {
        this.command = command;
        is = new ByteArrayInputStream(data);
        dis = new FastDataInputStream(is);
         os = new ByteArrayOutputStream();
        dos = new FastDataOutputStream(os);
    }

    public byte getCommand() {
        return command;
    }

    public void setCommand(int cmd) {
        setCommand((byte) cmd);
    }

    public void setCommand(byte cmd) {
        command = cmd;
    }

    public byte[] getData() {
        return os.toByteArray();
    }

    public FastDataInputStream reader() {
        return dis;

    }

    public FastDataOutputStream writer() {
        return dos;
    }

    public void cleanup() {
        try {
            if (dis != null) {
                dis.close();
            }
            if (dos != null) {
                dos.close();
            }
        } catch (IOException e) {
            
        }
    }

    public void dispose() {
        this.cleanup();
        this.dis = null;
        this.is = null;
        this.dos = null;
        this.os = null;
//        // HoangAnhDz.logError("________Dispose networks_________");
    }

}
