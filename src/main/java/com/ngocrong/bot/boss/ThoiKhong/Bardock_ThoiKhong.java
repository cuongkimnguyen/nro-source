/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.bot.boss.ThoiKhong;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.mob.Mob;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class Bardock_ThoiKhong extends Boss {

    public Bardock_ThoiKhong() {
        super();
        this.name = "Bardock";
        setInfo(200_000_000L, 20000, 20, 3, 5);
        this.percentDame = 10;
        setDefaultPart();
        setTypePK((byte) 5);
    }
    private static Logger logger = Logger.getLogger(Bardock_ThoiKhong.class);

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        int random = Utils.nextInt(100);
        Item item;
        if (random < 50) {
            item = new Item(16);
        } else if (random < 85) {
            item = new Item(17);
        } else if (random < 95) {
            item = new Item(ItemName.DA_MA_THUAT);
        } else {
            item = new Item(ItemName.DA_GALLERY);
        }
        item.setDefaultOptions();
        item.quantity = 1;
        dropItem(item, (Player) obj);
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList();
            Skill skill;
            skill = Skills.getSkill((byte) 0, (byte) 1).clone();
            skills.add(skill);
        } catch (CloneNotSupportedException ex) {
            
            logger.error("init skill err", ex);
        }
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        super.sendNotificationWhenAppear(map);
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        super.sendNotificationWhenDead(name);
    }

    @Override
    public void setDefaultLeg() {
        this.setLeg((short) 1020);
    }

    @Override
    public void setDefaultBody() {
        this.setBody((short) 1019);
    }

    @Override
    public long injure(Player plAtt, Mob mobAtt, long dameInput) {
        return 100000;
    }

    @Override
    public void setDefaultHead() {
        this.setHead((short) 1018);
    }
    public static final int[] MAPS = new int[]{160, 161, 162, 163};

    @Override
    public void startDie() {
        super.startDie();

        Utils.setTimeout(() -> {
            Bardock_ThoiKhong bl = new Bardock_ThoiKhong();
            bl.setLocation(MAPS[Utils.nextInt(MAPS.length)], -1);
        }, 10 * 60000);

    }
}
