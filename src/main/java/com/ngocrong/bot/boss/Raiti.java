package com.ngocrong.bot.boss;

import _HunrProvision.boss.Boss;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Raiti extends Boss {

    public Raiti() {
        super();
        this.limit = -1;
        this.name = "Raiti";
        setInfo(500L, Integer.MAX_VALUE, 10, 1000, 10);
        setDefaultPart();
        setTypePK((byte) 5);
        this.limitDame = 10;
        this.percentDame = 5;
        this.canReactDame = false;
    }

    @Override
    public void initSkill() {
        try {
            this.skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) 1, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 5, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 3, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 4, (byte) 7).clone());
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Raiti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        SessionManager.chatVip(String.format("%s đã tiêu diệt %s", name, this.name));
    }

    @Override
    public long injure(Player plAtt, com.ngocrong.mob.Mob mob, long dameInput) {
        if (plAtt == null || plAtt.getSession() == null || plAtt.getSession().user.getActivated() == 0) {
            return 0;
        }
        return Math.min(dameInput, this.limitDame);
    }

    @Override
    public void throwItem(Object obj) {
//        if (Utils.isTrue(1, 2)) {
        if (!(obj instanceof Player)) {
            return;
        }
        Player p = (Player) obj;
        //     p.pointRaiti++;
//            p.pointBoss++;
        //   p.isChangePoint = true;
        int itemId = Utils.nextInt(441, 447);
        Item item = new Item(itemId);
        item.setDefaultOptions();
        item.quantity = 1;
        ItemMap itemMap = new ItemMap(zone.autoIncrease++);
        itemMap.item = item;
        itemMap.playerID = Math.abs(p.id);
        itemMap.x = getX();
        itemMap.y = zone.map.collisionLand(getX(), getY());
        zone.addItemMap(itemMap);
        zone.service.addItemMap(itemMap);
//        }
    }

    @Override
    public void startDie() {
        int[] mapIDs = new int[]{0, 7, 14};
        super.startDie();
        Utils.setTimeout(() -> {
            Raiti boss = new Raiti();
            boss.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
        }, 5 * 60 * 1000);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 490);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 491);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 492);
    }
}
