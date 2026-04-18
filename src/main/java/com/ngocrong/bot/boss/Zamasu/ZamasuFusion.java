/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.bot.boss.Zamasu;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.mob.Mob;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class ZamasuFusion extends Boss {

    public boolean isSuper;

    public ZamasuFusion(boolean isSuper) {
        super();
        this.isSuper = isSuper;
        this.limit = -1;
        if (isSuper) {
            this.name = "Zamasu 2";
            setInfo(1_000_000_000L, 1000000, 500000, 1000, 10);
        } else {
            this.name = "Zamasu";
            setInfo(500_000_000L, 1000000, 1000000, 1000, 10);
        }
        var limitdamage = isSuper ? 50_000_000 : 500_000;
        this.limitDame = limitdamage;
        setDefaultPart();
        setTypePK((byte) 5);
        point = 5;
    }

    private static final Logger logger = Logger.getLogger(ZamasuFusion.class);

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) 1, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 5, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 3, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 4, (byte) 7).clone());
        } catch (Exception ex) {
            
            logger.error("init skill err");
        }
    }

    @Override
    public void setDefaultLeg() {
        if (isSuper) {
            setLeg((short) 1288);
        } else {
            setLeg((short) 905);
        }
    }

    @Override
    public void setDefaultBody() {
        if (isSuper) {
            setBody((short) 1287);
        } else {
            setBody((short) 904);
        }
    }

    @Override
    public void setDefaultHead() {
        if (isSuper) {
            setHead((short) 1286);
        } else {
            setHead((short) 903);
        }
    }

    @Override
    public long injure(Player plAtt, Mob mob, long dameInput) {
        var limitDame = isSuper ? 50_000_000 : 500_000;
        return Math.min(limitDame, dameInput);
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        Item item = new Item(ItemName.VE_TICH_DIEM);
        item.quantity = 3;
        dropItem(item, (Player) obj);

        item = new Item(ItemName.VE_TICH_DIEM);
        item.quantity = 1;
        dropItem(item, null);
        item = new Item(ItemName.VE_TICH_DIEM);
        item.quantity = 1;
        dropItem(item, null);
    }

    @Override
    public void startDie() {
        final Zone zone = this.zone;
        super.startDie();
        if (!isSuper) {
            Utils.setTimeout(() -> {
                ZamasuFusion bl = new ZamasuFusion(true);
                bl.setLocation(zone);
            }, 3000);
        } else {
            Utils.setTimeout(() -> {
                ZamasuFusion bl = new ZamasuFusion(false);
                bl.setLocation(182, -1);
            }, 10 * 60000);
        }
    }
}
