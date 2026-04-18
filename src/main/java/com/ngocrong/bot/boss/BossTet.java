/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.bot.boss;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.fide.KuKu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.model.RandomItem;
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
public class BossTet extends Boss {

    public BossTet() {
        super();
        this.limit = -1;
        this.name = "Boss Sự kiện";
        setInfo(1000, Integer.MAX_VALUE, 10000, 10000, 10);
        setDefaultPart();
        setTypePK((byte) 5);
        this.limitDame = 1;
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
            skills.add(Skills.getSkill((byte) 8, (byte) 7).clone());
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
        this.setLeg((short) 91);
    }

    @Override
    public void setDefaultBody() {
        this.setBody((short) 90);
    }

    @Override
    public void setDefaultHead() {
        this.setHead((short) 89);
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        Player c = (Player) obj;
        int percent = Utils.nextInt(100);
        Item item;
        if (percent < 25) {
            item = new Item(2199);
        } else if (percent < 95) {
            item = new Item(16);
        } else {
            item = new Item(987);
        }
        item.quantity = 1;
        ItemMap itemMap = new ItemMap(zone.autoIncrease++);
        itemMap.item = item;
        itemMap.playerID = Math.abs(c.id);
        itemMap.x = getX();
        itemMap.y = zone.map.collisionLand(getX(), getY());
        zone.addItemMap(itemMap);
        zone.service.addItemMap(itemMap);
    }

    @Override
    public void startDie() {
        int oldMap = this.zone.map.mapID;
        int[] mapIDs = new int[]{oldMap};
        super.startDie();
        Utils.setTimeout(() -> {
            BossTet boss = new BossTet();
            boss.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
        }, 5 * 60000);
    }
}
