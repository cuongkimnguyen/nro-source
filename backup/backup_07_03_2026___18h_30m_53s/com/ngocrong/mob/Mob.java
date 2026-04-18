package com.ngocrong.mob;

//import com.ngocrong.NQMP.DaNangCap.EventDaNangCap;
//import com.ngocrong.NQMP.TamThangBa.Event1;
//import com.ngocrong.NQMP.Tet2025.EventTet2025;
//import com.ngocrong.NQMP.SummerBeach.SummerBeachEvent;
//import com.ngocrong.NQMP.Event.NuocMiaEvent;
//import com.ngocrong.NQMP.Event.LuaThanEvent;
//import com.ngocrong.NQMP.Event.QuocKhanh;
import _HunrProvision.HoangAnhDz;
import com.ngocrong.bot.BotCold;
import com.ngocrong.bot.Disciple;
import com.ngocrong.consts.Cmd;
import com.ngocrong.consts.MobName;
import com.ngocrong.event.Event;
import com.ngocrong.item.ItemOption;
import com.ngocrong.map.tzone.ZTreasure;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.model.Hold;
import com.ngocrong.model.RandomItem;
import com.ngocrong.network.Message;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Server;
import com.ngocrong.server.DropRateService;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.SpecialSkill;
import com.ngocrong.task.TaskText;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import _HunrProvision.services.BoMongService;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemTime;
import com.ngocrong.mob._BigBoss.Hirudegarn;
import com.ngocrong.network.FastDataOutputStream;
import com.ngocrong.server.Config;
import com.ngocrong.server.SessionManager;
import lombok.Data;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class Mob {

    public enum ItemType {
        NONE, GOLD, ITEM, EQUIP, GEM, CARD, EVENT
    }

    private static Logger logger = Logger.getLogger(Mob.class);

    public static final int[] LEVEL = {-1, -1, 1, 2, 3, 4, 5, 6, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11, 11, 12, 12, 12, 12, -1, -1, -1};
    public static final int[][][] OPTIONS = {{{127, 139}, {128, 140}, {129, 141}}, {{130, 142}, {131, 143}, {132, 144}}, {{133, 136}, {134, 137}, {135, 138}}};

    public static ArrayList<MobTemplate> vMobTemplate = new ArrayList<>();
    public static byte[] data;
    public static int baseId = 0;

    public static void addMobTemplate(MobTemplate mob) {
        vMobTemplate.add(mob);
    }

    public static MobTemplate getMobTemplate(int id) {
        return vMobTemplate.get(id);
    }

    public static void createData() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(vMobTemplate.size());
            for (MobTemplate mob : vMobTemplate) {
                dos.writeByte(mob.mobTemplateId);
                dos.writeByte(mob.type);
                dos.writeUTF(mob.name);
                dos.writeLong(mob.hp);
                dos.writeByte(mob.rangeMove);
                dos.writeByte(mob.speed);
                dos.writeByte(mob.dartType);
            }
            data = bos.toByteArray();
            dos.close();
            bos.close();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setHPforMob(long hp) {
        this.hp = maxHp = hpDefault = hp;
    }
    public static final long DELAY_RESPAWN = 5000L;
    public int mobId;
    public byte templateId;
    public long hp, maxHp, hpDefault;
    public boolean isMobMe;
    public short x;
    public short y;
    public byte status;
    public byte levelBoss;
    public byte sys;
    public byte level;
    public byte type;
    public boolean isDisable, isDontMove, isFire, isIce, isWind, isBoss;
    protected long lastTimeAttack = 0L;
    protected long attackDelay = 2000L;
    protected ArrayList<Player> listPlayer = new ArrayList<>();
    public long damage1;
    public long damage2;
    public Zone zone;
    public ArrayList<ItemMap> items = new ArrayList<>();
    public long deadTime;
    public boolean isFreeze, isSleep, isBlind;
    public int seconds;
    public final ArrayList<ItemTime> vItemTime = new ArrayList<>();
    public Lock lock = new ReentrantLock();
    public boolean isHeld;
    public Hold hold;
    public int timeLive;
    public int percentDamage;
    public boolean isChangeBody;
    public short body;
    public int dameDown;

    public void addItemTime(ItemTime item) {
        synchronized (this.vItemTime) {
            for (ItemTime itm : this.vItemTime) {
                if (itm.id == item.id) {
                    itm.seconds = item.seconds;
                    return;
                }
            }
            this.vItemTime.add(item);
        }
    }

    public void throwItem(Player _c) {
        if (_c == null) {
            return;
        }
        if (_c.isBot()) {
            return;
        }
        if (zone == null) {
            return;
        }
        if (this.templateId == 70) {
            if (Utils.isTrue(5, 100)) {
                Player c = _c;
                int[] tempId = new int[]{ItemName.AO_THAN_LINH, ItemName.AO_THAN_NAMEC, ItemName.AO_THAN_XAYDA};
                Item item = new Item(tempId[Utils.nextInt(tempId.length)]);
                item.setDefaultOptions();
                ItemMap itemMap = new ItemMap(zone.autoIncrease++);
                itemMap.item = item;
                itemMap.playerID = Math.abs(c.id);
                itemMap.x = getX();
                itemMap.y = zone.map.collisionLand(getX(), getY());
                zone.addItemMap(itemMap);
                zone.service.addItemMap(itemMap);
            }
            return;
        }
        if (_c.taskMain != null) {
            int itemTask = -1;
            if (_c.taskMain.id == 2 && _c.taskMain.index == 0) {
                int mobTemplateID = (new int[]{MobName.KHUNG_LONG, MobName.LON_LOI, MobName.QUY_DAT})[_c.gender];
                if (templateId == mobTemplateID) {
                    itemTask = ItemName.DUI_GA;
                }
            }
            if (_c.taskMain.id == 8 && _c.taskMain.index == 1) {
                int mobTemplateID = (new int[]{MobName.PHI_LONG_ME, MobName.QUY_BAY_ME, MobName.THAN_LAN_ME})[_c.gender];
                if (templateId == mobTemplateID) {
                    if (Utils.nextInt(2) == 0) {
                        itemTask = ItemName.NGOC_RONG_7_SAO;
                        _c.taskNext();
                    } else {
                        _c.service.sendThongBao(TaskText.TASK_8_1[_c.gender]);
                    }
                }
            }
            if (_c.taskMain.id == 14 && _c.taskMain.index == 1) {
                int mobTemplateID = (new int[]{MobName.OC_MUON_HON, MobName.OC_SEN, MobName.HEO_XAYDA_ME})[_c.gender];
                if (templateId == mobTemplateID) {
                    if (Utils.nextInt(10) == 0) {
                        itemTask = ItemName.TRUYEN_TRANH;
                        _c.service.sendThongBao("Bạn đã tìm thấy cuốn truyện rồi, hãy click đôi vào đối tượng để lấy");
                    } else {
                        _c.service.sendThongBao(TaskText.TASK_14_1[_c.gender]);
                    }
                }
            }
            if (itemTask != -1) {
                ItemMap task = new ItemMap(zone.autoIncrease++);
                Item item = new Item(itemTask);
                item.setDefaultOptions();
                item.quantity = 1;

                task.playerID = Math.abs(_c.id);
                task.isPickedUp = false;
                task.x = (short) (this.x + Utils.nextInt(-30, 30));
                task.y = zone.map.collisionLand(this.x, this.y);
                task.item = item;
                this.items.add(task);
                zone.addItemMap(task);
            }
        }
        if ((_c.zone.map.mapID == 1 || _c.zone.map.mapID == 8 || _c.zone.map.mapID == 15)) {
            ItemMap task = new ItemMap(zone.autoIncrease++);
            Item item = new Item(ItemName.DUI_GA_NUONG);
            item.setDefaultOptions();
            item.quantity = 1;

            task.playerID = Math.abs(_c.id);
            task.isPickedUp = false;
            task.x = (short) (this.x + Utils.nextInt(-30, 30));
            task.y = zone.map.collisionLand(this.x, this.y);
            task.item = item;
            this.items.add(task);
            zone.addItemMap(task);
        }
//        if (this.body == 4132 && isChangeBody) {
//            ItemMap chocolate = new ItemMap(zone.autoIncrease++);
//            Item item = new Item(ItemName.SOCOLA);
//            item.setDefaultOptions();
//            item.quantity = 1;
//            chocolate.playerID = Math.abs(_c.id);
//            chocolate.isPickedUp = false;
//            chocolate.x = (short) (this.x + Utils.nextInt(-30, 30));
//            chocolate.y = zone.map.collisionLand(this.x, this.y);
//            chocolate.item = item;
//            this.items.add(chocolate);
//            zone.addItemMap(chocolate);
//        }
        if (_c.isMayDo() && zone.map.isFuture()) {
            int rd = Utils.nextInt(20);
            if (rd == 0) {
                ItemMap capsule = new ItemMap(zone.autoIncrease++);
                Item item = new Item(ItemName.VIEN_CAPSULE_KI_BI);
                item.setDefaultOptions();
                item.quantity = 1;
                capsule.playerID = Math.abs(_c.id);
                capsule.isPickedUp = false;
                capsule.x = (short) (this.x + Utils.nextInt(-30, 30));
                capsule.y = zone.map.collisionLand(this.x, this.y);
                capsule.item = item;
                this.items.add(capsule);
                zone.addItemMap(capsule);
                if (_c.taskMain.id == 26 && _c.taskMain.index == 0) {
                    _c.updateTaskCount(1);
                }
            }
        }
        if (zone.map.mapID == 183 || zone.map.mapID == 184) {
                int random = (_c.danhHieu() != null && _c.danhHieu().template.id == ItemName.DANH_HIEU_THIEN_TU) ? 3 : 1;
                if (Utils.isTrue(random, 3000)) {
                    ItemMap maquai = new ItemMap(zone.autoIncrease++);
                    Item item = new Item(ItemName.MA_QUAI);
                    item.setDefaultOptions();
                    item.quantity = 1;
                    maquai.playerID = Math.abs(_c.id);
                    maquai.isPickedUp = false;
                    maquai.x = (short) (this.x + Utils.nextInt(-30, 30));
                    maquai.y = zone.map.collisionLand(this.x, this.y);
                    maquai.item = item;
                    this.items.add(maquai);
                    zone.addItemMap(maquai);
                }
                if (Utils.isTrue(random, 30000)) {
                    ItemMap tinhthe = new ItemMap(zone.autoIncrease++);
                    Item item = new Item(ItemName.TINH_THE);
                    item.setDefaultOptions();
                    item.quantity = 1;
                    tinhthe.playerID = Math.abs(_c.id);
                    tinhthe.isPickedUp = false;
                    tinhthe.x = (short) (this.x + Utils.nextInt(-30, 30));
                    tinhthe.y = zone.map.collisionLand(this.x, this.y);
                    tinhthe.item = item;
                    this.items.add(tinhthe);
                    zone.addItemMap(tinhthe);
                }
            
        }
        if (zone.map.isMapThoiKhong()) {
            if (Utils.isTrue(1, 3000)) {
                ItemMap honlinhthu = new ItemMap(zone.autoIncrease++);
                Item item = new Item(ItemName.HON_LINH_THU);
                item.setDefaultOptions();
                item.quantity = 1;
                honlinhthu.playerID = Math.abs(_c.id);
                honlinhthu.isPickedUp = false;
                honlinhthu.x = (short) (this.x + Utils.nextInt(-30, 30));
                honlinhthu.y = zone.map.collisionLand(this.x, this.y);
                honlinhthu.item = item;
                this.items.add(honlinhthu);
                zone.addItemMap(honlinhthu);
            }
            if (Utils.isTrue(1, 25000)) {
                ItemMap thangtinhthach = new ItemMap(zone.autoIncrease++);
                Item item = new Item(ItemName.THANG_TINH_THACH);
                item.setDefaultOptions();
                item.quantity = 1;
                thangtinhthach.playerID = Math.abs(_c.id);
                thangtinhthach.isPickedUp = false;
                thangtinhthach.x = (short) (this.x + Utils.nextInt(-30, 30));
                thangtinhthach.y = zone.map.collisionLand(this.x, this.y);
                thangtinhthach.item = item;
                this.items.add(thangtinhthach);
                zone.addItemMap(thangtinhthach);
            }
        }
        if (zone.map.isMapPet()) {
            if (Utils.isTrue(1, DropRateService.getPetFinal4s())) {
                ItemMap itemMap = new ItemMap(zone.autoIncrease++);
                Item item = new Item(RandomItem.DO_CUOI.next());
                item.setDefaultOptions();
                item.addRandomOption(4, 4);
                item.quantity = 1;
                itemMap.playerID = Math.abs(_c.id);
                itemMap.isPickedUp = false;
                itemMap.x = (short) (this.x + Utils.nextInt(-30, 30));
                itemMap.y = zone.map.collisionLand(this.x, this.y);
                itemMap.item = item;
                this.items.add(itemMap);
                zone.addItemMap(itemMap);
            }
            if (Utils.isTrue(1, DropRateService.getPetFinal5s())) {
                ItemMap itemMap = new ItemMap(zone.autoIncrease++);
                Item item = new Item(RandomItem.DO_CUOI.next());
                item.setDefaultOptions();
                item.addRandomOption(5, 5);
                item.quantity = 1;
                itemMap.playerID = Math.abs(_c.id);
                itemMap.isPickedUp = false;
                itemMap.x = (short) (this.x + Utils.nextInt(-30, 30));
                itemMap.y = zone.map.collisionLand(this.x, this.y);
                itemMap.item = item;
                this.items.add(itemMap);
                zone.addItemMap(itemMap);
            }
            if (Utils.isTrue(1, DropRateService.getPetFinal6s())) {
                ItemMap itemMap = new ItemMap(zone.autoIncrease++);
                Item item = new Item(RandomItem.DO_CUOI.next());
                item.setDefaultOptions();
                item.addRandomOption(6, 6);
                item.quantity = 1;
                itemMap.playerID = Math.abs(_c.id);
                itemMap.isPickedUp = false;
                itemMap.x = (short) (this.x + Utils.nextInt(-30, 30));
                itemMap.y = zone.map.collisionLand(this.x, this.y);
                itemMap.item = item;
                this.items.add(itemMap);
                zone.addItemMap(itemMap);
            }
            if (Utils.isTrue(1, DropRateService.getPetFinal7s())) {
                ItemMap itemMap = new ItemMap(zone.autoIncrease++);
                Item item = new Item(RandomItem.DO_CUOI.next());
                item.setDefaultOptions();
                item.addRandomOption(7, 7);
                item.quantity = 1;
                itemMap.playerID = Math.abs(_c.id);
                itemMap.isPickedUp = false;
                itemMap.x = (short) (this.x + Utils.nextInt(-30, 30));
                itemMap.y = zone.map.collisionLand(this.x, this.y);
                itemMap.item = item;
                this.items.add(itemMap);
                zone.addItemMap(itemMap);
            }
            if (Utils.isTrue(1, DropRateService.getPetFinal8s())) {
                ItemMap itemMap = new ItemMap(zone.autoIncrease++);
                Item item = new Item(RandomItem.DO_CUOI.next());
                item.setDefaultOptions();
                item.addRandomOption(8, 8);
                item.quantity = 1;
                itemMap.playerID = Math.abs(_c.id);
                itemMap.isPickedUp = false;
                itemMap.x = (short) (this.x + Utils.nextInt(-30, 30));
                itemMap.y = zone.map.collisionLand(this.x, this.y);
                itemMap.item = item;
                this.items.add(itemMap);
                zone.addItemMap(itemMap);
            }
            if (Utils.isTrue(1, DropRateService.getPetDivine())) {
                ItemMap itemMap = new ItemMap(zone.autoIncrease++);
                Item item = new Item(RandomItem.DO_THAN_LINH.next());
                item.setDefaultOptions();
                item.quantity = 1;
                itemMap.playerID = Math.abs(_c.id);
                itemMap.isPickedUp = false;
                itemMap.x = (short) (this.x + Utils.nextInt(-30, 30));
                itemMap.y = zone.map.collisionLand(this.x, this.y);
                itemMap.item = item;
                this.items.add(itemMap);
                zone.addItemMap(itemMap);
            }
            if (Utils.isTrue(1, DropRateService.getPetDestroy())) {
                int[][] hd = new int[][]{
                    {ItemName.AO_HUY_DIET_TD, ItemName.QUAN_HUY_DIET_TD, ItemName.GANG_HUY_DIET_TD, ItemName.GIAY_HUY_DIET_TD, ItemName.NHAN_HUY_DIET},
                    {ItemName.AO_HUY_DIET_NM, ItemName.QUAN_HUY_DIET_NM, ItemName.GANG_HUY_DIET_NM, ItemName.GIAY_HUY_DIET_NM, ItemName.NHAN_HUY_DIET},
                    {ItemName.AO_HUY_DIET_XD, ItemName.QUAN_HUY_DIET_XD, ItemName.GANG_HUY_DIET_XD, ItemName.GIAY_HUY_DIET_XD, ItemName.NHAN_HUY_DIET}
                };
                int[] arr = hd[Math.max(0, Math.min(hd.length - 1, _c.gender))];
                ItemMap itemMap = new ItemMap(zone.autoIncrease++);
                Item item = new Item(arr[Utils.nextInt(arr.length)]);
                item.setDefaultOptions();
                item.addItemOption(new ItemOption(30, 0));
                item.quantity = 1;
                itemMap.playerID = Math.abs(_c.id);
                itemMap.isPickedUp = false;
                itemMap.x = (short) (this.x + Utils.nextInt(-30, 30));
                itemMap.y = zone.map.collisionLand(this.x, this.y);
                itemMap.item = item;
                this.items.add(itemMap);
                zone.addItemMap(itemMap);
            }
        }
        if (_c.isSetThanLinh()) {
            int rd = Utils.nextInt(50);
            if (rd < _c.getCountItemLevel((byte) 13)) // Count set thần linh
            {
                ItemMap food = new ItemMap(zone.autoIncrease++);
                Item item = new Item(RandomItem.FOOD.next());
                item.addItemOption(new ItemOption(30, 0));
                item.quantity = 1;
                food.playerID = Math.abs(_c.id);
                food.isPickedUp = false;
                food.x = (short) (this.x + Utils.nextInt(-30, 30));
                food.y = zone.map.collisionLand(this.x, this.y);
                food.item = item;
                this.items.add(food);
                zone.addItemMap(food);
            }

        }
        if (_c.isDoSaoPhaLe()) {
            int rd = Utils.nextInt(100);
            if (rd == 0) {
                ItemMap spl = new ItemMap(zone.autoIncrease++);
                Item item = new Item(RandomItem.SAO_PHA_LE.next());
                item.setDefaultOptions();
                item.quantity = 1;
                spl.playerID = Math.abs(_c.id);
                spl.isPickedUp = false;
                spl.x = (short) (this.x + Utils.nextInt(-30, 30));
                spl.y = zone.map.collisionLand(this.x, this.y);
                spl.item = item;
                this.items.add(spl);
                zone.addItemMap(spl);
            }
        }
//        if (this.zone.map.isFuture()) {
//            int rd = Utils.nextInt(300);
//            if (rd == 0) {
//                ItemMap locphat3s = new ItemMap(zone.autoIncrease++);
//                Item item = new Item(ItemName.NGOC_RONG_LOC_PHAT_3_SAO);
//                item.quantity = 1;
//                locphat3s.playerID = Math.abs(_c.id);
//                locphat3s.isPickedUp = false;
//                locphat3s.x = (short) (this.x + Utils.nextInt(-30, 30));
//                locphat3s.y = zone.map.collisionLand(this.x, this.y);
//                locphat3s.item = item;
//                this.items.add(locphat3s);
//                zone.addItemMap(locphat3s);
//            }
//        }
//        if (this.zone.map.isNappa()) {
//            int rd = Utils.nextInt(300);
//            if (rd == 0) {
//                ItemMap locphat2s = new ItemMap(zone.autoIncrease++);
//                Item item = new Item(ItemName.NGOC_RONG_LOC_PHAT_2_SAO);
//                item.quantity = 1;
//                locphat2s.playerID = Math.abs(_c.id);
//                locphat2s.isPickedUp = false;
//                locphat2s.x = (short) (this.x + Utils.nextInt(-30, 30));
//                locphat2s.y = zone.map.collisionLand(this.x, this.y);
//                locphat2s.item = item;
//                this.items.add(locphat2s);
//                zone.addItemMap(locphat2s);
//            }
//        }
//        if (this.zone.map.isMapTet()) {
//            int rd = Utils.nextInt(250);
//            if (rd == 0) {
//                ItemMap locphat = new ItemMap(zone.autoIncrease++);
//                int itemID = Utils.nextInt(ItemName.NGOC_RONG_LOC_PHAT_1_SAO, ItemName.NGOC_RONG_LOC_PHAT_1_SAO + 6);
//                Item item = new Item(itemID);
//                item.quantity = 1;
//                locphat.playerID = Math.abs(_c.id);
//                locphat.isPickedUp = false;
//                locphat.x = (short) (this.x + Utils.nextInt(-30, 30));
//                locphat.y = zone.map.collisionLand(this.x, this.y);
//                locphat.item = item;
//                this.items.add(locphat);
//                zone.addItemMap(locphat);
//            }
//        }
        if (this.zone.map.isNappa() || this.zone.map.isCold() || this.zone.map.isFuture()) {
            dropManhSuuTam(_c);
            int rd = Utils.nextInt(200);
            if (rd == 0) {
                ItemMap spl = new ItemMap(zone.autoIncrease++);
                Item item = new Item(RandomItem.MOB_NAPPA_COLD_FUTURE.next());
                item.setDefaultOptions();
                item.quantity = 1;
                spl.playerID = Math.abs(_c.id);
                spl.isPickedUp = false;
                spl.x = (short) (this.x + Utils.nextInt(-30, 30));
                spl.y = zone.map.collisionLand(this.x, this.y);
                spl.item = item;
                this.items.add(spl);
                zone.addItemMap(spl);
            }
        }
        if (this.zone.map.mapID == 178 || this.zone.map.mapID == 179 || this.zone.map.mapID == 180) {
            boolean rd = Utils.isTrue(_c.exitsItemTime(ItemTimeName.BUA_MA_THUAT) ? 4 : 1, 200);
            if (rd) {
                ItemMap spl = new ItemMap(zone.autoIncrease++);
                Item item = new Item(Utils.nextInt(2275, 2277));
                item.setDefaultOptions();
                item.quantity = 1;
                spl.playerID = Math.abs(_c.id);
                spl.isPickedUp = false;
                spl.x = (short) (this.x + Utils.nextInt(-30, 30));
                spl.y = zone.map.collisionLand(this.x, this.y);
                spl.item = item;
                this.items.add(spl);
                zone.addItemMap(spl);
            }
        }
        if (this.zone.map.isCold()) {
            int rd = HoangAnhDz.checkDrop();
            if (rd != -1) {
                ItemMap itemDrop = new ItemMap(zone.autoIncrease++);
                Item item = new Item(rd);
                item.setDefaultOptions();
                item.quantity = 1;
                if (rd <= 281) {
                    if (Utils.isTrue(95, 100)) {
                        item.addRandomOption(1, 3);
                    } else {
                        item.addRandomOption(2, 5);
                    }
                } else {
                    SessionManager.chatVip(String.format("%s vừa nhặt được %s tại %s", _c.name, item.template.name, this.zone.map.name));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    String currentDate = dateFormat.format(new Date());
                    Utils.LogDrop(String.format("%s : %s vừa nhặt được %s tại %s", currentDate, _c.name, item.template.name, this.zone.map.name));
                }
                itemDrop.playerID = Math.abs(_c.id);
                itemDrop.isPickedUp = false;
                itemDrop.x = (short) (this.x + Utils.nextInt(-30, 30));
                itemDrop.y = zone.map.collisionLand(this.x, this.y);
                itemDrop.item = item;
                this.items.add(itemDrop);
                zone.addItemMap(itemDrop);
            }
//            int dropnr6s = Utils.nextInt(500);
//            if (dropnr6s == 1) {
//                ItemMap locphat6s = new ItemMap(zone.autoIncrease++);
//                Item item = new Item(ItemName.NGOC_RONG_LOC_PHAT_6_SAO);
//                item.quantity = 1;
//                locphat6s.playerID = Math.abs(_c.id);
//                locphat6s.isPickedUp = false;
//                locphat6s.x = (short) (this.x + Utils.nextInt(-30, 30));
//                locphat6s.y = zone.map.collisionLand(this.x, this.y);
//                locphat6s.item = item;
//                this.items.add(locphat6s);
//                zone.addItemMap(locphat6s);
//            }
        }
//        EventTet2025.mobReward(_c);
//        EventDaNangCap.MobReward(_c);
//        Event1.mobReward(_c);
//        SummerBeachEvent.mobReward(_c, this);
        //  NuocMiaEvent.mobReward(_c, this);
        com.ngocrong.event.OsinTetEvent.mobReward(_c, this);

//        LuaThanEvent.mobReward(_c, this);
//        QuocKhanh.mobReward(_c, this);

//        else if (this.zone.map.isNormalMap()) {
        ////            int rd = Utils.nextInt(50);
//            int rd = 0;
//            if (rd == 0) {
//                ItemMap spl = new ItemMap(zone.autoIncrease++);
//                Item item = new Item(RandomItem.MOB_NORMAL.next());
//                item.setDefaultOptions();
//                if (item.isFirstItemShop()) {
//                    item.setParamsSKHGender(item);
//                }
//                item.randomParam();
//                item.quantity = 1;
//                spl.playerID = Math.abs(_c.id);
//                spl.isPickedUp = false;
//                spl.x = (short) (this.x + Utils.nextInt(-30, 30));
//                spl.y = zone.map.collisionLand(this.x, this.y);
//                spl.item = item;
//                this.items.add(spl);
//                zone.addItemMap(spl);
//            }
//        }
        int itemID = -1;
        Item item = null;
        if (!zone.map.isMapDeTu()) {

            ItemType type = RandomItem.MOB.next();
            if (type == ItemType.EVENT) {
                if (Math.abs(this.level - _c.info.level) < 5) {
                    item = new Item(Event.getItems().next());
                    item.quantity = 1;
                    item.setDefaultOptions();
                }
            } else if (type == ItemType.GOLD) {
                int gold1 = this.level * 100;
                int gold2 = gold1 - (gold1 / Utils.nextInt(1, 3));
                int gold = Utils.nextInt(gold2, gold1);
                if (_c.info.options[155] > 0) {
                    gold += gold * _c.info.options[155] / 100;
                }
                SpecialSkill sp = _c.getSpecialSkill();
                if (sp != null) {
                    if (sp.id == 29) {
                        gold += gold * sp.param / 100;
                    }
                }
                itemID = Utils.getItemGoldByQuantity(gold);
                item = new Item(itemID);
                item.setDefaultOptions();
                item.quantity = gold;
            }
            if (zone.map.mapID == 156 || zone.map.mapID == 157) {
                int rd = Utils.nextInt(100);
                if (rd == 0) {
                    ItemMap food = new ItemMap(zone.autoIncrease++);
                    item = new Item(ItemName.MANH_VO_BONG_TAI);
                    item.addItemOption(new ItemOption(30, 0));
                    item.quantity = 1;
                    item.isLock = true;
                    food.playerID = Math.abs(_c.id);
                    food.isPickedUp = false;
                    food.x = (short) (this.x + Utils.nextInt(-30, 30));
                    food.y = zone.map.collisionLand(this.x, this.y);
                    food.item = item;
                    this.items.add(food);
                    zone.addItemMap(food);
                    _c.itemDrop[0]++;
                    return;
                }
            } else if (zone.map.mapID == 158) {
                int rate = _c.exitsItemTime(ItemTimeName.X3MVBTC3) ? 100 : 300;
                int rd = Utils.nextInt(rate);
                if (rd == 0) {
                    ItemMap food = new ItemMap(zone.autoIncrease++);
                    item = new Item(ItemName.MANH_VO_BONG_TAI_CAP_3);
                    item.addItemOption(new ItemOption(30, 0));
                    item.quantity = 1;
                    item.isLock = true;
                    food.playerID = Math.abs(_c.id);
                    food.isPickedUp = false;
                    food.x = (short) (this.x + Utils.nextInt(-30, 30));
                    food.y = zone.map.collisionLand(this.x, this.y);
                    food.item = item;
                    this.items.add(food);
                    zone.addItemMap(food);
                    _c.itemDrop[5]++;
                    return;
                }
                int rd2 = Utils.nextInt(1000);
                if (rd2 == 0) {
                    ItemMap food = new ItemMap(zone.autoIncrease++);
                    item = new Item(ItemName.MANH_HON_BONG_TAI);
                    item.quantity = 1;
                    food.playerID = Math.abs(_c.id);
                    food.isPickedUp = false;
                    food.x = (short) (this.x + Utils.nextInt(-30, 30));
                    food.y = zone.map.collisionLand(this.x, this.y);
                    food.item = item;
                    this.items.add(food);
                    zone.addItemMap(food);
                    _c.itemDrop[1]++;
                    return;
                }
            }
//
            if (Utils.nextInt(100) == 5) {
                int[] dnc = new int[]{220, 221, 222, 223, 224};
                ItemMap danangcap = new ItemMap(zone.autoIncrease++);

                item = new Item(dnc[Utils.nextInt(dnc.length)]);
                item.quantity = 1;
                danangcap.playerID = Math.abs(_c.id);
                danangcap.isPickedUp = false;
                danangcap.x = (short) (this.x + Utils.nextInt(-30, 30));
                danangcap.y = zone.map.collisionLand(this.x, this.y);
                danangcap.item = item;
                this.items.add(danangcap);
                zone.addItemMap(danangcap);
                _c.itemDrop[2]++;
                return;
            }
        }

//        else if (type == ItemType.GEM) {
//            item = new Item(861);
//            item.setDefaultOptions();
//            item.quantity = 1;
//        } else if (type == ItemType.ITEM) {
//            int rd = Utils.nextInt(10);
//            switch (rd) {
//                case 0:
//                    itemID = 191;
//                    break;
//
//                case 1:
//                    itemID = 192;
//                    break;
//
//                case 2:
//                    itemID = 74;
//                    break;
//
//                case 3:
//                    itemID = 225;
//                    break;
//            }
//            if (itemID != -1) {
//                item = new Item(itemID);
//                item.setDefaultOptions();
//                item.quantity = 1;
//            }
//        }
//        else if (type == ItemType.EQUIP) {
//            int level = LEVEL[this.level];
//            if (level != -1) {
//                int p2 = level;
//                if (p2 < 2) {
//                    p2 = 2;
//                }
//                if (Utils.nextInt(p2) == 0) {
//                    Server server = DragonBall.getInstance().getServer();
//                    itemID = server.randomItemTemplate(_c.getGender(), level);
//                    if (itemID != -1) {
//                        item = new Item(itemID);
//                        item.quantity = 1;
//                        item.setDefaultOptions();
//
//                        if (Utils.nextInt(5) == 0) {
//                            for (ItemOption option : item.options) {
//                                int p = Utils.nextInt(1, 10);
//                                int add = option.param * p / 100;
//                                if (add == 0) {
//                                    add = 1;
//                                }
//                                option.param += (Utils.nextInt(2) == 0 ? 1 : -1) * add;
//                                if (option.param <= 0) {
//                                    option.param = 1;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        else if (type == ItemType.CARD) {
//            int id = -1;
//            int rank = 0;
//            for (CardTemplate c : Card.templates) {
//                if (c.templateID != -1 && c.templateID == this.templateId) {
//                    id = c.id;
//                    rank = c.rank;
//                    break;
//                }
//            }
//            if (id != -1) {
//                if (Utils.nextInt(1 + rank) == 0) {
//                    item = new Item(id);
//                    item.setDefaultOptions();
//                    item.quantity = 1;
//                }
//            }
//        }
        if (zone.map.isTreasure()) {
            int[] arr = {2, 3, 4, 5, 6, 7};
            ZTreasure z = (ZTreasure) zone;
            int level = z.getTreasure().getLevel();
            int gold = 300 * level;
            if (gold > 30000) {
                gold = 30000;
            }
            itemID = Utils.getItemGoldByQuantity(gold);
            int index = level / 20;
            int loop = arr[index];
            for (int i = 0; i < loop; i++) {
                Item itemGold = new Item(itemID);
                itemGold.setDefaultOptions();
                itemGold.quantity = gold;
                ItemMap itemMap = new ItemMap(zone.autoIncrease++);
                itemMap.item = itemGold;
                itemMap.playerID = Math.abs(_c.id);
                itemMap.x = (short) (getX() + Utils.nextInt(-loop * 10, loop * 10));
                itemMap.y = zone.map.collisionLand(getX(), getY());
                zone.addItemMap(itemMap);
                zone.service.addItemMap(itemMap);
            }
        }
//        if (zone.map.mapID >= 168 && zone.map.mapID <= 174) {
//            int[] itemevent = {ItemName.GAO_TAM_THOM, ItemName.LA_BANG_TUOI, ItemName.HAT_DUA_HONG, ItemName.HOA_MAI_VANG, ItemName.THIT_BA_CHI, ItemName.LUA_HONG_TET, ItemName.THUNG_NEP, ItemName.LA_DONG, ItemName.THUNG_DAU_XANH, ItemName.THIT_HEO, ItemName.LUA_NAU_BANH};
//            int rd = Utils.nextInt(1);
//            if (rd == 0) {
//                ItemMap itevent = new ItemMap(zone.autoIncrease++);
//                item = new Item(itemevent[Utils.nextInt(itemevent.length)]);
//                item.quantity = 1;
//                itevent.playerID = Math.abs(_c.id);
//                itevent.isPickedUp = false;
//                itevent.x = (short) (this.x + Utils.nextInt(-30, 30));
//                itevent.y = zone.map.collisionLand(this.x, this.y);
//                itevent.item = item;
//                this.items.add(itevent);
//                zone.addItemMap(itevent);
//                return;
//            }
//        }
        if (item != null) {
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.playerID = Math.abs(_c.id);
            itemMap.isPickedUp = false;
            itemMap.x = (short) (this.x + Utils.nextInt(-30, 30));
            itemMap.y = zone.map.collisionLand(this.x, this.y);
            itemMap.item = item;
            itemMap.isThrowFromMob = true;
            itemMap.killerIsHuman = _c.isHuman();
            itemMap.mobLevel = this.level;
            this.items.add(itemMap);
            zone.addItemMap(itemMap);
        }
    }

    public void dropManhSuuTam(Player player) {
        var _c = player;
        if (Utils.isTrue(5, 100)) {
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.playerID = Math.abs(_c.id);
            itemMap.isPickedUp = false;
            itemMap.x = (short) (this.x + Utils.nextInt(-30, 30));
            itemMap.y = zone.map.collisionLand(this.x, this.y);
            itemMap.item = new Item(Utils.nextInt(828, 841));
            itemMap.isThrowFromMob = true;
            itemMap.killerIsHuman = _c.isHuman();
            itemMap.mobLevel = this.level;
            this.items.add(itemMap);
            zone.addItemMap(itemMap);
        }
    }

    public void setDefault() {
        this.status = 4;
        this.hp = this.maxHp = hpDefault;
        this.levelBoss = 0;
        this.sys = 0;
        this.damage1 = this.hp / 20;
        this.damage2 = this.damage1 - this.damage1 / 10;

        if (this.templateId == 70) {
            this.damage1 = 5000;
            this.damage2 = 5000;
        }

    }

    public void setLevelBoss() {
        this.levelBoss = 0;
        if (!zone.isHadSuperMob && !zone.map.isBarrack()) {
            if (this.level >= 7) {
                if (Utils.nextInt(10) == 0) {
                    this.levelBoss = 1;
                    zone.isHadSuperMob = true;
                }
            }
        }
    }

    public void respawn() {
        this.status = 4;
        this.items.clear();
        setLevelBoss();
        this.hp = this.maxHp;
    }

    public void setBody(short body) {
        this.body = body;
        this.isChangeBody = true;
    }

    public void clearBody() {
        this.isChangeBody = false;
    }

    public void addChar(Player _player) {
        if (!listPlayer.contains(_player)) {
            this.listPlayer.add(_player);
        }
    }

    public List<Player> getChars() {
        ArrayList<Player> list = new ArrayList<>();
        for (Player _player : this.listPlayer) {
            if (!_player.isDead() && _player.zone == this.zone) {
                int distance = Utils.getDistance(this.x, this.y, _player.getX(), _player.getY());
                if (distance < 500) {
                    list.add(_player);
                }
            }
        }
        return list;
    }

    public void attack(Player _owner, Object obj) {
        int skill_id = _owner.select.template.id;
        if (skill_id != SkillName.CHIEU_DAM_DEMON && skill_id != SkillName.CHIEU_MASENKO && skill_id != SkillName.LIEN_HOAN && skill_id != SkillName.CHIEU_DAM_DRAGON && skill_id != SkillName.CHIEU_KAMEJOKO && skill_id != 9 && skill_id != 4 && skill_id != 5) {
            return;
        }
        long damageFull = _owner.info.damageFull;
        long dame = Utils.percentOf(damageFull, this.percentDamage);
        if (_owner.isSetPikkoroDaimao()) {
            dame += Utils.percentOf(dame, 100);
        }
        if (obj instanceof Player) {
            Player target = (Player) obj;
            if (target.isDead()) {
                return;
            }
            target.lock.lock();
            try {
                if (target.limitDame != -1) {
                    return;
                }
                if (target.isProtected() && dame >= target.info.hp) {
                    dame = target.info.hp - 1;
                }

                //_char.info.hp -= dame;
                dame = target.injure(null, this, dame);
                target.info.hp -= dame;
                if (target.info.hp <= 0) {
                    _owner.kill(target);
                    target.killed(_owner);
                    target.startDie();
                }

                _owner.zone.service.mobMeUpdate(_owner, target, dame, (byte) -1, (byte) 2);
            } finally {
                target.lock.unlock();
            }
        } else {
            Mob target = (Mob) obj;
            if (target == null || target.zone == null || target.zone.map == null || target.isDead()) {
                return;
            }
            if (target.templateId == MobName.HIRUDEGARN) {
                return;
            }
            if (target.templateId == MobName.MOC_NHAN) {
                dame = 10;
            }
//            if (target.zone.map.isNguHanhSon()) {
//                dame = 80000;
//            }
            target.lock.lock();
            try {
                target.hp -= dame;
                long hp = target.hp;
                if (target.hp <= 0) {
                    _owner.kill(obj);
                    target.startDie(dame, false, _owner);
                }
                _owner.zone.service.mobMeUpdate(_owner, target, dame, (byte) -1, (byte) 3);
            } finally {
                target.lock.unlock();
            }
        }
    }

    /**
     *
     */
    public void attack(Player target) {
        try {
            try {
                if (target == null) {
                    List<Player> list = getChars();
                    if (list.size() > 0) {
                        int index = Utils.nextInt(list.size());
                        target = list.get(index);
                    } else if (this.maxHp >= 6000) {
                        int distanceClosest = -1;
                        List<Player> list2 = zone.getListChar(Zone.TYPE_HUMAN, Zone.TYPE_PET, Zone.TYPE_ESCORT);
                        for (Player enemy : list2) {
                            try {
                                if (enemy.isDead() || enemy.isInvisible() || enemy.isVoHinh() || enemy.checkEffectOfSatellite(344) > 0) {
                                    continue;
                                }
                                int distance = Utils.getDistance(this.x, this.y, enemy.getX(), enemy.getY());
                                if (distance > 60) {
                                    continue;
                                }
                                if (distanceClosest == -1 || distance < distanceClosest) {
                                    distanceClosest = distance;
                                    target = enemy;
                                }
                            } catch (Exception e) {
                                logger.error(String.format("attack err - enemy is null: %b", enemy == null), e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("attack err - block 1", e);
                logger.error(String.format("attack err - zone is null: %b", zone == null), e);
            }
            if (target != null && target.zone == this.zone) {
                boolean isMiss = Utils.nextInt(100) < (target.info.percentMiss + 5);
                long dameHp = Utils.nextLong(this.damage2, this.damage1);
                if (isChangeBody) {
                    dameHp -= Utils.percentOf(dameHp, this.dameDown);
                }
                dameHp -= target.info.defenseFull;
                if (dameHp <= 0) {
                    dameHp = 1;
                }
                if (target.checkEffectOfSatellite(344) > 0) {
                    dameHp -= dameHp / 5;
                }
                if (target.isBuaDaTrau()) {
                    dameHp /= 2;
                }
                if (target.isDisciple()) {
                    Disciple disciple = (Disciple) target;
                    if (disciple.master.isBuaDeTu()) {
                        dameHp /= 2;
                    }
                }
                if (target.isGiapXen()) {
                    dameHp /= 2;
                }
                dameHp -= Utils.percentOf(dameHp, target.info.options[94]);
                if (target.info.options[157] > 0) {
                    long pM = target.info.mp * 100 / target.info.mpFull;
                    if (pM < 20) {
                        dameHp -= Utils.percentOf(dameHp, target.info.options[157]);
                    }
                }
                if (target.isDisciple()) {
                    Disciple disciple = (Disciple) target;
                    if (disciple.master.isBuaManhMe()) {
                        dameHp /= 2;
                    }
                }

                if (this.levelBoss != 0) {
                    dameHp = target.info.hpFull / 10;
                }
                if (zone.map.isNguHanhSon()) {
                    dameHp = target.info.hp / 20;
                }
                if (target.isBuaBatTu()) {
                    if (target.info.hp == 1) {
                        isMiss = true;
                    }
                    if (dameHp >= target.info.hp) {
                        dameHp = target.info.hp - 1;
                    }
                }

                dameHp = target.injure(null, this, dameHp);
                if (target.limitDame != -1) {
                    isMiss = true;
                }
                if (target instanceof Disciple) {
                    if (zone.map.isMapPet()) {
                        isMiss = true;
                    }
                }
                if (dameHp > 0) {
                    dameHp = target.injure(null, this, dameHp);
                    long reactDame = Utils.percentOf(dameHp, target.info.options[97]);
                    if (reactDame >= this.hp) {
                        reactDame = this.hp - 1;
                    }
                    if (reactDame > 0) {
                        if (this.hp > 1) {
                            this.hp -= reactDame;
                            zone.service.attackNpc(reactDame, false, this, (byte) 36);
                        } else if (this.hp == 1) {
                            zone.service.attackNpc(-1, false, this, (byte) 36);
                        }
                    }
                }
                if (!isMiss && target.isProtected()) {
                    dameHp = 1;
                    if (dameHp >= target.info.hp) {
                        isMiss = true;
                    }
                }
                int dameMp = 0;
                if (!isMiss) {
                    target.lock.lock();
                    try {
                        if (isDead() || target.isDead()) {
                            return;
                        }
                        target.info.hp -= dameHp;
                        target.info.mp -= dameMp;
                        attack(target, dameHp, dameMp);
                        if (target.info.hp <= 0) {
                            kill(target);
                            target.killed(this);
                            target.startDie();
                        }
                    } finally {
                        target.lock.unlock();
                    }
                } else {
                    zone.service.attackNpc(-1, false, this, (byte) -1);
                }
            }
        } catch (Exception e) {
            logger.error("attack err", e.getCause());
        }
    }

    public void attack(Player _player, long dameHp, int dameMp) {
        try {
            Message ms = new Message(Cmd.NPC_ATTACK_ME);
            FastDataOutputStream ds = ms.writer();
            ds.writeInt(this.mobId);
            ds.writeLong(dameHp);
            if (dameMp > 0) {
                ds.writeInt(dameMp);
            }
            ds.flush();
            _player.service.sendMessage(ms);
            ms.cleanup();

            ms = new Message(Cmd.NPC_ATTACK_PLAYER);
            ds = ms.writer();
            ds.writeInt(this.mobId);
            ds.writeInt(_player.id);
            ds.writeLong(_player.info.hp);
            if (dameMp > 0) {
                ds.writeLong(_player.info.mp);
            }
            ds.flush();
            zone.service.sendMessage(ms, _player);
            ms.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public boolean isDead() {
        return this.status == 0;
    }

    public void kill(Player victim) {

    }

    public void startDie(long dame, boolean isCrit, Player killer) {
        try {
            try {
                if (this.templateId != MobName.MOC_NHAN) {
                    throwItem(killer);
                }
                this.listPlayer.clear();
                this.status = 0;
                this.hp = 0;
                if (levelBoss == 1) {
                    zone.isHadSuperMob = false;
                }
                clearBody();
                clearEffect();
                this.deadTime = System.currentTimeMillis();
                Message ms = new Message(Cmd.NPC_DIE);
                FastDataOutputStream ds = ms.writer();
                ds.writeInt(this.mobId);
                ds.writeLong(dame);
                ds.writeBoolean(isCrit);
                ds.writeByte(this.items.size());
                for (ItemMap item : this.items) {
                    ds.writeShort(item.id);
                    ds.writeShort(item.item.id);
                    ds.writeShort(item.x);
                    ds.writeShort(item.y);
                    ds.writeInt(item.playerID);
                }
                ds.flush();
                zone.service.sendMessage(ms, null);
                ms.cleanup();
                if (killer.isDisciple()) {
                    Disciple disciple = (Disciple) killer;
                    killer = disciple.master;
                }
                if (killer.isBuaThuHut() || killer.isAutoPlay() || killer instanceof BotCold) {
                    for (ItemMap itemMap : items) {
                        killer.pickItem(itemMap, 10);
                    }
                }
                // Log để debug kill boss
                if (this.isBoss) {
                    logger.info(String.format("[BoMong] Boss %s killed by player %s", this.getClass().getSimpleName(), killer != null ? String.valueOf(killer.id) : "NULL"));
                }
                
                if (killer != null && killer.currentNhiemVuBoMong != null) {
                    BoMongService nv = killer.currentNhiemVuBoMong;
                    logger.info(String.format("[BoMong] Player %d has nhiem vu: loaiNv=%d, isBoss=%s", killer.id, nv.loaiNv, this.isBoss));
                    
                    if (nv.loaiNv == BoMongService.LOAI_KILL_MOB && !this.isBoss) {
                        nv.tienDo++;
                        killer.checkBoMongProgress(nv);
                        killer.saveBoMongNhiemVu();
                        if (nv.tienDo >= nv.yeuCau) {
                            killer.completeNhiemVuBoMong();
                        }
                    } else if (nv.loaiNv == BoMongService.LOAI_KILL_BOSS && this.isBoss) {
                        String bossClassName = this.getClass().getSimpleName();
                        logger.info(String.format("[BoMong] Player %d kill boss %s: Starting check. Current progress: %d/%d", killer.id, bossClassName, nv.tienDo, nv.yeuCau));
                        
                        // Đảm bảo bossIds được load nếu null hoặc rỗng
                        if (nv.bossIds == null || nv.bossIds.length == 0) {
                            logger.warn(String.format("[BoMong] Player %d kill boss %s: bossIds is null or empty, reloading...", killer.id, bossClassName));
                            nv.bossIds = BoMongService.getBossNamesByDifficulty(nv.doKho);
                            if (nv.bossIds == null || nv.bossIds.length == 0) {
                                logger.error(String.format("[BoMong] Player %d kill boss %s: Cannot load bossIds for difficulty %d", killer.id, bossClassName, nv.doKho));
                            } else {
                                logger.info(String.format("[BoMong] Player %d kill boss %s: Reloaded %d boss names", killer.id, bossClassName, nv.bossIds.length));
                            }
                        }
                        
                        boolean isValidBoss = false;
                        if (nv.bossIds != null && nv.bossIds.length > 0) {
                            logger.info(String.format("[BoMong] Player %d kill boss %s: Checking against %d boss names: %s", 
                                killer.id, bossClassName, nv.bossIds.length, String.join(", ", nv.bossIds)));
                            for (String bossName : nv.bossIds) {
                                logger.info(String.format("[BoMong] Player %d kill boss: Comparing '%s' with '%s'", killer.id, bossClassName, bossName));
                                if (bossClassName.equals(bossName)) {
                                    isValidBoss = true;
                                    logger.info(String.format("[BoMong] Player %d kill boss %s: MATCH! Updating progress %d/%d", killer.id, bossClassName, nv.tienDo + 1, nv.yeuCau));
                                    break;
                                }
                            }
                        } else {
                            logger.warn(String.format("[BoMong] Player %d kill boss %s: bossIds is still null or empty after reload", killer.id, bossClassName));
                        }
                        
                        if (isValidBoss) {
                            nv.tienDo++;
                            killer.checkBoMongProgress(nv);
                            killer.saveBoMongNhiemVu();
                            logger.info(String.format("[BoMong] Player %d kill boss %s: Progress updated to %d/%d", killer.id, bossClassName, nv.tienDo, nv.yeuCau));
                            if (nv.tienDo >= nv.yeuCau) {
                                logger.info(String.format("[BoMong] Player %d kill boss %s: Task completed!", killer.id, bossClassName));
                                killer.completeNhiemVuBoMong();
                            }
                        } else {
                            logger.warn(String.format("[BoMong] Player %d kill boss %s: Boss name '%s' not in required list. Required bosses: %s", 
                                killer.id, bossClassName, bossClassName, 
                                (nv.bossIds != null && nv.bossIds.length > 0) ? String.join(", ", nv.bossIds) : "NONE"));
                        }
                    } else {
                        logger.info(String.format("[BoMong] Player %d kill: Condition not met. loaiNv=%d (need %d), isBoss=%s", 
                            killer.id, nv.loaiNv, BoMongService.LOAI_KILL_BOSS, this.isBoss));
                    }
                } else {
                    // Không log để tránh spam - chỉ log khi có nhiệm vụ
                }
            } finally {
                zone.addWaitForRespawn(this);
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public boolean meCantAttack() {
        return (!this.isFreeze && !this.isSleep && !isHeld && !this.isBlind);
    }

    public void setTimeForItemtime(int id, int seconds) {
        synchronized (this.vItemTime) {
            for (ItemTime item : this.vItemTime) {
                if (item.id == id) {
                    item.seconds = seconds;
                    break;
                }
            }
        }
    }

    public void clearEffect() {
        setTimeForItemtime(3, 0);
        setTimeForItemtime(4, 0);
        setTimeForItemtime(5, 0);
        if (hold != null) {
            hold.close();
        }
    }

    public void update() {
        if (this.seconds > 0) {
            this.seconds--;
            if (this.seconds == 0) {
                this.isFreeze = false;
            }
        }
        synchronized (this.vItemTime) {
            ArrayList<ItemTime> listRemove = new ArrayList<>();
            for (ItemTime item : this.vItemTime) {
                item.update();
                if (item.seconds <= 0) {
                    switch (item.id) {
                        case 3:
                            this.isSleep = false;
                            zone.service.setEffect(null, this.mobId, Skill.REMOVE_EFFECT, Skill.MONSTER, (byte) 41);
                            break;

                        case 4:
                            this.isBlind = false;
                            zone.service.setEffect(null, this.mobId, Skill.REMOVE_EFFECT, Skill.MONSTER, (byte) 40);
                            break;

                        case 5:
                            this.clearBody();
                            zone.service.changeBodyMob(this, (byte) 0);
                            break;
                    }
                    listRemove.add(item);
                }
            }
            this.vItemTime.removeAll(listRemove);
        }
        if (this.status == 4 && this.levelBoss == 0) {
            long now = System.currentTimeMillis();
            if (now - this.lastTimeAttack > this.attackDelay) {
                this.lastTimeAttack = now;
                if (zone.getNumPlayer() > 0) {
                    if (this.templateId != MobName.MOC_NHAN) {
                        if (meCantAttack()) {
                            attack(null);
                        }
                    }
                }
            }
        }
    }
}
