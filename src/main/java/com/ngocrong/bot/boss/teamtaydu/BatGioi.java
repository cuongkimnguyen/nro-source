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

public class BatGioi extends TeamTayDu {

    private static final Logger logger = Logger.getLogger(BatGioi.class);

    private final TayDuManager team;

    public BatGioi(TayDuManager team) {
        super();
        this.team = team;
        this.distanceToAddToList = 1000;
        this.limit = 1000;
        this.name = "Bát Giới";
        setInfo(500000000, 1500000, 13000, 100, 5);
        this.willLeaveAtDeath = false;
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
            Item item = new Item(548);
            item.options.add(new ItemOption(77, 25));
            item.options.add(new ItemOption(103, 25));
            item.options.add(new ItemOption(94, 10));
            item.options.add(new ItemOption(93, 1));
            dropItem(item, (Player) obj);
            super.throwItem(obj);
        }
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 464); // Theo yêu cầu, Bát Giới dùng chung leg với Ngộ Không
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 466);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 465);
    }
}
