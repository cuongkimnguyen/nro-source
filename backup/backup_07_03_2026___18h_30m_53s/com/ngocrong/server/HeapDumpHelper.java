/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.server;

import com.sun.management.HotSpotDiagnosticMXBean;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

public class HeapDumpHelper {

    private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
    private static volatile HotSpotDiagnosticMXBean hotspotMBean;

    public static void dumpHeap(String filePath, boolean live) {
        try {
            initHotspotMBean();
            hotspotMBean.dumpHeap(filePath, live);
            System.out.println(" Heap dump saved to: " + filePath);
        } catch (Exception e) {
            System.err.println(" Failed to dump heap: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveHeap() {
        System.gc();
        HeapDumpHelper.dumpHeap("heap_" + System.currentTimeMillis() + ".hprof", true);
    }

    private static void initHotspotMBean() throws Exception {
        if (hotspotMBean == null) {
            synchronized (HeapDumpHelper.class) {
                if (hotspotMBean == null) {
                    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                    hotspotMBean = ManagementFactory.newPlatformMXBeanProxy(
                            server, HOTSPOT_BEAN_NAME, HotSpotDiagnosticMXBean.class);
                }
            }
        }
    }
}
