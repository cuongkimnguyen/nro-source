/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.Tet2025;

import static com.ngocrong.NQMP.Tet2025.EventTet2025.Lixi;
import static com.ngocrong.NQMP.Tet2025.EventTet2025.Lixi2;
import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.Boss_Tet;
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
public class BossTet2 extends Boss {

    Boss_Tet maps;

    public BossTet2(Boss_Tet map) {
        super();
        this.limit = -1;
        this.name = "Boss Tết 2";
        setInfo(500L, Integer.MAX_VALUE, 10000, 10000, 10);
        setDefaultPart();
        this.limitDame = 1;
        maps = map;
    }
    private static final Logger logger = Logger.getLogger(BossTet2.class);

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) 1, (byte) 7).clone());
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

        item = new Item(Lixi2);
        item.setDefaultOptions();

        item.quantity = 1;
        ItemMap itemMap = new ItemMap(zone.autoIncrease++);
        itemMap.item = item;
        itemMap.playerID = Math.abs(c.id);
        itemMap.x = getX();
        itemMap.y = zone.map.collisionLand(getX(), getY());
        zone.addItemMap(itemMap);
        zone.service.addItemMap(itemMap);

//        ((Player) obj).pointKillBoss++;
//        ((Player) obj).isChangePoint = true;
//        ((Player) obj).service.sendThongBao("Bạn nhận thêm 1 tích điểm hạ Boss");
    }

    @Override
    public void startDie() {
        maps.next();
        super.startDie();
    }
}
