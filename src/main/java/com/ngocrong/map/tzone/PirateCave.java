package com.ngocrong.map.tzone;

import com.ngocrong.map.Treasure;
import com.ngocrong.map.TMap;
import com.ngocrong.user.Player;

import java.util.List;

public class PirateCave extends ZTreasure {

    private long last;

    public PirateCave(Treasure treasure, TMap map, int zoneId) {
        super(treasure, map, zoneId);
    }

    @Override
    public void update() {
        super.update();
        long now = System.currentTimeMillis();
        if (now - last >= 1000) {
            last = now;
            trapped();
        }
    }

    public void trapped() {
        List<Player> list = getListChar(TYPE_HUMAN, TYPE_PET);
        int subHp = treasure.getLevel() * 2500;
        for (Player _c : list) {
            if (_c.isDead()) {
                continue;
            }
            if (_c.getX() >= 264 && _c.getX() <= 1008 && _c.getY() >= 936 && _c.getY() <= 1032) {
                _c.info.hp -= subHp;
                service.attackPlayer(_c, subHp, false, (byte) 49);
                if (_c.info.hp <= 0) {
                    _c.killed(null);
                    _c.startDie();
                }
            }
        }
    }

}
