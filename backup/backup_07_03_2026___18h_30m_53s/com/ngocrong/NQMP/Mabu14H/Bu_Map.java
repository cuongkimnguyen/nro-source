/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.Mabu14H;

import _HunrProvision.HoangAnhDz;
import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.MapName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.mob.Mob;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class Bu_Map extends Boss {

    static final byte[][] bitArrays = {
        {1, 0, 0, 0, 0},
        {0, 1, 0, 0, 0},
        {0, 0, 1, 0, 0},
        {0, 0, 0, 1, 0},
        {0, 0, 0, 0, 1}
    };
    byte[] bitDrop = new byte[]{};
    public static ArrayList<Bu_Map> list = new ArrayList<>();

    public static void initBoss() {
        list.clear();
        TMap map = MapManager.getInstance().getMap(MapName.CONG_PHI_THUYEN_2);
        for (Zone zone : map.zones) {
            Bu_Map mabu = new Bu_Map((byte) 0);
            mabu.setLocation(zone);
            list.add(mabu);
        }
    }

    private static final Logger logger = Logger.getLogger(Bu_Map.class);
    private final byte level;

    public Bu_Map(byte level) {
        super();
        this.level = level;
        this.distanceToAddToList = 500;
        this.limit = -1;
        this.percentDame = 20;
        switch (this.level) {
            case 0:
                this.name = "Bư Mập";
                setInfo(500_000_000, 1000000, 15000, 1000, 10);
                bitDrop = bitArrays[Utils.nextInt(bitArrays.length)];
                break;
            case 1:
                this.name = "Super Bư";
                setInfo(500_000_000, 1000000, 20000, 1000, 15);
                break;
            case 2:
                this.name = "Bư Tênk";
                setInfo(500_000_000, 1000000, 20000, 1000, 20);
                break;
            case 3:
                this.name = "Bư Han";
                setInfo(500_000_000, 1000000, 20000, 1000, 25);
                break;
            case 4:
                this.name = "KidBu";
                setInfo(200_000, 1000000, 20000, 1000, 30);
                break;
            default:
                break;
        }
        this.waitingTimeToLeave = 100;

        setDefaultHead();
        setDefaultBody();
        setDefaultLeg();
        setTypePK((byte) 5);
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) 0, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 2, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 4, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 8, (byte) 7).clone());
        } catch (CloneNotSupportedException ex) {
            
            logger.error("init skill error", ex);
        }
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        if (level == 0) {
            SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
        }
        logger.debug(String.format("BOSS %s vừa xuất hiện tại %s khu vực %d", this.name, map, zone.zoneID));
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        int[] Quan = {ItemName.QUAN_THAN_LINH, ItemName.QUAN_THAN_LINH, ItemName.QUAN_THAN_LINH,
            ItemName.QUAN_THAN_NAMEC, ItemName.QUAN_THAN_NAMEC, ItemName.QUAN_THAN_NAMEC, ItemName.QUAN_THAN_NAMEC,
            ItemName.QUAN_THAN_XAYDA};
        Player c = (Player) obj;
        if (bitDrop[this.level] == 1) {
            Item item = new Item(Quan[Utils.nextInt(Quan.length)]);
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

    }

    @Override
    public long injure(Player plAtt, Mob mob, long dameInput) {
        if (dameInput <= 0) {
            return -1;
        }
        long dame = dameInput;
        if (level == 4) {
            var skill = plAtt.select;
            if (skill == null) {
                return -1;
            }
            if (skill.template.id == SkillName.QUA_CAU_KENH_KHI || skill.template.id == SkillName.MAKANKOSAPPO) {
                return dameInput;
            } else {
                return -1;
            }
        }
        return dameInput;
    }

    @Override
    public void update() {
        LocalDateTime now = LocalDateTime.now();
        if (now.getHour() != 14 && zone != null) {
            System.err.println("hourt : " + now.getHour());
            startDie();
            return;
        }
        super.update();
    }

    @Override
    public void sendNotificationWhenDead(String name) {
    }

    @Override
    public void setDefaultHead() {
        switch (this.level) {
            case 0:
                setHead((short) 297);
                break;
            case 1:
                setHead((short) 421);
                break;
            case 2:
                setHead((short) 424);
                break;
            case 3:
                setHead((short) 427);
                break;
            case 4:
                setHead((short) 439);
                break;
            default:
                break;
        }
    }

    @Override
    public void setDefaultBody() {
        switch (this.level) {
            case 0:
                setBody((short) 298);
                break;
            case 1:
                setBody((short) 422);
                break;
            case 2:
                setBody((short) 425);
                break;
            case 3:
                setBody((short) 428);
                break;
            case 4:
                setBody((short) 440);
                break;
            default:
                break;
        }
    }

    @Override
    public void setDefaultLeg() {
        switch (this.level) {
            case 0:
                setLeg((short) 299);
                break;
            case 1:
                setLeg((short) 423);
                break;
            case 2:
                setLeg((short) 426);
                break;
            case 3:
                setLeg((short) 429);
                break;
            case 4:
                setLeg((short) 441);
                break;
            default:
                break;
        }
    }

    @Override
    public void startDie() {
        Zone z = zone;
        super.startDie();
        if (level < 4 && LocalDateTime.now().getHour() == 14) {
            Bu_Map bu = new Bu_Map((byte) (level + 1));
            bu.setLocation(z);
            bu.bitDrop = this.bitDrop;
        }
    }

    @Override
    public void killed(Object obj) {
        super.killed(obj);
        if (obj == null) {
            return;
        }
        Player killer = (Player) obj;
        killer.addAccumulatedPoint(this.point);
        if (killer.taskMain != null && killer.taskMain.id == 28 && killer.taskMain.index == level) {
            killer.updateTaskCount(1);
        }
    }
}
