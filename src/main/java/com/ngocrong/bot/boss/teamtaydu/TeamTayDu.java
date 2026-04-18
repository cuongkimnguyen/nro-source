package com.ngocrong.bot.boss.teamtaydu;

import _HunrProvision.boss.Boss;
import com.ngocrong.item.Item;
import com.ngocrong.mob.Mob;
import com.ngocrong.server.DropRateService;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;

/**
 * Lớp cơ sở cho các boss thuộc team Tây Du Ký
 */
public class TeamTayDu extends Boss {

    public TeamTayDu() {
        super();
        this.percentDame = 5;

    }

    @Override
    public long injure(Player player, Mob mob, long dame) {
        Skill skill = player.select;
        if (skill == null) {
            return 0;
        }
        if (skill.template.id == SkillName.TU_PHAT_NO || skill.template.id == SkillName.MAKANKOSAPPO
                || skill.template.id == SkillName.QUA_CAU_KENH_KHI) {
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
    public void throwItem(Object obj) {
        if (obj instanceof Player) {
            Player pl = (Player) obj;
            int thoivang = Utils.nextInt(5, 10);
            for (int i = 0; i < 10; i++) {
                int tempId = Utils.nextInt(5) == 0 ? 16 : 17;
                this.dropItem(new Item(tempId), null);
            }
            for (int i = 0; i < 30; i++) {
                int tempId = Utils.nextInt(220, 224);
                this.dropItem(new Item(tempId), null);
            }
            for (int i = 0; i < 15; i++) {
                int tempId = Utils.nextInt(381, 385);
                this.dropItem(new Item(tempId), null);
            }
            for (int i = 0; i < thoivang; i++) {
                int tempId = 457;
                this.dropItem(new Item(tempId), null);
            }
            if (Utils.isTrue(1 * DropRateService.getMobRate(), 100)) {
                // dothanlinh
                int[] temp = new int[] { 555, 556, 557, 558, 559, 560, 563, 565, 567 };
                Item item = new Item(temp[Utils.nextInt(temp.length)]);
                item.setDefaultOptions();
                dropItem(item, pl);
            }
        }
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

        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
