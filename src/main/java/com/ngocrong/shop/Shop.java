package com.ngocrong.shop;

import com.ngocrong.consts.ItemName;
import com.ngocrong.item.ItemOption;
import com.ngocrong.item.ItemTemplate;
import com.ngocrong.model.Npc;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Server;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillBook;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import lombok.Data;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

@Data
public class Shop implements Cloneable {

    private static Logger logger = Logger.getLogger(Shop.class);
    private static ArrayList<Shop> shops = new ArrayList<>();

    public static void addShop(Shop shop) {
        shops.add(shop);
    }

    public static Shop getShopSkill(Player _c) {
        Shop shop = getShop(16);
        Shop shopSkill = new Shop();
        shopSkill.setTypeShop(1);
        for (int i = 0; i < 3; i++) {
            Tab tab1 = shop.tabs.get(i);
            Tab tab2 = new Tab();
            tab2.setTabName(tab1.getTabName());
            tab2.setType(tab1.getType());
            ArrayList<ItemTemplate> list = tab1.getListItem(_c);
            for (ItemTemplate item : list) {
                SkillBook book = Skills.getSkillBook(item.id);
                if (book != null) {
                    int skillID = book.id;
                    int level = book.level;
                    Skill skill = _c.getSkillByID(skillID);
                    if (skill == null || skill.point < level) {
                        if (item.powerRequire == 0) {
                            skill = Skills.getSkill((byte) skillID, (byte) level);
                            item.powerRequire = skill.powerRequire;
                        }
                        tab2.addItem(item);
                    }
                }
            }
            shopSkill.addTab(tab2);
        }
        return shopSkill;
    }

    public static Shop getShop(int npcId) {
        if (npcId == 7 || npcId == 8 || npcId == 9) {
            npcId = -1;
        }
        for (Shop shop : shops) {
            if (shop.npcId == npcId) {
                return shop;
            }
        }
        return null;
    }

    private ArrayList<Tab> tabs = new ArrayList<>();
    private int typeShop;
    private String tableName;
    private int npcId;
    private Npc npc;
    public boolean canBuyMore = true;

    public void init() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Statement stmt = MySQLConnect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM " + this.tableName + " order by tab asc ,id DESC");
            while (res.next()) {
                try {
                    int itemId = res.getInt("item_id");
                    long gold = 0;
                    int gem = 0;
                    short iconSpec = 0;
                    int buySpec = 0;
                    if (typeShop == 0) {
                        gold = res.getLong("buy_gold");
                        gem = res.getInt("buy_gem");
                    }
                    if (typeShop == 3) {
                        iconSpec = res.getShort("icon_special");
                        buySpec = res.getInt("buy_special");
                    }
                    boolean isNew = res.getBoolean("new");
                    boolean isPreview = res.getBoolean("preview");
                    int tab = res.getInt("tab");
                    int expired = res.getInt("expired");
                    ArrayList<ItemOption> options = new ArrayList<>();
                    String optionsCheck = res.getString("options");
                    if (optionsCheck != null) {
                        JSONArray jArr = new JSONArray(res.getString("options"));
                        for (int i = 0; i < jArr.length(); i++) {
                            JSONObject obj = jArr.getJSONObject(i);
                            int id = obj.getInt("id");
                            int param = obj.getInt("param");
                            options.add(new ItemOption(id, param));
                        }
                    }
                    ItemTemplate template = server.iTemplates.get(itemId);
                    //System.err.println("tempID : " + itemId);
                    if (template != null) {
                        if (template.id == ItemName.THOI_VANG) {
                            template.resalePrice = gold;
                        }
                        template.buyGold = gold;
                        template.buyGem = gem;
                        template.iconSpec = iconSpec;
                        template.buySpec = buySpec;
                        template.isNew = isNew;
                        template.isPreview = isPreview;
                        template = template.clone();
                        template.setOptions(expired, options);
                        addItem(template, tab);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            res.close();
            stmt.close();
        } catch (Exception ex) {
            
            //System.err.println("Error at 130");
            logger.error("failed!", ex);
        }
    }

    private void addItem(ItemTemplate item, int tab) {
        this.tabs.get(tab).addItem(item);
    }

    public void addTab(Tab tab) {
        this.tabs.add(tab);
    }

    public ArrayList<ItemTemplate> getListItem(Player _c) {
        ArrayList<ItemTemplate> list = new ArrayList<>();
        for (Tab tab : this.tabs) {
            list.addAll(tab.getListItem(_c));
        }
        return list;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
