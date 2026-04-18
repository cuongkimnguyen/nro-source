package _HunrProvision.boss;

import com.ngocrong.consts.Cmd;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemTime;
import com.ngocrong.lib.RandomCollection;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.model.RandomItem;
import com.ngocrong.network.FastDataOutputStream;
import com.ngocrong.network.Message;
import com.ngocrong.network.Service;
import com.ngocrong.server.DropRateService;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillName;
import com.ngocrong.user.Player;
import com.ngocrong.user.Info;
import com.ngocrong.util.Utils;
//import com.ngocrong.NQMP.Event.LuaThanEvent;
//import com.ngocrong.NQMP.Event.QuocKhanh;
import _HunrProvision.services.BoMongService;
import com.ngocrong.bot.Bot;
import com.ngocrong.bot.VirtualBot;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Boss extends Player implements Bot {

    private static final Logger logger = Logger.getLogger(Boss.class);
    public static RandomCollection<Integer> ITEM_GOD = new RandomCollection<>();

    public int limit = -1;
    public long waitingTimeToLeave;
    public String sayTheLastWordBeforeDie;
    public ArrayList<Player> listTarget;
    public int distanceToAddToList;
    public boolean willLeaveAtDeath;
    public long lastTimeSkillShoot;
    public int point;
    public boolean isShow = true;
    public byte percentDame = -1;
    public boolean canDispose = false;
    public static List<Boss> listBoss = new ArrayList<>();

    public static String strBoss(Player player) {
        String x = "";
        for (Boss b : listBoss) {
            if (b != null && b.zone != null && b.zone.map != null) {
                x += b.name + " - ";
                x += b.zone.map.name;
                x += player.getSession().user.getRole() == 1 ? " - Khu " + b.zone.zoneID : "";
                x += "\n";
            }
        }
        return x;
    }

    public void GeneralDrop(Player player) {
        RandomCollection<Integer> rc = new RandomCollection<>();
        rc.add(30, 15);
        rc.add(30, 16);
        rc.add(5, ItemName.AO_THAN_LINH);
        rc.add(4, ItemName.AO_THAN_XAYDA);
        rc.add(3, ItemName.AO_THAN_NAMEC);

        rc.add(4, ItemName.QUAN_THAN_LINH);
        rc.add(2, ItemName.QUAN_THAN_NAMEC);
        rc.add(1, ItemName.QUAN_THAN_XAYDA);

        rc.add(3, ItemName.GANG_THAN_LINH);
        rc.add(3, ItemName.GANG_THAN_XAYDA);
        rc.add(3, ItemName.GANG_THAN_NAMEC);
        rc.add(3, ItemName.GIAY_THAN_LINH);

        rc.add(3, ItemName.GIAY_THAN_XAYDA);
        rc.add(2, ItemName.GIAY_THAN_NAMEC);
        rc.add(4, ItemName.NHAN_THAN_LINH);
        int get = rc.next();
        Item item = new Item(get);
        item.setDefaultOptions();
        item.quantity = 1;
        dropItem(item, player);
    }

    public void dropItem(Item item, Player player) {
        ItemMap itemMap = new ItemMap(zone.autoIncrease++);
        itemMap.item = item;
        itemMap.playerID = player == null ? -1 : Math.abs(player.id);
        itemMap.x = (short) Utils.nextInt(getX() - 100, getX() + 100);
        itemMap.y = (short) Math.min(zone.map.collisionLand(itemMap.x, getY()), getY());
        zone.addItemMap(itemMap);
        zone.service.addItemMap(itemMap);
    }

    protected void dropGroupA(Player player) {
        RandomCollection<Integer> rc = new RandomCollection<>();
        rc.add(10, ItemName.NGOC_RONG_3_SAO);
        rc.add(20, ItemName.NGOC_RONG_4_SAO);
        rc.add(20, ItemName.NGOC_RONG_5_SAO);
        rc.add(20, ItemName.NGOC_RONG_6_SAO);
        rc.add(30, ItemName.NGOC_RONG_7_SAO);
        int id = rc.next();
        Item item = new Item(id);
        item.setDefaultOptions();
        item.quantity = 1;
        dropItem(item, player);
    }

    protected void dropGroupB(Player player) {
        int percent = Utils.nextInt(100);
        if (percent < 10) {
            Item item = new Item(RandomItem.DO_CUOI.next());
            item.setDefaultOptions();
            item.addRandomOption(1, 5);
            item.quantity = 1;
            dropItem(item, player);
        }
    }

    protected void dropGroupC(Player player) {

        try {
            int percent = Utils.nextInt(100);
            int itemId;
            //RadioTest
            if (percent < 100 - DropRateService.getBossRate()) {
                itemId = 190;
            } else {
                if (Utils.isTrue(DropRateService.getTilePhoiBoss(), 100)) {
                    itemId = RandomItem.DO_THAN_LINH_PHOI.next();
                } else {
                    itemId = RandomItem.DO_THAN_LINH_VIP.next();
                }
            }
            Item item = new Item(itemId);
            item.setDefaultOptions();
            item.quantity = itemId == 190 ? 30000 : 1;
            dropItem(item, player);
            if (item.template.id >= 555) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                String currentDate = dateFormat.format(new Date());
                Utils.LogDrop(String.format("%s : %s hạ boss %s nhận được %s ", currentDate, player.name, this.name, item.template.name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[][] infoBoss(Player player) {
        List<Boss> listCheck = new ArrayList<>();
        for (Boss b : listBoss) {
            if (b != null && b.zone != null && b.zone.map != null && b.isShow && b.isBoss() && !(b instanceof VirtualBot)) {
                listCheck.add(b);
            }
        }
        String[][] result = new String[listCheck.size()][];
        for (int i = 0; i < result.length; i++) {
            Boss boss = listCheck.get(i);
            result[i] = new String[3];
            result[i][0] = String.valueOf(boss.getHead());
            result[i][1] = String.valueOf(boss.name);
            result[i][2] = String.valueOf(boss.zone.map.name + (player.getSession().user.getRole() == 1 ? " - " + boss.zone.zoneID : ""));
        }
        return result;
    }

    public static void sendInfoBoss(Player player) throws IOException {
        String[][] info = infoBoss(player);
        Message msg = player.service.messageSubCommand((byte) -99);
        FastDataOutputStream ds = msg.writer();
        try {
            ds.writeByte(6);
            ds.writeInt(info.length);
            for (int i = 0; i < info.length; i++) {
                String[] x = info[i];
                ds.writeShort(Short.parseShort(x[0]));
                ds.writeUTF(x[1]);
                ds.writeUTF(x[2]);
            }
            player.service.sendMessage(msg);
        } finally {
            ds.flush();
            msg.cleanup();
        }
    }

    static {
//        ITEM_GOD.add(1, ItemName.AO_THAN_LINH);
//        ITEM_GOD.add(1, ItemName.AO_THAN_NAMEC);
//        ITEM_GOD.add(1, ItemName.AO_THAN_XAYDA);
//        ITEM_GOD.add(1, ItemName.QUAN_THAN_NAMEC);
//        ITEM_GOD.add(1, ItemName.QUAN_THAN_LINH);
//        ITEM_GOD.add(1, ItemName.GIAY_THAN_LINH);
//        ITEM_GOD.add(1, ItemName.GIAY_THAN_XAYDA);
    }

    public Boss() {
        int id = (int) (System.currentTimeMillis() - 1649080000000L) + Utils.nextInt(1000);
        this.id = -Math.abs(id);
        service = new Service(this);
        setDefaultPart();
        this.listTarget = new ArrayList<>();
        info = new Info(this);
        info.setPowerLimited();
        info.setStamina();
        info.setInfo();
        info.recovery(Info.ALL, 100, false);
        info.percentMiss = 1;
        idMount = -1;
        setHaveMount(false);
        this.waitingTimeToLeave = 1000;
        effects = new ArrayList<>();
        itemTimes = new ArrayList<>();
        distanceToAddToList = 500;
        willLeaveAtDeath = true;
        listBoss.add(this);

        initSkill();
        sortSkill();
        point = 0;
    }

    public void addTarget(Player _c) {
        if (!listTarget.contains(_c)) {
            this.listTarget.add(_c);
        }
    }

    public void setInfo(long hp, long mp, long dame, int def, int crit) {
        info.originalHP = hp;
        info.originalMP = Long.MAX_VALUE;
        info.originalDamage = dame;
        info.originalDefense = def;
        info.originalCritical = crit;
        info.setInfo();
        info.hp = hp;
        info.mp = Long.MAX_VALUE;
        info.recovery(Info.ALL, 100, false);
    }

    @Override
    public void updateSkin() {
        setDefaultPart();
        if (this.isChocolate()) {
            setHead((short) 412);
            setBody((short) 413);
            setLeg((short) 414);
        }
        if (this instanceof VirtualBot && this.fusionType != 0) {
            super.updateSkin();
        }
    }

    public abstract void initSkill();

    public void sortSkill() {
        for (int i = 0; i < skills.size() - 1; i++) {
            Skill skill = (Skill) skills.get(i);
            for (int j = i + 1; j < skills.size(); j++) {
                Skill skill2 = (Skill) skills.get(j);
                if (skill2.coolDown > skill.coolDown) {
                    Skill skill3 = skill2;
                    skill2 = skill;
                    skill = skill3;
                    skills.set(i, skill);
                    skills.set(j, skill2);
                }
            }
        }
    }

    public boolean isMeCanAttackOtherPlayer(Player cAtt) {
        return cAtt != null && !cAtt.isMiniDisciple() && (((cAtt.typePk == 3 && this.typePk == 3) || (this.typePk == 5 || cAtt.typePk == 5 || ((int) this.typePk == 1 && (int) cAtt.typePk == 1)) || ((int) this.typePk == 4 && (int) cAtt.typePk == 4) || (this.testCharId >= 0 && this.testCharId == cAtt.id) || (this.killCharId >= 0 && this.killCharId == cAtt.id && !this.isLang()) || (cAtt.killCharId >= 0 && cAtt.killCharId == this.id && !this.isLang()) || (this.flag == 8 && cAtt.flag != 0) || (this.flag != 0 && cAtt.flag == 8) || (this.flag != cAtt.flag && this.flag != 0 && cAtt.flag != 0)) && cAtt.statusMe != 14) && cAtt.statusMe != 5;
    }

    public ArrayList<Player> getEnemiesClosest() {
        ArrayList<Player> list = new ArrayList<>();
        for (Player enemy : listTarget) {
            if (enemy.isDead() || enemy.zone != zone) {
                continue;
            }
            if (enemy.isBoss()) {
                continue;
            }
            if (isMeCanAttackOtherPlayer(enemy)) {
                if (limit == -1) {
                    list.add(enemy);
                } else {
                    int d = Utils.getDistance(getX(), getY(), enemy.getX(), enemy.getY());
                    if (d < limit) {
                        list.add(enemy);
                    }
                }
            }
        }
        return list;
    }

    public Player randomChar(List<Player> enemies) {
        List<Player> list = new ArrayList<>();
        for (Player enemy : enemies) {
            if (enemy.isDead() || enemy.zone != this.zone || enemy == this) {
                continue;
            }
            if (isMeCanAttackOtherPlayer(enemy)) {
                list.add(enemy);
            }
        }
        Player c = null;
        int size = list.size();
        if (size > 0) {
            int r = Utils.nextInt(size);
            c = list.get(r);
        }
        return c;
    }

    public void setLocation(int mapID, int zoneID) {
        TMap map = MapManager.getInstance().getMap(mapID);
        if (zoneID == -1) {
            zoneID = map.randomZoneID();
        }
        Zone zone = map.getZoneByID(zoneID);
        setLocation(zone);
    }

    public void setLocation(Zone zone) {
        TMap map = zone.map;
        int w = map.width;
        int h = map.height;
        this.setX((short) (w / 2));
        this.setY(map.collisionLand(getX(), (short) 24));
        zone.enter(this);
        sendNotificationWhenAppear(map.name);
    }

    @Override
    public boolean meCanAttack() {
        return !isDead() && !isFreeze() && !isSleep() && !isHeld();
    }

    @Override
    public boolean meCanMove() {
        return super.meCanMove() && getHold() == null;
    }

    @Override
    public void attack(Object obj) {
        long now = System.currentTimeMillis();
        if (now - lastTimeSkillShoot < 1000) {
            return;
        }
        Player target = (Player) obj;
        if (target.inVisible) {
            return;
        }
        Skill skill = selectSkillAttack();
        if (skill != null) {
            int d = Utils.getDistance(0, 0, skill.dx, skill.dy);
            if (skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_ANTOMIC) {
                lastTimeSkillShoot = now;
            }
            this.select = skill;
            moveTo((short) (target.getX() + Utils.nextInt(-d, d)), target.getY());
            zone.attackPlayer(this, target);
        }
    }

    public void moveTo(short x, short y) {
        if (isDead()) {
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

    @Override
    public Object targetDetect() {
        List<Player> enemiesCanAttack = getEnemiesClosest();
        if (enemiesCanAttack.size() > 0) {
            Player target = randomChar(enemiesCanAttack);
            if (target != null && !target.isBoss()) {
                return target;
            }
        }
        return null;
    }

    @Override
    public void killed(Object killer) {
        super.killed(killer);
        if (killer instanceof Player) {
            Player _c = (Player) killer;
            sendNotificationWhenDead(_c.name);
//            LuaThanEvent.bossReward(_c, this);
//            QuocKhanh.bossReward(_c, this);
            
            if (_c != null && _c.currentNhiemVuBoMong != null && !(this instanceof VirtualBot)) {
                 BoMongService nv = _c.currentNhiemVuBoMong;
                
                if (nv.loaiNv == BoMongService.LOAI_KILL_BOSS) {
                    String bossClassName = this.getClass().getSimpleName();
                    
                    if (nv.bossIds == null || nv.bossIds.length == 0) {
                        nv.bossIds = BoMongService.getBossNamesByDifficulty(nv.doKho);
                        if (nv.bossIds == null || nv.bossIds.length == 0) {
                            logger.error(String.format("[BoMong] Player %d killed boss %s: Cannot load bossIds for difficulty %d", _c.id, bossClassName, nv.doKho));
                            return;
                        }
                    }
                    
                    boolean isValidBoss = false;
                    if (nv.bossIds != null && nv.bossIds.length > 0) {
                        for (String bossName : nv.bossIds) {
                            if (bossClassName.equals(bossName)) {
                                isValidBoss = true;
                                break;
                            }
                        }
                    }
                    
                    if (isValidBoss) {
                        nv.tienDo++;
                        _c.checkBoMongProgress(nv);
                        _c.saveBoMongNhiemVu();
                        logger.info(String.format("[BoMong] Player %d killed boss %s: Progress %d/%d", _c.id, bossClassName, nv.tienDo, nv.yeuCau));
                        if (nv.tienDo >= nv.yeuCau) {
                            logger.info(String.format("[BoMong] Player %d killed boss %s: Task completed!", _c.id, bossClassName));
                            _c.completeNhiemVuBoMong();
                        }
                    } else {
                        logger.debug(String.format("[BoMong] Player %d killed boss %s: Not in required list (need: %s)", 
                            _c.id, bossClassName, 
                            (nv.bossIds != null && nv.bossIds.length > 0) ? String.join(", ", nv.bossIds) : "NONE"));
                    }
                }
            }
        }
    }

    // @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
        logger.debug(String.format("BOSS %s vừa xuất hiện tại %s khu vực %d", this.name, map, zone.zoneID));
    }

    //@Override
    public void sendNotificationWhenDead(String name) {
        SessionManager.chatVip(String.format("%s: Đã tiêu diệt được %s mọi người đều ngưỡng mộ.", name, this.name));
    }
//
//    public abstract void sendNotificationWhenAppear(String map);
//
//    public abstract void sendNotificationWhenDead(String name);

    @Override
    public void startDie() {
        listBoss.remove(this);
        listTarget.clear();
        if (this.sayTheLastWordBeforeDie != null) {
            chat(this.sayTheLastWordBeforeDie);
        }
        this.typePk = 0;
        super.startDie();
        if (willLeaveAtDeath) {
            Utils.setTimeout(() -> {
                if (zone != null) {
                    zone.leave(this);
                }
            }, waitingTimeToLeave);
        }

    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public boolean isEscort() {
        return false;
    }

    @Override
    public void move() {
        if (!meCanMove()) {
            return;
        }
        TMap map = zone.map;
        int w = map.width;
        int h = map.height;
        int x = Utils.nextInt(50, w - 50);
        int y = Utils.nextInt(24, h - 24);
        setX((short) (getX() + Utils.nextInt(-100, 100)));
        if (getX() < 50) {
            setX((short) 50);
        }
        if (getX() > w - 50) {
            setX((short) (w - 50));
        }
        setY(map.collisionLand(getX(), (short) y));
        setY(map.collisionLand(getX(), (short) 0));
        zone.service.move(this);
    }

    public void addTargetToList() {
        List<Player> list = zone.getListChar(Zone.TYPE_HUMAN, Zone.TYPE_PET);
        for (Player _c : list) {
            if (_c != this && !_c.isVoHinh() && !_c.isInvisible()) {
                int d = Utils.getDistance(getX(), getY(), _c.getX(), _c.getY());
                if (d < this.distanceToAddToList) {
                    addTarget(_c);
                }
            }
        }
    }

    @Override
    public void updateEveryOneSeconds() {
        if (!isDead()) {
            if (!isRecoveryEnergy() && !isCharge()) {
                move();
            }
            addTargetToList();
            if (isRecoveryEnergy()) {
                skillNotFocus((byte) 2);
            }
        }
        if (canDispose) {
            if (zone == null) {
                lastAttack = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - lastAttack >= 15 * 60000 && zone != null) {
                lastAttack = System.currentTimeMillis();
                this.startDie();
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

    public boolean isAttack() {
        return typePk != 0;
    }

    @Override
    public void updateEveryHalfSeconds() {
        this.info.percentMiss = this.info.options[108] = Math.max(this.info.options[108], 10);
        super.updateEveryHalfSeconds();
        if (!isDead()) {
            if (isAttack() && meCanAttack()) {
                if (!isRecoveryEnergy() && !isCharge()) {
                    Object target = targetDetect();
                    if (target != null) {
                        attack(target);
                    }
                    useSkillNotFocus();
                }
            }
        }
    }

    public Skill selectSkillAttack() {
        List<Skill> list = new ArrayList<>();
        for (Skill skill : skills) {
            if (!skill.isCooldown()) {
                if (skill.template.type == 1) {
                    list.add(skill);
                }
            }
        }
        if (list.size() > 0) {
            int index = Utils.nextInt(list.size());
            return list.get(index);
        } else {
            return null;
        }
    }

    public void useSkillNotFocus() {
        for (Skill skill : skills) {
            if (!skill.isCooldown()) {
                if (skill.template.type == 3) {
                    select = skill;
                    switch (select.template.id) {
                        case SkillName.THAI_DUONG_HA_SAN:
                            skillNotFocus((byte) 0);// tdhs
                            break;

                        case SkillName.TAI_TAO_NANG_LUONG:
                            if (info.hp < info.hpFull || info.mp < info.mp) {
                                skillNotFocus((byte) 1);// tái tạo
                            }
                            break;

                        case SkillName.DE_TRUNG:
                            skillNotFocus((byte) 8);// đẻ trứng
                            break;

                        case SkillName.BIEN_HINH:
                            skillNotFocus((byte) 6);// biến hình
                            break;

                        case SkillName.TU_PHAT_NO:
                            //skillNotFocus((byte) 7);// Tự phát nổ
                            break;

                        case SkillName.KHIEN_NANG_LUONG:
                            skillNotFocus((byte) 9);// khiên năng lượng
                            break;

                        case SkillName.HUYT_SAO:
                            skillNotFocus((byte) 10);// Huýt sáo
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void wakeUpFromDead() {
        if (!isDead()) {
            return;
        }
        this.statusMe = 1;
        this.info.hp = this.info.hpFull;
        this.info.mp = this.info.mpFull;
        setDead(false);
        service.sendMessage(new Message(Cmd.ME_LIVE));
        if (zone != null) {
            zone.service.playerLoadLive(this);
        }
    }

    @Override
    public void updateEveryThirtySeconds() {
        super.updateEveryThirtySeconds();
    }

    @Override
    public void updateEveryOneMinutes() {

    }

    @Override
    public void chat(String chat) {
        if (zone != null) {
            zone.service.chat(this, chat);
        }
    }

    public void startProtect(int seconds) {
        ItemTime item = new ItemTime(ItemTimeName.KHIEN_NANG_LUONG, 3784, seconds, false);
        this.setProtected(true);
        addItemTime(item);
        zone.service.setEffect(null, this.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 33);
    }

    @Override
    public abstract void setDefaultLeg();

    @Override
    public abstract void setDefaultBody();

    @Override
    public abstract void setDefaultHead();

}
