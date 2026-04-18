package com.ngocrong.bot.boss.Cell;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.fide.Fide;
import com.ngocrong.bot.boss.fide.KuKu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.lib.RandomCollection;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.server.Config;
import com.ngocrong.server.DropRateService;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class XenCon extends Boss {

    private static final Logger logger = Logger.getLogger(Fide.class);
    public static RandomCollection<Integer> ITEMS = new RandomCollection<>();

    static {
        ITEMS.add(20, ItemName.GANG_THAN_LINH);
        ITEMS.add(20, ItemName.GANG_THAN_XAYDA);
        ITEMS.add(20, ItemName.GANG_THAN_NAMEC);
        ITEMS.add(20, ItemName.NHAN_THAN_LINH);
        ITEMS.add(10, ItemName.NGOC_RONG_3_SAO);
        ITEMS.add(10, ItemName.NGOC_RONG_2_SAO);

    }

    public int index = -1;

    public XenCon() {
        super();
        this.distanceToAddToList = 500;
        this.limit = -1;
        this.name = "Xên Con";
        setInfo(2_000_000_000L, 1000000, 80000, 1000, 50);
        if (Config.serverID() == 2) {
            setInfo(1_000_000_000L, 1000000, 80000, 1000, 50);
        }
        this.waitingTimeToLeave = 0;
        setTypePK((byte) 5);
    }

    public XenCon(int index) {
        this();
        this.name = "Xên Con " + (index + 1);
        this.index = index;
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) 0, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 1, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 2, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 3, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 4, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 5, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 14, (byte) 7).clone());
        } catch (CloneNotSupportedException ex) {
            
            logger.error("init skill");
        }
    }

    @Override
    public void killed(Object obj) {
        super.killed(obj);
        if (obj == null) {
            return;
        }
        Player killer = (Player) obj;
//        killer.pointBoss += 3;
        killer.isChangePoint = true;
        if (killer.taskMain != null && killer.taskMain.id == 26 && killer.taskMain.index == 2) {
            killer.updateTaskCount(1);
        }
    }

    public void throwItem2(Object obj) {
        if (obj == null) {
            return;
        }
        // dothanlinh
        if (Utils.isTrue(1 * DropRateService.getMobRate(), 10)) {
            int[] ao = {ItemName.AO_THAN_LINH, ItemName.AO_THAN_XAYDA, ItemName.AO_THAN_NAMEC};
            int[] quan = {ItemName.QUAN_THAN_LINH, ItemName.QUAN_THAN_NAMEC, ItemName.QUAN_THAN_XAYDA,
                ItemName.QUAN_THAN_LINH, ItemName.QUAN_THAN_NAMEC};
            RandomCollection<Item> rc = new RandomCollection<>();

            // Thêm áo với tỷ lệ 10%
            for (int idAo : ao) {
                rc.add(1, new Item(idAo));
            }

            // Thêm quần với tỷ lệ 10%
            for (int idQuan : quan) {
                rc.add(1, new Item(idQuan));
            }

            // Random vật phẩm theo tỷ lệ
            Item item = rc.next();
            item.setDefaultOptions();
            item.quantity = 1;
            Player c = (Player) obj;

            // Rơi vật phẩm chính
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.item = item;
            itemMap.playerID = Math.abs(c.id);
            itemMap.x = getX();
            itemMap.y = zone.map.collisionLand(getX(), getY());
            zone.addItemMap(itemMap);
            zone.service.addItemMap(itemMap);

            for (int i = 0; i < 10; i++) {

                itemMap = new ItemMap(zone.autoIncrease++);
                itemMap.item = new Item(457);
                itemMap.playerID = Math.abs(c.id);
                itemMap.x = (short) Utils.nextInt(getX() - 100, getX() + 100);
                itemMap.y = zone.map.collisionLand(getX(), getY());
                zone.addItemMap(itemMap);
                zone.service.addItemMap(itemMap);
            }
        }

    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        dropGroupC((Player) obj);
    }

    @Override
    public void startDie() {
        super.startDie();
        var _index = this.index;
        Utils.setTimeout(() -> {
            int[] mapId = new int[]{92, 93, 94, 96, 97, 98, 99, 100, 102, 103};
            XenCon xc = new XenCon(_index);
            xc.setLocation(mapId[Utils.nextInt(mapId.length)], -1);
        }, 10 * 60000L);
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
        setLeg((short) 266);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 265);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 264);
    }

}
