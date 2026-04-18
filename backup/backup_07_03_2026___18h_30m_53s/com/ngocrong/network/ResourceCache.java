package com.ngocrong.network;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache để lưu danh sách resource files theo zoomLevel
 */
public class ResourceCache {
    private static ResourceCache instance;
    private final Map<Integer, ArrayList<String>> resourceCache = new ConcurrentHashMap<>();
    private final Map<Integer, Long> lastModified = new ConcurrentHashMap<>();

    private ResourceCache() {}

    public static synchronized ResourceCache getInstance() {
        if (instance == null) {
            instance = new ResourceCache();
        }
        return instance;
    }

    /**
     * Lấy danh sách resource files theo zoomLevel với cache
     */
    public ArrayList<String> getResourcePaths(int zoomLevel) {
        String folder = "resources/data/" + zoomLevel;
        File folderFile = new File(folder);
        
        if (!folderFile.exists()) {
            return new ArrayList<>();
        }

        long currentModified = folderFile.lastModified();
        Long cachedModified = lastModified.get(zoomLevel);

        // Kiểm tra cache có hợp lệ không
        if (cachedModified != null && cachedModified == currentModified && resourceCache.containsKey(zoomLevel)) {
            return new ArrayList<>(resourceCache.get(zoomLevel));
        }

        // Build cache mới
        ArrayList<String> paths = new ArrayList<>();
        addPath(paths, folderFile);
        
        resourceCache.put(zoomLevel, new ArrayList<>(paths));
        lastModified.put(zoomLevel, currentModified);
        
        return paths;
    }

    /**
     * Đệ quy thêm path vào danh sách
     */
    private void addPath(ArrayList<String> paths, File file) {
        if (file.isFile()) {
            paths.add(file.getPath());
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    addPath(paths, f);
                }
            }
        }
    }

    /**
     * Xóa cache để reload
     */
    public void clearCache(int zoomLevel) {
        resourceCache.remove(zoomLevel);
        lastModified.remove(zoomLevel);
    }

    /**
     * Xóa toàn bộ cache
     */
    public void clearAllCache() {
        resourceCache.clear();
        lastModified.clear();
    }
}