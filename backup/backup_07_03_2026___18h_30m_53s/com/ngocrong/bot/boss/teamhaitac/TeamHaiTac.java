package com.ngocrong.bot.boss.teamhaitac;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.mob.Mob;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for the TeamHaiTac bosses
 */
public class TeamHaiTac extends Boss {

    // Counter to track how many gold bars have been dropped across all TeamHaiTac bosses
    protected static AtomicInteger totalGoldDropped = new AtomicInteger(0);
    // Maximum number of gold bars to drop across all TeamHaiTac bosses
    protected static final int MAX_GOLD_TO_DROP = 30;

    public TeamHaiTac() {
        super();
        this.percentDame = 5;
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        Player c = (Player) obj;

        // Calculate how many gold bars are left to drop
        int goldLeft = MAX_GOLD_TO_DROP - totalGoldDropped.get();

        // Determine how many gold bars to drop for this boss
        int goldToDrop = Math.min(Utils.nextInt(2, 5), goldLeft);

        // Update the counter atomically
        totalGoldDropped.addAndGet(goldToDrop);

        // Drop the gold bars
        for (int i = 0; i < goldToDrop; i++) {
            Item item = new Item(ItemName.THOI_VANG);
            item.setDefaultOptions();
            item.quantity = 1;
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.item = item;
            itemMap.playerID = -1;
            itemMap.x = (short) Utils.nextInt(50, zone.map.width - 50);
            itemMap.y = zone.map.collisionLand(itemMap.x, getY());
            zone.addItemMap(itemMap);
            zone.service.addItemMap(itemMap);
        }
    }

    // Reset the gold counter when all bosses are defeated or when spawning a new set
    public static void resetGoldCounter() {
        totalGoldDropped.set(0);
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DRAGON, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DEMON, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_GALICK, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_KAMEJOKO, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_MASENKO, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_ANTOMIC, (byte) 7).clone());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public long injure(Player player, Mob mob, long dame) {
        Skill skill = player.select;
        if (skill == null) {
            return 0;
        }
        if (skill.template.id == SkillName.TU_PHAT_NO || skill.template.id == SkillName.MAKANKOSAPPO || skill.template.id == SkillName.QUA_CAU_KENH_KHI) {
            return 0;
        }
        if (mob != null) {
            return 0;
        }
        if (dame > 1_000_000) {
            return 1_000_000;
        }
        return dame;
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDefaultLeg() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDefaultBody() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDefaultHead() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
