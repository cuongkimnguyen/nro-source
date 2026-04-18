//package com.ngocrong.bot.boss.nguhanhson;
//
//import com.ngocrong.bot.Boss;
//import com.ngocrong.consts.ItemName;
//import com.ngocrong.item.Item;
//import com.ngocrong.item.ItemMap;
//import com.ngocrong.lib.RandomCollection;
//import com.ngocrong.map.tzone.Zone;
//import com.ngocrong.server.PlayerManager;
//import com.ngocrong.skill.Skills;
//import com.ngocrong.user.Player;
//import com.ngocrong.util.Utils;
//import org.apache.log4j.Logger;
//
//import java.util.ArrayList;
//
//public class BatGioi extends Boss {
//    private static final Logger logger = Logger.getLogger(BatGioi.class);
//
//    public static RandomCollection<Integer> ITEMS = new RandomCollection<>();
//
//    static {
//        ITEMS.add(1, ItemName.QUAN_THAN_NAMEC);
//        ITEMS.add(1, ItemName.AO_THAN_NAMEC);
//        ITEMS.add(0.1, ItemName.GANG_THAN_NAMEC);
//        ITEMS.add(0.5, ItemName.GIAY_THAN_NAMEC);
//    }
//
//    public BatGioi() {
//        super();
//        this.limit = -1;
//        this.name = "Bát giới";
//        setInfo(3000000000L, 1000000, 500000, 0, 10);
//        setDefaultPart();
//        setTypePK((byte) 5);
//    }
//
//    @Override
//    public void initSkill() {
//        try {
//            skills = new ArrayList<>();
//            skills.add(Skills.getSkill((byte) 1, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) 5, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) 3, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) 4, (byte) 7).clone());
//        } catch (Exception ex) { 
//            logger.error("init skill err");
//        }
//    }
//
//    @Override
//    public void throwItem(Object obj) {
//        if (obj == null) {
//            return;
//        }
//        Player c = (Player) obj;
//        int percent = Utils.nextInt(100);
//        if (percent < 20) {
//            Item item = new Item(Boss.ITEM_GOD.next());
//            item.setDefaultOptions();
//            item.randomParam();
//            item.quantity = 1;
//            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
//            itemMap.item = item;
//            itemMap.playerID = Math.abs(c.id);
//            itemMap.x = getX();
//            itemMap.y = zone.map.collisionLand(getX(), getY());
//            zone.addItemMap(itemMap);
//            zone.service.addItemMap(itemMap);
//        } else if (percent < 50) {
//            Item item = new Item(ItemName.DA_XANH_LAM);
//            item.setDefaultOptions();
//            item.quantity = Utils.nextInt(20, 30);
//            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
//            itemMap.item = item;
//            itemMap.playerID = Math.abs(c.id);
//            itemMap.x = getX();
//            itemMap.y = zone.map.collisionLand(getX(), getY());
//            zone.addItemMap(itemMap);
//            zone.service.addItemMap(itemMap);
//        } else {
//            Item item = new Item(ItemName.NGOC_RONG_3_SAO);
//            item.setDefaultOptions();
//            item.quantity = 1;
//            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
//            itemMap.item = item;
//            itemMap.playerID = Math.abs(c.id);
//            itemMap.x = getX();
//            itemMap.y = zone.map.collisionLand(getX(), getY());
//            zone.addItemMap(itemMap);
//            zone.service.addItemMap(itemMap);
//        }
//    }
//
//    @Override
//    public void sendNotificationWhenAppear(String map) {
//        PlayerManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
//    }
//
//    public void sendNotificationWhenDead(String name) {
//        PlayerManager.chatVip(String.format("%s: Đã tiêu diệt được %s mọi người đều ngưỡng mộ.", name, this.name));
//    }
//
//    @Override
//    public void setDefaultLeg() {
//        setLeg((short) 464);
//    }
//
//    @Override
//    public void setDefaultBody() {
//        setBody((short) 466);
//    }
//
//    @Override
//    public void setDefaultHead() {
//        setHead((short) 465);
//    }
//
//    public void startDie() {
//        Zone z = zone;
//        super.startDie();
//        Utils.setTimeout(() -> {
//            Boss boss = new NgoKhong();
//            boss.setLocation(z);
//        }, 10000);
//    }
//
//    @Override
//    public long formatDamageInjure(Object attacker, long dame) {
//        Player player = (Player) attacker;
//        if (player.gender == 0 || player.gender == 2) {
//            return dame / 10;
//        }
//        return dame;
//    }
//}
