package com.ngocrong.shop;

import _HunrProvision.HoangAnhDz;
import com.ngocrong.consts.ItemName;
import com.ngocrong.data.ConsignmentItemData;
import com.ngocrong.item.ItemOption;
import com.ngocrong.consts.Cmd;
import com.ngocrong.item.Item;
import com.ngocrong.network.Message;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.server.Language;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;
import com.ngocrong.network.FastDataOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Consignment {

    private static final Logger logger = Logger.getLogger(Consignment.class);
    private static Consignment instance;

    public HashMap<Integer, ConsignmentItem> items;

    public ReadWriteLock lock = new ReentrantReadWriteLock();

    private static final String[] TAB = {"Trang\nbị", "Sự\nkiện", "Linh\ntinh", "Đang\nbán"};

    public static Consignment getInstance() {
        if (instance == null) {
            instance = new Consignment();
        }
        return instance;
    }

    public void init() {
        items = new HashMap<>();
        ConsignmentItemStatus[] statuses = new ConsignmentItemStatus[]{
            ConsignmentItemStatus.ON_SALE,
            ConsignmentItemStatus.SOLD,
            ConsignmentItemStatus.EXPIRED_24H
        };
        List<ConsignmentItemData> dataList = GameRepository.getInstance().consignmentItem.findByStatusIn(statuses);
        for (ConsignmentItemData data : dataList) {
            ConsignmentItem item = new ConsignmentItem(data);
            if(!HoangAnhDz.isItemRemove(item.item.template.id))
            {items.put(item.id, item);}
        }
        Utils.setScheduled(this::updateNewDay, 86400, 0, 0);
    }

    public void addItem(Player player, int index, int quantity, int price) {

        lock.writeLock().lock();
        try {
            if (player.isTrading()) {
                return;
            }
            Item item = player.itemBag[index];
            if (item == null) {
                return;
            }
            if (quantity < 1 && item.template.isUpToUp) {
                return;
            }
            if (quantity < 0 && !item.template.isUpToUp) {
                return;
            }
//            if (items.values().stream()
//                    .filter(i -> i.sellerId == player.id
//                    && (i.status == ConsignmentItemStatus.ON_SALE || i.status == ConsignmentItemStatus.SOLD))
//                    .count() > 10) {
//                player.service.sendThongBao("Chỉ có thể kí tối đa 10 vật phẩm");
//                return;
//            }
            long gold = 200000000;
            if (player.gold < gold) {
                player.service.sendThongBao("Bạn không đủ vàng để ký");
                return;
            }
            if (price < 1) {
                player.service.sendThongBao("Giá kí phải từ 1 thỏi vàng trở lên");
                return;
            }
            if (price > 5000) {
                player.service.sendThongBao("Chỉ có thể ký tối đa 5000 thỏi vàng");
                return;
            }
            if (item == null) {
                return;
            }
            if (!item.isCanSaleToConsignment()) {
                player.service.sendThongBao("Không thể kí vật phẩm này");
                return;
            }
            if (item.quantity < quantity) {
                player.service.sendThongBao("Số lượng không đủ");
                return;
            }
            ConsignmentItemData data = new ConsignmentItemData(player, item, quantity, price);
            GameRepository.getInstance().consignmentItem.save(data);
            ConsignmentItem itemConsignment = new ConsignmentItem(data);
            items.put(itemConsignment.id, itemConsignment);
            player.subGold(gold);
            player.removeItem(index, quantity);
            showShop(player);
            player.service.sendThongBao("Treo bán vật phẩm thành công");
            player.saveData();
        } catch (Exception ex) {
            
            //System.err.println("Error at 132");
            logger.error("failed!", ex);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void getItem(Player player, int id) {
        ConsignmentItem itemConsignment = items.get(id);
        if (itemConsignment == null || itemConsignment.sellerId != player.id) {
            return;
        }
        itemConsignment.lock.lock();
        try {
            if (player.isBagFull()) {
                player.service.sendThongBao(Language.ME_BAG_FULL);
                return;
            }
            if (itemConsignment.status == ConsignmentItemStatus.ON_SALE || itemConsignment.status == ConsignmentItemStatus.EXPIRED_24H) {
                Item item = itemConsignment.item.clone();
                if (player.addItem(item)) {
                    itemConsignment.setStatus(ConsignmentItemStatus.CANCEL_SALE);
                    itemConsignment.receiveTime = new Timestamp(System.currentTimeMillis());
                    player.service.sendThongBao("Hủy bán vật phẩm thành công");
                    showShop(player);
                    player.saveData();
                }
            } else if (itemConsignment.status == ConsignmentItemStatus.SOLD) {
                itemConsignment.setStatus(ConsignmentItemStatus.RECEIVED_MONEY);
                Item item = new Item(ItemName.THOI_VANG);
                item.setDefaultOptions();
                item.quantity = (int) ((double) itemConsignment.price * 0.98);
                player.addItem(item);
                player.service.sendThongBao(String.format("Bạn nhận được %d thỏi vàng", itemConsignment.price));
                showShop(player);
                player.saveData();
            }
        } finally {
            itemConsignment.lock.unlock();
        }
    }

    public void buyItem(Player player, int itemId) {

        ConsignmentItem itemConsignment = items.get(itemId);
        if (itemConsignment == null || itemConsignment.sellerId == player.id) {
            return;
        }
        itemConsignment.lock.lock();
        try {
            if (player.isBagFull()) {
                player.service.sendThongBao(Language.ME_BAG_FULL);
                return;
            }
            if (itemConsignment.status != ConsignmentItemStatus.ON_SALE) {
                player.service.sendThongBao("Vật phẩm đã được bán, hủy bán hoặc hết hạn sử dụng");
                return;
            }
            int day = itemConsignment.item.getExpiry();
            if (day != -1 && day <= 0) {
                player.service.sendThongBao("Vật phẩm đã hết hạn sử dụng");
                return;
            }
            Item item = player.getThoiVangCanTrade();
            if (item == null || item.quantity < itemConsignment.price) {
                player.service.sendThongBao("Bạn không đủ thỏi vàng");
                return;
            }
            player.removeItem(item.indexUI, itemConsignment.price);
            player.addItem(itemConsignment.item.clone());
            itemConsignment.setStatus(ConsignmentItemStatus.SOLD);
            itemConsignment.buyerId = player.id;
            itemConsignment.buyTime = new Timestamp(System.currentTimeMillis());
            if (item.quantity > 1) {
                player.service.sendThongBao(String.format("Bạn nhận được x%d %s", itemConsignment.item.quantity, itemConsignment.item.template.name));
            } else {
                player.service.sendThongBao(String.format("Bạn nhận được %s", itemConsignment.item.template.name));
            }
            showShop(player);
            player.saveData();

        } finally {
            itemConsignment.lock.unlock();
        }
    }

    public byte getTab(ConsignmentItem itemMarket, Player player) {
        Item item = itemMarket.item;
        if (itemMarket.sellerId == player.getId()) {
            if (itemMarket.status == ConsignmentItemStatus.ON_SALE || itemMarket.status == ConsignmentItemStatus.SOLD || itemMarket.status == ConsignmentItemStatus.EXPIRED_24H) {
                return 3;
            }
        }
        if (item.template.type < 5) {
            return 0;
        }
        if (item.template.type == 27) {
            return 1;
        } else {
            return 2;
        }
    }

    public void showShop(Player player) {
        try {
            HashMap<Integer, HashMap<Integer, ConsignmentItem>> consignmentItemHashMap = new HashMap<>();
            consignmentItemHashMap.put(0, new HashMap<>());
            consignmentItemHashMap.put(1, new HashMap<>());
            consignmentItemHashMap.put(2, new HashMap<>());
            consignmentItemHashMap.put(3, new HashMap<>());
            HashMap<Integer, ConsignmentItem> itemMarketHashMap = getItems();
            for (ConsignmentItem itemMarket : itemMarketHashMap.values()) {
                if (itemMarket.status == ConsignmentItemStatus.ON_SALE
                        || (itemMarket.sellerId == player.getId() && (itemMarket.status == ConsignmentItemStatus.SOLD || itemMarket.status == ConsignmentItemStatus.EXPIRED_24H))) {
                    byte tab = getTab(itemMarket, player);
                    consignmentItemHashMap.get((int) tab).put(itemMarket.id, itemMarket);
                }
            }
            Message mss = new Message(Cmd.SHOP);
            FastDataOutputStream ds = mss.writer();
            ds.writeByte(2);
            ds.writeBoolean(false);
            ds.writeByte(consignmentItemHashMap.size());
            int tab_index = 0;
            for (HashMap<Integer, ConsignmentItem> tab : consignmentItemHashMap.values()) {
                ds.writeUTF(TAB[tab_index++]);
                ds.writeInt(tab.size());
                for (ConsignmentItem consignmentItem : tab.values()) {
                    ds.writeShort(consignmentItem.item.template.id);
                    ds.writeInt(consignmentItem.id);
                    ds.writeInt(consignmentItem.price);
                    ds.writeInt(consignmentItem.item.quantity);

                    ds.writeByte(consignmentItem.status == ConsignmentItemStatus.ON_SALE || consignmentItem.status == ConsignmentItemStatus.EXPIRED_24H ? 0 : 1);
//                    //System.err.println("Status : " + consignmentItem.status + " value : " + (consignmentItem.status == ConsignmentItemStatus.ON_SALE ? 0 : 1));
                    ds.writeBoolean(consignmentItem.sellerId == player.getId());
                    ds.writeByte(consignmentItem.item.options.size());
                    for (ItemOption option : consignmentItem.item.options) {
                        ds.writeShort(option.optionTemplate.id);
                        ds.writeInt(option.param);
                    }
                    ds.writeBoolean(false);
                    ds.writeBoolean(false);
                }
            }
            ds.flush();
            player.service.sendMessage(mss);
        } catch (IOException ex) {
            
            //System.err.println("Error at 131");
            logger.error("failed!", ex);
        }
    }

    public HashMap<Integer, ConsignmentItem> getItems() {
        lock.readLock().lock();
        try {
            return items;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void saveData() {
        //lock.readLock().lock();
        try {
            for (ConsignmentItem itemConsignment : items.values()) {
                itemConsignment.saveData();
            }
        } finally {
            //lock.readLock().unlock();
        }
    }

    public void checkExpiredItemsByTime() {
        lock.writeLock().lock();
        try {
            long currentTime = System.currentTimeMillis();
            // 48 tiếng = 48 * 60 * 60 * 1000 = 172800000 milliseconds
            long expiry48Hours = 24 * 60 * 60 * 1000L;

            for (ConsignmentItem itemConsignment : items.values()) {
                if (itemConsignment.status == ConsignmentItemStatus.ON_SALE) {
                    itemConsignment.lock.lock();
                    try {
                        // Double check status vì có thể đã thay đổi
                        if (itemConsignment.status == ConsignmentItemStatus.ON_SALE
                                && itemConsignment.createTime != null) {

                            long timeSinceCreated = currentTime - itemConsignment.createTime.getTime();
                            if (timeSinceCreated > expiry48Hours) {
                                itemConsignment.setStatus(ConsignmentItemStatus.EXPIRED_24H);
                                logger.info("Item ID " + itemConsignment.id + " expired after 48 hours - Seller ID: "
                                        + itemConsignment.sellerId + ", Price: " + itemConsignment.price + " gold");
                            }
                        }
                    } finally {
                        itemConsignment.lock.unlock();
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error in checkExpiredItemsByTime", ex);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateNewDay() {
        lock.writeLock().lock();
        try {
            for (ConsignmentItem itemConsignment : items.values()) {
                if (itemConsignment.status == ConsignmentItemStatus.ON_SALE) {
                    itemConsignment.lock.lock();
                    try {
                        if (itemConsignment.status == ConsignmentItemStatus.ON_SALE) {
                            for (ItemOption option : itemConsignment.item.options) {
                                if (option.optionTemplate.id == 93) {
                                    option.param--;
                                    if (option.param <= 0) {
                                        itemConsignment.setStatus(ConsignmentItemStatus.EXPIRED);
                                        break;
                                    }
                                }
                            }
                        }
                    } finally {
                        itemConsignment.lock.unlock();
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

}
