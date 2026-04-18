package com.ngocrong.bot.boss.BossDisciple;

import _HunrProvision.HoangAnhDz;
import _HunrProvision.boss.Boss;
import com.ngocrong.bot.Disciple;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.item.ItemTime;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.mob.Mob;
import com.ngocrong.mob.MobFactory;
import com.ngocrong.mob.MobType;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Info;
import com.ngocrong.user.Player;
import static com.ngocrong.user.Player.TAI_TAO;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Broly extends Boss {

    private static final Logger logger = Logger.getLogger(Broly.class);

    public int level;
    public Skill[] TTNL = new Skill[7];

    // Status Attack System
    public int statusAttack = 1; // 1: xa đánh, 2: cận chiến
    public long lastCheckStatusAttack = 0;
    public int distance = 35; // Khoảng cách để chuyển đổi giữa cận chiến và xa đánh
    public int currentStatus;
    public long lastChangeStatus = 0;
    public Skill oldSelect;
    public long lastTTNL;

    public Broly() {
        super();
        this.distanceToAddToList = 100;
        this.limit = 500;
        this.level = level;
        setInfo(Utils.nextInt(50000, 100000), 100000, 10, 100, 20);
        this.name = "Broly " + Utils.nextInt(100);
        setDefaultPart();
        this.waitingTimeToLeave = 5000;
        this.sayTheLastWordBeforeDie = "Các ngươi hãy chờ đấy, ta sẽ quay lại sau";
        setTypePK((byte) 5);
        point = 0;
    }

    public long getDameAttack(Player plAtt) {
        long baseDame = this.info.hpFull / 10;
        long dameCap;
        long weakPlayerHpThreshold = 50000;
        if (plAtt.info.hpFull < weakPlayerHpThreshold) {
            dameCap = plAtt.info.hpFull / 10;
        } else {
            dameCap = plAtt.info.hpFull / 20;
        }
        long dameAfterCap = Math.min(baseDame, dameCap);
        double randomFactor = Utils.nextInt(90, 111) / 100.0;
        long dameRandomized = (long) (dameAfterCap * randomFactor);
        long finalDame = dameRandomized - plAtt.info.defenseFull;
        finalDame = finalDame + Utils.percentOf(finalDame, (this.select.damage - 100));

        if (finalDame <= 0) {
            return 1;
        }
        return finalDame;
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            for (int i = 0; i <= 5; i++) {
                for (int j = 1; j <= 7; j++) {
                    skills.add(Skills.getSkill((byte) i, (byte) j).clone());
                }
            }
            TTNL = new Skill[7];
            for (int i = 0; i < 7; i++) {
                TTNL[i] = Skills.getSkill((byte) 8, (byte) (i + 1)).clone();
            }
        } catch (Exception ex) {
            
            logger.error("init skill err");
        }
    }

    // Status Attack System Methods
    public void checkStatusAttack() {
        if (System.currentTimeMillis() - lastCheckStatusAttack >= 1000) { // Check mỗi 1s
            lastCheckStatusAttack = System.currentTimeMillis();

            // Tìm player gần nhất để xét khoảng cách
            Player nearestPlayer = findNearestPlayer();
            if (nearestPlayer != null) {
                int distanceToPlayer = Utils.getDistance(getX(), getY(), nearestPlayer.getX(), nearestPlayer.getY());

                if (distanceToPlayer <= distance) {
                    if (statusAttack != 2) {
                        statusAttack = 2; // Cận chiến
                    }
                } else {
                    if (statusAttack != 1) {
                        statusAttack = 1; // Xa đánh
                    }
                }
            }
        }
    }

    public Player findNearestPlayer() {
        Player nearestPlayer = null;
        int minDistance = Integer.MAX_VALUE;

        for (Player player : listTarget) {
            if (player != null && !player.isDead() && player.zone == this.zone) {
                int dist = Utils.getDistance(getX(), getY(), player.getX(), player.getY());
                if (dist < minDistance) {
                    minDistance = dist;
                    nearestPlayer = player;
                }
            }
        }
        return nearestPlayer;
    }

    // Đếm số người trong khu vực (zone)
    public int countPlayersInZone() {
        int count = 0;
        if (this.zone != null && this.zone.players != null) {
            for (Player player : this.zone.players) {
                if (player != null && !player.isDead()) {
                    count++;
                }
            }
        }
        return count;
    }

    // Kiểm tra có nên ưu tiên TTNL dựa trên số người
    public boolean shouldPrioritizeTTNL() {
        int playerCount = countPlayersInZone();
        return playerCount > 2; // Ưu tiên TTNL khi có >2 người
    }

    public Skill getSkillByStatus() {
        if (statusAttack == 1) {
            // Xa đánh: chọn skill chưởng (1,3,5)
            int[] rangeSkills = {1, 3, 5};
            int randomIndex = Utils.nextInt(rangeSkills.length);
            return skills.get(rangeSkills[randomIndex]);
        } else {
            // Cận chiến: chọn skill cận chiến (0,2,4) 
            int[] meleeSkills = {0, 2, 4};
            int randomIndex = Utils.nextInt(meleeSkills.length);
            return skills.get(meleeSkills[randomIndex]);
        }
    }

    public void usingTTNL() {
        if (!isKhongChe() && System.currentTimeMillis() - lastTTNL >= 1000) {
            lastTTNL = System.currentTimeMillis();
            this.oldSelect = this.select = TTNL[Utils.nextInt(TTNL.length)];
            startRecoveryEnery();
            if (zone != null) {
                zone.service.skillNotFocus(this, (byte)1, null, null);
            }
        }
    }

    public void checkAttack() {
        if (this.info.hp > 0) {
            this.info.mp = this.info.mpFull = Long.MAX_VALUE;
        }
        boolean flag = false;
        if (System.currentTimeMillis() - lastChangeStatus >= 1000) {
            lastChangeStatus = System.currentTimeMillis();
            if (this.info.hp < info.hpFull && Utils.isTrue(7, 10)) {
                int percentHP = (int) ((this.info.hp * 100) / info.hpFull);
                boolean prioritizeTTNL = shouldPrioritizeTTNL();
                if (percentHP > 80) {
                    if (prioritizeTTNL) {
                        if (Utils.isTrue(9, 10)) {
                            usingTTNL();
                            flag = true;
                        } else {
                            this.select = getSkillByStatus();
                        }
                    } else {
                        if (Utils.isTrue(3, 10)) {
                            this.select = getSkillByStatus();
                        } else {
                            usingTTNL();
                            flag = true;
                        }
                    }
                } else {
                    if (Utils.isTrue(1, 10)) {
                        upPoint();
                    }
                    if (Utils.isTrue(1, 2)) {
                        if (prioritizeTTNL) {
                            if (Utils.isTrue(4, 5)) {
                                usingTTNL();
                                flag = true;
                            } else {
                                this.select = getSkillByStatus();
                            }
                        } else {
                            if (Utils.isTrue(2, 10)) {
                                this.select = getSkillByStatus();
                            } else {
                                usingTTNL();
                                flag = true;
                            }
                        }
                    }
                }
            }
        }
        if (!flag) {
            checkStatusAttack();
        }
        if (System.currentTimeMillis() - lastUseRecoveryEnery >= 2000 && this.isRecoveryEnergy) {
            stopRecoveryEnery();
        }
    }

    public void upPoint() {
        if (!isKhongChe()) {
            long hp = (long) (info.hpFull / (Utils.nextInt(25, 35)));

            if (hp > 0) {
                info.hp += hp;
                info.hpFull += hp;
                info.hp = Math.min(16070777, info.hp);
                info.hpFull = Math.min(16070777, info.hpFull);
                zone.service.playerLoadHP(this, (byte) 0);
            }
        }
    }

    public void joinMap() {
        Integer[] mapIdArray = new Integer[]{5, 13, 10, 20, 19, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38};
        List<Integer> mapList = new ArrayList<>(Arrays.asList(mapIdArray));
        Collections.shuffle(mapList);
        for (Integer mapId : mapList) {
            TMap map = MapManager.getInstance().getMap(mapId);
            if (map == null || map.zones.isEmpty()) {
                continue;
            }
            List<Zone> zoneList = new ArrayList<>(map.zones);
            Collections.shuffle(zoneList);
            for (Zone zone : zoneList) {
                if (zone != null && zone.getBossInZone().isEmpty() && zone.zoneID >= 2) {
                    this.setLocation(map.mapID, zone.zoneID);
                    return; // Thoát khỏi phương thức vì đã tìm được chỗ
                }
            }
        }
    }

    @Override
    public void sendNotificationWhenAppear(String map) {

    }

    @Override
    public void sendNotificationWhenDead(String text) {

    }

    @Override
    public void addTargetToList() {
        super.addTargetToList();
    }

    @Override
    public void startDie() {
        int map = this.zone.map.mapID;
        Zone zone = this.zone;
        listTarget.clear();
        super.startDie();
        long HP = info.hpFull;
        if (HP < 1000000) {
            Utils.setTimeout(() -> {
                Broly broly = new Broly();
                broly.setInfo(HP, Long.MAX_VALUE, 1000, 1000, 0);
                broly.joinMap();
            }, 30000);
        } else {
            Utils.setTimeout(() -> {
                SuperBroly superbroly = new SuperBroly();
                superbroly.setInfo(HP, Long.MAX_VALUE, 1000, 1000, 0);
                superbroly.setLocation(map, -1);
            }, 5000);
        }
    }

    public void checkDie() {
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
    }

    @Override
    public void addTarget(Player _c) {
        if (_c != this) {
            if (!listTarget.contains(_c)) {
                this.listTarget.add(_c);
                int playerCount = countPlayersInZone();
                if (playerCount > 2) {
                    chat(String.format("Nhiều kẻ thù vậy... %s! Ta sẽ sử dụng toàn bộ sức mạnh!", _c.name));
                } else {
                    chat(String.format("Mi làm ta nổi giận rồi đó %s", _c.name));
                }
            }
        }
    }

    @Override
    public long injure(Player plAtt, Mob mob, long dameInput) {
//        // HoangAnhDz.logError("injure");
        if (plAtt == null) {
            return 0;
        }
        addTarget(plAtt);
        var skill = plAtt.select;
        if (skill != null) {
            if (skill.template.id == SkillName.QUA_CAU_KENH_KHI) {
                long baseDamage = (long) (plAtt.info.damageFull * 1.0); // 100% damage của player
                return Utils.nextLong((long) (baseDamage * 0.9), (long) (baseDamage * 1.1));
            } else if (skill.template.id == SkillName.MAKANKOSAPPO) {

                long piercingDamage = dameInput * 5 / 100;
                return Utils.nextLong((long) (piercingDamage * 0.9), (long) (piercingDamage * 1.1));
            }
        }
        if (plAtt.info != null && plAtt.info.damageFull < 40000) {
            return 1;
        }
        long maxDamagePerHit = this.info.hpFull / 100;
        return Math.min(dameInput, maxDamagePerHit);
    }

    @Override
    public void update() {
        checkAttack();
        super.update();
    }

    @Override
    public void updateEveryThirtySeconds() {
        if (!isDead()) {
            int playerCount = countPlayersInZone();
            if (playerCount > 2) {
                chat("Nhiều kẻ địch... Ta sẽ không nhân nhượng!");
            } else {
                chat("Tránh xa ta ra, đừng làm ta nổi giận");
            }
        }
    }

    @Override
    public void move() {

    }

    @Override
    public void skillNotFocus(byte type) {
        if (!isRecoveryEnergy) {
            if (!meCanAttack()) {
                return;
            }
        } else {
            if (isDead()) {
                return;
            }
        }
        long now = System.currentTimeMillis();
        if (type == 2) {
            if (this.isRecoveryEnergy) {
                if (this.info.hp >= this.info.hpFull && this.info.mp >= this.info.mpFull) {
                    stopRecoveryEnery();
                } else if (this.isRecoveryEnergy && now - this.lastUpdates[TAI_TAO] >= 1000) {
                    this.lastUpdates[TAI_TAO] = now;
                    int percent = Utils.nextInt(10, 30);
                    this.info.recovery(Info.ALL, percent, true);
                }
                long percent = (long) this.info.hp * 100L / (long) this.info.hpFull;
                if (this.info.mp >= this.info.mpFull) {
                    if (percent >= 100) {
                        stopRecoveryEnery();
                    }
                }
                zone.service.chat(this, "Phục hồi năng lượng " + percent + "%");
            }
            return;
        }
        select.lastTimeUseThisSkill = now;
        if (zone != null) {
            zone.service.skillNotFocus(this, type, null, null);
        }
    }

    public void moveTo2(Player target) {
        int x = Utils.nextInt(target.getX() - 100, target.getX() + 100);
        int y = zone.map.collisionLand((short) x, target.getY());
        super.moveTo(x, y);
    }

    @Override
    public void updateEveryOneSeconds() {
        if (!isDead()) {
            addTargetToList();
            if (isRecoveryEnergy()) {
                skillNotFocus((byte) 2);
            }
        }

        updateTimeLiveMobMe();
        updateItemTime();
        if (this.freezSeconds > 0) {
            this.freezSeconds--;
            if (this.freezSeconds == 0) {
                setFreeze(false);
            }
        }
        if (this.timeIsMoneky > 0) {
            this.timeIsMoneky--;
            if (this.timeIsMoneky == 0) {
                timeOutIsMonkey();
            }
        }
    }

    @Override
    public void attack(Object obj) {
        Player target = (Player) obj;
        if (target.inVisible) {
            return;
        }
        if (Utils.isTrue(1, 20)) {
            moveTo2(target);
            return;
        }
        Skill skill = selectSkillAttack();
        if (skill != null) {
            this.select = skill;
            zone.attackPlayer(this, target);
        }
    }

    @Override
    public Skill selectSkillAttack() {
        Player nearestTarget = findNearestPlayer();
        if (nearestTarget == null) {
            return super.selectSkillAttack();
        }

        // Tính khoảng cách thực tế với target
        int distanceToTarget = Utils.getDistance(getX(), getY(), nearestTarget.getX(), nearestTarget.getY());

        // Xác định loại skill cần dùng dựa trên khoảng cách
        boolean useMeleeSkills = distanceToTarget <= distance;
        List<Skill> suitableSkills = new ArrayList<>();
        for (Skill skill : skills) {
            // Chỉ xét skill tấn công (type == 1) và không cooldown
            if (skill.template.type != 1 || skill.isCooldown()) {
                continue;
            }

            int skillId = skill.template.id;
            boolean isSkillSuitable = false;

            if (useMeleeSkills) {
                // Cận chiến: skill ID 0, 2, 4
                if (skillId == 0 || skillId == 2 || skillId == 4) {
                    isSkillSuitable = true;
                }
            } else {
                // Xa đánh: skill ID 1, 3, 5
                if (skillId == 1 || skillId == 3 || skillId == 5) {
                    isSkillSuitable = true;
                }
            }

            if (isSkillSuitable) {
                suitableSkills.add(skill);
            }
        }

        // Nếu có skill phù hợp, chọn random
        if (suitableSkills.size() > 0) {
            int randomIndex = Utils.nextInt(suitableSkills.size());
            Skill selectedSkill = suitableSkills.get(randomIndex);
            return selectedSkill;
        }

        // Fallback: không có skill phù hợp available
        List<Skill> allAttackSkills = new ArrayList<>();
        for (Skill skill : skills) {
            if (skill.template.type == 1 && !skill.isCooldown()) {
                allAttackSkills.add(skill);
            }
        }

        if (allAttackSkills.size() > 0) {
            int randomIndex = Utils.nextInt(allAttackSkills.size());
            Skill fallbackSkill = allAttackSkills.get(randomIndex);
            return fallbackSkill;
        }

        // Cuối cùng: không có skill nào available
        return null;
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 291);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 292);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 293);
    }
}
