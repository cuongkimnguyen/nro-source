package com.ngocrong.model;

import com.ngocrong.map.tzone.Zone;
import com.ngocrong.item.Item;
import lombok.Data;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@Data
public class History {

    private static Logger logger = Logger.getLogger(History.class);

    public static final byte COMMON = 0;
    public static final byte LOGIN = 1;
    public static final byte LOGOUT = 2;
    public static final byte TRADE_SEND = 3;
    public static final byte TRADE_RECEIVE = 4;
    public static final byte BUY_ITEM = 5;
    public static final byte SELL_ITEM = 6;
    public static final byte THROW_ITEM = 7;
    public static final byte PICK_ITEM = 8;

    private int playerID;
    private byte type;
    private ArrayList<Item> items;
    private JSONObject befores;
    private JSONObject afters;
    private JSONObject maps;
    private String extras;
    private long time;

    public History(int id, byte type) {
        this.playerID = id;
        this.type = type;
        this.extras = "";
        this.befores = new JSONObject();
        this.afters = new JSONObject();
        this.maps = new JSONObject();
        this.items = new ArrayList<>();
        this.time = System.currentTimeMillis();

    }

    public void setBefores(long gold, int gem, int gemLock) {
        try {
            befores.put("gold", gold);
            befores.put("gem", gem);
            befores.put("gem_lock", gemLock);
        } catch (JSONException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setAfters(long gold, int gem, int gemLock) {
        try {
            afters.put("gold", gold);
            afters.put("gem", gem);
            afters.put("gem_lock", gemLock);
        } catch (JSONException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void setZone(Zone zone) {
        try {
            maps.put("mapID", zone.map.mapID);
            maps.put("zoneID", zone.zoneID);
        } catch (JSONException ex) {
            
            logger.error("set zone err", ex);
        }
    }

    public void setSeller(int id, int receive) {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("seller", id);
            jObj.put("receive", receive);
            extras = jObj.toString();
        } catch (JSONException ex) {
            
            logger.error("set seller err", ex);
        }
    }

    public void setBuyer(int id, int price) {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("buyer", id);
            jObj.put("price", price);
            extras = jObj.toString();
        } catch (JSONException ex) {
            
            logger.error("set buyer err", ex);
        }
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void setPartner(int id, String name) {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("partner_id", id);
            jObj.put("partner_name", name);
            extras = jObj.toString();
        } catch (JSONException ex) {
            
            logger.error("set buyer err", ex);
        }
    }

    public void save() {
    }
}
