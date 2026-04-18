//package com.ngocrong.bot.boss;
//
//import com.ngocrong.bot.Boss;
//import com.ngocrong.consts.ItemName;
//import com.ngocrong.item.Item;
//import com.ngocrong.item.ItemMap;
//import com.ngocrong.map.tzone.Zone;
//import com.ngocrong.user.Player;
//import com.ngocrong.util.Utils;
//
//import java.util.ArrayList;
//
//public class GaPika extends Boss {
//
//    public GaPika() {
//        super();
//        this.distanceToAddToList = 100;
//        this.limit = 500;
//        name = "Gà Pika";
//        setInfo(Utils.nextInt(20000000, 30000000), 100000, 1000, 100, 20);
//        setDefaultPart();
//        this.waitingTimeToLeave = 5000;
//        this.sayTheLastWordBeforeDie = "Con gà cục tác lá chanh...";
//        setTypePK((byte) 5);
//        point = 0;
//    }
//
//    @Override
//    public void initSkill() {
//        skills = new ArrayList<>();
//    }
//
//
//    @Override
//    public void sendNotificationWhenAppear(String map) {
//
//    }
//
//
//    @Override
//    public void sendNotificationWhenDead(String name) {
//
//    }
//
//    @Override
//    public long formatDamageInjure(Object attacker, long dame) {
//        return Math.min(dame, info.hpFull / 100);
//    }
//
//    @Override
//    public void setDefaultLeg() {
//        setLeg((short) 559);
//    }
//
//
//    @Override
//    public void setDefaultBody() {
//        setBody((short) 558);
//    }
//
//    @Override
//    public void setDefaultHead() {
//        setHead((short) 557);
//    }
//
//    @Override
//    public void startDie() {
//        Zone z = zone;
//        super.startDie();
//        Utils.setTimeout(() -> {
//            GaPika gaPika = new GaPika();
//            gaPika.setLocation(z);
//        }, 60000);
//    }
//
//    @Override
//    public void throwItem(Object obj) {
//        if (obj == null) {
//            return;
//        }
//        Player c = (Player) obj;
//        Item item = new Item(ItemName.BO_VANG_EVENT_8_2023);
//        item.setDefaultOptions();
//        item.quantity = Utils.nextInt(3, 5);
//        ItemMap itemMap = new ItemMap(zone.autoIncrease++);
//        itemMap.item = item;
//        itemMap.playerID = Math.abs(c.id);
//        itemMap.x = getX();
//        itemMap.y = zone.map.collisionLand(getX(), getY());
//        zone.addItemMap(itemMap);
//        zone.service.addItemMap(itemMap);
//    }
//}
