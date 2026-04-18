//package com.ngocrong.bot.boss.android;
//
//import com.ngocrong.bot.Boss;
//import com.ngocrong.consts.ItemName;
//import com.ngocrong.consts.MapName;
//import com.ngocrong.item.Item;
//import com.ngocrong.item.ItemMap;
//import com.ngocrong.item.ItemOption;
//import com.ngocrong.map.tzone.Zone;
//import com.ngocrong.server.PlayerManager;
//import com.ngocrong.skill.SkillName;
//import com.ngocrong.skill.Skills;
//import com.ngocrong.user.Player;
//import com.ngocrong.util.Utils;
//import org.apache.log4j.Logger;
//
//import java.util.ArrayList;
//
//public class DarkPic extends Boss {
//    private final static Logger logger = Logger.getLogger(DarkPic.class);
//
//    public boolean isSuper;
//
//    public static int count;
//
//    public DarkPic(boolean isSuper) {
//        super();
//        this.isSuper = isSuper;
//        this.limit = 1000;
//        if (isSuper) {
//            this.name = "Super Pic";
//            setInfo(20000000000L + 2000000000L * count, 1000000, 1000000, 1000000, 50);
//            info.options[201] = 50;
//        } else {
//            count++;
//            this.name = "Dark Pic";
//            setInfo(10000000000L + 1000000000L * count, 1000000, 500000, 1000000, 30);
//            info.options[201] = 20;
//        }
//        info.options[202] = 30;
//        point = 5;
//        setDefaultPart();
//        setTypePK((byte) 5);
//    }
//
//    @Override
//    public void initSkill() {
//        try {
//            skills = new ArrayList<>();
//            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_GALICK, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) SkillName.CHIEU_KAMEJOKO, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) SkillName.TAI_TAO_NANG_LUONG, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) SkillName.KHIEN_NANG_LUONG, (byte) 7).clone());
//        } catch (Exception ex) { 
//        }
//    }
//
//    @Override
//    public void sendNotificationWhenAppear(String map) {
//        PlayerManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
//        logger.debug(String.format("BOSS %s vừa xuất hiện tại %s khu vực %d", this.name, map, zone.zoneID));
//
//    }
//
//    @Override
//    public void sendNotificationWhenDead(String name) {
//        PlayerManager.chatVip(String.format("%s: Đã tiêu diệt được %s mọi người đều ngưỡng mộ.", name, this.name));
//    }
//
//    @Override
//    public void setDefaultLeg() {
//        if (isSuper) {
//            setLeg((short) 638);
//        } else {
//            setLeg((short) 239);
//        }
//    }
//
//    @Override
//    public void setDefaultBody() {
//        if (isSuper) {
//            setBody((short) 637);
//        } else {
//            setBody((short) 238);
//        }
//    }
//
//    @Override
//    public void setDefaultHead() {
//        if (isSuper) {
//            setHead((short) 636);
//        } else {
//            setHead((short) 237);
//        }
//    }
//
//    @Override
//    public void startDie() {
//        Zone z = zone;
//        super.startDie();
//        if (!isSuper) {
//            DarkPic darkPic = new DarkPic(true);
//            darkPic.setLocation(z);
//        } else {
//            int[] mapIDs = new int[]{MapName.NAM_BULON, MapName.DONG_BULON};
//            Utils.setTimeout(() -> {
//                DarkPic pic = new DarkPic(false);
//                pic.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
//            }, 60000);
//        }
//    }
//
//    @Override
//    public void update() {
//        super.update();
//        if (info.options[201] == 0) {
//            info.options[201] = isSuper ? 50 : 10;
//        }
//        if (info.options[202] == 0) {
//            info.options[202] = 30;
//        }
//    }
//
//    @Override
//    public void killed(Object obj) {
//        super.killed(obj);
//        if (obj == null) {
//            return;
//        }
//        Player killer = (Player) obj;
//        if (!isSuper && killer.taskMain != null && killer.taskMain.id == 38 && killer.taskMain.index == 1) {
//            killer.updateTaskCount(1);
//        }
//        if (isSuper && killer.taskMain != null && killer.taskMain.id == 38 && killer.taskMain.index == 2) {
//            killer.updateTaskCount(1);
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
//        if (percent < 10) {
//            int[] items = new int[]{ItemName.AO_THAN_XAYDA, ItemName.AO_THAN_LINH, ItemName.AO_THAN_NAMEC, ItemName.QUAN_THAN_NAMEC, ItemName.QUAN_THAN_LINH, ItemName.GIAY_THAN_LINH, ItemName.GIAY_THAN_XAYDA};
//            Item item = new Item(items[Utils.nextInt(items.length)]);
//            item.setDefaultOptions();
//            for (ItemOption o : item.options) {
//                int p = Utils.nextInt(0, 15);
//                o.param += o.param * p / 100;
//            }
//            int percent_star = Utils.nextInt(10);
//            if (percent_star < 5) {
//                item.addItemOption(new ItemOption(107, 6));
//            } else {
//                item.addItemOption(new ItemOption(107, Utils.nextInt(1, 5)));
//            }
//            item.quantity = 1;
//            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
//            itemMap.item = item;
//            itemMap.playerID = Math.abs(c.id);
//            itemMap.x = getX();
//            itemMap.y = zone.map.collisionLand(getX(), getY());
//            zone.addItemMap(itemMap);
//            zone.service.addItemMap(itemMap);
//        } else if (percent < 30) {
//            Item item;
//            if (isSuper) {
//                item = new Item(ItemName.GIAY_THAN_NAMEC);
//            } else {
//                item = new Item(ItemName.QUAN_THAN_XAYDA);
//            }
//            item.setDefaultOptions();
//            for (ItemOption o : item.options) {
//                int p = Utils.nextInt(0, 15);
//                o.param += o.param * p / 100;
//            }
//            int percent_star = Utils.nextInt(10);
//            if (percent_star < 5) {
//                item.addItemOption(new ItemOption(107, 6));
//            } else {
//                item.addItemOption(new ItemOption(107, Utils.nextInt(1, 5)));
//            }
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
//}
