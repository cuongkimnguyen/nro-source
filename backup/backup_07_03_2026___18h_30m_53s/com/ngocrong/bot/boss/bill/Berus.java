package com.ngocrong.bot.boss.bill;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.MapName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.RandomCollection;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.model.RandomItem;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Berus extends Boss {

    private static final Logger logger = Logger.getLogger(Berus.class);

    static {

    }

    public Berus() {
        super();
        this.limit = -1;
        this.name = "Berus";
        setInfo(300000000L, 1000000, 500000, 10000, 10);
        setDefaultPart();
        setTypePK((byte) 5);
        limitDame = 1_000_000;
    }

    @Override
    public long injure(Player plAtt, com.ngocrong.mob.Mob mob, long dameInput) {
        if (plAtt == null || plAtt.getSession() == null || plAtt.getSession().user.getActivated() == 0) {
            return 0;
        }
        return Math.min(dameInput, limitDame);
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) 1, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 5, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 3, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 4, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 6, (byte) 7).clone());
            // skills.add(Skills.getSkill((byte) 19, (byte) 7).clone());
        } catch (Exception ex) {
            
            logger.error("init skill err");
        }
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 510);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 509);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 508);
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
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        Player c = (Player) obj;
//        c.pointBoss += 3;
//        c.isChangePoint = true;
        int percent = Utils.nextInt(100);
        if (percent < 2) {
            Item item = new Item(RandomItem.DO_THAN_LINH.next());
            item.setDefaultOptions();
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.item = item;
            itemMap.playerID = Math.abs(c.id);
            itemMap.x = getX();
            itemMap.y = zone.map.collisionLand(getX(), getY());
            zone.addItemMap(itemMap);
            zone.service.addItemMap(itemMap);
        } else if (percent < 4) {
            int[] awj = new int[]{
                650, 651, 658,
                652, 653, 660,
                654, 655, 662
            };
            Item item = new Item(awj[Utils.nextInt(awj.length)]);
            item.setDefaultOptions();
            item.options.add(new ItemOption(30, 0));
            item.quantity = 1;
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.item = item;
            itemMap.playerID = Math.abs(c.id);
            itemMap.x = getX();
            itemMap.y = zone.map.collisionLand(getX(), getY());
            zone.addItemMap(itemMap);
            zone.service.addItemMap(itemMap);
        } else if (percent < 94) {
            int[] ngocrong = new int[]{
                16, 17, 18
            };
            Item item = new Item(ngocrong[Utils.nextInt(ngocrong.length)]);
            item.setDefaultOptions();
            item.quantity = 1;
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.item = item;
            itemMap.playerID = Math.abs(c.id);
            itemMap.x = getX();
            itemMap.y = zone.map.collisionLand(getX(), getY());
            zone.addItemMap(itemMap);
            zone.service.addItemMap(itemMap);
        } else {
            int[] itemcap2 = new int[]{
                1021, 1022, 1023
            };
            Item item = new Item(itemcap2[Utils.nextInt(itemcap2.length)]);
            item.setDefaultOptions();
            item.quantity = 1;
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.item = item;
            itemMap.playerID = Math.abs(c.id);
            itemMap.x = getX();
            itemMap.y = zone.map.collisionLand(getX(), getY());
            zone.addItemMap(itemMap);
            zone.service.addItemMap(itemMap);
        }
        for (int i = 0; i < 10; i++) {
            var item = new Item(190);
            item.quantity = 500_000_000;
            this.dropItem(item, null);
        }
    }

    @Override
    public void startDie() {
        final Zone zone = this.zone;
        super.startDie();
        Utils.setTimeout(() -> {
            Boss boss = new Whis();
            boss.setLocation(zone);
        }, 2000);
    }
}
