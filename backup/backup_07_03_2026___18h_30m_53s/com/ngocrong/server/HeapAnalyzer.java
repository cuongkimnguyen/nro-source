/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.server;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ClassLoadingMXBean;

public class HeapAnalyzer {

    public static void printMemoryInfo() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemory = memoryMXBean.getHeapMemoryUsage();

        System.out.println("\n========= HEAP MEMORY USAGE =========");
        System.out.println("Used:  " + formatSize(heapMemory.getUsed()));
        System.out.println("Max:   " + formatSize(heapMemory.getMax()));
        System.out.println("Free:  " + formatSize(heapMemory.getMax() - heapMemory.getUsed()));
        System.out.println("======================================\n");
    }

    public static void printLoadedClassInfo() {
        ClassLoadingMXBean classMX = ManagementFactory.getClassLoadingMXBean();
        System.out.println("========= CLASS LOADING INFO =========");
        System.out.println("Loaded classes: " + classMX.getLoadedClassCount());
        System.out.println("Total loaded:   " + classMX.getTotalLoadedClassCount());
        System.out.println("Unloaded:       " + classMX.getUnloadedClassCount());
        System.out.println("======================================\n");
    }

    private static String formatSize(long bytes) {
        return String.format("%.2f MB", bytes / 1024.0 / 1024.0);
    }
}
