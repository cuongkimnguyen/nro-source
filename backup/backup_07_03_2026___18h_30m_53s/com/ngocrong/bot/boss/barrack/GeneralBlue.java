package com.ngocrong.bot.boss.barrack;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.event.Event;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.tzone.ZTreasure;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.model.RandomItem;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class GeneralBlue extends Boss {
    
    private static Logger logger = Logger.getLogger(GeneralBlue.class);
    
    public GeneralBlue() {
        super();
        this.limit = -1;
        this.isShow = false;
        this.name = "Trung úy Xanh lơ";
        setInfo(500, 1000000, 400, 10, 5);
        info.percentMiss = 20;
        point = 3;
    }
    
    public void setLocation(Zone zone) {
        setX((short) 843);
        setY((short) 384);
        zone.enter(this);
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
            skill = Skills.getSkill((byte) 6, (byte) 3).clone();
            skills.add(skill);
        } catch (CloneNotSupportedException ex) {
            
            logger.error("init skill error", ex);
        }
    }
    
    @Override
    public void updateEveryOneSeconds() {
        super.updateEveryOneSeconds();
        if (this.typePk == 0 && listTarget.size() > 0) {
            setTypePK((byte) 5);
        }
    }
    
    @Override
    public void setDefaultHead() {
        setHead((short) 135);
    }
    
    @Override
    public void setDefaultBody() {
        setBody((short) 136);
    }
    
    @Override
    public void setDefaultLeg() {
        setLeg((short) 137);
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
