package com.ngocrong.user;

import com.ngocrong.item.Item;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DanhHieu {
    
    private static final Logger logger = Logger.getLogger(DanhHieu.class);
    private static final HashMap<Integer, int[]> danhHieuMap = new HashMap<>();
    
    static {
        init();
    }
    
    private static void init() {
        danhHieuMap.put(2036, new int[]{21962, 6});
        danhHieuMap.put(2067, new int[]{31259, 6});
        danhHieuMap.put(2068, new int[]{31260, 6});
        danhHieuMap.put(2158, new int[]{31262, 6});
        danhHieuMap.put(2159, new int[]{31264, 6});
        danhHieuMap.put(1998, new int[]{15510, 6});
        danhHieuMap.put(2177, new int[]{32227, 6});
        danhHieuMap.put(2178, new int[]{32229, 6});
        danhHieuMap.put(2179, new int[]{32231, 6});
        danhHieuMap.put(2180, new int[]{32233, 6});
        danhHieuMap.put(2181, new int[]{32235, 6});
        danhHieuMap.put(2182, new int[]{32237, 6});
        danhHieuMap.put(2183, new int[]{32239, 6});
        danhHieuMap.put(2184, new int[]{32241, 6});
        danhHieuMap.put(2185, new int[]{32243, 6});
        danhHieuMap.put(2187, new int[]{32247, 6});
        danhHieuMap.put(2188, new int[]{32249, 6});
        danhHieuMap.put(2198, new int[]{16661, 6});
        danhHieuMap.put(2260, new int[]{9138, 6});
        danhHieuMap.put(2310, new int[]{9140, 6});
        danhHieuMap.put(2316, new int[]{21961, 6});
        danhHieuMap.put(2332, new int[]{21955, 6});
        danhHieuMap.put(2333, new int[]{21956, 6});
        danhHieuMap.put(2334, new int[]{21957, 6});
        danhHieuMap.put(2322, new int[]{9168, 6});
        danhHieuMap.put(2323, new int[]{9166, 6});
        danhHieuMap.put(2324, new int[]{9164, 6});
        danhHieuMap.put(2325, new int[]{9162, 6});
        danhHieuMap.put(1974, new int[]{9654, 6});
        danhHieuMap.put(1975, new int[]{9656, 6});
        danhHieuMap.put(1976, new int[]{9658, 6});
        danhHieuMap.put(2358, new int[]{9652, 6});
        danhHieuMap.put(2436, new int[]{9427, 9});
        danhHieuMap.put(2481, new int[]{23500, 6});
        danhHieuMap.put(2482, new int[]{23501, 6});
        
        danhHieuMap.put(2452, new int[]{19331, 6});
        logger.info("Đã load " + danhHieuMap.size() + " danh hiệu");
    }
    
    public static int[] getImgDanhHieu(Item item) {
        if (item == null || item.template == null) {
            return new int[]{-1, 0};
        }
        
        int itemId = item.template.id;
        int[] result = danhHieuMap.get(itemId);
        if (result != null) {
            return result;
        }
        
        return new int[]{-1, 0};
    }
    
    public static Map<Integer, int[]> getAllDanhHieu() {
        return new HashMap<>(danhHieuMap);
    }
    
    public static void addDanhHieu(int itemId, int imageId, int frame) {
        synchronized (danhHieuMap) {
            danhHieuMap.put(itemId, new int[]{imageId, frame});
            logger.info("Đã thêm danh hiệu: itemId=" + itemId + ", imageId=" + imageId + ", frame=" + frame);
        }
    }
}
