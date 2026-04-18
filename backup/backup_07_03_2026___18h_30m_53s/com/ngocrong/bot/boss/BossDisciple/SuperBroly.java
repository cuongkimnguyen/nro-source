package com.ngocrong.bot.boss.BossDisciple;

import _HunrProvision.HoangAnhDz;
import _HunrProvision.boss.Boss;
import com.ngocrong.item.Item;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.mob.Mob;
import com.ngocrong.server.Config;
import com.ngocrong.server.SessionManager;
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

public class SuperBroly extends Boss {

    private static final Logger logger = Logger.getLogger(SuperBroly.class);

    public int level;
    public Skill[] TTNL = new Skill[7];

    // Status Attack System - Giống như Broly
    public int statusAttack = 1; // 1: xa đánh, 2: cận chiến
    public long lastCheckStatusAttack = 0;
    public int distance = 80; // Khoảng cách lớn hơn Broly thường (75 -> 80)
    public int currentStatus;
    public long lastChangeStatus = 0;
    public Skill oldSelect;
    public long lastTTNL;
    public long lastTimeSkillShoot = 0;

    public SuperBroly() {
        super();
        this.distanceToAddToList = 120; // Tăng từ 100 lên 120
        this.limit = 600; // Tăng từ 500 lên 600
        this.level = level;
        setInfo(Utils.nextInt(1000000, 2000000), 100000, 15, 150, 25); // Stat mạnh hơn
        this.name = "Super Broly " + Utils.nextInt(100);
        setDefaultPart();
        this.waitingTimeToLeave = 3000; // Giảm từ 5000 xuống 3000 - aggressive hơn
        setTypePK((byte) 5);
        point = 0;
        if (Config.serverID() == 2) {
            setInfo(100000000, 100000, 15, 150, 25);
        }
    }

    public long getDameAttack(Player plAtt) {
        long baseDame = this.info.hpFull / 8; // Mạnh hơn Broly (từ /10 thành /8)
        long dameCap;
        long weakPlayerHpThreshold = 75000; // Tăng từ 50000

        if (plAtt.info.hpFull < weakPlayerHpThreshold) {
            dameCap = plAtt.info.hpFull / 8; // Mạnh hơn (/10 -> /8)
        } else {
            dameCap = plAtt.info.hpFull / Utils.nextInt(15, 25); // Damage cao hơn và ít biến động hơn
        }

        long dameAfterCap = Math.min(baseDame, dameCap);
        double randomFactor = Utils.nextInt(95, 116) / 100.0; // Range rộng hơn
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
            // Thêm tất cả skill như Broly nhưng chỉ lấy level cao
            for (int i = 0; i <= 5; i++) {
                for (int j = 5; j <= 7; j++) { // Chỉ lấy skill level 6-7 (mạnh hơn)
                    skills.add(Skills.getSkill((byte) i, (byte) j).clone());
                }
            }

            // Skill TTNL
            TTNL = new Skill[7];
            for (int i = 0; i < 7; i++) {
                TTNL[i] = Skills.getSkill((byte) 8, (byte) (i + 1)).clone();
            }
        } catch (Exception ex) {
            
            logger.error("init skill err");
        }
    }

    // Status Attack System Methods - Giống Broly
    public void checkStatusAttack() {
        if (System.currentTimeMillis() - lastCheckStatusAttack >= 800) { // Nhanh hơn Broly (1000 -> 800)
            lastCheckStatusAttack = System.currentTimeMillis();

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
        if (!isKhongChe() && System.currentTimeMillis() - lastTTNL >= 1000) { // Nhanh hơn Broly (5000 -> 4000)
            lastTTNL = System.currentTimeMillis();
            this.oldSelect = this.select = TTNL[Utils.nextInt(TTNL.length)];
            startRecoveryEnery();
            if (zone != null) {
                zone.service.skillNotFocus(this, (byte) 1, null, null);
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
        if (!isKhongChe() && Config.serverID() == 1) {
            long hp = (long) (info.hpFull / (Utils.nextInt(20, 30))); // Buff nhiều hơn Broly (25-35 -> 20-30)

            if (hp > 0) {
                info.hp += hp;
                info.hpFull += hp;
                info.hp = Math.min(16070777, info.hp); // Cao hơn Broly (16M -> 20M)
                info.hpFull = Math.min(16070777, info.hpFull);
                zone.service.playerLoadHP(this, (byte) 0);
            }
        }
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        super.sendNotificationWhenAppear(map);
    }

    @Override
    public void sendNotificationWhenDead(String text) {
        super.sendNotificationWhenDead(text);
    }

    @Override
    public void addTargetToList() {
        super.addTargetToList(); // Kích hoạt lại method này
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
                    return;
                }
            }
        }
    }

    @Override
    public void startDie() {
        int map = this.zone.map.mapID;
        Zone zone = this.zone;
        listTarget.clear();
        super.startDie();
        long HP = info.hpFull;

        // SuperBroly mạnh hơn sẽ respawn lâu hơn và ở map cũ
        Utils.setTimeout(() -> {
            SuperBroly superbroly = new SuperBroly();
            superbroly.setInfo(HP, Long.MAX_VALUE, 1500, 1500, 0); // Mạnh gấp đôi khi respawn
            superbroly.joinMap(); // Spawn lại cùng map
        }, 15 * 60000); // 20 phút thay vì 15 phút
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null || !(obj instanceof Player)) {
            return;
        }
        if (Config.serverID() == 1) {
            Player pl = (Player) obj;
            if (pl.myDisciple == null) {
                pl.createDisciple(0);
            }
        } else {
            if (Utils.isTrue(5, 10)) {
                Item item = new Item(568);
                this.dropItem(item, (Player) obj);
            }
        }
        // Có thể thêm reward đặc biệt cho SuperBroly
    }

    @Override
    public void addTarget(Player _c) {
        if (_c != this) {
            if (!listTarget.contains(_c)) {
                this.listTarget.add(_c);
                chat(String.format("Mi làm ta nổi giận rồi đó %s", _c.name));
            }
        }
    }

    @Override
    public long injure(Player plAtt, Mob mob, long dameInput) {
        // HoangAnhDz.logError("injure");
        if (plAtt == null) {
            return 0;
        }
        addTarget(plAtt);
        if (Config.serverID() == 1) {
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
        }
        long maxDamagePerHit = this.info.hpFull / 100;
        if (Config.serverID() == 2) {
            maxDamagePerHit = 10_000_000;
        }
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
            chat("Tránh xa ta ra, đừng làm ta nổi giận");
        }
    }

    @Override
    public void move() {
//        // SuperBroly di chuyển ít hơn để tập trung tấn công
//        if (Utils.nextInt(5) == 0) { // Giảm từ nextInt(3) xuống nextInt(5)
//            super.move();
//        }
    }

    // Thêm method tấn công giống Broly
    public void moveTo2(Player target) {
        int x = Utils.nextInt(target.getX() - 120, target.getX() + 120); // Range lớn hơn
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
        long now = System.currentTimeMillis();
        if (now - lastTimeSkillShoot < 800) { // Nhanh hơn Broly (1000 -> 800)
            return;
        }
        Player target = (Player) obj;
        if (target.inVisible) {
            return;
        }
        if (Utils.isTrue(1, 20)) { // Ít di chuyển hơn Broly (1/20 -> 1/25)
            moveTo2(target);
            return;
        }
        Skill skill = selectSkillAttack();
        if (skill != null) {
            if (skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_ANTOMIC) {
                lastTimeSkillShoot = now;
            }
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

        int distanceToTarget = Utils.getDistance(getX(), getY(), nearestTarget.getX(), nearestTarget.getY());
        boolean useMeleeSkills = distanceToTarget <= distance;
        List<Skill> suitableSkills = new ArrayList<>();

        for (Skill skill : skills) {
            if (skill.template.type != 1 || skill.isCooldown()) {
                continue;
            }

            int skillId = skill.template.id;
            boolean isSkillSuitable = false;

            if (useMeleeSkills) {
                if (skillId == 0 || skillId == 2 || skillId == 4) {
                    isSkillSuitable = true;
                }
            } else {
                if (skillId == 1 || skillId == 3 || skillId == 5) {
                    isSkillSuitable = true;
                }
            }

            if (isSkillSuitable) {
                suitableSkills.add(skill);
            }
        }

        if (suitableSkills.size() > 0) {
            int randomIndex = Utils.nextInt(suitableSkills.size());
            return suitableSkills.get(randomIndex);
        }

        // Fallback: tất cả skill tấn công
        List<Skill> allAttackSkills = new ArrayList<>();
        for (Skill skill : skills) {
            if (skill.template.type == 1 && !skill.isCooldown()) {
                allAttackSkills.add(skill);
            }
        }

        if (allAttackSkills.size() > 0) {
            return allAttackSkills.get(Utils.nextInt(allAttackSkills.size()));
        }

        return null;
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
                } else if (this.isRecoveryEnergy && now - this.lastUpdates[TAI_TAO] >= 800) { // Nhanh hơn
                    this.lastUpdates[TAI_TAO] = now;
                    int percent = Utils.nextInt(5, 15); // Hồi nhiều hơn Broly (3-10 -> 5-15)
                    this.info.recovery(Info.ALL, percent, true);
                }
                long percent = (long) this.info.hp * 100L / (long) this.info.hpFull;
                if (this.info.mp >= this.info.mpFull) {
                    if (percent >= 100) {
                        stopRecoveryEnery();
                    }
                }
                zone.service.chat(this, "Phục hồi năng lượng tối thượng " + percent + "%");
            }
            return;
        }
        select.lastTimeUseThisSkill = now;
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 294);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 295);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 296);
    }
}
