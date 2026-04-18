package com.ngocrong.security.multilayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MultiLayerLogger {

    public static volatile boolean EnableLogging = true;
    private static final Object lock = new Object();
    private static final String LOG_FILE = "MultiLayerLog.txt";

    public static void log(String message) {
        if (!EnableLogging) return;
        String line = String.format("[%s] %s\n", timestamp(), message);
        synchronized (lock) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(new File(LOG_FILE), true);
                fw.write(line);
            } catch (IOException ignored) {
            } finally {
                if (fw != null) try { fw.close(); } catch (IOException ignored) {}
            }
        }
    }

    private static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(new Date());
    }
}


