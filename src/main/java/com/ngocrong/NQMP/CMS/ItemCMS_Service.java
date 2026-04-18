/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.CMS;

import _HunrProvision.HoangAnhDz;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.item.ItemTemplate;
import com.ngocrong.model.Npc;
import com.ngocrong.network.Message;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.shop.Shop;
import com.ngocrong.shop.Tab;
import com.ngocrong.user.Player;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class ItemCMS_Service {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ItemCMS_Service.class);

    public static void ShowCMSItem(Player player, Npc npc) {
        List<ItemCMS> list = ItemCMS.findList(player.getSession().user.getId());
        try {
            Shop shop = new Shop();
            shop.setTypeShop(4);
            Tab tab = new Tab();
            tab.setTabName("Vật\n phẩm");
            tab.setType(1);
            for (Item item : ItemCMS.CMS_to_Item(list)) {
                ItemTemplate template = new ItemTemplate();
                template.id = (short) item.id;
                template.isNew = item.template.isNew;
                template.isPreview = item.template.isPreview;
                template.head = item.template.head;
                template.body = item.template.body;
                template.leg = item.template.leg;
                template.part = item.template.part;
                template.reason = "Vật phẩm từ Hệ thống";
                template.options = new ArrayList<>();
                if (item.quantity >= 1000) {
                    template.options.add(new ItemOption(171, item.quantity / 1000));
                } else if (item.quantity > 1) {
                    template.options.add(new ItemOption(31, item.quantity));
                }
                for (ItemOption option : item.options) {
                    template.options.add(option);
                }
                tab.addItem(template);
            }
            shop.addTab(tab);
            player.shop = shop;
            player.shop.setNpc(npc);
            player.service.viewShop(shop);
            isView(player.id);
            player.itemsCMS = new ArrayList<>();
            player.itemsCMS.addAll(list);
        } catch (Exception ex) {
            
            logger.error(ex);
        }
    }
    private static final String UPDATE_STATUS_QUERY = "UPDATE nr_items_cms SET status = 0 WHERE pId = ? and status != 0";

    public static void isView(int playerId) {
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(UPDATE_STATUS_QUERY);
            ps.setInt(1, playerId);
            try {
                int updated = ps.executeUpdate();
            } catch (Exception e) {
                
                logger.error("Lỗi khi update status cho player " + playerId, e);
            } finally {
                ps.close();
            }
        } catch (Exception ex) {
            
            logger.error("Lỗi kết nối khi update status cho player " + playerId, ex);
        }
    }

    public static ItemCMS getItembyIndex(Player player, int index) {
        if (player.itemsCMS == null || player.itemsCMS.isEmpty() || player.itemsCMS.size() < index) {
            return null;
        }
        return player.itemsCMS.get(index);
    }
    private static final String DELETE_QUERY = "delete from nr_items_cms where id = ? and pId = ?";

    public static void removeItemCMS(int playerID, int itemID) {
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(DELETE_QUERY);
            ps.setInt(1, itemID);
            ps.setInt(2, playerID);
            try {
                int updated = ps.executeUpdate();
            } catch (Exception e) {
                
                logger.error("Lỗi khi DELETE_QUERYcho player " + playerID, e);
            } finally {
                ps.close();
            }
        } catch (Exception ex) {
            
            logger.error("Lỗi kết nối khi DELETE_QUERY cho player " + playerID, ex);
        }

    }

    public static void getItem(Player player, int index) {
        if (player == null) {
            return;
        }
        if (System.currentTimeMillis() - player.lastActionCMS <= 1000) {
            player.service.sendThongBao("Đợi 1 lát...");
            return;
        }

        ItemCMS cms = getItembyIndex(player, index);
        if (cms == null) {
            player.service.sendThongBao("Có lỗi xảy ra");
            return;
        }
        player.lastActionCMS = System.currentTimeMillis();
        Item item = new Item(cms.itemId);
        item.quantity = cms.quantity;
        item.setDefaultOptions();
        if (player.addItem(item)) {
            if (item.quantity > 1) {
                player.service.serverMessage2(
                        String.format("Bạn nhận được %d %s", item.quantity, item.template.name));
            } else {
                player.service.serverMessage2(String.format("Bạn nhận được %s", item.template.name));
            }
            removeItemCMS(player.getSession().user.getId(), cms.id);
        } else {
            player.service.serverMessage2("Hành trang đã đầy, cần 1 ô trống trong hành trang để nhận vật phẩm");
        }

        ShowCMSItem(player, player.shop.getNpc());
    }

    public static void deleteItem(Player player, int index) {
        ItemCMS cms = getItembyIndex(player, index);
        if (cms == null) {
            player.service.sendThongBao("Có lỗi xảy ra");
            return;
        }
        removeItemCMS(player.getSession().user.getId(), cms.id);
        ShowCMSItem(player, player.shop.getNpc());
    }

    public static void getAll(Player player) {
        if (player == null) {
            return;
        }
        if (System.currentTimeMillis() - player.lastActionCMS <= 1000) {
            player.service.sendThongBao("Đợi 1 lát...");
            return;
        }
        for (ItemCMS cms : player.itemsCMS) {
            Item item = new Item(cms.itemId);
            item.quantity = cms.quantity;
            item.setDefaultOptions();
            if (player.addItem(item)) {
                if (item.quantity > 1) {
                    player.service.serverMessage2(
                            String.format("Bạn nhận được %d %s", item.quantity, item.template.name));
                } else {
                    player.service.serverMessage2(String.format("Bạn nhận được %s", item.template.name));
                }
                removeItemCMS(player.getSession().user.getId(), cms.id);
            } else {
                player.service.serverMessage2("Hành trang đã đầy, cần 1 ô trống trong hành trang để nhận vật phẩm");
            }

        }
        ShowCMSItem(player, player.shop.getNpc());
    }

}
