package com.ngocrong.map.tzone;

import com.ngocrong.map.Treasure;
import com.ngocrong.map.TMap;
import com.ngocrong.mob.Mob;
import com.ngocrong.mob.MobFactory;
import com.ngocrong.mob.MobTemplate;
import com.ngocrong.mob.MobType;

public class PiratePort extends ZTreasure {

    public PiratePort(Treasure treasure, TMap map, int zoneId) {
        super(treasure, map, zoneId);
        int level = treasure.getLevel();
        int p = level * ((level + 10) / 20);
        if (p < 1) {
            p = 1;
        }
        Mob m = MobFactory.getMob(MobType.GUARD_ROBOT);
        byte templateID = 72;
        m.setMobId(allCountMob--);
        MobTemplate template = Mob.getMobTemplate(templateID);
        m.setX((short) 550);
        m.setY((short) 336);
        m.setTemplateId(templateID);
        m.setLevel(template.level);
        int hpDefault = 300000 * p;
        m.setHpDefault(hpDefault);
        m.setDefault();
        m.setZone(this);
        addMob(m);
    }

}
