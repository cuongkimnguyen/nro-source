/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _HunrProvision.daihoivothuat;

import static _HunrProvision.services.DaiHoiVoThuat_23Service.removeDHVT;
import com.ngocrong.bot.TrongTai;
import com.ngocrong.bot.boss.dhvt23.BossDHVT;
import com.ngocrong.bot.boss.dhvt23.ChaPa;
import com.ngocrong.bot.boss.dhvt23.ChanXu;
import com.ngocrong.bot.boss.dhvt23.JackyChun;
import com.ngocrong.bot.boss.dhvt23.LiuLiu;
import com.ngocrong.bot.boss.dhvt23.ODo;
import com.ngocrong.bot.boss.dhvt23.Bardock;
import com.ngocrong.bot.boss.dhvt23.PonPut;
import com.ngocrong.bot.boss.dhvt23.SoiHecQuyn;
import com.ngocrong.bot.boss.dhvt23.TauPayPay;
import com.ngocrong.bot.boss.dhvt23.ThienXinHang;
import com.ngocrong.bot.boss.dhvt23.Xinbato;
import com.ngocrong.bot.boss.dhvt23.Yamcha;
import com.ngocrong.consts.CMDPk;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.consts.MapName;
import com.ngocrong.item.ItemTime;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.network.Service;
import com.ngocrong.skill.Skill;
import com.ngocrong.user.Player;

/**
 *
 * @author Administrator
 */
public class DaiHoiVoThuat_23 {

    public Service service;
    public Player player;
    public byte round;
    public short time;
    public byte timeWait;
    public Zone zone;
    public TrongTai trongtai;
    public BossDHVT currentBoss;
    public static long last = System.currentTimeMillis();

    public DaiHoiVoThuat_23(Player player, byte round) {
        this.player = player;
        this.service = new Service(player.getSession());
        this.round = round;
        time = 180;
        timeWait = 15;
    }

    public BossDHVT initBoss() {
        switch (round) {
            case 0:
                return new SoiHecQuyn(player);
            case 1:
                return new ODo(player);
            case 2:
                return new Xinbato(player);
            case 3:
                return new ChaPa(player);
            case 4:
                return new PonPut(player);
            case 5:
                return new ChanXu(player);
            case 6:
                return new TauPayPay(player);
            case 7:
                return new Yamcha(player);
            case 8:
                return new JackyChun(player);
            case 9:
                return new ThienXinHang(player);
            case 10:
                return new LiuLiu(player);
            case 11:
                return new Bardock(player);
        }
        end();
        return null;
    }

    public void end() {
        timeWait = -1;
        time = -1;
        player.setCommandPK(CMDPk.NORMAL);
        player.setX((short) 300);
        player.setY((short) 360);
        player.zone.service.move2(player);
        TMap map = MapManager.getInstance().getMap(MapName.DAI_HOI_VO_THUAT_3);
        int zoneid = 0;
        for (int i = 0; i < map.zones.size(); i++) {
            if (map.zones.get(i).players.isEmpty()) {
                zoneid = i;
                break;
            }
        }
        player.zone = map.getZoneByID(zoneid);
        map.enterZone(player, zoneid);
        player.service.sendThongBao("Bạn đã vô địch đại hội võ thuật lần thứ 23");
        removeThis();
    }

    public void joinMap() {

        player.setX((short) 300);
        player.setY((short) 264);
        player.zone.service.move2(player);
        player.zone.leave(player);
        TMap map = MapManager.getInstance().getMap(MapName.DAI_HOI_VO_THUAT_3);
        int zoneid = 0;
        for (int i = 0; i < map.zones.size(); i++) {
            if (map.zones.get(i).players.size() == 0) {
                zoneid = i;
                break;
            }
        }
        player.zone = map.getZoneByID(zoneid);
        map.enterZone(player, zoneid);
        trongtai = new TrongTai(player.zone);
        this.zone = player.zone;
    }

    public void chat(String chat) {
        if (this.zone == null) {
            return;
        }
        if (trongtai == null) {
            return;
        }
        if (player == null) {
            return;
        }
        trongtai.chat(chat);
    }

    public void removeThis() {

        if (player != null) {
            player.setCommandPK(CMDPk.NORMAL);
            player.setTypePK((byte) 0);
            player.testCharId = -1;
        }

        if (currentBoss != null) {
            zone.leave(currentBoss);
            currentBoss.setCommandPK(CMDPk.NORMAL);
            currentBoss.setTypePK((byte) 0);
            currentBoss.testCharId = -1;
            currentBoss.zone = null;
        }

        if (trongtai != null) {
            zone.leave(trongtai);
            trongtai.zone = null;
        }
        // Xóa khỏi danh sách DaiHoiVoThuat_23
        // Giải phóng các tài nguyên liên kết
        this.service = null;
        this.player = null;
        this.zone = null;
        this.trongtai = null;
        this.currentBoss = null;

        removeDHVT(this);
    }

    public void update() {
        if (player != null && player.zone != null) {
            if (zone != null && player.zone != zone) {
                removeThis();
                return;
            }
            if (timeWait > 0) {
                if (System.currentTimeMillis() - last >= 1000) {
                    timeWait--;
                    last = System.currentTimeMillis();
                    switch (timeWait) {
                        case 13:
                            currentBoss = initBoss();
                            if (round == 4 || round == 6 || round == 8 || round == 10) {
                                for (Skill skill : player.getSkills()) {
                                    skill.lastTimeUseThisSkill = 0L;
                                }
                                service.updateCoolDown(player.getSkills());
                            }
                            ItemTime item = new ItemTime(ItemTimeName.THOI_MIEN, 3782, 14, false);
                            player.addItemTime(item);
                            player.setSleep(true);
                            service.setEffect(null, player.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 41);
                            player.info.hp = player.info.hpFull;
                            player.info.mp = player.info.mpFull;
                            service.playerLoadAll(player);
                            service.playerLoadAll(currentBoss);
                            player.moveTo(300, 264);
                            zone.service.setPosition(player, (byte) 0, 300, 264);
                            currentBoss.moveTo(475, 264);
                            TMap map = MapManager.getInstance().getMap(MapName.DAI_HOI_VO_THUAT_3);
                            map.enterZone(trongtai, player.zone.zoneID);
                            trongtai.moveTo(385, 264);
                            break;
                        case 11:

                            chat("Trận đấu giữa " + player.name + " vs " + currentBoss.name + " sắp diễn ra"); // 7 STUN
                            break;
                        case 7:
                            chat("Xin quý vị khán giả cho 1 tràng pháo tay cổ vũ cho 2 đấu thủ nào"); // 3 STUN
                            break;
                        case 4:
                            chat("Mọi người hãy ổn định chỗ ngồi, trận đấu sẽ bắt đầu sau 3 giây nữa"); // 0 STUN
                            break;
                        case 3:
                            chat("Trận đấu bắt đầu"); // -1 STUN
                            break;
                        case 2:
                            trongtai.zone.leave(trongtai);
                            player.moveTo(300, 264);
                            player.zone.service.move2(player);
                            currentBoss.moveTo(475, 264);
                            service.chat(player, "OK");
                            service.chat(currentBoss, "OK");
                            break;
                        case 1:
                            player.setCommandPK(CMDPk.THACH_DAU);
                            currentBoss.setCommandPK(CMDPk.THACH_DAU);
                            player.testCharId = currentBoss.id;
                            currentBoss.testCharId = player.id;
                            player.setTypePK((byte) 3);
                            currentBoss.setTypePK((byte) 3);
                            time = 181;
                            break;
                    }
                }

                return;
            }
            if (time > 0) {
                if (System.currentTimeMillis() - last >= 1000) {
                    time--;
                }
                if (currentBoss != null) {
                    currentBoss.update();
                    if (currentBoss.isDead() || currentBoss.isDie) {
                        winRound();
                        try {
                            currentBoss.zone.leave(currentBoss);
                        } catch (Exception e) {
                            

                        }
                    }
                }
            } else {
                timeout();
            }
        } else {
            removeThis();
        }
    }

    public void timeout() {
    }

    public void winRound() {
        round++;
        player.roundDHVT23 = round;
        if (round < 11) {
            timeWait = 15;
            time = 180;
        } else {
            end();
        }
    }
}
