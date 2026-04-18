package com.ngocrong.map.tzone;

import com.ngocrong.consts.MobName;
import com.ngocrong.model.MessageTime;
import com.ngocrong.user.Player;
import com.ngocrong.bot.boss.barrack.GeneralWhite;
import com.ngocrong.clan.Clan;
import com.ngocrong.clan.ClanMember;
import com.ngocrong.consts.MapName;
import com.ngocrong.map.Barrack;
import com.ngocrong.map.TMap;
import com.ngocrong.mob.Mob;
import com.ngocrong.server.SessionManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class ZBarrack extends Zone {

    private static Logger logger = Logger.getLogger(ZBarrack.class);
    private Barrack barrack;

    public ZBarrack(Barrack barrack, TMap map, int zoneId) {
        super(map, zoneId);
        this.barrack = barrack;
    }

    public void setBarrack(Clan clan) {
        long hp = 8000;
        long dame = 300;
        for (ClanMember mem : clan.getMembers()) {
            Player _player = SessionManager.findChar(mem.playerID);
            if (_player != null) {
                if (_player.info.hpFull > hp && _player.info.damageFull >= 300) {
                    hp = _player.info.hpFull;
                    dame = _player.info.damageFull;
                }
            }
        }
        for (Mob mob : this.mobs) {
            mob.maxHp = dame * 10;
            mob.hp = mob.maxHp;
            mob.damage1 = hp / 10;
            mob.damage2 = mob.damage1 - (mob.damage1 / 10);
        }
        for (Player _player : players) {
            if (_player.isBoss()) {
                _player.info.hpFull = dame * 100;
                _player.info.hp = _player.info.hpFull;
                _player.info.damageFull = hp / 20;
            }
        }

    }

    @Override
    public void respawn(ArrayList<Mob> mobs) {
        if (map.mapID == MapName.TUONG_THANH_3) {
            boolean isBossLive = false;
            try {
                Player boss = getBoss(GeneralWhite.class);
                if (boss != null && !boss.isDead()) {
                    isBossLive = true;
                }
            } catch (Exception e) {
                
                logger.error("check err", e);
            }
            for (Mob mob : mobs) {
                if (mob.templateId == MobName.BULON && isBossLive) {
                    mob.respawn();
                    removeWaitForRespawn(mob);
                    if (service != null) {
                        service.respawn(mob);
                    } else {
                        logger.error("service is null");
                    }
                }
            }
        }
    }

    @Override
    public void enter(Player p) {
        super.enter(p);
        if (p.isHuman()) {
            MessageTime ms = new MessageTime(MessageTime.DOANH_TRAI, "Trại độc nhãn", (short) barrack.getCountDown());
            p.addMessageTime(ms);
        }
    }

    @Override
    public void leave(Player p) {
        super.leave(p);
        if (p.isHuman()) {
            p.setTimeForMessageTime(MessageTime.DOANH_TRAI, (short) 0);
        }
    }
}
