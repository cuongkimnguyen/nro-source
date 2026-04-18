package com.ngocrong.bot;

import com.ngocrong.data.DiscipleData;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.SkillPet;
import com.ngocrong.util.Utils;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.mob.Mob;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.user.Info;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Disciple extends Player {

    private static final Logger logger = Logger.getLogger(Disciple.class);

    public byte discipleStatus;
    public Player master;
    public byte skillOpened;
    public ArrayList<Player> listTarget;
    private boolean isAttacking;
    private long deadTime;
    public byte typeDisciple;
    public byte petBonus;

    public Disciple() {
        this.effects = new ArrayList<>();
        this.itemTimes = new ArrayList<>();
        this.idMount = -1;
        setHaveMount(false);
        this.listTarget = new ArrayList<>();
    }

    public void setMaster(Player master) {
        this.master = master;
    }

    @Override
    public int checkEffectOfSatellite(int id) {
        return master.checkEffectOfSatellite(id);
    }

    public boolean isEmptyBody() {
        for (Item it : this.itemBody) {
            if (it != null) {
                return false;
            }
        }
        return true;
    }

    public void setFlag() {
        if (flag != master.flag) {
            flag = master.flag;
            if (zone != null) {
                zone.service.flag(this);
            }
        }
    }

    public boolean isHaveFood() {
        if (master == null) {
            return false;
        }
        return master.isHaveFood();
    }

    @Override
    public void addExp(byte type, long exp, boolean canX2, boolean isAddForMember) {
        if (info.power >= info.powerLimitMark.power) {
            return;
        }
        if (info.power + exp >= info.powerLimitMark.power) {
            exp = info.powerLimitMark.power - info.power;
        }
        boolean isMature = info.power >= 1500000;
      //  exp *= 6;
        super.addExp(type, exp, canX2, isAddForMember);
        if (!isMature) {
            if (info.power >= 1500000) {
                master.service.chat(this, "Sư phụ ơi, con trưởng thành rồi");
                updateSkin();
            }
        }
    }

    @Override
    public boolean isBoss() {
        return false;
    }

    @Override
    public boolean isDisciple() {
        return true;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public boolean isMiniDisciple() {
        return false;
    }

    @Override
    public boolean isEscort() {
        return false;
    }

    public void startDie() {
        super.startDie();
        this.deadTime = System.currentTimeMillis();
    }

    public void potentialUp() {
        long pointNeed = 0;
        if (this.info.power >= 105000000000L) {
            if (this.info.originalHP < 600000) {
                this.info.originalHP = 600000;
            }
            if (this.info.originalMP < 600000) {
                this.info.originalMP = 600000;
            }
            if (this.info.originalDamage < 32000) {
                this.info.originalDamage = 32000;
            }
        } else if (this.info.power >= 80000000000L) {
            if (this.info.originalHP < 550000) {
                this.info.originalHP = 550000;
            }
            if (this.info.originalMP < 550000) {
                this.info.originalMP = 550000;
            }
            if (this.info.originalDamage < 28000) {
                this.info.originalDamage = 28000;
            }
        } else if (this.info.power >= 40000000000L) {
            if (this.info.originalHP < 350000) {
                this.info.originalHP = 350000;
            }
            if (this.info.originalMP < 350000) {
                this.info.originalMP = 350000;
            }
            if (this.info.originalDamage < 20000) {
                this.info.originalDamage = 20000;
            }
        } else if (this.info.power >= 18000000000L) {
            if (this.info.originalHP < 220000) {
                this.info.originalHP = 220000;
            }
            if (this.info.originalMP < 220000) {
                this.info.originalMP = 220000;
            }
            if (this.info.originalDamage < 11000) {
                this.info.originalDamage = 11000;
            }
        }
        if (info.originalDamage < info.powerLimitMark.damage) {
            pointNeed = (2L * info.originalDamage + Info.DAMAGE_FROM_1000_TIEM_NANG - 1) / 2 * Info.EXP_FOR_ONE_ADD;
            if (pointNeed <= info.potential) {
                info.originalDamage++;
                info.potential -= pointNeed;
                info.setInfo();
            }
        }
        if (Utils.nextInt(2) == 0) {
            if (info.originalHP < info.powerLimitMark.hp) {
                pointNeed = (2L * (info.originalHP + 1000) + Info.HP_FROM_1000_TIEM_NANG - 20) / 2;
                if (pointNeed <= info.potential) {
                    info.originalHP += 20;
                    info.potential -= pointNeed;
                    info.setInfo();
                    return;
                }
            }
        } else {
            if (info.originalMP < info.powerLimitMark.mp) {
                pointNeed = (2L * (info.originalMP + 1000) + Info.MP_FROM_1000_TIEM_NANG - 20) / 2;
                if (pointNeed <= info.potential) {
                    info.originalMP += 20;
                    info.potential -= pointNeed;
                    info.setInfo();
                    return;
                }
            }
        }
        if (info.originalCritical < info.powerLimitMark.critical) {
            pointNeed = 50000000L;
            for (int i = 0; i < info.originalCritical; i++) {
                pointNeed *= 5L;
            }
            if (pointNeed <= info.potential) {
                info.originalCritical++;
                info.potential -= pointNeed;
                info.setInfo();
                return;
            }
        }
        if (info.originalDefense < info.powerLimitMark.defense) {
            pointNeed = 2L * (info.originalDefense + 5) / 2 * 100000;
            if (pointNeed <= info.potential) {
                info.originalDefense++;
                info.potential -= pointNeed;
                info.setInfo();
                return;
            }
        }
    }

    public boolean updateSkill(int index, int idSk) {
        if (this.skillOpened < (index + 1)) {
            this.master.service.serverMessage("Đệ tử của bạn cần học kĩ năng trước khi sử dụng vật phẩm này");
            return false;
        }
        try {
            byte skillID = 0;
            switch (index) {
                case 1:
                    skillID = SkillName.CHIEU_KAMEJOKO;
                    break;
                case 2:
                    skillID = SkillName.THAI_DUONG_HA_SAN;
                    break;
                case 3:
                    skillID = SkillName.BIEN_HINH;
                    break;
                default:
                    break;
            }
            Skill skill = Skills.getSkill(skillID, (byte) 1);

            if (skill != null) {
                skill = skill.clone();
                if (skill.id == SkillName.CHIEU_KAMEJOKO) {
                    skill.coolDown = 1000;
                }
                skills.set(index, skill);
            }
            this.master.service.serverMessage("Bạn đã đổi skill thành công");
            return true;
        } catch (Exception ex) {
            logger.error("error", ex);
        }
        return false;
    }

    public void learnSkill() {
        try {
            if (skillOpened < SkillPet.list.size()) {
                SkillPet skillPet = SkillPet.list.get(skillOpened);
                if (info.power >= skillPet.powerRequire) {
                    byte skillID;
                    Skill skill = null;
                    if (skillOpened == 1) {
                        int random = Utils.nextInt(100);
                        if (random < 40) {
                            skillID = SkillName.CHIEU_KAMEJOKO;
                        } else if (random < 70) {
                            skillID = SkillName.CHIEU_MASENKO;
                        } else {
                            skillID = SkillName.CHIEU_ANTOMIC;
                        }
                        skill = Skills.getSkill(skillID, (byte) 1);
                    }
                    if (skillOpened == 2) {
                        if (Utils.nextInt(10) == 0) {
                            skillID = SkillName.THAI_DUONG_HA_SAN;
                        } else if (Utils.nextInt(3) == 0) {
                            skillID = SkillName.TAI_TAO_NANG_LUONG;
                        } else {
                            skillID = SkillName.KAIOKEN;
                        }
                        skill = Skills.getSkill(skillID, (byte) 1);
                    }
                    if (skillOpened == 3 || skillOpened == 0) {
                        skillID = skillPet.skills[Utils.nextInt(skillPet.skills.length)];
                        skill = Skills.getSkill(skillID, (byte) 1);
                    }
                    if (typeDisciple == 2) {
                        if (skillOpened == 1) {
                            skillID = SkillName.CHIEU_KAMEJOKO;
                            skill = Skills.getSkill(skillID, (byte) 1);
                        } else if (skillOpened == 2) {
                            if (Utils.nextInt(3) == 0) {
                                skillID = SkillName.THAI_DUONG_HA_SAN;
                            } else {
                                skillID = SkillName.TAI_TAO_NANG_LUONG;
                            }
                            skill = Skills.getSkill(skillID, (byte) 1);
                        }

                    }
                    if (skill != null) {
                        skillOpened++;
                        addSkill(skill.clone());
                    }
                }
            }
        } catch (Exception e) {
            
            logger.error("learn skill error", e);
        }
    }

    public void addSkill(Skill skill) {
        if (skill.template.id == SkillName.CHIEU_DAM_DRAGON || skill.template.id == SkillName.CHIEU_DAM_DEMON || skill.template.id == SkillName.CHIEU_DAM_GALICK) {
            skill.coolDown = 700;
        }
        if (skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_ANTOMIC) {
            skill.coolDown = 1300;
        }
        skills.add(skill);
    }

    public void changeSkillIndex(int index) {
        if (this.skillOpened < index) {
            this.master.service.sendThongBao("Đệ tử của bạn chưa mở khóa kĩ năng này");
            return;
        }
        try {
            byte skillID = 0;
            int random = Utils.nextInt(100);

            if (typeDisciple == 2) {
                if (index == 2) {
                    skillID = SkillName.CHIEU_KAMEJOKO;
                } else if (index == 3) {
                    if (random < 50) {
                        skillID = SkillName.THAI_DUONG_HA_SAN;
                    } else {
                        skillID = SkillName.TAI_TAO_NANG_LUONG;
                    }
                }
            } else if (index == 2) {
                if (random < 50) {
                    skillID = SkillName.CHIEU_MASENKO;
                } else if (random < 85) {
                    skillID = SkillName.CHIEU_ANTOMIC;
                } else {
                    skillID = SkillName.CHIEU_KAMEJOKO;
                }
            } else if (index == 3) {
                if (random < 70) {
                    skillID = SkillName.KAIOKEN;
                } else if (random < 85) {
                    skillID = SkillName.TAI_TAO_NANG_LUONG;
                } else {
                    skillID = SkillName.THAI_DUONG_HA_SAN;
                }
            } else if (index == 4) {
                if (random < 40) {
                    skillID = SkillName.DE_TRUNG;
                } else if (random < 80) {
                    skillID = SkillName.KHIEN_NANG_LUONG;
                } else {
                    skillID = SkillName.BIEN_HINH;
                }
            }
            Skill skill = Skills.getSkill(skillID, (byte) 1);
            if (skill != null) {
                skill = skill.clone();
                skill.coolDown = 1000;
                skills.set(index - 1, skill);
            }
        } catch (Exception ex) {
            
            logger.error("error change skill pet: ", ex);
        }
    }

    @Override
    public void setDefaultHead() {
        if (typeDisciple == 2) {
            setHead((short) 421);
            return;
        }
        if (typeDisciple == 1) {
            setHead((short) 297);
            return;
        }
        switch (this.gender) {
            case 0:
                if (info.power < 1500000) {
                    setHead((short) 285);
                } else {
                    setHead((short) 304);
                }
                break;

            case 1:
                if (info.power < 1500000) {
                    setHead((short) 288);
                } else {
                    setHead((short) 305);
                }
                break;

            case 2:
                if (info.power < 1500000) {
                    setHead((short) 282);
                } else {
                    setHead((short) 303);
                }
                break;
        }
    }

    @Override
    public void setDefaultBody() {
        if (typeDisciple == 2) {
            setBody((short) 422);
            return;
        }
        if (typeDisciple == 1) {
            setBody((short) 298);
            return;
        }
        if (info.power < 1500000) {
            switch (this.gender) {
                case 0:
                    setBody((short) 286);
                    break;

                case 1:
                    setBody((short) 289);
                    break;

                case 2:
                    setBody((short) 283);
                    break;
            }
        } else {
            super.setDefaultBody();
        }
    }

    @Override
    public void setDefaultLeg() {
        if (typeDisciple == 2) {
            setLeg((short) 423);
            return;
        }
        if (typeDisciple == 1) {
            setLeg((short) 299);
            return;
        }
        if (info.power < 1500000) {
            switch (this.gender) {
                case 0:
                    setLeg((short) 287);
                    break;

                case 1:
                    setLeg((short) 290);
                    break;

                case 2:
                    setLeg((short) 284);
                    break;
            }
        } else {
            super.setDefaultLeg();
        }
    }

    public ArrayList<KeyValue> getInfoSkill() {
        ArrayList<KeyValue> list = new ArrayList<>();
        for (Skill skill : skills) {
            KeyValue keyValue = new KeyValue((short) skill.id, skill.template.name);
            list.add(keyValue);
        }
        for (int i = list.size(); i < SkillPet.list.size(); i++) {
            SkillPet skillPet = SkillPet.list.get(i);
            KeyValue keyValue = new KeyValue((short) -1, skillPet.getMoreInfo());
            list.add(keyValue);
        }
        return list;
    }

    public void changeSkill(int index) {
        if (index == 0 && skillOpened >= 3) {
            Skill skill = skills.get(2);
            if (skill.template.id == SkillName.KAIOKEN) {
                long hp = info.hpFull / 10;
                long mp = info.mpFull / 10;
                if (info.hp >= hp && info.mp >= mp) {
                    this.select = skill;
                    return;
                }
            }
        }
        this.select = skills.get(index);
    }

    public Object targetDetect() {
        int limit = 40;
        if (skillOpened >= 2) {
            limit = 200;
        }
        if (discipleStatus == 2) {
            limit = 600;
        }
        List<Player> enemiesCanAttack = getEnemiesClosestMaster(limit);
        if (!enemiesCanAttack.isEmpty()) {
            Player target = findEnemyClosest(enemiesCanAttack);
            if (target != null) {
                return target;
            }
        }
        List<Mob> mobsCantAttack = getMobsClosestMaster(limit);
        if (!mobsCantAttack.isEmpty()) {
            Mob target = findMobClosest(mobsCantAttack);
            if (target != null) {
                return target;
            }
        }
        return null;
    }

    public void attack(Object obj) {
        if (isDead()) {
            return;
        }
        isAttacking = true;
        short x, y;
        if (obj instanceof Player) {
            Player target = (Player) obj;
            x = target.getX();
            y = target.getY();
        } else {
            Mob target = (Mob) obj;
            x = target.x;
            y = target.y;
        }
        boolean isSelected = false;
        int d = Utils.getDistance(getX(), getY(), x, y);
        int s = 0;
        if (d < 60) {
            changeSkill(0);
            isSelected = true;
        } else {
            if (skillOpened >= 2) {
                if (d < 200) {
                    changeSkill(1);
                    isSelected = true;
                    s = 1;
                }
            }
        }
        if (!isSelected) {
            if (discipleStatus == 2) {
                moveTo(x, y);
                attack(obj);
            }
        } else {
            if (System.currentTimeMillis() - master.lastAttack < 900000 || master.isBuaDeTu()) {
                if (!select.isCooldown()) {
                    if (s == 0) {
                        moveTo((short) (x + Utils.nextInt(-20, 20)), y);
                    }
                    if (obj instanceof Player) {
                        Player target = (Player) obj;
                        zone.attackPlayer(this, target);
                    } else {
                        Mob target = (Mob) obj;
                        zone.attackNpc(this, target, false);
                    }
                }
                lastAttack = System.currentTimeMillis();
            }
        }
        isAttacking = false;
    }
    public long lastAttack;

    public void moveTo(short x, short y) {
        if (isDead() || master.isDead()) {
            return;
        }
        if (isBlind() || isFreeze() || isSleep() || isCharge()) {
            return;
        }
        setX(x);
        setY(y);
        if (zone != null) {
            zone.service.move(this);
        }
    }

    public void followMaster() {
        if (isRecoveryEnergy()) {
            stopRecoveryEnery();
        }
        short x = (short) (master.getX() + Utils.nextInt(-50, 50));
        short y = master.getY();
        moveTo(x, y);
    }

    public void move() {
        if (isDead() || zone == null) {
            return;
        }
        if (isBlind() || isFreeze() || isSleep() || isCharge() || isStone()) {
            return;
        }
        if (this.discipleStatus == 0) {
            followMaster();
        }
    }

    @Override
    public void updateEveryHalfSeconds() {
        potentialUp();
        super.updateEveryOneMinutes();
    }

    @Override
    public void updateEveryOneSeconds() {
        if (master == null || master.zone == null) {
            if (zone != null) {
                zone.leave(this);
                return;
            }
        }
        long now = System.currentTimeMillis();
        if (isDead() && now - deadTime >= 30000 && this.master.zone != null) {
            followMaster();
            this.info.hp = info.hpFull;
            this.info.mp = info.mpFull;
            setDead(false);
            if (zone != null) {
                zone.leave(this);
            }
            if (discipleStatus != 3) {
                this.statusMe = 1;
                if (master.zone != null && !master.zone.map.isMapSingle()) {
                    master.zone.enter(this);
                    master.service.chat(this, "Sư phụ ơi con đây nè");
                }
            }
        }
        if (this.isRecoveryEnergy()) {
            skillNotFocus((byte) 2);
        }
        if (!(isBlind() || isFreeze() || isSleep() || isCharge())) {
            if (discipleStatus == 1) {
                int d = Utils.getDistance(master.getX(), master.getY(), getX(), getY());
                if (d > 50) {
                    followMaster();
                    zone.service.move(this);
                }
            }
        }
        learnSkill();

        super.updateEveryOneSeconds();
    }

    public void updateEveryOneMinutes() {
        super.updateEveryOneMinutes();
        moveTo((short) (getX() + Utils.nextInt(-50, 50)), getY());
    }

    public void updateEveryFiveSeconds() {
        super.updateEveryFiveSeconds();
        long now = System.currentTimeMillis();
        if (info.stamina == 0 || info.mp <= (info.mpFull * 10 / 100) || info.hp <= (info.hpFull * 10 / 100)) {
            if (!master.doUsePotion()) {
                master.service.chat(this, "Sư phụ ơi cho con đậu thần");
            }
        } else if (now - master.lastAttack >= 900000 && !master.isBuaDeTu()) {
            master.service.chat(this, "Sao sư phụ không đánh đi?");
        }
    }

    @Override
    public void update() {
        super.update();
        if (!(master.isDead() || isDead() || isFreeze() || isSleep() || isHeld() || isStone()) && (discipleStatus == 1 || discipleStatus == 2) && !isAttacking) {
            changeSkill(0);
            if (skillOpened >= 3) {
                Skill skill = skills.get(2);
                if ((skill.template.id == SkillName.THAI_DUONG_HA_SAN || skill.template.id == SkillName.TAI_TAO_NANG_LUONG) && !skill.isCooldown()) {
                    changeSkill(2);
                }
            }
            if (skillOpened >= 4) {
                Skill skill = skills.get(3);
                if (!skill.isCooldown()) {
                    changeSkill(3);
                }
            }
            if (!this.isRecoveryEnergy() && !isCharge()) {
                Object target = targetDetect();
                if (target != null) {
                    attack(target);
                } else {
                    int d = Utils.getDistance(getX(), getY(), master.getX(), master.getY());
                    if (d > 50) {
                        followMaster();
                    }
                }
                switch (select.template.id) {

                    case SkillName.THAI_DUONG_HA_SAN:
                        skillNotFocus((byte) 0);// tdhs
                        break;

                    case SkillName.TAI_TAO_NANG_LUONG:
                        if (info.hp < info.hpFull || info.mp < info.mpFull) {
                            skillNotFocus((byte) 1);// tái tạo
                        }
                        break;

                    case SkillName.DE_TRUNG:
                        skillNotFocus((byte) 8);// đẻ trứng
                        break;

                    case SkillName.BIEN_HINH:
                        skillNotFocus((byte) 6);// biến ình
                        break;

                    case SkillName.KHIEN_NANG_LUONG:
                        skillNotFocus((byte) 9);// khiên năng lượng
                        break;

                }
            }
        }
        if (skillOpened >= 2) {
            Skill skill = skills.get(1);
            if (skill != null
                    && (skill.template.id == SkillName.CHIEU_DAM_DEMON
                    || skill.template.id == SkillName.CHIEU_DAM_DRAGON
                    || skill.template.id == SkillName.CHIEU_DAM_GALICK)) {
                changeSkillIndex(2);
            }
        }
    }

    @Override
    public void saveData() {
        try {
            Gson g = new Gson();
            if (isDead()) {
                info.hp = 1;
            }
            ArrayList<Item> bodys = new ArrayList<>();
            for (Item item : this.itemBody) {
                if (item != null) {
                    bodys.add(item);
                }
            }
            JSONArray skills = new JSONArray();
            for (Skill skill : this.skills) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("id", skill.template.id);
                    obj.put("level", skill.point);
                    obj.put("last_time_use", skill.lastTimeUseThisSkill);
                    skills.put(obj);
                } catch (JSONException ex) {
                    
                    logger.error("failed!", ex);
                }
            }
            DiscipleData discipleData = new DiscipleData();
            discipleData.type = typeDisciple;
            discipleData.bonus = petBonus;
            discipleData.id = this.id;
            discipleData.name = name;
            discipleData.skill = skills.toString();
            discipleData.info = g.toJson(this.info);
            discipleData.itemBody = g.toJson(bodys);
            discipleData.planet = gender;
            discipleData.status = discipleStatus;
            discipleData.skillOpened = skillOpened;
            GameRepository.getInstance().disciple.save(discipleData);
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public ArrayList<Player> getEnemiesClosestMaster(int distance) {
        ArrayList<Player> list = new ArrayList<>();
        for (Player enemy : this.listTarget) {
            if (enemy.isDead() || enemy.zone != zone) {
                continue;
            }
            if (distance == -1) {
                list.add(enemy);
            } else {
                int d = Utils.getDistance(master.getX(), master.getY(), enemy.getX(), enemy.getY());
                if (d < distance) {
                    list.add(enemy);
                }
            }
        }
        return list;
    }

    public ArrayList<Mob> getMobsClosestMaster(int distance) {
        ArrayList<Mob> list = new ArrayList<>();
        if (zone != null) {
            List<Mob> list2 = zone.getListMob();
            for (Mob mob : list2) {
                if (mob.isDead()) {
                    continue;
                }
                if (distance == -1) {
                    list.add(mob);
                } else {
                    int d = Utils.getDistance(master.getX(), master.getY(), mob.x, mob.y);
                    if (d < distance) {
                        list.add(mob);
                    }
                }
            }
        }
        return list;
    }

    public Player findEnemyClosest(List<Player> enemies) {
        int distanceClosest = -1;
        Player enemyClosest = null;
        for (Player enemy : enemies) {
            if (enemy.isDead() || enemy.zone != this.zone) {
                continue;
            }
            if (!isMeCanAttackOtherPlayer(enemy)) {
                continue;
            }
            int distance = Utils.getDistance(getX(), getY(), enemy.getX(), enemy.getY());
            if (distanceClosest == -1 || distance < distanceClosest) {
                distanceClosest = distance;
                enemyClosest = enemy;
            }
        }
        return enemyClosest;
    }

    public Mob findMobClosest(List<Mob> mobs) {
        int distanceClosest = -1;
        Mob mobClosest = null;
        for (Mob mob : mobs) {
            if (mob.status == 0) {
                continue;
            }
            int distance = Utils.getDistance(getX(), getY(), mob.x, mob.y);
            if (distanceClosest == -1 || distance < distanceClosest) {
                distanceClosest = distance;
                mobClosest = mob;
            }
        }
        return mobClosest;
    }

    public void addTarget(Player _player) {
        if (_player == this || _player == master) {
            return;
        }
        if (!this.listTarget.contains(_player)) {
            this.listTarget.add(_player);
            if (zone != null) {
                String chat = String.format("Mi làm ta nổi giận rồi %s", _player.name);
                zone.service.chat(this, chat);
            }
        }
    }

}
