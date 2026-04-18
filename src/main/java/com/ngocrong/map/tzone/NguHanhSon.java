package com.ngocrong.map.tzone;

import com.ngocrong.consts.MobName;
import com.ngocrong.map.MapService;
import com.ngocrong.mob.*;
import com.ngocrong.map.TMap;
import com.ngocrong.model.Npc;

import java.util.ArrayList;
import java.util.Arrays;

public class NguHanhSon extends Zone {

    private long lastChat;

    public NguHanhSon(TMap map, int zoneId) {
        super(map, zoneId);
    }

    @Override
    public void initial() {
        long now = System.currentTimeMillis();
        service = new MapService(this);
        Arrays.fill(lastUpdates, now);
        this.numPlayer = 0;
        this.maxPlayer = 15;
        this.isHadSuperMob = false;
        this.mobs = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.players = new ArrayList<>();
        this.items = new ArrayList<>();
        this.satellites = new ArrayList<>();
        this.waitForRespawn = new ArrayList<>();
        for (MobCoordinate mobCoordinate : this.map.mobs) {
            Mob m = MobFactory.getMob(MobType.MOB);
            m.setMobId(allCountMob--);
            byte templateID = mobCoordinate.getTemplateID();
            MobTemplate template = Mob.getMobTemplate(templateID);
            m.setX(mobCoordinate.getX());
            m.setY(mobCoordinate.getY());
            m.setTemplateId(templateID);
            m.setLevel(template.level);
            long hpDefault = template.hp;
            if (templateID == MobName.KHI_LONG_DO) {
                hpDefault = 400000;
            }
            if (templateID == MobName.KHI_LONG_VANG) {
                hpDefault = 450000;
            }
            if (templateID == MobName.QUY_CHIM) {
                hpDefault = 300000;
            }
            hpDefault = 30000;
            m.setHpDefault(hpDefault);
            m.setDefault();
            m.setZone(this);
            addMob(m);
        }
        for (Npc npc : this.map.npcs) {
            Npc n = npc.clone();
            addNpc(n);
        }
    }

    public void update() {
        super.update();
        long now = System.currentTimeMillis();
        if (now - lastChat >= 5000) {
            lastChat = now;
            service.npcChat((short) 48, "Chu mi nga");
        }
    }

}
