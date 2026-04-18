package com.ngocrong.bot.boss.mabu;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.MapName;
import com.ngocrong.event.Event;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.model.RandomItem;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.user.Info;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Yacon extends Boss {

    private static final Logger logger = Logger.getLogger(Yacon.class);

    public int count;

    public Yacon() {
        super();
        this.limit = -1;
        this.name = "Yacon";
        this.isShow = false;
        setInfo(40000000L, 1000000, 1000, 10, 5);
        info.percentMiss = 10;
        willLeaveAtDeath = false;
        setTypePK((byte) 5);
        setHaveEquipInvisible(true);
        point = 5;
        this.limitDame = this.info.originalHP / 20;
    }

    @Override
    public void setInfo(long hp, long mp, long dame, int def, int crit) {
        info.originalHP = hp;
        info.originalMP = mp;
        info.originalDamage = dame;
        info.originalDefense = def;
        info.originalCritical = crit;
        info.setInfo();
        info.recovery(Info.ALL, 100, false);
        info.percentMiss = 10;
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) 0, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 2, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 4, (byte) 7).clone());
        } catch (CloneNotSupportedException ex) {
            
            logger.error("init skill error", ex);
        }
    }

//    @Override
//    public long formatDamageInjure(Object attacker, long dame) {
//        return Math.min(dame, info.hpFull / 20);
//    }
    @Override
    public void killed(Object obj) {
        super.killed(obj);
        if (obj == null) {
            return;
        }
        Player killer = (Player) obj;
        killer.addAccumulatedPoint(this.point);
        if (killer.taskMain != null && killer.taskMain.id == 27 && killer.taskMain.index == 3) {
            killer.updateTaskCount(1);
        }
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        Player c = (Player) obj;
        int percent = Utils.nextInt(1000);
        Item item;
        if (percent < 226) {
            item = new Item(RandomItem.DO_CUOI.next());
            item.setDefaultOptions();
            item.addRandomOptionMabu(percent);
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
    public void sendNotificationWhenAppear(String map) {
    }

    @Override
    public void sendNotificationWhenDead(String name) {
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 415);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 416);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 417);
    }

    public void startDie() {
        super.startDie();
        Utils.setTimeout(() -> {
            setTypePK((byte) 5);
            wakeUpFromDead();
        }, 60000);
    }

}
