package com.ngocrong.bot.boss.barrack;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.event.Event;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.model.RandomItem;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class GeneralWhite extends Boss {

    private static Logger logger = Logger.getLogger(GeneralWhite.class);

    public ArrayList<String> chats = new ArrayList<>();

    public GeneralWhite() {
        super();
        this.limit = 100;
        this.name = "Trung úy Trắng";
        this.isShow = false;
        chats.add("Ha ha ha");
        chats.add("Xem mi dùng cách nào để hạ được ta");
        chats.add("Bulon đâu tiêu diệt hết bọn chúng cho ta");
        setInfo(500, 1000000, 400, 10, 5);
        info.percentMiss = 20;
        setTypePK((byte) 5);
        point = 3;
    }

    public void setLocation(Zone zone) {
        setX((short) 927);
        setY((short) 384);
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
            if (isMeCanAttackOtherPlayer(enemy)) {
                if (limit == -1) {
                    list.add(enemy);
                } else {
                    if (enemy.getX() > 755 && enemy.getX() < 1060) {
                        list.add(enemy);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public void move() {
        setX((short) Utils.nextInt(755, 1060));
        setY((short) 384);
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
    public void updateEveryFiveSeconds() {
        super.updateEveryFiveSeconds();
        if (!isDead()) {
            int index = Utils.nextInt(chats.size());
            chat(chats.get(index));
        }
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 141);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 142);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 143);
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

    @Override
    public void sendNotificationWhenAppear(String map) {
    }

    @Override
    public void sendNotificationWhenDead(String name) {
    }
}
