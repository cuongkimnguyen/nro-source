package com.ngocrong.crackball;

import com.ngocrong.NQMP.TamThangBa.newVQTD;
import com.ngocrong.consts.Cmd;
import com.ngocrong.consts.ItemName;
import com.ngocrong.data.VongQuayThuongDeData;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.item.ItemTemplate;
import com.ngocrong.lib.RandomCollection;
import com.ngocrong.network.FastDataOutputStream;
import com.ngocrong.network.Message;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Server;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.user.Player;
import com.ngocrong.server.SQLStatement;
import com.ngocrong.util.Utils;
import lombok.Data;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

@Data
public class CrackBall {

    private static Logger logger = Logger.getLogger(CrackBall.class);

    public static HashMap<Byte, RandomCollection> randoms;

    public static final byte VONG_QUAY_THUONG = 0;
    public static final byte VONG_QUAY_DAC_BIET = 1;
    public static final byte CAPSULE_KI_BI = 2;

    public static void loadItem() {
        try {
            randoms = new HashMap<>();
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(SQLStatement.INIT_LUCKY_WHEEL);
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int itemId = rs.getInt("item_id");
                    int quantity = rs.getInt("quantity");
                    String name = rs.getString("name");
                    double rate = rs.getDouble("rate");
                    int expire = rs.getInt("expire");
                    byte type = rs.getByte("type");
                    Reward rw = new Reward();
                    rw.setId(id);
                    rw.setName(name);
                    rw.setItemId(itemId);
                    rw.setQuantity(quantity);
                    rw.setRate(rate);
                    rw.setExpire(expire);
                    RandomCollection<Reward> rd = randoms.get(type);
                    if (rd == null) {
                        rd = new RandomCollection<>();
                        randoms.put(type, rd);
                    }
                    rd.add(rate, rw);
                }
            } finally {
                rs.close();
                ps.close();
            }
        } catch (SQLException ex) {
            
            logger.error("failed!", ex);
        }
    }

    private int[] imgs;
    private byte typePrice;
    private int price;
    private int idTicket;
    private byte type;
    private Player player;
    private byte quantity;
    private volatile RandomCollection<Reward> rd;

    public void setRandom() {
        this.rd = randoms.get(this.type);
    }

    public void show() {
        try {
            Message ms = new Message(Cmd.LUCKY_ROUND);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(0);
            ds.writeByte(imgs.length);
            for (int idImage : this.imgs) {
                ds.writeShort(idImage);
            }
            ds.writeByte(this.typePrice);
            ds.writeInt(this.price);
            ds.writeShort(this.idTicket);
            ds.flush();
            player.service.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void result() {
        try {
            Item itm = player.getItemInBag(idTicket);
            int numTicket = 0;
            if (itm != null) {
                numTicket = itm.quantity;
            }
            int q = this.quantity;
            if (numTicket > q) {
                numTicket = q;
            }
            q -= numTicket;
//            int price = q * this.price;
            if (typePrice == 0) {
                int index = player.getIndexBagById(ItemName.THOI_VANG);
                Item tv = null;
                if (index != -1) {
                    tv = player.itemBag[index];
                }
                if (tv == null || tv.quantity < q) {
                    player.service.sendThongBao("Bạn không đủ thỏi vàng");
                    return;
                }
//                player.pointThoiVang += q;
//                player.isChangePoint = true;
                player.removeItem(index, q);
            }
            if (itm != null) {
                player.removeItem(itm.indexUI, numTicket);
            }
            Server server = DragonBall.getInstance().getServer();
            Message ms = new Message(Cmd.LUCKY_ROUND);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(1);
            ds.writeByte(quantity);
            for (int i = 0; i < quantity; i++) {
                Reward rw = rd.next();
                ItemTemplate itemTemplate = server.iTemplates.get(rw.getItemId());
                Item item = new Item(itemTemplate.id);
                item.setDefaultOptions();
                item.quantity = rw.getQuantity();
                switch (item.id) {
                    case ItemName.CAI_TRANG_SIEU_THAN:
                    case ItemName.CAI_TRANG_THO_BUNMA_SEXY:
                        item.addItemOption(new ItemOption(50, Utils.nextInt(21) + 40));
                        item.addItemOption(new ItemOption(77, Utils.nextInt(21) + 40));
                        item.addItemOption(new ItemOption(103, Utils.nextInt(21) + 40));
                        item.addItemOption(new ItemOption(14, Utils.nextInt(11) + 5));
                        item.addItemOption(new ItemOption(5, Utils.nextInt(21) + 40));
                        if (Utils.nextInt(9) != 0) {
                            item.addItemOption(new ItemOption(93, Utils.nextInt(5) + 3));
                        }
                        break;
                    case ItemName.CAN_DAU_VAN_NGU_SAC:
                    case ItemName.NGOC_THO:
                    case ItemName.LUOI_HAI_THAN_CHET:
                    case ItemName.CANH_DOI_DRACULA:
                        item.addItemOption(new ItemOption(50, Utils.nextInt(3, 20)));
                        item.addItemOption(new ItemOption(77, Utils.nextInt(3, 20)));
                        item.addItemOption(new ItemOption(103, Utils.nextInt(3, 20)));
                        if (Utils.nextInt(9) != 0) {
                            item.addItemOption(new ItemOption(93, Utils.nextInt(3, 7)));
                        }
                        break;
                    default:
                        break;
                }
                int expire = rw.getExpire();
                if (expire != -1) {
                    item.addItemOption(new ItemOption(93, expire));
                }
                player.boxCrackBall.add(item);
                ds.writeShort(itemTemplate.iconID);
            }
            ds.flush();
            player.service.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void resultVQTD() {
        int q = this.quantity;
        if (typePrice == 0) {
            int index = player.getIndexBagById(ItemName.VE_QUAY_NGOC_VANG);
            Item vequay = null;
            if (index != -1) {
                vequay = player.itemBag[index];
            }
            if (vequay == null || vequay.quantity < q) {
                index = player.getIndexBagById(ItemName.THOI_VANG);
                Item tv = null;
                if (index != -1) {
                    tv = player.itemBag[index];
                }
                if (tv == null || tv.quantity < q) {
                    player.service.serverMessage("Bạn không đủ thỏi vàng");
                    return;
                }
            }
//            player.pointThoiVang += q;
//            player.isChangePoint = true;
            player.removeItem(index, q);
        }

        Server server = DragonBall.getInstance().getServer();
        for (int i = 0; i < quantity; i++) {
            if (true) {
                newVQTD.reward(player);
                continue;
            }
            Reward rw = rd.next();
            ItemTemplate itemTemplate = server.iTemplates.get(rw.getItemId());
            int itemId = -1;
            if (itemTemplate.id == ItemName.NGOC_RONG_LOC_PHAT_1_SAO) {
                itemId = Utils.nextInt(ItemName.NGOC_RONG_LOC_PHAT_1_SAO, ItemName.NGOC_RONG_LOC_PHAT_1_SAO + 6);
            } else {
                itemId = itemTemplate.id;
            }
            Item item = new Item(itemId);
            item.setDefaultOptions();
            int itemQuantity;
            if (item.id == 190) {
                itemQuantity = Utils.nextInt(5000000, 20000000);
            } else {
                itemQuantity = rw.getQuantity();
            }
            item.quantity = itemQuantity;
            switch (item.id) {
                case ItemName.CAI_TRANG_ARALE:
                case ItemName.CAI_TRANG_GATCHAN:
                case ItemName.CAI_TRANG_OBOTCHAMAN:
                    item.options.clear();
                    item.addItemOption(new ItemOption(77, Utils.nextInt(10, 20)));
                    item.addItemOption(new ItemOption(103, Utils.nextInt(10, 20)));
                    item.addItemOption(new ItemOption(50, Utils.nextInt(10, 20)));
                    item.addItemOption(new ItemOption(101, Utils.nextInt(30, 100)));
                    if (Utils.nextInt(100) >= 10) {
                        item.addItemOption(new ItemOption(93, Utils.nextInt(1, 3)));
                    }
                    break;
                case ItemName.THU_CUOI_VE_SAU:
                    item.options.clear();
                    item.addItemOption(new ItemOption(77, Utils.nextInt(2, 4)));
                    item.addItemOption(new ItemOption(103, Utils.nextInt(2, 4)));
                    item.addItemOption(new ItemOption(50, Utils.nextInt(2, 4)));
                    if (Utils.nextInt(100) >= 10) {
                        item.addItemOption(new ItemOption(93, Utils.nextInt(1, 7)));
                    }
                    break;
                case ItemName.BELLA_QUYEN_RU:
                    item.options.clear();
                    item.addItemOption(new ItemOption(77, Utils.nextInt(24, 30)));
                    item.addItemOption(new ItemOption(103, Utils.nextInt(24, 30)));
                    item.addItemOption(new ItemOption(50, Utils.nextInt(24, 30)));
                    if (Utils.nextInt(100) >= 10) {
                        item.addItemOption(new ItemOption(93, Utils.nextInt(1, 7)));
                    }
                    break;
                case ItemName.FIONA_NGOT_NGAO:
                    item.options.clear();
                    item.addItemOption(new ItemOption(77, Utils.nextInt(24, 30)));
                    item.addItemOption(new ItemOption(103, Utils.nextInt(24, 30)));
                    item.addItemOption(new ItemOption(50, Utils.nextInt(24, 30)));
                    item.addItemOption(new ItemOption(94, Utils.nextInt(10, 15)));
                    if (Utils.nextInt(100) >= 10) {
                        item.addItemOption(new ItemOption(93, Utils.nextInt(1, 7)));
                    }
                    break;
                case ItemName.PET_KE_XAM_LANG:
                    item.options.clear();
                    item.addItemOption(new ItemOption(77, Utils.nextInt(7, 12)));
                    item.addItemOption(new ItemOption(103, Utils.nextInt(7, 12)));
                    item.addItemOption(new ItemOption(50, Utils.nextInt(7, 12)));
                    if (Utils.nextInt(100) >= 10) {
                        item.addItemOption(new ItemOption(93, Utils.nextInt(1, 7)));
                    }
                    break;
                case ItemName.HAO_QUANG_RUC_RO:
                    item.options.clear();
                    item.addItemOption(new ItemOption(77, Utils.nextInt(7, 13)));
                    item.addItemOption(new ItemOption(103, Utils.nextInt(7, 13)));
                    item.addItemOption(new ItemOption(50, Utils.nextInt(7, 13)));
                    item.addItemOption(new ItemOption(95, Utils.nextInt(5, 10)));
                    if (Utils.nextInt(100) >= 10) {
                        item.addItemOption(new ItemOption(93, Utils.nextInt(1, 7)));
                    }
                    break;
                case ItemName.CANH_THIEN_THAN:
                    item.options.clear();
                    item.addItemOption(new ItemOption(77, Utils.nextInt(7, 13)));
                    item.addItemOption(new ItemOption(103, Utils.nextInt(7, 13)));
                    item.addItemOption(new ItemOption(50, Utils.nextInt(7, 13)));
                    item.addItemOption(new ItemOption(94, Utils.nextInt(3, 7)));
                    if (Utils.nextInt(100) >= 10) {
                        item.addItemOption(new ItemOption(93, Utils.nextInt(1, 7)));
                    }
                    break;
                case ItemName.LINH_THU_AC_MA:
                    item.options.clear();
                    item.addItemOption(new ItemOption(77, Utils.nextInt(3, 6)));
                    item.addItemOption(new ItemOption(103, Utils.nextInt(3, 6)));
                    item.addItemOption(new ItemOption(50, Utils.nextInt(3, 6)));
                    if (Utils.nextInt(100) >= 10) {
                        item.addItemOption(new ItemOption(93, Utils.nextInt(1, 7)));
                    }
                    break;
                default:
                    break;
            }
            int expire = rw.getExpire();
            if (expire != -1) {
                item.addItemOption(new ItemOption(93, Utils.nextInt(1, 7)));
            }
            player.boxCrackBall.add(item);
        }
        player.service.serverMessage("Bạn đã nhận được phần thưởng, hãy nhận vật phẩm và tiếp tục tham gia vòng quay");
//            player.currentGoldbarPaid +=  100;
//        try {
//            // quay 100 lan
//            EventTieuThoiVangData ev = new EventTieuThoiVangData();
//            ev.setPlayerName(player.name);
//            ev.setPoint(100);
//            ev.setCreateDate(Instant.now());
//            ev.setModifyDate(Instant.now());
//            GameRepository.getInstance().eventTieuThoiVang.save(ev);
//        } catch (Exception e) { 
//            logger.error("id player " + player.id + ": " + 50 + "tv chua dc cong");
//        }
        Optional<VongQuayThuongDeData> resp = GameRepository.getInstance().eventVQTD.findFirstByName(player.id);
        if (resp.isPresent()) {
            VongQuayThuongDeData ev = resp.get();
            int point = ev.getPoint() + quantity;
            ev.setPoint(point);
            player.numberVongQuay = point;
            GameRepository.getInstance().eventVQTD.save(ev);
        } else {
            try {
                VongQuayThuongDeData ev = new VongQuayThuongDeData();
                ev.setPlayerName(player.id);
                ev.setPoint(quantity);
                ev.setCreateDate(Instant.now());
                ev.setModifyDate(Instant.now());
                GameRepository.getInstance().eventVQTD.save(ev);
                player.numberVongQuay = quantity;
                player.service.serverMessage("Bạn đã nhận được phần thưởng, hãy nhận vật phẩm và tiếp tục tham gia vòng quay");
            } catch (Exception e) {
                
                logger.error("Save error VQTD " + player.name + " so luong: " + quantity);
            }
        }
    }
}
