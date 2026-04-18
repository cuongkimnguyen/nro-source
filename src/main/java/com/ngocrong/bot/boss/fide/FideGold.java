package com.ngocrong.bot.boss.fide;

import _HunrProvision.boss.Boss;
import _HunrProvision.boss.BossManager;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.consts.ItemName;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.mob.Mob;
import com.ngocrong.model.RandomItem;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class FideGold extends Boss {

    private static final Logger logger = Logger.getLogger(FideGold.class);
    boolean checkProtect;

    public FideGold() {
        super();
        this.distanceToAddToList = 500;
        this.limit = -1;
        this.name = "Fide Gold";
        setInfo(20_000, 1000000, 100000, 1000, 50);
        this.limitDame = 1;
        this.waitingTimeToLeave = 0;
        setTypePK((byte) 5);
        point = 5;
    }

    @Override
    public long injure(Player plAtt, Mob mobAtt, long dameInput) {
        if (!checkProtect && this.info.hp <= 100) {
            checkProtect = true;
            this.startProtect(30);
        }
        return 1;
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
            //skills.add(Skills.getSkill((byte) 8, (byte) 7).clone());
        } catch (CloneNotSupportedException ex) {
            
            logger.error("init skill");
        }
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 504);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 503);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 502);
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        Zone z = zone;
        if (z != null) {
            dropDragonBall(z, ItemName.NGOC_RONG_3_SAO, 20);
            dropDragonBall(z, ItemName.NGOC_RONG_4_SAO, 60);
            dropDragonBall(z, ItemName.NGOC_RONG_5_SAO, 100);
        }
    }

    @Override
    public void startDie() {
        BossManager.setFideGold(null);
        super.startDie();
    }

    private void dropDragonBall(Zone z, int itemId, int count) {
        for (int i = 0; i < count; i++) {
            Item item = new Item(itemId);
            item.quantity = 1;
            ItemMap im = new ItemMap(z.autoIncrease++);
            im.item = item;
            im.playerID = -1;
            im.x = (short) Utils.nextInt(0, z.map.width);
            im.y = z.map.collisionLand(im.x, getY());
            z.addItemMap(im);
            z.service.addItemMap(im);
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
}
