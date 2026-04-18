package com.ngocrong.bot;

import _HunrProvision.ConfigStudio;
import com.ngocrong.data.BotConfigData;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemTemplate;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Server;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.json.JSONArray;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class VirtualBot_SoSinh extends VirtualBot {

    public static int BotSS;
    int currentEquipLevel = -1;
    long lastCheckEquip = 0;

    public VirtualBot_SoSinh(String name) {
        super(name);
        this.gender = (byte) Utils.nextInt(3);
        this.info.power = Utils.nextInt(1500000, 1500005);
        this.setInfo(Utils.nextInt(3000, 5000), Long.MAX_VALUE, Utils.nextInt(50, 70), 10000, 1);
        
        setHeadByGender();
        applyBotConfig();
        currentEquipLevel = getCurrentEquipLevel();
        timeSpawn = System.currentTimeMillis();
    }
    
    private void applyBotConfig() {
        try {
            List<BotConfigData> allConfigs = GameRepository.getInstance().botConfig.findAll();
            
            for (BotConfigData config : allConfigs) {
                String botnameJsonStr = config.getBotnameJson();
                if (botnameJsonStr == null || botnameJsonStr.trim().isEmpty()) {
                    continue;
                }
                
                JSONArray botnameArray;
                try {
                    botnameArray = new JSONArray(botnameJsonStr);
                } catch (Exception e) {
                    continue;
                }
                
                boolean found = false;
                
                for (int i = 0; i < botnameArray.length(); i++) {
                    String configBotname = botnameArray.optString(i, "");
                    if (configBotname.equals(this.name)) {
                        JSONArray usedNamesArray = new JSONArray();
                        if (config.getUsedBotnames() != null && !config.getUsedBotnames().isEmpty()) {
                            usedNamesArray = new JSONArray(config.getUsedBotnames());
                        }
                        
                        boolean alreadyUsed = false;
                        for (int j = 0; j < usedNamesArray.length(); j++) {
                            if (usedNamesArray.optString(j, "").equals(this.name)) {
                                alreadyUsed = true;
                                break;
                            }
                        }
                        
                        if (!alreadyUsed) {
                            found = true;
                        }
                        break;
                    }
                }
                
                if (found) {
                    int totalRatio = (config.ratioCaitrang != null ? config.ratioCaitrang : 0) + 
                                   (config.ratioTrangbi != null ? config.ratioTrangbi : 0);
                    
                    if (totalRatio <= 0) {
                        equipItemsByPower();
                        return;
                    }
                    
                    int random = Utils.nextInt(totalRatio);
                    int caitrangRatio = config.ratioCaitrang != null ? config.ratioCaitrang : 0;
                    
                    if (random < caitrangRatio && config.getCaitrangIdJson() != null && !config.getCaitrangIdJson().isEmpty()) {
                        try {
                            String caitrangJsonStr = config.getCaitrangIdJson().trim();
                            if (caitrangJsonStr.startsWith("[")) {
                                JSONArray caitrangArray = new JSONArray(caitrangJsonStr);
                                if (caitrangArray.length() > 0 && !caitrangArray.isNull(0)) {
                                    int caitrangId = caitrangArray.getInt(0);
                                    equipCaiTrang(caitrangId);
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            equipItemsByPower();
                            return;
                        }
                    }
                    
                    equipItemsByPower();
                    return;
                }
            }
            
            equipItemsByPower();
        } catch (Exception e) {
            equipItemsByPower();
        }
    }
    
    private void equipCaiTrang(int caitrangId) {
        try {
            Server server = DragonBall.getInstance().getServer();
            if (server == null || server.iTemplates == null) {
                return;
            }
            
            ItemTemplate template = server.iTemplates.get(caitrangId);
            if (template == null || template.type != Item.TYPE_HAIR) {
                equipItemsByPower();
                return;
            }
            
            if (itemBody == null) {
                itemBody = new Item[16];
            }
            
            Item caiTrangItem = new Item(caitrangId);
            caiTrangItem.setDefaultOptions();
            caiTrangItem.isNhapThe = false;
            itemBody[5] = caiTrangItem;
            itemBody[5].indexUI = 5;
            
            info.setInfo();
            setBotFlags();
            updateSkin();
            forceSetBodyLeg();
            
            if (service != null) {
                service.setItemBody();
            }
            if (zone != null && zone.service != null) {
                zone.service.updateBody((byte) -1, this);
            }
        } catch (Exception e) {
            equipItemsByPower();
        }
    }
    
    private void setHeadByGender() {
        try {
            short[][] hair = new short[][]{
                {64, 30, 31}, {9, 29, 32}, {6, 27, 28}
            };
            short head = hair[this.gender][Utils.nextInt(hair[this.gender].length)];
            
            Method setHeadMethod = Player.class.getDeclaredMethod("setHead", short.class);
            setHeadMethod.setAccessible(true);
            setHeadMethod.invoke(this, head);
            
            Field headDefaultField = Player.class.getDeclaredField("headDefault");
            headDefaultField.setAccessible(true);
            headDefaultField.setShort(this, head);
        } catch (Exception e) {
            try {
                short head = (short) (this.gender == 0 ? 64 : this.gender == 1 ? 9 : 6);
                Field headField = Player.class.getDeclaredField("head");
                Field headDefaultField = Player.class.getDeclaredField("headDefault");
                headField.setAccessible(true);
                headDefaultField.setAccessible(true);
                headField.setShort(this, head);
                headDefaultField.setShort(this, head);
            } catch (Exception e2) {
            }
        }
    }
    
    private int getCurrentEquipLevel() {
        if (itemBody != null && itemBody[0] != null) {
            return itemBody[0].template.level;
        }
        return -1;
    }
    
    private void equipItemsByPower() {
        try {
            Server server = DragonBall.getInstance().getServer();
            if (server == null || server.iTemplates == null || server.iTemplates.isEmpty()) {
                return;
            }
            
            if (itemBody == null) {
                itemBody = new Item[16];
            }
            
            int bestLevel = -1;
            int[] bestItems = new int[5];
            
            for (int level = 10; level >= 0; level--) {
                int[] items = findItemSetByLevel(server, level, gender, info.power);
                if (items[0] > 0 && items[1] > 0 && items[2] > 0 && items[3] > 0) {
                    bestLevel = level;
                    bestItems = items;
                    break;
                }
            }
            
            if (bestLevel >= 0) {
                if (!canEquipItemSet(server, bestItems, info.power)) {
                    return;
                }
                
                if (bestItems[0] > 0) {
                    Item bodyItem = new Item(bestItems[0]);
                    bodyItem.setDefaultOptions();
                    itemBody[0] = bodyItem;
                    itemBody[0].indexUI = 0;
                }
                
                if (bestItems[1] > 0) {
                    Item legItem = new Item(bestItems[1]);
                    legItem.setDefaultOptions();
                    itemBody[1] = legItem;
                    itemBody[1].indexUI = 1;
                }
                
                if (bestItems[2] > 0) {
                    Item gloveItem = new Item(bestItems[2]);
                    gloveItem.setDefaultOptions();
                    itemBody[2] = gloveItem;
                    itemBody[2].indexUI = 2;
                }
                
                if (bestItems[3] > 0) {
                    Item shoeItem = new Item(bestItems[3]);
                    shoeItem.setDefaultOptions();
                    itemBody[3] = shoeItem;
                    itemBody[3].indexUI = 3;
                }
                
                if (bestItems[4] > 0) {
                    Item headItem = new Item(bestItems[4]);
                    headItem.setDefaultOptions();
                    headItem.isNhapThe = false;
                    itemBody[5] = headItem;
                    itemBody[5].indexUI = 5;
                }
                
                info.setInfo();
                setBotFlags();
                updateSkin();
                forceSetBodyLeg();
                
                if (service != null) {
                    service.setItemBody();
                }
                if (zone != null && zone.service != null) {
                    zone.service.updateBody((byte) -1, this);
                }
            }
        } catch (Exception e) {
        }
    }
    
    private int[] findItemSetByLevel(Server server, int level, int gender, long power) {
        int[] items = new int[5];
        
        for (ItemTemplate item : server.iTemplates.values()) {
            if (item.id == 693 || item.id == 691 || item.id == 692) {
                continue;
            }
            
            boolean genderMatch = (item.gender == gender || item.gender == 3);
            
            if (item.level == level && item.require <= power && genderMatch) {
                if (item.type == 0 && items[0] == 0) {
                    items[0] = item.id;
                } else if (item.type == 1 && items[1] == 0) {
                    items[1] = item.id;
                } else if (item.type == 2 && items[2] == 0) {
                    items[2] = item.id;
                } else if (item.type == 3 && items[3] == 0) {
                    items[3] = item.id;
                } else if (item.type == 5 && items[4] == 0) {
                    items[4] = item.id;
                }
            }
            
            if (items[0] > 0 && items[1] > 0 && items[2] > 0 && items[3] > 0) {
                break;
            }
        }
        
        return items;
    }
    
    private void setBotFlags() {
        try {
            Field isNhapTheField = Player.class.getDeclaredField("isNhapThe");
            Field isMonkeyField = Player.class.getDeclaredField("isMonkey");
            isNhapTheField.setAccessible(true);
            isMonkeyField.setAccessible(true);
            isNhapTheField.setBoolean(this, false);
            isMonkeyField.setBoolean(this, false);
        } catch (Exception e) {
        }
    }
    
    private boolean canEquipItemSet(Server server, int[] itemIds, long power) {
        for (int itemId : itemIds) {
            if (itemId > 0) {
                ItemTemplate template = server.iTemplates.get(itemId);
                if (template != null && template.require > power) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void forceSetBodyLeg() {
        if (itemBody == null) {
            return;
        }
        
        try {
            Field bodyField = Player.class.getDeclaredField("body");
            Field legField = Player.class.getDeclaredField("leg");
            Field headField = Player.class.getDeclaredField("head");
            bodyField.setAccessible(true);
            legField.setAccessible(true);
            headField.setAccessible(true);
            
            if (itemBody[0] != null && itemBody[0].template != null) {
                bodyField.setShort(this, itemBody[0].template.part);
            }
            if (itemBody[1] != null && itemBody[1].template != null) {
                legField.setShort(this, itemBody[1].template.part);
            }
            
            if (itemBody[5] != null && itemBody[5].template != null && !itemBody[5].isNhapThe) {
                if (itemBody[5].template.part != -1) {
                    headField.setShort(this, itemBody[5].template.part);
                } else if (itemBody[5].template.head > 0) {
                    headField.setShort(this, itemBody[5].template.head);
                }
            } else {
                short[][] hair = new short[][]{
                    {64, 30, 31}, {9, 29, 32}, {6, 27, 28}
                };
                short head = hair[this.gender][Utils.nextInt(hair[this.gender].length)];
                headField.setShort(this, head);
            }
        } catch (Exception e) {
        }
    }
    
    @Override
    public void update() {
        this.info.mp = this.info.mpFull = Long.MAX_VALUE;

        if (System.currentTimeMillis() - lastCheckEquip >= 5000) {
            checkAndUpgradeEquip();
            lastCheckEquip = System.currentTimeMillis();
        }

        updateNextMap();
        if (zone != null) {
            if (this.zone.map.mapID != mapNext) {
                TMap map = MapManager.getInstance().getMap(mapNext);
                if (map != null) {
                    Zone zone = map.getMinPlayerZone();
                    this.zone.leave(this);
                    zone.enter(this);
                }
            }
        } else {
            TMap map = MapManager.getInstance().getMap(mapNext);
            if (map != null) {
                Zone zone = map.getMinPlayerZone();
                zone.enter(this);
            }
        }

        attack();
    }
    
    private void checkAndUpgradeEquip() {
        try {
            Server server = DragonBall.getInstance().getServer();
            if (server == null || server.iTemplates == null || server.iTemplates.isEmpty()) {
                return;
            }
            
            int bestLevel = -1;
            int[] bestItems = new int[5];
            
            for (int level = 10; level >= 0; level--) {
                if (level <= currentEquipLevel) {
                    break;
                }
                int[] items = findItemSetByLevel(server, level, gender, info.power);
                if (items[0] > 0 && items[1] > 0 && items[2] > 0 && items[3] > 0) {
                    bestLevel = level;
                    bestItems = items;
                    break;
                }
            }
            
            if (bestLevel > currentEquipLevel) {
                if (!canEquipItemSet(server, bestItems, info.power)) {
                    return;
                }
                
                if (itemBody == null) {
                    itemBody = new Item[16];
                }
                
                if (bestItems[0] > 0) {
                    Item bodyItem = new Item(bestItems[0]);
                    bodyItem.setDefaultOptions();
                    itemBody[0] = bodyItem;
                    itemBody[0].indexUI = 0;
                }
                
                if (bestItems[1] > 0) {
                    Item legItem = new Item(bestItems[1]);
                    legItem.setDefaultOptions();
                    itemBody[1] = legItem;
                    itemBody[1].indexUI = 1;
                }
                
                if (bestItems[2] > 0) {
                    Item gloveItem = new Item(bestItems[2]);
                    gloveItem.setDefaultOptions();
                    itemBody[2] = gloveItem;
                    itemBody[2].indexUI = 2;
                }
                
                if (bestItems[3] > 0) {
                    Item shoeItem = new Item(bestItems[3]);
                    shoeItem.setDefaultOptions();
                    itemBody[3] = shoeItem;
                    itemBody[3].indexUI = 3;
                }
                
                if (bestItems[4] > 0) {
                    Item headItem = new Item(bestItems[4]);
                    headItem.setDefaultOptions();
                    headItem.isNhapThe = false;
                    itemBody[5] = headItem;
                    itemBody[5].indexUI = 5;
                }
                
                currentEquipLevel = bestLevel;
                info.setInfo();
                setBotFlags();
                updateSkin();
                forceSetBodyLeg();
                
                if (service != null) {
                    service.setItemBody();
                }
                if (zone != null && zone.service != null) {
                    zone.service.updateBody((byte) -1, this);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    void setBag1() {
        byte[] bag = new byte[]{19, 20, 21, 22, 105, 106};
        this.clanID = Utils.nextInt(0, 100);
        this.setBag(bag[Utils.nextInt(bag.length)]);
        this.name = Utils.getAbbre("[" + ConfigStudio.SLOGAN_BOTSOSINH + "]") + this.name;
    }

    @Override
    public boolean isBoss() {
        return false;
    }

    @Override
    public boolean isHuman() {
        return true;
    }

    public void updateNextMap() {
        long time = System.currentTimeMillis() - this.timeSpawn;
        int oldMapNext = this.mapNext;
        if (time > 700_000 && this.clanID == -1) {
            setBag1();
        }
        if (time < 50_000) {
            this.mapNext = this.gender == 0 ? 0 : this.gender == 1 ? 7 : 14;

        } else if (time < 100_000) {
            this.mapNext = this.gender == 0 ? 1 : this.gender == 1 ? 8 : 15;

        } else if (time < 150_000) {
            this.mapNext = this.gender == 0 ? 2 : this.gender == 1 ? 9 : 16;

        } else if (time < 300_000) {
            this.mapNext = this.gender == 0 ? 9 : this.gender == 1 ? 16 : 2;
            if (oldMapNext != this.mapNext) {
                this.setInfo(Utils.nextInt(18000, 22000), Utils.nextInt(18000, 22000), Utils.nextInt(500, 700), 10000, 1);
            }

        } else if (time < 450_000) {
            this.mapNext = this.gender == 0 ? 16 : this.gender == 1 ? 2 : 9;

        } else if (time < 500_000) {
            this.mapNext = this.gender == 0 ? 3 : this.gender == 1 ? 11 : 17;
            if (oldMapNext != this.mapNext) {
                this.setInfo(Utils.nextInt(18000, 22000), Utils.nextInt(18000, 22000), Utils.nextInt(500, 700), 10000, 1);
            }

        } else if (time < 600_000) {
            this.mapNext = this.gender == 0 ? 18 : this.gender == 1 ? 4 : 12;
            if (oldMapNext != this.mapNext) {
                this.setInfo(Utils.nextInt(18000, 22000), Utils.nextInt(18000, 22000), Utils.nextInt(200, 500), 10000, 1);
            }

        } else if (time < 650_000) {
            this.mapNext = this.gender == 0 ? 18 : this.gender == 1 ? 4 : 12;
            if (oldMapNext != this.mapNext) {
                this.setInfo(Utils.nextInt(18000, 22000), Utils.nextInt(18000, 22000), Utils.nextInt(500, 800), 10000, 1);
            }

        } else if (time < 700_000) {
            this.mapNext = 27;
            if (oldMapNext != this.mapNext) {
                this.setInfo(Utils.nextInt(18000, 22000), Utils.nextInt(18000, 22000), Utils.nextInt(500, 1000), 10000, 1);
            }

        } else if (time < 800_000) {
            this.mapNext = 31;

        } else if (time < 900_000) {
            this.mapNext = 35;

        } else if (time < 1_000_000) {
            this.mapNext = 30;
            if (oldMapNext != this.mapNext) {
                this.setInfo(this.info.hpFull, 3000000, 1000, 10000, 1);
            }

        } else if (time < 1_100_000) {
            this.mapNext = 34;

        } else if (time < 1_150_000) {
            this.mapNext = 38;

        } else if (time < 1_200_000) {
            this.mapNext = 6;
            if (oldMapNext != this.mapNext) {
                this.setInfo(this.info.hpFull, 3000000, 3000, 10000, 1);
            }

        } else if (time < 1_300_000) {
            this.mapNext = 10;

        } else if (time < 1_400_000) {
            this.mapNext = 19;

        } else {
            this.mapNext = -1;
            this.zone.leave(this);
            this.close();
        }
    }

}
