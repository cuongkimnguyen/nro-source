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
import com.ngocrong.user.Info;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class BaoCat extends Boss {

    public BaoCat() {
        super();
        this.limit = -1;
        this.name = "Boss Test Dame";
        setInfo(100_000_000_000L, Integer.MAX_VALUE, 10000, 10000, 10);
        setDefaultPart();
        setTypePK((byte) 5);
    }
    private static final Logger logger = Logger.getLogger(BossTet.class);

    public void joinMap() {
        this.setLocation(5, 0);
        this.zone.service.setPosition(this, (byte) 0, (short) 335, (short) 288);
    }

    @Override
    public void initSkill() {
        this.skills = new ArrayList();
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
    public void setDefaultHead() {
        setHead((short) 2141);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 2142);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 2143);
    }
    public long lastHeal = System.currentTimeMillis();

    @Override
    public void update() {
        if (System.currentTimeMillis() - lastHeal >= 60000) {
            this.info.hp = this.info.hpFull;
            lastHeal = System.currentTimeMillis();
            info.recovery(Info.ALL, 100, false);
        }
    }

    @Override
    public void throwItem(Object obj) {

    }

    @Override
    public void startDie() {
        super.startDie();
        Utils.setTimeout(() -> {
            BaoCat boss = new BaoCat();
            boss.joinMap();
        }, 1000);
    }
}
