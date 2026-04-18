package com.ngocrong.map.tzone;

import com.ngocrong.consts.MobName;
import com.ngocrong.map.MapService;
import com.ngocrong.map.TMap;
import com.ngocrong.map.Treasure;
import com.ngocrong.mob.*;
import com.ngocrong.model.MessageTime;
import com.ngocrong.model.Npc;
import com.ngocrong.user.Player;
import lombok.Data;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

@Data
public class ZTreasure extends Zone {

    protected Treasure treasure;
    private static Logger logger = Logger.getLogger(ZTreasure.class);

    public ZTreasure(Treasure treasure, TMap map, int zoneId) {
        super(map, zoneId);
        this.treasure = treasure;
        initial2();
    }

    @Override
    public void initial() {
    }

    public void initial2() {
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
        int level = treasure.getLevel();
        int p = level * ((level + 10) / 20);
        if (p < 1) {
            p = 1;
        }
        for (MobCoordinate mobCoordinate : this.map.mobs) {
            byte templateID = mobCoordinate.getTemplateID();
            Mob m = MobFactory.getMob(MobType.MOB);
            m.setMobId(allCountMob--);
            MobTemplate template = Mob.getMobTemplate(templateID);
            m.setX(mobCoordinate.getX());
            m.setY(mobCoordinate.getY());
            m.setTemplateId(templateID);
            m.setLevel(template.level);
            long hpDefault = template.hp;
            if (templateID == MobName.LINH_DOC_NHAN) {
                hpDefault = 34200L * p;
            } else if (templateID == MobName.LINH_DOC_NHAN_2) {
                hpDefault = 34400L * p;
            } else if (templateID == MobName.SOI_XAM) {
                hpDefault = 34600L * p;
            } else if (templateID == MobName.ROBOT_BAO_VE) {
                hpDefault = 300000L * p;
            } else {
                hpDefault = 40000L * p;
            }
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

    @Override
    public void respawn(ArrayList<Mob> mobs) {

    }

    @Override
    public void enter(Player p) {
        super.enter(p);
        if (p.isHuman()) {
            MessageTime ms = new MessageTime(MessageTime.HANG_KHO_BAU, "Hang kho báu", (short) treasure.getCountDown());
            p.addMessageTime(ms);
        }
    }

    @Override
    public void leave(Player p) {
        super.leave(p);
        if (p.isHuman()) {
            p.setTimeForMessageTime(MessageTime.HANG_KHO_BAU, (short) 0);
        }
    }

}
