package com.ngocrong.bot.boss.teamtaydu;

import _HunrProvision.boss.Boss;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.map.TayDuManager;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class NgoKhong extends TeamTayDu {

    private static final Logger logger = Logger.getLogger(NgoKhong.class);

    private final TayDuManager team;

    public NgoKhong(TayDuManager team) {
        super();
        this.team = team;
        this.distanceToAddToList = 1000;
        this.limit = 1000;
        this.name = "Ngộ Không";
        setInfo(500000000, 1500000, 15000, 100, 5);
        this.willLeaveAtDeath = false;
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
            skills.add(Skills.getSkill((byte) SkillName.KHIEN_NANG_LUONG, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) SkillName.TROI, (byte) 7).clone());
        } catch (Exception ex) {
            logger.error("init skill err", ex);
        }
    }

    @Override
    public void startDie() {
        try {
            super.startDie();
        } finally {
            team.checkBossDeath();
            zone.leave(this);
        }
    }

    @Override
    public void throwItem(Object obj) {
        if (obj instanceof Player) {
            Item item = new Item(547);
            item.options.add(new ItemOption(77, 25));
            item.options.add(new ItemOption(103, 25));
            item.options.add(new ItemOption(5, Utils.nextInt(10, 15)));
            item.options.add(new ItemOption(93, 1));
            dropItem(item, (Player) obj);
            super.throwItem(obj);
        }
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 464);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 463);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 462);
    }
}
