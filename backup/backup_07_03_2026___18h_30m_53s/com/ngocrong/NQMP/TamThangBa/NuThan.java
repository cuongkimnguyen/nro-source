/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.TamThangBa;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.BossTet;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.mob.Mob;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class NuThan extends Boss {

    public NuThan() {
        super();
        this.limit = -1;
        this.name = "Nữ Thần";
        setInfo(100, Integer.MAX_VALUE, 10000, 10000, 10);
        setDefaultPart();
        this.isShow = false;
        setTypePK((byte) 5);
    }
    private static final Logger logger = Logger.getLogger(BossTet.class);

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) 1, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 5, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 3, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 4, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 19, (byte) 7).clone());

        } catch (Exception ex) {
            
            logger.error("init skill err");
        }
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
        logger.debug(String.format("BOSS %s vừa xuất hiện tại %s khu vực %d", this.name, map, zone.zoneID));
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        SessionManager.chatVip(String.format("%s: Đã tiêu diệt được %s mọi người đều ngưỡng mộ.", name, this.name));
    }

    @Override
    public void setDefaultLeg() {
        this.setLeg((short) 1236);
    }

    @Override
    public void setDefaultBody() {
        this.setBody((short) 1235);
    }

    @Override
    public void setDefaultHead() {
        this.setHead((short) 1234);
    }

    @Override
    public long injure(Player plAtt, Mob mob, long dameInput) {
        return Utils.nextInt(1, 3);
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        Player c = (Player) obj;
        if (Utils.isTrue(30, 100)) {
            dropItem(new Item(457), c);
        }
        if (Utils.isTrue(5, 100)) {
            dropItem(new Item(987), c);
        }
        dropItem(new Item(2237), c);
        Item gold = new Item(190);
        gold.quantity = 50_000_000;
        dropItem(gold, c);
    }

    @Override
    public void startDie() {
        int oldMap = this.zone.map.mapID;
        int[] mapIDs = new int[]{oldMap};
        super.startDie();
        Utils.setTimeout(() -> {
            NuThan boss = new NuThan();
            boss.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
        }, 10 * 60000);
    }

}
