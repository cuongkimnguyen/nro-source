package com.ngocrong.clan;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngocrong.data.ClanMemberData;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.user.Player;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ClanMember {

    public long id;
    public int playerID;
    public String name;
    public short head, leg, body;
    public byte role;
    public long powerPoint;
    public int donate;
    public int receiveDonate;
    public int clanPoint;
    public int currClanPoint;
    public Timestamp joinTime;
    public ArrayList<KeyValue<String, Item>> items;
    public ArrayList<ClanReward> rewards;

    public ClanMember() {
        items = new ArrayList<>();
        rewards = new ArrayList<>();
    }

    public ClanMember(ClanMemberData data) {
        id = data.id;
        name = data.name;
        playerID = data.playerId;
        role = data.role;
        head = data.head;
        body = data.body;
        leg = data.leg;
        donate = data.donate;
        receiveDonate = data.receiveDonate;
        clanPoint = data.clanPoint;
        currClanPoint = data.currentPoint;
        powerPoint = data.powerPoint;
        joinTime = data.joinTime;
        rewards = new Gson().fromJson(data.clanReward, new TypeToken<List<ClanReward>>() {
        }.getType());
        if (rewards == null) {
            rewards = new ArrayList<>();
        }
        items = new ArrayList<>();
    }

    public String getStrJoinTime() {
        Date date = new Date(joinTime.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        return formatter.format(date);
    }

    public boolean isLeader() {
        return this.role == 0;
    }

    public int getNumberOfDaysJoinClan() {
        long now = System.currentTimeMillis();
        return (int) (((now / 1000) - joinTime.getTime()) / 60 / 60 / 24);
    }

    public int getNumberOfRewardsCanBeReceived() {
        int n = 0;
        for (ClanReward r : rewards) {
            if (!r.isCanBeReceivedDirectly()) {
                n++;
            }
        }
        return n;
    }

    public int getIndexReward(int star) {
        int index = -1;
        int i = 0;
        for (ClanReward r : rewards) {
            if (r.getStar() == star) {
                index = i;
                break;
            }
            i++;
        }
        return index;
    }

    public ClanReward getClanReward(int star) {
        int index = getIndexReward(star);
        if (index < 0 || index >= rewards.size()) {
            return null;
        }
        return rewards.get(index);
    }

    public void addClanReward(ClanReward r) {
        int index = getIndexReward(r.getStar());
        if (index != -1) {
            rewards.set(index, r);
        } else {
            rewards.add(r);
        }
    }

    public void addItem(Item item, String giver) {
        items.add(new KeyValue<String, Item>(giver, item));
    }

    public void receiveItem(Player _player) {
        for (KeyValue<String, Item> keyValue : this.items) {
            String name = keyValue.key;
            Item item = keyValue.value;
            this.receiveDonate++;
            _player.addItem(item);
            _player.service.sendThongBao(String.format("Bạn nhận được %s từ %s", item.template.name, name));
        }
        items.clear();
    }

    public void saveData() {
        try {
            long now = System.currentTimeMillis();
            ArrayList<ClanReward> rewards = new ArrayList<>();
            for (ClanReward r : this.rewards) {
                if (r.getTimeEnd() > now) {
                    rewards.add(r);
                }
            }
            GameRepository.getInstance().clanMember.saveData(id, role, head, body, leg,
                    powerPoint, donate, receiveDonate, clanPoint,
                    currClanPoint, new Gson().toJson(rewards));
        } finally {
        }
    }
}
