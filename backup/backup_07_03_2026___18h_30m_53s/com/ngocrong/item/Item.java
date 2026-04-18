package com.ngocrong.item;

import com.google.gson.annotations.SerializedName;
import com.ngocrong.consts.ItemName;
import com.ngocrong.server.Config;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Server;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Item {

    private static Logger logger = Logger.getLogger(Item.class);

    public static final int TYPE_BODY_MIN = 0;
    public static final int TYPE_BODY_MAX = 6;
    public static final int TYPE_AO = 0;
    public static final int TYPE_QUAN = 1;
    public static final int TYPE_GANGTAY = 2;
    public static final int TYPE_GIAY = 3;
    public static final int TYPE_RADA = 4;
    public static final int TYPE_HAIR = 5;
    public static final int TYPE_DAUTHAN = 6;
    public static final int TYPE_NGOCRONG = 12;
    public static final int TYPE_SACH = 7;
    public static final int TYPE_NHIEMVU = 8;
    public static final int TYPE_GOLD = 9;
    public static final int TYPE_DIAMOND = 10;
    public static final int TYPE_BALO = 11;
    public static final int TYPE_AMULET = 13;
    public static final int TYPE_DANH_HIEU = 17;
    public static final int TYPE_PET_THEO_SAU = 18;
    public static final int TYPE_PET_BAY = 19;

    public static final int TYPE_PET_BAY_BAC_1 = 21;
    public static final int TYPE_PET_BAY_BAC_2 = 38;
    public static final int TYPE_VAT_PHAM_PHU_TRO = 39;
    public static final int TYPE_THU_CUOI_1 = 23;
    public static final int TYPE_THU_CUOI_2 = 24;
    public static final int TYPE_NGOC_BOI = 26;
    public static final int TYPE_HAO_QUANG = 35;
    public static final int TYPE_DIAMOND_LOCK = 34;

    public static final byte BOX_BAG = 0;
    public static final byte BAG_BOX = 1;
    public static final byte BOX_BODY = 2;
    public static final byte BODY_BOX = 3;
    public static final byte BAG_BODY = 4;
    public static final byte BODY_BAG = 5;
    public static final byte BAG_PET = 6;
    public static final byte PET_BAG = 7;

    @SerializedName("index")
    public int indexUI;
    public int id;
    public int quantity;
    public ArrayList<ItemOption> options;

    public transient String info;
    public transient String content;
    public transient boolean isLock;
    public transient boolean isCantSaleForPlay, isCantSale, isNhapThe;
    public transient byte typeThrow;
    public transient ItemTemplate template;
    public transient Lock lock = new ReentrantLock();
    public transient long require;
    public transient int upgrade;
    public transient int levelMosaicStone;
    public transient boolean isSet;

    public Item() {
    }

    public Item(int id) {
        this.id = id;
        init();
    }

    public void setDefaultOptions() {
        this.options.clear();
        for (ItemOption option : this.template.options) {
            addItemOption(new ItemOption(option.optionTemplate.id, option.param));
        }

    }

    public void load(JSONObject obj) {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            this.id = obj.getInt("id");
            init();
            this.indexUI = obj.getInt("index");
            this.quantity = obj.getInt("quantity");
            JSONArray options = obj.getJSONArray("options");
            addItemOptions(options);
            if (!this.template.isUpToUp() && this.quantity > 1) {
                this.quantity = 1;
            }
            if (this.quantity > config.getMaxQuantity()) {
                this.quantity = config.getMaxQuantity();
            }
        } catch (JSONException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void init() {
        try {
            Server server = DragonBall.getInstance().getServer();
            this.template = server.iTemplates.get(id);
            int id = this.id;
            byte type = template.type;
            this.typeThrow = 1;// bỏ ra mất luôn

            // drop item
            if (type == 8 || type == 6 || type == 12 || type == 32 || type == 33 || this.id == 521 || id == ItemName.THOI_VANG) {
                this.typeThrow = 2;// ko cho bỏ
            }
            if (type == 5 || type == 7 || type == 8 || type == 14 || type == 16 || type == 22 || type == 23 || type == 24 || type == 25 || type == 30 || type == 31 || type == 11) {
                this.isCantSale = true;
            }
            // new
//        if (type >= 0 && type <= 12) {
//            this.typeThrow = 1;
//        }
            this.info = "";
            this.content = "";
            this.options = new ArrayList<>();
            this.require = template.require;
        } catch (Exception e) {
            System.err.println("Errror at template id : " + this.id);
            e.printStackTrace();
        }

    }

    public void checkAdd() {
        if (this.template.type < 5) {
            if (this.options != null) {
                for (var x : this.options) {
                    if (x != null && x.optionTemplate.id == 222) {
                        this.options.remove(x);
                        return;
                    }
                }

            }
        }

    }

    public boolean isLock() {
        return isLock || template.isLock;
    }

    public void addItemOption(ItemOption itemOption) {
        setAttribute(itemOption);
        this.options.add(itemOption);
    }

    public void addRandomOption(int percent) {
        if (!this.options.isEmpty()) {
            for (ItemOption io : this.options) {
                io.param += (io.param * percent / 100);
            }
        }
    }

    public void addRandomOption(int minStar, int maxStar) {
        if (!this.options.isEmpty()) {
            for (ItemOption io : this.options) {
                io.param += (io.param * (Utils.nextInt(0, 15)) / 100);
            }
        }
        if (maxStar > 0) {
            this.options.add(new ItemOption(107, Utils.nextInt(minStar, maxStar)));
        }
    }

    public void addRandomOptionMabu(int random) {
        // Tăng thông số của tất cả option hiện có lên 0-15%
        if (!this.options.isEmpty()) {
            for (ItemOption io : this.options) {
                io.param += (io.param * (Utils.nextInt(0, 15)) / 100);
            }
        }
        if (random < 200) {          // 200/1000 = 20% cho giá trị 1-4
            this.options.add(new ItemOption(107, Utils.nextInt(1, 4)));
        } else if (random < 220) {   // 20/1000 = 2% cho giá trị 5
            this.options.add(new ItemOption(107, 5));
        } else if (random < 225) {   // 5/1000 = 0.5% cho giá trị 6
            this.options.add(new ItemOption(107, 6));
        } else if (random < 226) {   // 1/1000 = 0.1% cho giá trị 7
            this.options.add(new ItemOption(107, 7));
        }
    }

    public static final int NAPPA = 135;
    public static final int CADIC = 134;
    public static final int KAKAROT = 133;
    public static final int THIENXINHANG = 127;
    public static final int KIRIN = 128;
    public static final int SONGOKU = 129;
    public static final int PICOLO = 130;
    public static final int OCTIEU = 131;
    public static final int DAIMAO = 132;

    public void addOptionSKH(int idSKH) {
        switch (idSKH) {
            case 135: // nappa
            case 138:
                addItemOption(new ItemOption(135, 0));
                addItemOption(new ItemOption(138, 0));
                break;
            case 134: // cadic
            case 137:
                addItemOption(new ItemOption(134, 0));
                addItemOption(new ItemOption(137, 0));
                break;
            case 133: // kakarot
            case 136:
                addItemOption(new ItemOption(133, 0));
                addItemOption(new ItemOption(136, 0));
                break;
            case 127: // then xin hang
            case 139:
                addItemOption(new ItemOption(127, 0));
                addItemOption(new ItemOption(139, 0));
                break;
            case 128: // kirin
            case 140:
                addItemOption(new ItemOption(128, 0));
                addItemOption(new ItemOption(140, 0));
                break;
            case 129: // songoku
            case 141:
                addItemOption(new ItemOption(129, 0));
                addItemOption(new ItemOption(141, 0));
                break;
            case 130: // picolo
            case 195:
                addItemOption(new ItemOption(130, 0));
                addItemOption(new ItemOption(195, 0));
                break;
            case 131: // octieu
            case 143:
                addItemOption(new ItemOption(131, 0));
                addItemOption(new ItemOption(143, 0));
                break;
            case 132: // pikoro
            case 144:
                addItemOption(new ItemOption(132, 0));
                addItemOption(new ItemOption(144, 0));
                break;
            case 224: // tnsm
            case 225:
                addItemOption(new ItemOption(224, 0));
                addItemOption(new ItemOption(225, 0));
                break;

        }
    }

    public void putItemOption(int index, ItemOption itemOption) {
        setAttribute(itemOption);
        this.options.set(index, itemOption);
    }

    public void setAttribute(ItemOption itemOption) {
        if (itemOption.optionTemplate.id == 30) {
            this.isLock = true;
        }
        if (itemOption.optionTemplate.id >= 34 && itemOption.optionTemplate.id <= 36) {
            this.levelMosaicStone = itemOption.param;
        }
        if (itemOption.optionTemplate.id >= 127 && itemOption.optionTemplate.id <= 135) {
            this.isSet = true;
        }
        if (itemOption.optionTemplate.id == 72) {
            this.upgrade = itemOption.param;
        }
        if (itemOption.optionTemplate.id == 38) {
            this.isNhapThe = true;
        }
        if (itemOption.optionTemplate.id == 107 || itemOption.optionTemplate.type == 9) {
            this.typeThrow = 2;
        }
        if (itemOption.optionTemplate.id == 154) {
            this.isCantSaleForPlay = true;
        }
        if (itemOption.optionTemplate.id == 21) {
            this.require = (long) itemOption.param * 1000000000L;
        }
    }

    public void addItemOptions(JSONArray jsonOptions) {
        try {
            int lent = jsonOptions.length();
            for (int i = 0; i < lent; i++) {
                JSONObject j = jsonOptions.getJSONObject(i);
                int optionId = j.getInt("id");
                int optionParam = j.getInt("param");
                addItemOption(new ItemOption(optionId, optionParam));
            }
        } catch (JSONException e) {
            
            logger.error("failed!", e);
        }
    }

    public void setParamsSKH(Item item) {
        double setNappa = 0.11;
        double thenXinHang = 0.11;
        double kirin = 0.11;
        double sonGoKu = 0.11;
        double picolo = 0.11;
        double ocTieu = 0.11;
        double pikkoroDaimao = 0.11;
        double cadic = 0.11;
        double kakarot = 0.11;
        Random rd = new Random();
        double rdNumber = rd.nextDouble();
        if (rdNumber < setNappa) {
            item.addItemOption(new ItemOption(135, 0));
            item.addItemOption(new ItemOption(138, 0));
        } else if (rdNumber < setNappa + thenXinHang) {
            item.addItemOption(new ItemOption(127, 0));
            item.addItemOption(new ItemOption(139, 0));
        } else if (rdNumber < setNappa + thenXinHang + kirin) {
            item.addItemOption(new ItemOption(128, 0));
            item.addItemOption(new ItemOption(140, 0));
        } else if (rdNumber < setNappa + thenXinHang + kirin + sonGoKu) {
            item.addItemOption(new ItemOption(129, 0));
            item.addItemOption(new ItemOption(141, 0));
        } else if (rdNumber < setNappa + thenXinHang + kirin + sonGoKu + picolo) {
            item.addItemOption(new ItemOption(130, 0));
            item.addItemOption(new ItemOption(195, 0));
        } else if (rdNumber < setNappa + thenXinHang + kirin + sonGoKu + picolo + ocTieu) {
            item.addItemOption(new ItemOption(131, 0));
            item.addItemOption(new ItemOption(143, 0));
        } else if (rdNumber < setNappa + thenXinHang + kirin + sonGoKu + picolo + ocTieu + pikkoroDaimao) {
            item.addItemOption(new ItemOption(132, 0));
            item.addItemOption(new ItemOption(144, 0));
        } else if (rdNumber < setNappa + thenXinHang + kirin + sonGoKu + picolo + ocTieu + pikkoroDaimao + kakarot) {
            item.addItemOption(new ItemOption(133, 0));
            item.addItemOption(new ItemOption(136, 0));
        } else { // cadic
            item.addItemOption(new ItemOption(134, 0));
            item.addItemOption(new ItemOption(137, 0));
        }
    }

    public void setParamsSKHGender(Item item, int gender) {
        if (gender == 0) {
            if (Utils.nextInt(3) == 0) { // then xin hang
                item.addItemOption(new ItemOption(127, 0));
                item.addItemOption(new ItemOption(139, 0));
            } else if (Utils.nextInt(2) == 0) { //kirin
                item.addItemOption(new ItemOption(128, 0));
                item.addItemOption(new ItemOption(140, 0));
            } else { //songoku
                item.addItemOption(new ItemOption(129, 0));
                item.addItemOption(new ItemOption(141, 0));
            }
        }
        if (gender == 1) {
            if (Utils.nextInt(3) == 0) { // picolo
                item.addItemOption(new ItemOption(130, 0));
                item.addItemOption(new ItemOption(195, 0));
            } else if (Utils.nextInt(2) == 0) { //octieu
                item.addItemOption(new ItemOption(131, 0));
                item.addItemOption(new ItemOption(143, 0));
            } else { // pikkoro
                item.addItemOption(new ItemOption(132, 0));
                item.addItemOption(new ItemOption(144, 0));
            }
        }
        if (gender == 2) {
            if (Utils.nextInt(3) == 0) { // nappa
                item.addItemOption(new ItemOption(135, 0));
                item.addItemOption(new ItemOption(138, 0));
            } else if (Utils.nextInt(2) == 0) { // cadic
                item.addItemOption(new ItemOption(134, 0));
                item.addItemOption(new ItemOption(137, 0));
            } else { // kakarot
                item.addItemOption(new ItemOption(133, 0));
                item.addItemOption(new ItemOption(136, 0));
            }
        }

    }

    public void setParamsSKHGender(Item item) {
        switch (item.id) {
            case 2:
            case 8:
            case 23:
            case 29:
                if (Utils.nextInt(3) == 0) { // nappa
                    item.addItemOption(new ItemOption(135, 0));
                    item.addItemOption(new ItemOption(138, 0));
                } else if (Utils.nextInt(2) == 0) { // cadic
                    item.addItemOption(new ItemOption(134, 0));
                    item.addItemOption(new ItemOption(137, 0));
                } else { // kakarot
                    item.addItemOption(new ItemOption(133, 0));
                    item.addItemOption(new ItemOption(136, 0));
                }
                break;
            case 0:
            case 6:
            case 21:
            case 27:
                if (Utils.nextInt(3) == 0) { // then xin hang
                    item.addItemOption(new ItemOption(127, 0));
                    item.addItemOption(new ItemOption(139, 0));
                } else if (Utils.nextInt(2) == 0) { //kirin
                    item.addItemOption(new ItemOption(128, 0));
                    item.addItemOption(new ItemOption(140, 0));
                } else { //songoku
                    item.addItemOption(new ItemOption(129, 0));
                    item.addItemOption(new ItemOption(141, 0));
                }
                break;
            case 1:
            case 7:
            case 22:
            case 28:
                if (Utils.nextInt(3) == 0) { // picolo
                    item.addItemOption(new ItemOption(130, 0));
                    item.addItemOption(new ItemOption(195, 0));
                } else if (Utils.nextInt(2) == 0) { //octieu
                    item.addItemOption(new ItemOption(131, 0));
                    item.addItemOption(new ItemOption(143, 0));
                } else { // pikkoro
                    item.addItemOption(new ItemOption(132, 0));
                    item.addItemOption(new ItemOption(144, 0));
                }
                break;
            case 12:
                if (Utils.nextInt(8) == 0) { // nappa
                    item.addItemOption(new ItemOption(135, 0));
                    item.addItemOption(new ItemOption(138, 0));
                } else if (Utils.nextInt(8) == 0) { // cadic
                    item.addItemOption(new ItemOption(134, 0));
                    item.addItemOption(new ItemOption(137, 0));
                } else if (Utils.nextInt(8) == 0) { // kakarot
                    item.addItemOption(new ItemOption(133, 0));
                    item.addItemOption(new ItemOption(136, 0));
                } else if (Utils.nextInt(8) == 0) { // picolo
                    item.addItemOption(new ItemOption(130, 0));
                    item.addItemOption(new ItemOption(195, 0));
                } else if (Utils.nextInt(8) == 0) { //octieu
                    item.addItemOption(new ItemOption(131, 0));
                    item.addItemOption(new ItemOption(143, 0));
                } else if (Utils.nextInt(8) == 0) { // pikkoro
                    item.addItemOption(new ItemOption(132, 0));
                    item.addItemOption(new ItemOption(144, 0));
                } else if (Utils.nextInt(8) == 0) { // then xin hang
                    item.addItemOption(new ItemOption(127, 0));
                    item.addItemOption(new ItemOption(139, 0));
                } else if (Utils.nextInt(8) == 0) { //kirin
                    item.addItemOption(new ItemOption(128, 0));
                    item.addItemOption(new ItemOption(140, 0));
                } else if (Utils.nextInt(8) == 0) { //songoku
                    item.addItemOption(new ItemOption(129, 0));
                    item.addItemOption(new ItemOption(141, 0));
                }
                break;
        }
    }

    public Item clone() {
        Item item = new Item();
        item.template = this.template;
        if (this.options != null) {
            item.options = new ArrayList();
            for (ItemOption option : this.options) {
                item.addItemOption(new ItemOption(option.optionTemplate.id, option.param));
            }
        }
        item.id = this.id;
        item.indexUI = this.indexUI;
        item.quantity = this.quantity;
        item.content = this.content;
        item.info = this.info;
        item.typeThrow = this.typeThrow;
        item.isLock = this.isLock;
        item.isCantSaleForPlay = this.isCantSaleForPlay;
        item.isCantSale = this.isCantSale;
        item.require = this.require;
        item.isNhapThe = this.isNhapThe;
        return item;
    }

    public ItemOption getItemOption(int id) {
        for (ItemOption option : this.options) {
            if (option.id == id) {
                return option;
            }
        }
        return null;
    }

    public ArrayList<ItemOption> getDisplayOptions() {
        ArrayList<ItemOption> options = new ArrayList<>(this.options);
        if (options.isEmpty()) {
            options.add(new ItemOption(73, 0));
        }
        return options;
    }

    public ArrayList<ItemOption> getOptions() {
        ArrayList<ItemOption> options = new ArrayList<>();
        int damage = 0;
        int hp = 0;
        int mp = 0;
        for (ItemOption o : this.options) {
            int optionID = o.optionTemplate.id;
            if (optionID == 50) {
                damage += o.param;
            } else if (optionID == 77) {
                hp += o.param;
            } else if (optionID == 103) {
                mp += o.param;
            } else {
                options.add(o);
            }
        }
        if (damage > 0) {
            options.add(new ItemOption(50, damage));
        }
        if (hp > 0) {
            options.add(new ItemOption(77, hp));
        }
        if (mp > 0) {
            options.add(new ItemOption(103, mp));
        }
        return options;
    }

    public boolean isTypeBody() {
        return (0 <= (int) this.template.type && (int) this.template.type < 6) || (int) this.template.type == 32;
    }

    public boolean isTypeUIMe() {
        return this.template.type == 5 || this.template.type == 3 || this.template.type == 4;
    }

    public boolean isTypeUIShopView() {
        return this.isTypeUIShop() || (this.isTypeUIStore() || this.isTypeUIBook() || this.isTypeUIFashion());
    }

    public boolean isTypeUIShop() {
        return this.template.type == 20 || this.template.type == 21 || this.template.type == 22 || this.template.type == 23 || this.template.type == 24 || this.template.type == 25 || this.template.type == 26 || this.template.type == 27 || this.template.type == 28 || this.template.type == 29 || this.template.type == 16 || this.template.type == 17 || this.template.type == 18 || this.template.type == 19 || this.template.type == 2 || this.template.type == 6 || this.template.type == 8;
    }

    public boolean isTypeUIShopLock() {
        return this.template.type == 7 || this.template.type == 9;
    }

    public boolean isTypeUIStore() {
        return this.template.type == 14;
    }

    public boolean isTypeUIBook() {
        return this.template.type == 15;
    }

    public boolean isTypeUIFashion() {
        return this.template.type == 32;
    }

    public boolean isDoKH() {
        if (options != null && !options.isEmpty()) {
            for (ItemOption io : options) {
                if (io.id == 127 || io.id == 128 || io.id == 129 || io.id == 130 || io.id == 131 || io.id == 132
                        || io.id == 133 || io.id == 134 || io.id == 135 || io.id == 224 || io.id == 225) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDoTL() {
        return (this.id == ItemName.AO_THAN_XAYDA || this.id == ItemName.AO_THAN_NAMEC || this.id == ItemName.AO_THAN_LINH
                || this.id == ItemName.QUAN_THAN_XAYDA || this.id == ItemName.QUAN_THAN_NAMEC
                || this.id == ItemName.QUAN_THAN_LINH || this.id == ItemName.GIAY_THAN_LINH
                || this.id == ItemName.GIAY_THAN_XAYDA || this.id == ItemName.GIAY_THAN_NAMEC
                || this.id == ItemName.GANG_THAN_XAYDA || this.id == ItemName.GANG_THAN_NAMEC || this.id == ItemName.GANG_THAN_LINH
                || this.id == ItemName.NHAN_THAN_LINH) && !isLock();
    }

    public boolean isDoTLKH() {
        return this.isDoTL() && this.isDoKH();
    }

    @Override
    public String toString() {
        return String.format("[%d]%s(%d)", this.indexUI, this.template.name, this.quantity);
    }

    public boolean isCanSaleToConsignment() {
        if (template.id == 457) {
            return false;
        }
        if (template.id == ItemName.QUA_TRUNG) {
            return true;
        }
        if (template.id == 992) {
            return true;
        }
        if (template.id >= 663 && template.id <= 667) {
            return false;
        }
//        if (template.id == 2243 || template.id == 2261 || template.id == 568 || template.id == 2197) {
//            return true;
//        }
//        if (template.id == 2251 || template.id == 1994) {
//            return true;
//        }
        if (template.id == 1021 || template.id == 1022 || template.id == 1023 || (template.id >= 381 && template.id <= 384)) {
            return true;
            // item c1,2
        }
        if (template.id == 987) {
            // da bao ve
            return true;
        }
        if (template.id == 987) {
            // da bao ve
            return true;
        }

        if (template.type < 5 && (template.level == 13)) {
            return true;
        }
//        if (template.id == 752 || template.id == 752 || template.id == 2146 || template.id == 2147) {
//            return true;
//        }
//        if (template.id >= 925 && template.id <= 931) {
//            return true;
//        }
//        if (template.id >= 2106 && template.id <= 2112) {
//            return true;
//        }
        if (template.type == 29) {
            return true;
        }
        // spl-nr - da nang cap
        return template.type == 30 || template.type == 14 || template.type == 12 || getItemOption(86) != null;
    }

    public boolean isLastItemInShop() {
        return this.id == ItemName.AO_JEAN_CALIC || this.id == ItemName.AO_VANG_ZEALOT || this.id == ItemName.AO_LUONG_LONG
                || this.id == ItemName.QUAN_JEAN_CALIC || this.id == ItemName.QUAN_VANG_ZEALOT
                || this.id == ItemName.QUAN_LUONG_LONG || this.id == ItemName.GANG_VANG_ZEALOT
                || this.id == ItemName.GANG_LUONG_LONG || this.id == ItemName.GIAY_JEAN_CALIC
                || this.id == ItemName.GIAY_VANG_ZEALOT || this.id == ItemName.GIAY_LUONG_LONG || this.id == ItemName.GANG_JEAN_CALIC;
    }

    public boolean isFirstItemShop() {
        return this.id == ItemName.AO_VAI_THO || this.id == ItemName.QUAN_VAI_THO || this.id == ItemName.GIAY_VAI_THO
                || this.id == ItemName.GANG_VAI_THO || this.id == ItemName.AO_VAI_3_LO
                || this.id == ItemName.QUAN_VAI_DEN || this.id == ItemName.GIAY_NHUA
                || this.id == ItemName.GANG_VAI_DEN || this.id == ItemName.AO_SOI_LEN
                || this.id == ItemName.QUAN_SOI_LEN || this.id == ItemName.GANG_SOI_LEN || this.id == ItemName.GIAY_SOI_LEN
                || this.id == ItemName.RADA_CAP_1;
    }

    public boolean isDragonBallNamec() {
        return this.id == ItemName.NGOC_RONG_NAMEK_1_SAO || this.id == ItemName.NGOC_RONG_NAMEK_2_SAO || this.id == ItemName.NGOC_RONG_NAMEK_3_SAO
                || this.id == ItemName.NGOC_RONG_NAMEK_4_SAO || this.id == ItemName.NGOC_RONG_NAMEK_5_SAO
                || this.id == ItemName.NGOC_RONG_NAMEK_6_SAO || this.id == ItemName.NGOC_RONG_NAMEK_7_SAO;
    }

    public boolean isDoHD() {
        return this.id == ItemName.AO_HUY_DIET_TD || this.id == ItemName.AO_HUY_DIET_XD || this.id == ItemName.AO_HUY_DIET_NM
                || this.id == ItemName.QUAN_HUY_DIET_NM || this.id == ItemName.QUAN_HUY_DIET_TD
                || this.id == ItemName.QUAN_HUY_DIET_XD || this.id == ItemName.GIAY_HUY_DIET_NM
                || this.id == ItemName.GIAY_HUY_DIET_TD || this.id == ItemName.GIAY_HUY_DIET_XD
                || this.id == ItemName.GANG_HUY_DIET_NM || this.id == ItemName.GANG_HUY_DIET_TD || this.id == ItemName.GANG_HUY_DIET_XD
                || this.id == ItemName.NHAN_HUY_DIET;
    }

    public boolean isCanSuperior() {
        return this.template.type == Item.TYPE_HAIR || this.template.type == Item.TYPE_PET_BAY || this.template.type == Item.TYPE_NGOC_BOI
                || this.template.type == Item.TYPE_THU_CUOI_1 || this.template.type == Item.TYPE_THU_CUOI_2 || this.template.type == Item.TYPE_BALO || this.template.type == Item.TYPE_DANH_HIEU;
    }

    public int getExpiry() {
        ItemOption option = getItemOption(93);
        if (option != null) {
            return option.param;
        }
        return -1;
    }

    public void randomParam() {
        int per = Utils.nextInt(100);
        if (per < 10) {
            randomParam(10, 15);
        } else if (per < 30) {
            randomParam(0, 9);
        } else {
            randomParam(-15, 0);
        }
    }

    public void randomParam(int minPercent, int maxPercent) {
        for (ItemOption option : options) {
            if (option.param > 0) {
                int percent = Utils.nextInt(minPercent, maxPercent);
                option.param = Math.round(((float) option.param) / 100 * (float) (percent + 100));
                if (option.param < 1) {
                    option.param = 1;
                }
            }
        }
    }

    public int findOptions(int optionID) {
        for (ItemOption option : options) {
            if (option != null && option.optionTemplate.id == optionID) {
                return option.param;
            }
        }
        return -1;
    }

    public boolean isThucAn() {
        return this.template != null && this.template.id >= 663 && this.template.id <= 667;
    }
}
