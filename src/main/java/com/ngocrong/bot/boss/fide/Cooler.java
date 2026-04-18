package com.ngocrong.bot.boss.fide;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.MapName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.RandomCollection;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Cooler extends Boss {

    private static final Logger logger = Logger.getLogger(Cooler.class);
    private final byte level;

    public static final int[] MAPS = new int[]{MapName.HANG_BANG};

    public static int count;

    public static RandomCollection<Integer> ITEMS = new RandomCollection<>();

    static {
//        ITEMS.add(2, ItemName.AO_THAN_LINH);
//        ITEMS.add(2, ItemName.AO_THAN_NAMEC);
//        ITEMS.add(2, ItemName.AO_THAN_XAYDA);
//        ITEMS.add(2, ItemName.QUAN_THAN_NAMEC);
//        ITEMS.add(2, ItemName.QUAN_THAN_LINH);
//        ITEMS.add(0.5, ItemName.QUAN_THAN_XAYDA);
//        ITEMS.add(2, ItemName.GIAY_THAN_XAYDA);
//        ITEMS.add(2, ItemName.GIAY_THAN_LINH);
//        ITEMS.add(0.5, ItemName.GIAY_THAN_NAMEC);
//        ITEMS.add(0.5, ItemName.GANG_THAN_LINH);
//        ITEMS.add(0.5, ItemName.GANG_THAN_XAYDA);
//        ITEMS.add(0.5, ItemName.GANG_THAN_NAMEC);
    }

    public Cooler(byte level) {
        super();
        this.level = level;
        this.distanceToAddToList = 500;
        this.limit = -1;
        if (this.level == 0) {
            this.name = "Cooler";
            setInfo(300000000, 1000000, 100000, 100, 50);
        } else {
            this.name = "Cooler 2";
            setInfo(500000000, 1000000, 150000, 100, 50);
            count++;
        }
        this.waitingTimeToLeave = 0;
        setTypePK((byte) 5);
        point = 5;
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
        } catch (CloneNotSupportedException ex) {
            
            logger.error("init skill");
        }
    }

    @Override
    public void setDefaultLeg() {
        if (this.level == 0) {
            setHead((short) 317);
        } else {
            setHead((short) 320);
        }
    }

    @Override
    public void setDefaultBody() {
        if (this.level == 0) {
            setBody((short) 318);
        } else {
            setBody((short) 321);
        }
    }

    @Override
    public void setDefaultHead() {
        if (this.level == 0) {
            setLeg((short) 319);
        } else {
            setLeg((short) 322);
        }
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        var killer = (Player) obj;
//        killer.pointBoss += 3;
//        killer.isChangePoint = true;
        dropGroupC((Player) obj);

    }

    public void startDie() {
        Zone z = zone;
        super.startDie();
        if (level == 0) {
            Cooler cooler = new Cooler((byte) 1);
            //cooler.setLocation(z);
            cooler.setLocation(MAPS[Utils.nextInt(MAPS.length)], -1);
        } else {
            Utils.setTimeout(() -> {
                Cooler cooler = new Cooler((byte) 0);
                cooler.setLocation(MAPS[Utils.nextInt(MAPS.length)], -1);
            }, 600000);
        }
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
        logger.debug(String.format("BOSS %s vừa xuất hiện tại %s khu vực %d", this.name, map, zone.zoneID));
    }

    public void sendNotificationWhenDead(String name) {
        SessionManager.chatVip(String.format("%s: Đã tiêu diệt được %s mọi người đều ngưỡng mộ.", name, this.name));
    }

    @Override
    public void updateEveryFiveSeconds() {
        super.updateEveryFiveSeconds();
        /*if (zone != null) {
            List<Player> outZones = zone.getListChar(Zone.TYPE_HUMAN).stream()
                    .filter(player -> player.info.power < 70000000000L
                            || !player.isFullEquipStar(6)
                            || player.myPet == null
                            || player.myPet.info.power < 50000000000L
                            || !player.myPet.isFullEquipStar(6))
                    .collect(Collectors.toList());
            outZones.forEach(Player::goHome);
        }*/
    }
}
