package com.ngocrong.map.tzone;

import com.ngocrong.map.Treasure;
import com.ngocrong.bot.boss.barrack.GeneralBlue;
import com.ngocrong.map.TMap;

public class TreasureCave extends ZTreasure {

    public TreasureCave(Treasure treasure, TMap map, int zoneId) {
        super(treasure, map, zoneId);
        int level = treasure.getLevel();
        int p = level * ((level + 10) / 10);
        if (p < 1) {
            p = 1;
        }
        GeneralBlue generalBlue = new GeneralBlue();
        generalBlue.setInfo(1000000 * p, 100000, generalBlue.info.hpFull / 100, 100, 10);
        generalBlue.setX((short) 138);
        generalBlue.setY((short) 456);
        enter(generalBlue);
    }
}
