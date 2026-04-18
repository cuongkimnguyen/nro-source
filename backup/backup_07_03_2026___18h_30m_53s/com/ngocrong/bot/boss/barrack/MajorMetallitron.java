package com.ngocrong.bot.boss.barrack;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.event.Event;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.model.RandomItem;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class MajorMetallitron extends Boss {

    private static Logger logger = Logger.getLogger(MajorMetallitron.class);

    public MajorMetallitron() {
        super();
        this.limit = -1;
        this.name = "Trung úy Thép";
        this.isShow = false;
        setInfo(500, 1000000, 400, 10, 5);
        info.percentMiss = 50;
        setTypePK((byte) 5);
        point = 3;
    }

    @Override
    public void setLocation(Zone zone) {
        setX((short) 456);
        setY((short) 288);
        zone.enter(this);
    }

    @Override
    public ArrayList<Player> getEnemiesClosest() {
        ArrayList<Player> list = new ArrayList<>();
        for (Player enemy : listTarget) {
            if (enemy.isDead() || enemy.zone != zone) {
                continue;
            }
            if (enemy.isBoss()) {
                continue;
            }
            int type = -1;
            if (isMeCanAttackOtherPlayer(enemy)) {
                if ((type = zone.map.tileTypeAtPixel(enemy.getX(), enemy.getY()) & TMap.T_TOP) == TMap.T_TOP || (type & TMap.T_BRIDGE) == TMap.T_BRIDGE) {
                    list.add(enemy);
                }
            }
        }
        return list;
    }

    @Override
    public void move() {
        if (!meCanMove()) {
            return;
        }
        TMap map = zone.map;
        int w = map.width;
        int h = map.height;
        int y = 240;
        setX((short) (getX() + Utils.nextInt(-100, 100)));
        if (getX() < 50) {
            setX((short) 50);
        }
        if (getX() > 1100) {
            setX((short) 1100);
        }
        setY(map.collisionLand(getX(), (short) y));
        zone.service.move(this);
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList();
            Skill skill;
            skill = Skills.getSkill((byte) 0, (byte) 7).clone();
            skills.add(skill);
            skill = Skills.getSkill((byte) 1, (byte) 7).clone();
            skills.add(skill);
        } catch (CloneNotSupportedException ex) {
            
            logger.error("init skill err", ex);
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
        setHead((short) 129);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 130);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 131);
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        Player c = (Player) obj;
        Item item = new Item(Utils.nextInt(ItemName.NGOC_RONG_5_SAO, ItemName.NGOC_RONG_7_SAO));
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
