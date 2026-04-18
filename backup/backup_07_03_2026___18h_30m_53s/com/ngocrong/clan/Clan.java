package com.ngocrong.clan;

import com.google.gson.Gson;
import com.ngocrong.data.ClanData;
import com.ngocrong.map.Treasure;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.user.Player;
import com.ngocrong.item.Item;
import com.ngocrong.map.Barrack;
import com.ngocrong.map.tzone.ClanTerritory;
import com.ngocrong.server.SessionManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Clan {

    private static final Logger logger = Logger.getLogger(Clan.class);
    public static final byte LEVEL_MAX = 20;

    public int id;
    public String name;
    public String leaderName;
    public int leaderID;
    public String slogan;
    public byte imgID;
    public byte level;
    public int clanPoint;
    public long powerPoint;
    public byte maxMember;
    public Timestamp createTime;
    public List<ClanMember> members;
    public ArrayList<ClanMessage> messages;
    public Barrack barrack;
    public Treasure treasure;
    public ClanTerritory clanTerritory;
    public int remainingTimesCanEnterTreasure;
    public long lastTimeEnterTreasure = -1;
    public Item[] items;
    public String abbreviationName;
    public long lastTimeRename;
    public ReadWriteLock lockMember = new ReentrantReadWriteLock();
    public ReadWriteLock lockMessage = new ReentrantReadWriteLock();

    public Clan(ClanData data) {
        id = data.id;
        name = data.name;
        abbreviationName = data.abbreviation;
        slogan = data.slogan;
        leaderID = data.leaderId;
        leaderName = data.leaderName;
        level = data.level;
        imgID = data.imageId;
        clanPoint = data.clanPoint;
        powerPoint = data.powerPoint;
        maxMember = data.maxMember;
        createTime = data.createTime;
        initBoxItem();
        JSONArray json = new JSONArray(data.itemBox);
        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            Item item = new Item();
            item.load(obj);
            items[item.indexUI] = item;
        }
        messages = new ArrayList<>();
        members = new ArrayList<>();
    }

    public List<Player> getPlayersOnline() {
        List<Player> list = new ArrayList<>();
        for (ClanMember mem : this.getMembers()) {
            Player _player = SessionManager.findChar(mem.playerID);
            if (_player != null) {
                list.add(_player);
            }
        }
        return list;
    }

    public void initBoxItem() {
        int size = this.level - 1;
        if (size < 0) {
            size = 0;
        }
        this.items = new Item[size];
    }

    public void addMessage(ClanMessage message) {
        if (message.isNewMessage) {
            lockMessage.writeLock().lock();
            try {
                if (messages.size() > 30) {
                    messages.remove(29);
                }
                messages.add(0, message);
            } finally {
                lockMessage.writeLock().unlock();
            }
        }
        List<ClanMember> clanMembers = getMembers();
        synchronized (clanMembers) {
            for (ClanMember clanMember : clanMembers) {
                Player _c = SessionManager.findChar(clanMember.playerID);
                if (_c != null) {
                    if (_c.service != null) {
                        _c.service.clanMessage(message);
                    }
                }
            }
        }
    }

    public void clanUpdate(byte type, ClanMember clanMember, int deleteId) {
        List<ClanMember> clanMembers = getMembers();
        synchronized (clanMembers) {
            for (ClanMember mem : clanMembers) {
                Player _c = SessionManager.findChar(mem.playerID);
                if (_c != null) {
                    _c.service.clanUpdate(type, clanMember, deleteId);
                }
            }
        }
    }

    public void addClanRewardForMember(int star) {
        List<ClanMember> list = getMembers();
        long now = System.currentTimeMillis();
        for (ClanMember mem : list) {
            if (mem != null) {
                ClanReward r = new ClanReward();
                r.setStar(star);
                // Các sao nhận trực tiếp: 1 và 4, các sao còn lại phải đến NPC nhận
                boolean direct = star == 1 || star == 4 || star == 8 || star == 9 || star == 10;
                r.setCanBeReceivedDirectly(direct);
                r.setNumberOfTimesReceived(24);
                r.setTimeDelay(3600000);
                r.setTimeStart(now);
                r.setTimeEnd(now + (24 * 3600000));
                mem.addClanReward(r);
            }
        }
    }

    public void clanInfo() {
        List<ClanMember> clanMembers = getMembers();
        synchronized (clanMembers) {
            for (ClanMember mem : clanMembers) {
                Player _c = SessionManager.findChar(mem.playerID);
                if (_c != null) {
                    _c.service.clanInfo();
                }
            }
        }
    }

    public void addMember(ClanMember clanMember) {
        lockMember.writeLock().lock();
        try {
            members.add(clanMember);
        } finally {
            lockMember.writeLock().unlock();
        }
    }

    public List<ClanMember> getMembers() {
        lockMember.writeLock().lock();
        try {
            for (ClanMember clanMember : members) {
                Player _player = SessionManager.findChar(clanMember.playerID);
                if (_player != null && _player.info != null) {
                    clanMember.head = _player.getHead();
                    clanMember.body = _player.getBody();
                    clanMember.leg = _player.getLeg();
                    clanMember.powerPoint = _player.info.power;
                }
            }
            return members;
        } finally {
            lockMember.writeLock().unlock();
        }
    }

    public int getNumberMember() {
        lockMember.readLock().lock();
        try {
            return members.size();
        } finally {
            lockMember.readLock().unlock();
        }
    }

    public ClanMember getMember(int charID) {
        lockMember.readLock().lock();
        try {
            return members.stream().filter(m -> m.playerID == charID).findFirst().orElse(null);
        } finally {
            lockMember.readLock().unlock();
        }
    }

    public void updateBag() {
        lockMember.writeLock().lock();
        try {
            for (ClanMember clanMember : members) {
                Player _player = SessionManager.findChar(clanMember.playerID);
                if (_player != null && _player.info != null) {
                    _player.updateSkin();
                    _player.zone.service.updateBag(_player);
                }
            }
        } finally {
            lockMember.writeLock().unlock();
        }
    }

    public void deleteMessage(int messageID) {
        messages.stream().filter(m -> m.id == messageID).findFirst()
                .ifPresent(existMessage -> messages.remove(existMessage));
    }

    public boolean deleteMember(ClanMember clanMember) {
        lockMember.writeLock().lock();
        try {
            GameRepository.getInstance().clanMember.deleteById(clanMember.id);
            members.removeIf(m -> m.playerID == clanMember.playerID);
            clanUpdate((byte) 1, null, clanMember.playerID);
            return true;
        } catch (Exception exception) {
            return false;
        } finally {
            lockMember.writeLock().unlock();
        }
    }

    public ClanMessage getMessage(int messageID) {
        lockMessage.writeLock().lock();
        try {
            return messages.stream().filter(m -> m.id == messageID).findFirst().orElse(null);
        } finally {
            lockMessage.writeLock().unlock();
        }
    }

    public ClanMessage getMessage(int playerId, byte type) {
        return messages.stream().filter(m -> m.playerId == playerId && m.type == type).findAny().orElse(null);
    }

    public void sort() {
        try {
            members.sort(new Comparator<ClanMember>() {
                @Override
                public int compare(ClanMember m1, ClanMember m2) {
                    return Integer.compare(m1.role, m2.role);
                }
            });
        } catch (Exception ex) {
            
            logger.error("sort", ex);
        }
    }

    public void saveData() {
        lockMember.readLock().lock();
        try {
            List<Item> itemList = new ArrayList<>();
            for (Item item : items) {
                if (item != null) {
                    itemList.add(item);
                }
            }
            GameRepository.getInstance().clan.saveData(id, abbreviationName, leaderID, leaderName, slogan, imgID, level, clanPoint, powerPoint, maxMember, new Gson().toJson(itemList));
            for (ClanMember member : members) {
                member.saveData();
            }
        } catch (Exception ex) {
            
            logger.error("saveData", ex);
        } finally {
            lockMember.readLock().unlock();
        }
    }

    public String getStrCreateTime() {
        Date date = new Date(createTime.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        return formatter.format(date);
    }
}
