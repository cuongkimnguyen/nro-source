/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.bot.boss;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.lib.RandomCollection;
import com.ngocrong.map.tzone.Zone;
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
public class Chilled extends Boss {

    private static final Logger logger = Logger.getLogger(BlackGoku.class);

    private final boolean isSuper;

    public static final int[] MAPS = new int[]{160, 161, 162, 163};

    public static RandomCollection<Integer> ITEMS = new RandomCollection<>();

    public static RandomCollection<Integer> ITEM_SUPER = new RandomCollection<>();

    public static int count;

    static {
        ITEMS.add(33, ItemName.GIAY_THAN_XAYDA);
        ITEMS.add(33, ItemName.GIAY_THAN_LINH);
        ITEMS.add(33, ItemName.GIAY_THAN_NAMEC);
    }

    public Chilled(boolean isSuper) {
        super();
        this.isSuper = isSuper;
        this.limit = -1;
        if (isSuper) {
            this.name = "Chilled Cấp 2";
            setInfo(3000000000L, 1000000, 500000, 1000, 10);
            count++;
        } else {
            this.name = "Chilled Cấp 1";
            setInfo(2000000000L, 1000000, 100000, 1000, 10);
        }
        setDefaultPart();
        setTypePK((byte) 5);
        point = 5;
    }

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
    public void killed(Object obj) {
        super.killed(obj);
        if (obj == null) {
            return;
        }
        Player killer = (Player) obj;
        if (!isSuper && killer.taskMain != null && killer.taskMain.id == 29 && killer.taskMain.index == 5) {
            killer.updateTaskCount(1);
        }
        if (isSuper && killer.taskMain != null && killer.taskMain.id == 29 && killer.taskMain.index == 6) {
            killer.updateTaskCount(1);
        }
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 552);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 551);
    }

    @Override
    public void setDefaultHead() {
        if (isSuper) {
            setHead((short) 553);
        } else {
            setHead((short) 550);
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
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }

    }

    @Override
    public void startDie() {
        Zone z = zone;
        super.startDie();
        if (!isSuper) {
            Chilled bl = new Chilled(true);
            //bl.setLocation(z);
            bl.setLocation(z);
        } else {
            Utils.setTimeout(() -> {
                Chilled bl = new Chilled(false);
                bl.setLocation(z);
            }, Utils.nextInt(250000, 350000));
        }
    }

    @Override
    public void updateEveryFiveSeconds() {
        super.updateEveryFiveSeconds();
        /*if (zone != null) {
            List<Player> outZones = zone.getListChar(Zone.TYPE_HUMAN).stream()
                    .filter(player -> player.info.power < 80000000000L
                            || !player.isFullEquipStar(7)
                            || player.myPet == null
                            || player.myPet.info.power < 50000000000L
                            || !player.myPet.isFullEquipStar(7))
                    .collect(Collectors.toList());
            outZones.forEach(Player::goHome);
        }*/
    }

}
