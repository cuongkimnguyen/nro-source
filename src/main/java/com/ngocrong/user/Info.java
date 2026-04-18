package com.ngocrong.user;

import com.ngocrong.bot.MiniDisciple;
import com.ngocrong.bot.Disciple;
import com.ngocrong.clan.ClanReward;
import com.ngocrong.clan.ClanMember;
import com.ngocrong.collection.Card;
import com.ngocrong.consts.ItemName;
import com.ngocrong.effect.AmbientEffect;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
//import com.ngocrong.map.tzone.GravityRoom;
import com.ngocrong.model.Caption;
import com.ngocrong.model.PowerLimitMark;
import com.ngocrong.network.Message;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Server;
import com.ngocrong.util.Utils;
import com.google.gson.annotations.SerializedName;
import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemTimeName;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Info {

    public static final byte ALL = 0;
    public static final byte HP = 1;
    public static final byte MP = 2;

    public static final byte POWER = 0;
    public static final byte POTENTIAL = 1;
    public static final byte POWER_AND_POTENTIAL = 2;

    public static final int HP_FROM_1000_TIEM_NANG = 20;
    public static final int MP_FROM_1000_TIEM_NANG = 20;
    public static final int DAMAGE_FROM_1000_TIEM_NANG = 1;
    public static final int EXP_FOR_ONE_ADD = 100;
    public static final byte DEFENSE_FROM_1000_TIEM_NANG = 1;
    public static final byte CRITICIAL_FROM_1000_TIEM_NANG = 1;

    private static final Logger logger = Logger.getLogger(Info.class);

    @SerializedName("hp_goc")
    public long originalHP;// tiem nang
    @SerializedName("mp_goc")
    public long originalMP;// tiem nang
    @SerializedName("damage")
    public long originalDamage;// tiem nang
    @SerializedName("defense")
    public int originalDefense;// tiem nang
    @SerializedName("critical")
    public int originalCritical;// tiem nang
    @SerializedName("power")
    public long power;
    @SerializedName("potential")
    public long potential;
    @SerializedName("stamina")
    public short stamina;
    @SerializedName("max_stamina")
    public short maxStamina;
    @SerializedName("hp")
    public long hp;
    @SerializedName("mp")
    public long mp;
    @SerializedName("open_power")
    public byte numberOpenLimitedPower;
    @SerializedName("active_point")
    public int activePoint;

    public transient PowerLimitMark powerLimitMark;
    public transient Player _player;
    public transient int defenseFull;// hanh trang
    public transient int criticalFull;// hanh trang
    public transient long damageFull;// tan cong
    public transient long hpFull;// hanh trang
    public transient long mpFull;// hanh trang
    public transient byte speed;
    public transient long hpFullTemp;// hanh trang
    public transient long mpFullTemp;// hanh trang

    public transient int exactly, percentExactly;// hanh trang
    public transient int miss, percentMiss;

    public transient int level;
    public transient long levelPercent;
    public transient int[] options;
    public transient long optionLaze;
    public transient long optionTuSat;
    public transient long optionKame;
    public ArrayList<Integer> optionDamage;
    public ArrayList<Integer> optionHp;
    public ArrayList<Integer> optionMp;

    public Info(byte planet) {
        switch (planet) {
            case 0:// trai dat
                this.originalHP = 200;
                this.originalMP = 100;
                this.originalDamage = 12;
                break;

            case 1:// namec
                this.originalHP = 100;
                this.originalMP = 200;
                this.originalDamage = 12;
                break;

            case 2:// xayda
                this.originalHP = 100;
                this.originalMP = 100;
                this.originalDamage = 15;
                break;
        }
        this.originalCritical = 0;
        this.originalDefense = 0;
        this.power = 1200;
        this.potential = 1200;
        this.hpFull = this.originalHP;
        this.mpFull = this.originalMP;
    }

    public Info(Player _player) {
        this._player = _player;
        if (_player.isDisciple()) {
            this.originalHP = Utils.nextInt(9, 30) * 100;
            this.originalMP = Utils.nextInt(9, 30) * 100;
            this.originalDamage = Utils.nextInt(50, 100);
            this.originalCritical = Utils.nextInt(1, 4);
            this.originalDefense = Utils.nextInt(15, 30);
            this.power = 2000;
            this.potential = 2000;

        } else if (_player.isMiniDisciple()) {
            this.originalHP = 5000000;
            this.originalMP = 5000000;
            this.originalDamage = 0;
            this.originalCritical = 0;
            this.originalDefense = 0;
            this.power = 0;
            this.potential = 0;
        }
//        else if (_player instanceof TrongTai) {
//            this.originalHP = 500;
//            this.originalMP = 500;
//            this.originalDamage = 0;
//            this.originalCritical = 0;
//            this.originalDefense = 0;
//            this.power = 0;
//            this.potential = 0;
//        }
        this.hpFull = this.originalHP;
        this.mpFull = this.originalMP;
    }

    public void setChar(Player _player) {
        this._player = _player;
    }

    public void setPowerLimited() {
        this.powerLimitMark = PowerLimitMark.limitMark.get(this.numberOpenLimitedPower);
    }

    public void setStamina() {
        if (_player != null && _player.isDisciple()) {
            setMaxStamina();
            this.stamina = this.maxStamina;
        } else {
            this.stamina = 10000;
            this.maxStamina = 10000;
        }
    }

    public void updateStamina(int add) {
        this.stamina += add;
        if (this.stamina > this.maxStamina) {
            this.stamina = this.maxStamina;
        }
        if (this.stamina < 0) {
            this.stamina = 0;
        }
    }

    public void setMaxStamina() {
        this.maxStamina = (short) ((50 * this.level) + 450);
    }

    public void addOption(int id, int param) {
        switch (id) {
            case 77:
                optionHp.add(param);
                break;

            case 103:
                optionMp.add(param);
                break;

            case 49:
            case 50:
            case 147:
            case 195:
                optionDamage.add(param);
                break;

            case 16:
            case 148:
            case 114:
                this.speed += (byte) Utils.percentOf(this.speed, param);
                break;

            default:
                this.options[id] += param;
                break;
        }

    }

    public void setInfo() {
        Server server = DragonBall.getInstance().getServer();
        this.options = new int[server.iOptionTemplates.size()];
        if (optionDamage == null) {
            optionDamage = new ArrayList<>();
        } else {
            optionDamage.clear();
        }
        if (optionHp == null) {
            optionHp = new ArrayList<>();
        } else {
            optionHp.clear();
        }
        if (optionMp == null) {
            optionMp = new ArrayList<>();
        } else {
            optionMp.clear();
        }
        this.speed = 6;
        this.optionLaze = 0;
        this.optionTuSat = 0;
        this.optionKame = 0;
        this.hpFullTemp = this.originalHP;
        this.mpFullTemp = this.originalMP;
        this.damageFull = this.originalDamage;
        this.criticalFull = this.originalCritical;
        this.defenseFull = this.originalDefense * 4;
        int giapLuyenTap = -1;
        boolean isMacGiapLuyenTap = false;
        boolean isVoHinh = false;
        boolean isUnaffectedCold = false;
        boolean isHaveEquipTeleport = false;
        boolean isHaveEquipSelfExplosion = false;
        boolean isHaveEquipInvisible = false;
        boolean isHaveEquipTransformIntoChocolate = false;
        boolean isHaveEquipTransformIntoStone = false;
        boolean isHaveEquipMiNuong = false;
        boolean isHaveEquipBulma = false;
        boolean isHaveEquipXinbato = false;
        boolean isHaveEquipBuiBui = false;
        boolean isKhangTDHS = false;
        boolean isDoSaoPhaLe = false;
        boolean isOptLaze = false;
        boolean isOptTuSat = false;
        int setThienXinHang = 0, setKirin = 0, setSongoku = 0, setPicolo = 0, setOcTieu = 0, setPikkoroDaimao = 0, setKakarot = 0, setCaDic = 0, setNappa = 0;
        int setThanLinh = 0;
        int setHuyDiet = 0;
        int upgradeMin = -1;
        int n = 0;
        if (_player.itemBody != null) {
            for (Item item : _player.itemBody) {
                if (item != null) {
                    if (item.template.type < 5) {
                        n++;
                    }
                    if (item.isNhapThe && !_player.isNhapThe()) {
                        continue;
                    }
                    if (item.template.type == 32) {
                        if (giapLuyenTap == -1) {
                            giapLuyenTap = item.id;
                            isMacGiapLuyenTap = true;
                        }
                    }
                    if (item.id == 464 || item.id == 584) {
                        isHaveEquipBulma = true;
                    }
                    if (item.id == 860) {
                        isHaveEquipMiNuong = true;
                    }
                    if (item.id == 458) {
                        isHaveEquipXinbato = true;
                    }
                    if (item.id == 575) {
                        isHaveEquipBuiBui = true;
                    }
                    if (item.template.isThanLinh()) {
                        setThanLinh++;
                    }
                    if (item.template.isHuyDiet()) {
                        setHuyDiet++;
                    }
                    ArrayList<ItemOption> options = item.getOptions();
                    for (ItemOption o : options) {
                        int id = o.optionTemplate.id;
                        int param = o.param;
                        addOption(id, param);
                        if (id == 72) {
                            if (upgradeMin == -1 || upgradeMin > param) {
                                upgradeMin = param;
                            }
                        }
                        if (id == 110) {
                            isDoSaoPhaLe = true;
                        }
                        if (id == 116) {
                            isKhangTDHS = true;
                        }
                        if (id == 127) {
                            setThienXinHang++;
                        }
                        if (id == 128) {
                            setKirin++;
                        }
                        if (id == 129) {
                            setSongoku++;
                        }
                        if (id == 130) {
                            setPicolo++;
                        }
                        if (id == 131) {
                            setOcTieu++;
                        }
                        if (id == 132) {
                            setPikkoroDaimao++;
                        }
                        if (id == 133) {
                            setKakarot++;
                        }
                        if (id == 134) {
                            setCaDic++;
                        }
                        if (id == 135) {
                            setNappa++;
                        }
                        if (id == 25) {
                            isHaveEquipInvisible = true;
                        }
                        if (id == 26) {
                            isHaveEquipTransformIntoStone = true;
                        }
                        if (id == 105) {
                            isVoHinh = true;
                        }
                        if (id == 106) {
                            isUnaffectedCold = true;
                        }
                        if (id == 29) {
                            isHaveEquipTransformIntoChocolate = true;
                        }
                        if (id == 33) {
                            isHaveEquipTeleport = true;
                        }
                        if (id == 153) {
                            isHaveEquipSelfExplosion = true;
                        }
                        if (id == 197) {
//                            isOptLaze = true;
//                            _player.setParamOptLaze(param);
                            optionLaze += param;
                        }
                        if (id == 196) {
//                            isOptTuSat = true;
//                            _player.setParamOptTuSat(param);
                            optionTuSat += param;
                        }
                        if (id == 221) {
//                            isOptTuSat = true;
//                            _player.setParamOptTuSat(param);
                            optionKame += param;
                        }

                        if (id == 234) {
                            optionHp.add(param);
                        }
                        if (id == 235) {
                            optionMp.add(param);
                        }
                        if (id == 236) {
                            optionDamage.add(param);
                        }
                    }
                }
            }
        }
        if (n == 5) {
            _player.setIdEffSetItem((short) upgradeMin);
        }
        List<AmbientEffect> ambientEffects = _player.getAmbientEffects();
        if (ambientEffects != null) {
            for (AmbientEffect am : ambientEffects) {
                int[] o = am.getItemOption();
                addOption(o[0], o[1]);
            }
        }
//        for (Item danhhieu : _player.danhHieu) {
//            if (danhhieu != null) {
//                for (ItemOption o : danhhieu.options) {
//                    if (o != null) {
//                        int id = o.optionTemplate.id;
//                        int param = o.param;
//                        addOption(id, param);
//                    }
//                }
//            }
//        }

        if (_player instanceof Disciple) {
            Disciple disciple = (Disciple) _player;
            if (disciple.master != null && disciple.master.isUocLocPhat7()) {
                optionHp.add(5);
                optionMp.add(5);
                optionDamage.add(5);
            }
            if (disciple.master != null && disciple.master.isUocThienMenh5()) {
                optionHp.add(10);
                optionMp.add(10);
                optionDamage.add(10);
            }
        }
        _player.setVoHinh(isVoHinh);
        _player.setDoSaoPhaLe(isDoSaoPhaLe);
        _player.setUnaffectedCold(isUnaffectedCold);
        _player.setHaveEquipTeleport(isHaveEquipTeleport);
        _player.setHaveEquipSelfExplosion(isHaveEquipSelfExplosion);
        _player.setHaveEquipInvisible(isHaveEquipInvisible);
        _player.setHaveEquipTransformIntoChocolate(isHaveEquipTransformIntoChocolate);
        _player.setHaveEquipTransformIntoStone(isHaveEquipTransformIntoStone);
        _player.setHaveEquipBulma(isHaveEquipBulma);
        _player.setHaveEquipMiNuong(isHaveEquipMiNuong);
        _player.setHaveEquipBuiBui(isHaveEquipBuiBui);
        _player.setHaveEquipXinbato(isHaveEquipXinbato);
        _player.setKhangTDHS(isKhangTDHS);
        _player.setOptionLaze(isOptLaze);
        _player.setOptionTuSat(isOptTuSat);
        // set kich hoat
        _player.setSetCaDic(setCaDic == 5);
        _player.setSetKakarot(setKakarot == 5);
        _player.setSetKirin(setKirin == 5);
        _player.setSetNappa(setNappa == 5);
        _player.setSetOcTieu(setOcTieu == 5);
        _player.setSetPicolo(setPicolo == 5);
        _player.setSetPikkoroDaimao(setPikkoroDaimao == 5);
        _player.setSetSongoku(setSongoku == 5);
        _player.setSetThienXinHang(setThienXinHang == 5);
        _player.setSetThanLinh(setThanLinh >= 1);
        _player.setSetHuyDiet(setHuyDiet == 5);
        ArrayList<Card> cards = _player.getCards();
        if (cards != null) {
            for (Card c : cards) {
                if (c.isUse) {
                    for (ItemOption o : c.template.options) {
                        if (c.level >= o.activeCard) {
                            addOption(o.optionTemplate.id, o.param);
                        }
                    }
                }
            }
        }
        MiniDisciple mini = _player.getMiniDisciple();
        if (mini != null) {
//            if (mini.item != null) {
//                ArrayList<ItemOption> options = mini.item.getOptions();
//                for (ItemOption o : options) {
//                    addOption(o.optionTemplate.id, o.param);
//                }
//            }
        }
        if (_player.isNhapThe() && _player.myDisciple != null) {
            if (_player.typePorata > 0) {
                Item item = _player.getItemInBag(ItemName.BONG_TAI_PORATA_CAP_3);
                if (item == null) {
                    item = _player.getItemInBag(ItemName.BONG_TAI_PORATA_CAP_2);
                }
                if (item != null) {
                    ArrayList<ItemOption> options = item.getOptions();
                    for (ItemOption itemOption : options) {
                        addOption(itemOption.optionTemplate.id, itemOption.param);
                    }
                }
            }
            if (_player.myDisciple.petBonus > 0) {
                this.damageFull += Utils.percentOf(this.damageFull, _player.myDisciple.petBonus);
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, _player.myDisciple.petBonus);
                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, _player.myDisciple.petBonus);
            }
        }
        if (_player.isHuman()) {
            var itemCapsuleVIP = _player.getItemInBag(ItemName.CAPSULE_VIPPRO);
            if (itemCapsuleVIP != null && !itemCapsuleVIP.options.isEmpty()) {
                var itemOption = itemCapsuleVIP.options.get(0);
                addOption(itemOption.optionTemplate.id, itemOption.param);
            }
        }
        this.damageFull += this.options[0];
        this.hpFullTemp += ((this.options[2] + this.options[22]) * 1000L) + this.options[6] + this.options[48];
        this.mpFullTemp += ((this.options[2] + this.options[23]) * 1000L) + this.options[7] + this.options[48];
        this.criticalFull += this.options[14] + this.options[192];

        for (int param : optionDamage) {
            this.damageFull += Utils.percentOf(this.damageFull, param);
        }
        for (int param : optionHp) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, param);
        }
        for (int param : optionMp) {
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, param);
        }
        if (_player.isMonkey()) {
            this.criticalFull += 100;
        }
        this.defenseFull += this.options[47];
        if (_player.itemBag != null) {
            for (Item item : _player.itemBag) {
                if (item != null) {
                    if (item.template.type == 32) {
                        if (giapLuyenTap == -1) {
                            ArrayList<ItemOption> options = item.getOptions();
                            for (ItemOption option : options) {
                                if (option.optionTemplate.id == 9) {
                                    if (option.param > 0) {
                                        giapLuyenTap = item.id;
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        int dameAdd = 0;
        switch (giapLuyenTap) {
            case 529:
            case 534:
                dameAdd = 10;
                break;

            case 530:
            case 535:
                dameAdd = 20;
                break;

            case 531:
            case 536:
                dameAdd = 30;
                break;
            case 2268:
                dameAdd = 40;
                break;
        }
        if (isMacGiapLuyenTap) {
            dameAdd *= -1;
        }
        this.exactly = this.options[10];
        this.percentExactly = this.options[18];
        this.miss = this.options[17];
        this.percentMiss = this.options[108];
        this.damageFull += Utils.percentOf(this.damageFull, (dameAdd - _player.dameDown));
        if (this.options[155] > 0) {
            this.hpFullTemp /= 2;
            this.mpFullTemp /= 2;
            this.damageFull /= 2;
        }

        int phuX = _player.getPhuX();
        if (phuX > 0 && _player.zone.map.isBlackDragonBall()) {
            this.hpFullTemp *= phuX;
            this.mpFullTemp *= phuX;
        }

        if (_player.isSetNappa()) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 80);
        }
        if (_player.isSetPicolo()) {
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 80);
        }
        if (_player.isSetHuyDiet()) {
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
            this.damageFull += Utils.percentOf(this.damageFull, 10);
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
        }
        if (_player.isMonkey()) {
            this.hpFullTemp *= 2;
            this.mpFullTemp *= 2;
            int skillLevel = _player.getSkill(13).point;
            this.damageFull += Utils.percentOf(this.damageFull, skillLevel * 5L);
            this.speed += 2;
//            if (_player.isSetCaDic()) {
//                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 20);
//            }
        }

        if (_player.isBoHuyet()) {
            this.hpFullTemp *= 2;
        }
        if (_player.isBoKhi()) {
            this.mpFullTemp *= 2;
        }
        if (_player.isCuongNo()) {
            this.damageFull *= 2;
        }
        if (_player.isBoHuyet2()) {
            this.hpFullTemp += (this.hpFullTemp * 120 / 100);
        }
        if (_player.isBoKhi2()) {
            this.mpFullTemp += (this.mpFullTemp * 120 / 100);
        }
        if (_player.isCuongNo2()) {
            this.damageFull += (this.damageFull * 120 / 100);
        }
        if (_player.isDisciple()) {
            Disciple p = (Disciple) _player;
            if (p.master != null && p.master.exitsItemTime(ItemTimeName.DA_MA_THUAT_SELECT3)) {
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
                this.damageFull += Utils.percentOf(this.damageFull, 10);
            }

        }
        if (this._player.itemTimes != null && !this._player.itemTimes.isEmpty()) {
            if (this._player.exitsItemTime(ItemTimeName.DA_MA_THUAT_SELECT1)) {
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 15);
                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 15);
            }
            if (this._player.exitsItemTime(ItemTimeName.DA_MA_THUAT_SELECT2)) {
                this.damageFull += Utils.percentOf(this.damageFull, 15);
            }

            if (this._player.exitsItemTime(ItemTimeName.BANH_CUPCAKE)) {
                this.damageFull += Utils.percentOf(this.damageFull, 5);
            }
            if (this._player.exitsItemTime(ItemTimeName.BANH_DONUT)) {
                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 5);
            }
            if (this._player.exitsItemTime(ItemTimeName.BANH_KEM_NHO)) {
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 5);
            }
            if (this._player.exitsItemTime(ItemTimeName.BANH_TRAI_CAY)) {
                this.options[94] += 5;
            }
            if (this._player.exitsItemTime(ItemTimeName.BUA_TRANG_RAM)) {
                this.options[94] += 10;
            }
            if (this._player.exitsItemTime(ItemTimeName.BANH_SEN_HONG)) {
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
            }
            if (this._player.exitsItemTime(ItemTimeName.KEO_HUONG_NHAI)) {
                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
            }
            if (this._player.exitsItemTime(ItemTimeName.TRA_SEN)) {
                this.damageFull += Utils.percentOf(this.damageFull, 10);
            }
            if (this._player.exitsItemTime(ItemTimeName.BANH_PIA)) {
                this.options[94] += 10;
            }
            if (this._player.exitsItemTime(ItemTimeName.KEO_GUNG)) {
                this.options[5] += 5;
            }
            if (this._player.exitsItemTime(ItemTimeName.KEO_TONG_HOP)) {
                this.options[223] += 3;
                this.options[224] += 3;
                this.options[225] += 3;
            }
            if (this._player.exitsItemTime(ItemTimeName.CUA_RANG_ME)) {
                this.damageFull += Utils.percentOf(this.damageFull, 10);
            }
            if (this._player.exitsItemTime(ItemTimeName.BACH_TUOC_NUONG)) {
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
            }
            if (this._player.exitsItemTime(ItemTimeName.TOM_TAM_BOT_CHIEN_XU)) {
                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
            }
            if (this._player.exitsItemTime(ItemTimeName.MU_COI)) {
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
            }
            if (this._player.exitsItemTime(ItemTimeName.SEN_HONG)) {
                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
            }
            if (this._player.exitsItemTime(ItemTimeName.NGOI_SAO_HI_VONG)) {
                this.damageFull += Utils.percentOf(this.damageFull, 10);
            }
            if (this._player.exitsItemTime(ItemTimeName.SAO_VANG)) {
                this.options[101] += 30;
            }
            if (this._player.exitsItemTime(ItemTimeName.BANH_DEO_THO_NGOC)) {
                this.options[101] += 30;
            }
            if (this._player.exitsItemTime(ItemTimeName.NU_HOA_SEN)) {
                this.options[101] += 100;
            }
            if (this._player.exitsItemTime(ItemTimeName.BANH_DEO_HANG_NGA)) {
                this.options[101] += 100;
            }
            if (this._player.exitsItemTime(ItemTimeName.KEO_DEM_RAM)) {
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
            }
            if (this._player.exitsItemTime(ItemTimeName.BANH_NUONG_RONG_TRANG)) {
                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
            }

        }
        if (_player.clan != null) {
            ClanMember mem = _player.clan.getMember(_player.id);
            if (mem != null) {
                for (ClanReward clanReward : mem.rewards) {
                    if (clanReward.isExpired()) {
                        continue;
                    }
                    switch (clanReward.getStar()) {
                        case 1:
                            // +20% HP, KI và Sức đánh cho cả bang
                            this.damageFull += damageFull * 20 / 100;
                            this.hpFullTemp += hpFullTemp * 20 / 100;
                            this.mpFullTemp += mpFullTemp * 20 / 100;
                            break;
                        case 4:
                            // +10% TNSM sư phụ và đệ tử
                            options[101] += 10;
                            break;
//                        case 5:
//                            options[108] += 10;
//                            break;
//                        case 6:
//                            options[94] += 10;
//                            break;
//                        case 7:
//                            options[95] += 10;
//                            options[96] += 10;
//                            break;
                        case 8:
                            this.hpFullTemp += this.hpFullTemp * 5 / 100;
                            this.mpFullTemp += this.mpFullTemp * 5 / 100;
                            break;
                        case 9:
                            this.damageFull += this.damageFull * 5 / 100;
                            break;
                        case 10:
                            _player.setRewardTNSMDragonNamek(true);
                            break;
                    }
                }
            }
        }
        if (_player.isHaveFood()) {
            this.damageFull += this.damageFull / 10;
        }
        if (_player.isDuaXanh()) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
        }
        if (_player.isDauVang()) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 5);
        }
        if (_player.isDudu()) {
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
        }
        if (_player.isNuocTangCuong()) {
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 5);
        }
        if (_player.isNuocMiaSauRieng()) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
        }
        if (_player.isNuocMiaThom()) {
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
        }
        if (_player.isNuocMiaKhongLo()) {
            this.damageFull += Utils.percentOf(this.damageFull, 10);
        }
        if (_player.isMangCau()) {
            this.damageFull += Utils.percentOf(this.damageFull, 10);
        }
        if (_player.isTinhThach()) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
        }
        if (_player.isHoaThach()) {
            this.damageFull += Utils.percentOf(this.damageFull, 10);
        }

        if (_player.isUocLocPhat2()) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
        }
        if (_player.isUocLocPhat3()) {
            this.damageFull += Utils.percentOf(this.damageFull, 15);
        }
        if (_player.isCaNoiGian()) {
            this.damageFull += Utils.percentOf(this.damageFull, 5);
        }
        if (_player.isUocThienMenhKi()) {
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 15);
        }
        if (_player.isUocThienMenhHp()) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 15);
        }
        if (_player.isUocThienMenhDame()) {
            this.damageFull += Utils.percentOf(this.damageFull, 15);
        }
        if (_player.sachdacbiet[2]) {
            this.damageFull += Utils.percentOf(this.damageFull, 5);
        }
        if (_player.sachdacbiet[3]) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 5);
        }
        if (_player.sachdacbiet[4]) {
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 5);
        }
        if (_player.mapPhuHo == 113) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, _player.percentPhuHo);
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, _player.percentPhuHo);
            this.damageFull += Utils.percentOf(this.damageFull, _player.percentPhuHo);
        }
        if (_player.mapPhuHo == 127) {
            this.hpFullTemp += 5000000;
            this.mpFullTemp += 5000000;
            this.damageFull += 100000;
        }
        if (_player.sachdacbiet[5]) {
            this.options[5] += 5;
        }
        if (_player.sachdacbiet[6]) {
            this.options[94] += 5;
        }
        if (_player.isNgocThach()) {
            this.options[94] += 10;
        }
        if (_player.zone != null && _player.zone.map.isCold()) {
            if (!isUnaffectedCold && !(this._player instanceof Boss)) {
                this.hpFullTemp /= 2;
                this.damageFull /= 2;
            }
        }
        bonusLevelItem();
        checkClanOption();
        checkWhis();

        if (this.hpFullTemp <= 0) {
            this.hpFullTemp = 1;
        }
        if (this.mpFullTemp <= 0) {
            this.mpFullTemp = 1;
        }
        if (_player.isNhapThe() && _player.myDisciple != null && _player.myDisciple.info != null) {
            Disciple disciple = _player.myDisciple;
            this.hpFullTemp += disciple.info.hpFull;
            this.mpFullTemp += disciple.info.mpFull;
            this.damageFull += disciple.info.damageFull;
//            if (disciple.typeDisciple > 0) {
//                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
//                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
//                this.damageFull += Utils.percentOf(this.damageFull, 10);
//            }
        }
        if (_player.isHuytSao()) {
            long hpAdd = Utils.percentOf(this.hpFullTemp, this._player.hpPercent);
            this.hpFullTemp += hpAdd;
        }
        this.hpFull = this.hpFullTemp;
        this.mpFull = this.mpFullTemp;
        if (this.hp > this.hpFull) {
            this.hp = this.hpFull;
        }
        if (this.mp > this.mpFull) {
            this.mp = this.mpFull;
        }
        options[94] = Math.min(options[94], 85);
        percentMiss = Math.min(percentMiss, 85);
    }

    void checkWhis() {
        var pl = this._player;
        if (pl == null || pl.zone == null) {
            return;
        }
        if (_player.zone.map.isMapDeTu() && _player.exitsItemTime(ItemTimeName.BUA_MA_THUAT)) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 100);
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 100);
            this.damageFull += Utils.percentOf(this.damageFull, 100);
        }
        if (pl.zone.map.mapID == 154) {
            if (pl.exitsItemTime(ItemTimeName.DA_TANG_CUONG)) {
                this.damageFull += Utils.percentOf(this.damageFull, 30);
            }
            if (pl.exitsItemTime(ItemTimeName.DA_SDCM)) {
                this.options[5] += 30;
            }
            if (pl.exitsItemTime(ItemTimeName.DA_CUNG_CAP)) {
                this.options[94] += 30;
            }
            if (pl.exitsItemTime(ItemTimeName.DA_X2_CHI_SO)) {
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 100);
                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 100);
                this.damageFull += Utils.percentOf(this.damageFull, 100);
            }
            if (pl.exitsItemTime(ItemTimeName.DA_SINH_LUC)) {
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 20);
            }
            if (pl.exitsItemTime(ItemTimeName.DA_CHI_MANG)) {
                this.criticalFull = 100;
            }
            if (pl.exitsItemTime(ItemTimeName.DA_SUC_DANH)) {
                this.damageFull += Utils.percentOf(this.damageFull, 20);
            }
        }
    }

    void checkClanOption() {
        var pl = this._player;
        if (pl == null || pl.zone == null) {
            return;
        }
        int paramHP = options[223];
        int paramMP = options[224];
        int paramSD = options[225];
        if (pl.clanID != -1) {
            List<Player> list = pl.zone.getMemberSameClan(pl);
            synchronized (list) {
                for (Player player : list) {
                    paramHP += player.info.options[223];
                    paramMP += player.info.options[224];
                    paramSD += player.info.options[225];
                }
            }
        }
        paramHP = Math.min(5, paramHP);
        paramMP = Math.min(5, paramMP);
        paramSD = Math.min(5, paramSD);
        this.hpFullTemp += Utils.percentOf(this.hpFullTemp, paramHP);
        this.mpFullTemp += Utils.percentOf(this.mpFullTemp, paramMP);
        this.damageFull += Utils.percentOf(this.damageFull, paramSD);

        if (pl.isHuman() && pl.myDisciple != null) {
            int option = pl.myDisciple.info.options[226];
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, option);
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, option);
            this.damageFull += Utils.percentOf(this.damageFull, option);
        }

    }

    void bonusLevelItem() {
        byte[] num = new byte[10];
        if (_player.itemBody == null) {
            return;
        }
        for (var item : _player.itemBody) {
            if (item != null && item.template.type < 5 && item.findOptions(72) != -1) {
                for (int i = 0; i < item.findOptions(72); i++) {
                    num[i]++;
                }
            }
        }
        for (int i = 7; i <= num.length; i++) {
            if (num[i - 1] == 5) {
                byte paramBonus = 0;
                if (i == 6) {
                    paramBonus = 2;
                }
                if (i == 7) {
                    paramBonus = 4;
                }
                if (i == 8) {
                    paramBonus = 6;
                }
                if (i == 9) {
                    paramBonus = 20;
                }
                if (i == 10) {
                    paramBonus = 30;
                }
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, paramBonus);
                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, paramBonus);
                this.damageFull += Utils.percentOf(this.damageFull, paramBonus);
            }
        }

    }

    public void recovery(int type, int percent, boolean isUpdate) {
        long preHp = this.hp;
        long preMp = this.mp;
        long hp = Utils.percentOf(this.hpFull, percent);
        long mp = Utils.percentOf(this.mpFull, percent);
        long pHp = this.hp;
        long pMp = this.mp;
        switch (type) {
            case ALL:
                pHp += hp;
                pMp += mp;
                break;

            case HP:
                pHp += hp;
                break;

            case MP:
                pMp += mp;
                break;
        }
        if (pHp > this.hpFull) {
            pHp = this.hpFull;
        }
        if (pMp > this.mpFull) {
            pMp = this.mpFull;
        }
        this.hp = pHp;
        this.mp = pMp;
        if (isUpdate) {
            switch (type) {
                case ALL:
                    if (this.hp != preHp) {
                        _player.service.loadHP();
                        _player.zone.service.playerLoadBody(_player);
                    }
                    if (this.mp != preMp) {
                        _player.service.loadMP();
                    }
                    break;

                case HP:
                    if (this.hp != preHp) {
                        _player.service.loadHP();
                    }
                    _player.zone.service.playerLoadBody(_player);
                    break;

                case MP:
                    if (this.mp != preMp) {
                        _player.service.loadMP();
                    }
                    break;
            }
        }
    }

    public void recovery(int type, long number) {
        long preHp = this.hp;
        long preMp = this.mp;
        long pHp = this.hp;
        long pMp = this.mp;
        switch (type) {
            case ALL:
                pHp += number;
                pMp += number;
                break;

            case HP:
                pHp += number;
                break;

            case MP:
                pMp += number;
                break;
        }
        if (pHp > this.hpFull) {
            pHp = this.hpFull;
        }
        if (pMp > this.mpFull) {
            pMp = this.mpFull;
        }
        this.hp = pHp;
        this.mp = pMp;
        if (_player.zone != null) {
            if (this.hp != preHp) {
                _player.service.loadHP();
                _player.zone.service.playerLoadBody(_player);
            }
            if (this.mp != preMp) {
                _player.service.loadMP();
            }
        }
    }

    public void applyCharLevelPercent() {
        try {
            long num = 1L;
            long num2 = 0L;
            int num3 = 0;
            Server server = DragonBall.getInstance().getServer();
            int size = server.powers.size();
            for (int i = size - 1; i >= 0; i--) {
                if (this.power >= server.powers.get(i)) {
                    if (i == size - 1) {
                        num = 1L;
                    } else {
                        num = server.powers.get(i + 1) - server.powers.get(i);
                    }
                    num2 = this.power - server.powers.get(i);
                    num3 = i;
                    break;
                }
            }
            this.level = num3;
            this.levelPercent = num2 * 10000L / num;
        } catch (Exception ignored) {
            System.err.println("Error at 119");
        }
    }

    public long getTotalPotential() {
        long total = 0;
        long num = this.originalHP / Info.HP_FROM_1000_TIEM_NANG;
        total += num * (2 * (num + 1000) + ((num * Info.HP_FROM_1000_TIEM_NANG) - 20)) / 2;
        num = this.originalMP / Info.MP_FROM_1000_TIEM_NANG;
        total += num * (2 * (num + 1000) + ((num * Info.HP_FROM_1000_TIEM_NANG) - 20)) / 2;
        num = this.originalDamage / Info.DAMAGE_FROM_1000_TIEM_NANG;
        total += num * (2 * num + ((num * Info.DAMAGE_FROM_1000_TIEM_NANG) - 1)) / 2 * Info.EXP_FOR_ONE_ADD;
        if (this.originalDefense > 0) {
            total += 2 * (((long) this.originalDefense) + 5) / 2 * 100000;
        }
        if (this.originalCritical > 0) {
            long pointNeed5 = 50000000L;
            for (int i = 0; i < this.originalCritical; i++) {
                pointNeed5 *= 5L;
            }
            total += pointNeed5;
        }
        return total;
    }

    public void potentialUp(Message ms) {
        try {
            if (_player.isDead()) {
                return;
            }
            byte type = ms.reader().readByte();
            short num = ms.reader().readShort();
            switch (type) {
                case 0:
                    if (num != 1 && num != 10 && num != 100 && num != 1000 && num != 10000) {
                        return;
                    }
                    if (originalHP >= powerLimitMark.hp + 2000 || (originalHP + num * Info.HP_FROM_1000_TIEM_NANG) >= powerLimitMark.hp + 2000) {
                        _player.service.serverMessage2("HP của bạn đã đạt mức tối đa");
                        return;
                    }
                    long pointNeed1 = num * (2 * (this.originalHP + 1000) + ((num * Info.HP_FROM_1000_TIEM_NANG) - 20)) / 2;
                    if (this.potential < pointNeed1) {
                        return;
                    }
                    this.potential -= pointNeed1;
                    this.originalHP += (num * Info.HP_FROM_1000_TIEM_NANG);
                    break;
                case 1:
                    if (num != 1 && num != 10 && num != 100 && num != 1000 && num != 10000) {
                        return;
                    }
                    if (originalMP >= powerLimitMark.mp + 2000 || (originalMP + num * Info.MP_FROM_1000_TIEM_NANG) >= powerLimitMark.mp + 2000) {
                        _player.service.serverMessage2("MP của bạn đã đạt mức tối đa");
                        return;
                    }
                    long pointNeed2 = num * (2 * (this.originalMP + 1000) + ((num * Info.MP_FROM_1000_TIEM_NANG) - 20)) / 2;
                    if (this.potential < pointNeed2) {
                        return;
                    }
                    this.potential -= pointNeed2;
                    this.originalMP += (num * Info.MP_FROM_1000_TIEM_NANG);
                    break;
                case 2:
                    if (num != 1 && num != 10 && num != 100 && num != 1000 && num != 10000) {
                        return;
                    }
                    if (originalDamage >= powerLimitMark.damage + 100 || (originalDamage + num * Info.DAMAGE_FROM_1000_TIEM_NANG) >= powerLimitMark.damage + 100) {
                        _player.service.serverMessage2("Sức đánh của bạn đã đạt mức tối đa");
                        return;
                    }
                    long pointNeed3 = num * (2 * this.originalDamage + ((num * Info.DAMAGE_FROM_1000_TIEM_NANG) - 1)) / 2 * Info.EXP_FOR_ONE_ADD;
                    if (this.potential < pointNeed3) {
                        return;
                    }
                    this.potential -= pointNeed3;
                    this.originalDamage += (num * Info.DAMAGE_FROM_1000_TIEM_NANG);
                    break;
                case 3:
                    if (num != 1 && num != 10 && num != 100 && num != 1000 && num != 10000) {
                        return;
                    }
                    if (originalDefense >= powerLimitMark.defense + 100 || (originalDefense + num * Info.DEFENSE_FROM_1000_TIEM_NANG) >= powerLimitMark.defense + 100) {
                        _player.service.serverMessage2("Giáp của bạn đã đạt mức tối đa");
                        return;
                    }
                    long pointNeed4 = num * 2L * (this.originalDefense + 5) / 2 * 100000;
                    if (this.potential < pointNeed4) {
                        _player.service.serverMessage2(String.format("Còn thiếu %s tiềm năng mới có thể cộng", Utils.formatNumber(pointNeed4 - this.potential)));
                        return;
                    }
                    this.potential -= pointNeed4;
                    this.originalDefense += (num * Info.DEFENSE_FROM_1000_TIEM_NANG);
                    break;
                case 4:
                    if (num != 1) {
                        return;
                    }
                    if (originalCritical > powerLimitMark.critical) {
                        _player.service.serverMessage2("Chí mạng của bạn đã đạt mức tối đa");
                        return;
                    }
                    long pointNeed5 = 50000000L;
                    for (int i = 0; i < this.originalCritical; i++) {
                        pointNeed5 *= 5L;
                    }
                    if (this.potential < pointNeed5) {
                        return;
                    }
                    this.potential -= pointNeed5;
                    this.originalCritical += (num * Info.CRITICIAL_FROM_1000_TIEM_NANG);
                    break;
            }
            setInfo();
            _player.service.loadPoint();
            if (_player.taskMain.id == 3 && _player.taskMain.index == 0) {
                _player.taskNext();
                _player.setListAccessMap();
            }
        } catch (IOException ex) {
            
            System.err.println("Error at 118");
            logger.error("failed!", ex);
        }
    }

    public String getStrLevel() {
        String x = "";
        try {
            x = Caption.getCaption(_player.classId).get(this.level) + "+" + (this.levelPercent / 100L) + "." + (this.levelPercent % 100L) + "%";
        } catch (Exception e) {
            

        }
        return x;
    }

    public void addPowerOrPotential(byte type, long exp) {
        if (this.power >= this.powerLimitMark.power) {
            return;
        }
        if (this.power + exp >= this.powerLimitMark.power) {
            exp = this.powerLimitMark.power - this.power;
        }
        if (exp <= 0) {
            return;
        }
        switch (type) {
            case POWER_AND_POTENTIAL:
                this.power += exp;
                this.potential += exp;
                break;

            case POWER:
                this.power += exp;
                break;

            case POTENTIAL:
                this.potential += exp;
                break;
        }
        // update level
        if (type == POWER || type == POWER_AND_POTENTIAL) {
            Server server = DragonBall.getInstance().getServer();
            if (this.level < server.powers.size() - 1) {
                if (this.power >= server.powers.get(this.level + 1)) {
                    this.level++;
                    if (_player.isDisciple()) {
                        Disciple p = (Disciple) _player;
                        p.master.service.chat(p, "Sự phụ ơi, con lên cấp rồi");
                        setMaxStamina();
                    }
                }
            }
        }
        _player.service.addExp(type, exp);
    }
}
