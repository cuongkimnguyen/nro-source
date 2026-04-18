package com.ngocrong.map.tzone;

import com.ngocrong.bot.boss.karin.Yajiro;
import com.ngocrong.map.TMap;

public class KarinTower extends MapSingle {

    public Yajiro yajiro;

    public KarinTower(TMap map, int zoneId, byte typeTraining) {
        super(map, zoneId);
        initBoss(typeTraining);
    }

    private void initBoss(byte typeTraining) {
        yajiro = new Yajiro();
        yajiro.setX((short) 298);
        yajiro.setY((short) 408);
        if (typeTraining >= 1) {
            enter(yajiro);
        }
    }

}
