package com.ngocrong.map.tzone;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngocrong.NQMP.DHVTSieuHang.CloneSieuHang;
import com.ngocrong.NQMP.DHVT_SH.DHVT_SH_Service;
import com.ngocrong.NQMP.DHVT_SH.StartDHVT_SH;
import com.ngocrong.NQMP.DHVT_SH.SuperRank;
import com.ngocrong.NQMP.DHVT_SH.Top_SieuHang;
import _HunrProvision.HoangAnhDz;
import _HunrProvision.boss.Boss;
import com.ngocrong.bot.Disciple;
import com.ngocrong.bot.TrongTai;
import com.ngocrong.bot.boss.dhvt23.*;
import com.ngocrong.consts.CMDPk;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.MapName;
import com.ngocrong.data.DHVTSieuHangData;
import com.ngocrong.data.DiscipleData;
import com.ngocrong.data.PlayerData;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.item.ItemTime;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.network.Session;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Server;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Info;
import com.ngocrong.user.Player;
import _HunrProvision.ConfigStudio;
import _event.newyear_2026.EventNewYear2026;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
@Setter
public class ArenaSieuHang extends Zone {

    private static final Logger logger = Logger.getLogger(ArenaSieuHang.class);

    public TrongTai trongTai;
    private ArrayList<Integer> registrationList;
    public ReadWriteLock lock = new ReentrantReadWriteLock();
    private Player currFightingPlayer;
    private int countDownToStart;
    private int countDownToEnd;
    private long last;
    private Boss boss;
    private List<Boss> bosses;
    private boolean started;
    private boolean isFinish;
    private int countDown;
    private int clonePlayerId;
    private int challengerRanking;
    private int pointClone;
    private PlayerData data;
    private int bossPointDhvt;

    public ArenaSieuHang(TMap map, int zoneId) {
        super(map, zoneId);
        this.countDownToStart = 15;
        this.countDown = 180;
        this.countDownToEnd = 5;
        registrationList = new ArrayList<>();
        trongTai = new TrongTai();
        trongTai.setX((short) 375);
        trongTai.setY((short) 264);
        enter(trongTai);
    }

    public void fight() {
        started = true;
        leave(trongTai);
        currFightingPlayer.setX((short) 322);
        currFightingPlayer.setY((short) 312);
        service.setPosition(currFightingPlayer, (byte) 0);
        currFightingPlayer.info.recovery(Info.ALL, 100, true);
        for (Skill skill : currFightingPlayer.skills) {
            if (skill.coolDown > 0) {
                skill.lastTimeUseThisSkill = System.currentTimeMillis() - 3600000;
            }
        }
        currFightingPlayer.service.updateCoolDown(currFightingPlayer.skills);
        if (boss != null) {
            currFightingPlayer.testCharId = boss.id;
            boss.testCharId = currFightingPlayer.id;
            currFightingPlayer.setCommandPK(CMDPk.DAI_HOI_VO_THUAT);
            boss.setCommandPK(CMDPk.DAI_HOI_VO_THUAT);
            currFightingPlayer.setTypePK((byte) 3);
            boss.setTypePK((byte) 3);
        } else {
            close();
        }
    }

    @Override
    public void update() {
        super.update();
        if (currFightingPlayer != null && currFightingPlayer.zone != this) {
            lose();
            close();
        }
        if (currFightingPlayer != null && trongTai != null && running) {
            if (boss == null) {
                setNextBoss();
            }
            long now = System.currentTimeMillis();
            if (now - last >= 1000) {
                last = now;
                if (countDownToStart > 0) {
                    countDownToStart--;
                    if (countDownToStart == 10) {
                        this.service.playerLoadAll(boss);
                        service.chat(trongTai, "Trận đấu sắp diễn ra");
                    } else if (countDownToStart == 8) {
                        service.chat(trongTai, "3");
                    } else if (countDownToStart == 6) {
                        service.chat(trongTai, "2");
                    } else if (countDownToStart == 4) {
                        service.chat(trongTai, "1");
                    } else if (countDownToStart == 2) {
                        service.chat(trongTai, "Trận đấu bắt đầu");
                    } else if (countDownToStart == 0) {
                        fight();
                    }
                }
                if (started) {
                    if (countDown > 0) {
                        countDown--;
                        if (countDown == 0) {
                            if (!isFinish) {
                                currFightingPlayer.service.serverMessage("Bạn đã thất bại");
                                close();
                            }
                            return;
                        }
                        checkResult();
                        if (_HunrProvision.ConfigStudio.EVENT_NEWYEAR_2026) {
                            _event.newyear_2026.EventNewYear2026.checkHoldTime(currFightingPlayer);
                        }
                    }
                }
            }
        }
    }

    public void setNextBoss() {
        if (!running) {
            return;
        }
        boss = new CloneSieuHang();
        Gson gson = new Gson();
        boss.itemBody = new Item[100];
        try {
            JSONArray itemBody = new JSONArray(data.itemBody);
            int lent = itemBody.length();
            for (int i = 0; i < lent; i++) {
                try {
                    Item item = new Item();
                    item.load(itemBody.getJSONObject(i));
                    int index = item.template.type;
                    if (index == 7) {
                        continue;
                    }
                    if (index == 32) {
                        index = 6;
                    } else if (index == 23 || index == 24) {
                        index = 7;
                    } else if (index == 11) {
                        index = 8;
                    } else if (index == 40) {
                        index = 9;
                    } else if (index == 19) {
                        index = 11;
                    } else if (index == 26) {
                        index = 13;
                    } else if (index == 35) {
                        index = 14;
                    }
                    if (index > 35) {
                        index = 17;
                    }
                    boss.itemBody[index] = item;
                } catch (Exception e) {
                    
                    e.printStackTrace();
                    logger.error("Loi load clone player");
                    close();
                }
            }
        } catch (Exception e) {
            
            logger.debug("failed!", e);
        }
        boss.pointDhvtSieuhang = Math.max(bossPointDhvt, 0);
        boss.itemTimes = gson.fromJson(data.itemTime, new TypeToken<ArrayList<ItemTime>>() {
        }.getType());
        boss.name = data.name;
        boss.gender = data.gender;
        Info info = gson.fromJson(data.info, Info.class);
        boss.fusionType = data.fusion;
        boss.typePorata = data.porata;
        if (boss.fusionType != 1) {
            boss.setNhapThe(true);
        }
        boss.info.setChar(boss);
        boss.info.setPowerLimited();
        boss.setStatusItemTime();
        boss.setInfo(info.originalHP, info.originalMP, info.originalDamage, info.originalDefense, info.originalCritical);
        int head = data.head2;
        int body = data.body;
        int leg = data.leg;
        boss.setHead((short) head);
        boss.setBody((short) body);
        boss.setLeg((short) leg);
        try {
            JSONArray skills = new JSONArray(data.skill);
            int lent2 = skills.length();
            for (int i = 0; i < lent2; i++) {
                JSONObject obj = skills.getJSONObject(i);
                int templateId = obj.getInt("id");
                int level = obj.getInt("level");
                long lastTimeUseThisSkill = obj.getLong("last_time_use");
                Skill skill = Skills.getSkill(data.classId, templateId, level);
                if (skill != null && skill.template.id != SkillName.QUA_CAU_KENH_KHI && skill.template.id != SkillName.MAKANKOSAPPO
                        && skill.template.id != SkillName.TROI && skill.template.id != SkillName.THOI_MIEN && skill.template.id != SkillName.BIEN_HINH && skill.template.id != SkillName.DE_TRUNG) {
                    Skill skill2 = skill.clone();
                    if (data.id != 1) {
                        skill2.lastTimeUseThisSkill = lastTimeUseThisSkill;
                    }
                    boss.skills.add(skill2);
                }
            }
        } catch (Exception ignored) {

        }
        Disciple deTu = new Disciple();
        try {
            Optional<DiscipleData> discipleOptional = GameRepository.getInstance().disciple.findById(-clonePlayerId);
            if (discipleOptional.isPresent()) {

                DiscipleData discipleData = discipleOptional.get();
                deTu.typeDisciple = discipleData.type;
                deTu.itemBody = new Item[10];
                deTu.petBonus = discipleData.bonus;
                JSONArray itemBody = new JSONArray(discipleData.itemBody);
                int lent = itemBody.length();
                for (int i = 0; i < lent; i++) {
                    Item item = new Item();
                    item.load(itemBody.getJSONObject(i));
                    int index = item.template.type;
                    if (index == 32) {
                        index = 6;
                    } else if (index == 23 || index == 24) {
                        index = 7;
                    } else if (index == 11) {
                        index = 8;
                    } else if (index == 40) {
                        index = 9;
                    }
                    if (index > 9) {
                        index = 9;
                    }
                    deTu.itemBody[index] = item;
                }
                deTu.info = gson.fromJson(discipleData.info, Info.class);
                deTu.info.applyCharLevelPercent();
                deTu.info.setPowerLimited();
                deTu.info.setChar(deTu);
                deTu.info.setInfo();
            }
        } catch (Exception e) {
            

        }
        boss.myDisciple = deTu;
        boss.setX((short) 443);
        boss.setY((short) 312);
        setBoss(boss);
        enter(boss);
        boss.zone.service.setPosition(boss, (byte) 0);

    }

    public void checkResult() {
        if (!isFinish && started) {
            if ((!currFightingPlayer.isDead() && boss.isDead()) && currFightingPlayer.zone == this) {
                currFightingPlayer.service.serverMessage("Đối thủ đã kiệt sức, bạn đã thắng");
                win();
                close();
                HoangAnhDz.updateTopSieuHang();
            } else if (currFightingPlayer.isDead() || currFightingPlayer.zone != this) {
                lose();
                currFightingPlayer.service.serverMessage("Bạn đã thất bại");
                close();
            }
        }
    }

    public void close() {
      //  currFightingPlayer.service.serverMessage("Bạn đã thất bại");
        running = false;
        currFightingPlayer.setTypePK((byte) 0);
        currFightingPlayer.inFighting = false;
        currFightingPlayer.setLastTimeThachDau(System.currentTimeMillis());
        Server server = DragonBall.getInstance().getServer();
        if (server.isFightingDhvtSieuHang != null) {
            server.isFightingDhvtSieuHang.removeIf(id -> id.equals(clonePlayerId) || id.equals(currFightingPlayer.id));
        }
        if (currFightingPlayer.zone == this) {
            if (currFightingPlayer.isDead()) {
                currFightingPlayer.wakeUpFromDead();
            }
            leave(currFightingPlayer);
            TMap map = MapManager.getInstance().getMap(MapName.DAI_HOI_VO_THUAT_2);
            int zoneId = map.getZoneID();
            map.enterZone(currFightingPlayer, zoneId);
        }
        if (boss != null && boss.zone != null) {
            boss.startDie();
            leave(boss);
        }

    }

    public void win() {
        if (currFightingPlayer.superrank == null) {
            SuperRank.loadSuperRank(currFightingPlayer);
            if (currFightingPlayer.superrank == null) {
                DHVT_SH_Service.gI().checkTop(currFightingPlayer);
                SuperRank.loadSuperRank(currFightingPlayer);
            }
        }
        if (currFightingPlayer.superrank != null && currFightingPlayer.superrank.rank > bossPointDhvt) {
            int oldRank = currFightingPlayer.superrank.rank;
            int newRank = bossPointDhvt;
            currFightingPlayer.superrank.rank = newRank;
            Top_SieuHang.setNewRank(currFightingPlayer.id, currFightingPlayer.superrank.rank);
            Top_SieuHang.setNewRank(clonePlayerId, oldRank);

            DHVT_SH_Service.gI().updateRank(currFightingPlayer.id, currFightingPlayer.superrank.rank);
            DHVT_SH_Service.gI().updateRank(clonePlayerId, oldRank);

            if (_HunrProvision.ConfigStudio.EVENT_NEWYEAR_2026) {
                _event.newyear_2026.EventNewYear2026.onRankUp(currFightingPlayer, newRank, oldRank);
            }
        }
        StartDHVT_SH.listAttack.remove(currFightingPlayer.id);
        StartDHVT_SH.listAttack.remove(clonePlayerId);
        if (currFightingPlayer.superrank != null) {
            currFightingPlayer.superrank.lastAttack = System.currentTimeMillis();
        }
    }

    public void lose() {
        if (currFightingPlayer.superrank == null) {
            SuperRank.loadSuperRank(currFightingPlayer);
            if (currFightingPlayer.superrank == null) {
                DHVT_SH_Service.gI().checkTop(currFightingPlayer);
                SuperRank.loadSuperRank(currFightingPlayer);
            }
        }
        if (currFightingPlayer.superrank != null) {
            if (_HunrProvision.ConfigStudio.EVENT_NEWYEAR_2026) {
                int currentRank = currFightingPlayer.superrank.rank;
                _event.newyear_2026.EventNewYear2026.onRankDown(currFightingPlayer, currentRank, currentRank);
            } else {
                currFightingPlayer.superrank.ticket--;
            }
        }
        StartDHVT_SH.listAttack.remove(currFightingPlayer.id);
        StartDHVT_SH.listAttack.remove(clonePlayerId);
    }
}
