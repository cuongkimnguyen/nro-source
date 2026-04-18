package com.ngocrong.bot.boss.barrack;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.event.Event;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.TMap;
import com.ngocrong.model.RandomItem;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.user.Info;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class NinjaMurasaki extends Boss {

    private static Logger logger = Logger.getLogger(NinjaMurasaki.class);

    private boolean isCloned, isClone, isAttack;
    public int dir = 1;

    public NinjaMurasaki(int id, boolean isClone) {
        super();
        this.id = id;
        this.limit = -1;
        this.isClone = isClone;
        this.isShow = false;
        this.name = "Ninja áo tím";
        setInfo(500, 100000, 300, 10, 5);
        info.percentMiss = 30;
        setTypePK((byte) 5);
        point = 3;
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
    public void move() {
        if (!meCanMove()) {
            return;
        }
        TMap map = zone.map;
        int w = map.width;
        int h = map.height;
        int y = 312;
        if (isClone) {
            this.dir *= Utils.nextInt(2) == 0 ? 1 : -1;
        }
        setX((short) (this.getX() + (Utils.nextInt(50, 100) * dir)));
        if (getX() < 50) {
            setX((short) 50);
            dir *= -1;
        }
        if (getX() > w - 50) {
            setX((short) (w - 50));
            dir *= -1;
        }
        setY(map.collisionLand(getX(), (short) y));
        zone.service.move(this);
    }

    @Override
    public void updateEveryThirtySeconds() {
        super.updateEveryThirtySeconds();
        this.dir *= -1;
    }

    @Override
    public void updateEveryFiveSeconds() {
        super.updateEveryFiveSeconds();
        this.isAttack = true;
    }

    @Override
    public void attack(Object obj) {
        if (this.isAttack) {
            super.attack(obj);
            this.isAttack = false;
        }
    }

    public void updateEveryOneSeconds() {
        if (!isClone && !isCloned && !isDead()) {
            long p = info.hp * 100 / info.hpFull;
            if (p < 30) {
                isCloned = true;
                for (int i = 0; i < 10; i++) {
                    NinjaMurasaki boss = new NinjaMurasaki(-(100000000 + i), true);
                    boss.setInfo(info.hpFull / 10, 100000, info.damageFull / 10, 0, 5);
                    boss.isClone = true;
                    boss.setX(getX());
                    boss.setY(getY());
                    zone.enter(boss);
                }
                info.recovery(Info.ALL, 100, true);
            }
        }
        super.updateEveryOneSeconds();
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
    }

    @Override
    public void sendNotificationWhenDead(String name) {
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 123);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 124);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 125);
    }

    @Override
    public void throwItem(Object obj) {
        if (isClone) {
            return;
        }
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
