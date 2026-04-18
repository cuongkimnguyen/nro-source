package com.ngocrong.map.expansion.blackdragon;

import com.ngocrong.map.IMap;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.consts.MapName;
import org.apache.log4j.Logger;

public class MBlackDragonBall extends IMap<ZBlackDragonBall> {

    private static final Logger logger = Logger.getLogger(MBlackDragonBall.class);
    public static byte[] FLAG_ARRAY = {2, 3, 4, 5, 6, 7};
    // Chỉ sử dụng 4 bản đồ cho sự kiện Ngọc Rồng Sao Đen
    public static int[] MAPS = {
        MapName.HANH_TINH_M_2,
        MapName.HANH_TINH_POLARIS,
        MapName.HANH_TINH_CRETACEOUS,
        MapName.HANH_TINH_MONMAASU
    };

    public MBlackDragonBall() {
        super(3600);
        int[] zoneNumber = new int[]{5, 5, 3, 30};
        for (int i = 0; i < MAPS.length; i++) {
            int m = MAPS[i];
            TMap map = MapManager.getInstance().getMap(m);
            for (int j = 0; j < zoneNumber[i]; j++) {
                ZBlackDragonBall z = new ZBlackDragonBall(map, j, i);
                z.setMaxPlayer(30);
                zones.add(z);
                map.addZone(z);
            }
        }
    }

    @Override
    public void close() {
        MapManager.getInstance().blackDragonBall = null;
        for (ZBlackDragonBall z : zones) {
            try {
                z.closeBlackDragonBall();
            } catch (Exception ignored) {
            }
        }
    }
}
