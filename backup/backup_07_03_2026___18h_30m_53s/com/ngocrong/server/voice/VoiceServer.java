package com.ngocrong.server.voice;

import java.net.ServerSocket;
import java.net.Socket;

public class VoiceServer implements Runnable {
    private final int port;
    private ServerSocket server;
    private volatile boolean running;

    public VoiceServer(int port) {
        this.port = port;
    }

    public void start() {
        Thread t = new Thread(this, "VoiceServer");
        t.start();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
            server.setReuseAddress(true);
            running = true;
            //System.err.println("Active VoiceServer at port :" + port);
            while (running) {
                Socket socket = server.accept();
                //System.err.println("Avalible session voice");
                new VoiceSession(socket);
            }
        } catch (Exception ignored) {
        }
    }

    public void stop() {
        running = false;
        try {
            if (server != null) server.close();
        } catch (Exception ignored) {
        }
    }
}
