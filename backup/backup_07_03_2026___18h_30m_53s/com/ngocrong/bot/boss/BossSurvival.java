//package com.ngocrong.bot.boss;
//
//import com.ngocrong.bot.Boss;
//import com.ngocrong.consts.ItemName;
//import com.ngocrong.item.Item;
//import com.ngocrong.item.ItemMap;
//import com.ngocrong.map.expansion.survival.Survival;
//import com.ngocrong.skill.Skills;
//import com.ngocrong.user.Player;
//import com.ngocrong.util.Utils;
//import org.apache.log4j.Logger;
//
//import java.util.ArrayList;
//
//public class BossSurvival extends Boss {
//    private static final Logger logger = Logger.getLogger(BossSurvival.class);
//    public Survival survival;
//
//    public BossSurvival(Survival survival, int level) {
//        super();
//        this.limit = -1;
//        this.name = "BOT";
//        setInfo(500L * level, 1000000, 10L * level, 10, 10);
//        setDefaultPart();
//        setTypePK((byte) 5);
//        this.survival = survival;
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
//        long now = System.currentTimeMillis();
//        int size = Utils.nextInt(3, 5);
//        for (int i = 0; i < size; i++) {
//            int itemID;
//            if (Utils.nextInt(100) < 20) {
//                itemID = ItemName.DAU_THAN_CAP_1;
//            } else if (survival.round < 4) {
//                itemID = Utils.nextInt(2030, 2042);
//            } else if (survival.round < 8) {
//                itemID = Utils.nextInt(2043, 2055);
//            } else {
//                itemID = Utils.nextInt(2056, 2068);
//            }
//            Item item = new Item(itemID);
//            item.setDefaultOptions();
//            item.quantity = 1;
//            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
//            itemMap.item = item;
//            itemMap.playerID = -1;
//            itemMap.x = (short) (getX() + Utils.nextInt(20, 30) * i);
//            itemMap.y = zone.map.collisionLand(itemMap.x, getY());
//            itemMap.throwTime = now + 30000;
//            itemMap.lockTime = now + 20000;
//            zone.addItemMap(itemMap);
//            zone.service.addItemMap(itemMap);
//        }
//    }
//
//    public void sendNotificationWhenAppear(String map) {
//    }
//
//    @Override
//    public void sendNotificationWhenDead(String name) {
//
//    }
//
//    @Override
//    public void setDefaultLeg() {
//        setLeg((short) 524);
//    }
//
//    @Override
//    public void setDefaultBody() {
//        setBody((short) 525);
//    }
//
//    @Override
//    public void setDefaultHead() {
//        setHead((short) 528);
//    }
//}
