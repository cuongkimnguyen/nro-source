/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.CMS;

import _HunrProvision.HoangAnhDz;
import static com.ngocrong.collection.Card.templates;
import com.ngocrong.collection.CardTemplate;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.item.ItemTemplate;
import com.ngocrong.server.SQLStatement;
import com.ngocrong.server.mysql.MySQLConnect;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Administrator
 */
public class ItemCMS {

    private static final Logger logger = Logger.getLogger(ItemCMS.class);

    public int id;
    public int pId;
    public int itemId;
    public int quantity;
    public boolean isNew;
    private static final String FIND_ITEMS_QUERY = "SELECT * FROM nr_items_cms WHERE pId = ? ORDER BY `id` DESC";

    public static List<ItemCMS> findList(int playerId) {
        List<ItemCMS> items = new ArrayList<>();
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(FIND_ITEMS_QUERY);
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    items.add(createFromResultSet(rs, playerId));
                }
            } catch (Exception e) {
                
                e.printStackTrace();

            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception ex) {
            
            logger.error("Lỗi khi lấy danh sách item CMS cho player " + playerId, ex);
        }
        return items;
    }

    public static List<Item> CMS_to_Item(List<ItemCMS> list) {
        List<Item> items = new ArrayList<>();
        for (ItemCMS item : list) {
            Item x = new Item(item.itemId);
            x.template.isNew = item.isNew;
            x.quantity = item.quantity;
            items.add(x);
        }
        return items;
    }

    private static ItemCMS createFromResultSet(ResultSet rs, int playerId) throws SQLException {
        ItemCMS cms = new ItemCMS();
        cms.id = rs.getInt("id");
        cms.pId = playerId;
        cms.itemId = rs.getInt("itemid");
        cms.quantity = rs.getInt("quantity");
        cms.isNew = rs.getByte("status") == 1;
        return cms;
    }
}
