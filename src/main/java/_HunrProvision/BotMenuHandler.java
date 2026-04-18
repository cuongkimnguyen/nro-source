package _HunrProvision;

import _HunrProvision.HoangAnhDz;
import com.ngocrong.bot.VirtualBot_SoSinh;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.NpcName;
import com.ngocrong.data.BotConfigData;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BotMenuHandler {

    public static final int CMD_Menu_Bot = 2100;
    public static BotMenuHandler instance = new BotMenuHandler();

    public static BotMenuHandler gI() {
        return instance;
    }

    public void showMenu(Player player) {
        player.menus.clear();
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Tạo Bot", 1));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Tạo Bot Cold", 9));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Xóa Bot", 2));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Xóa Bot Khu hiện tại", 3));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Xem danh sách Bot", 4));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Reset tên Bot đã dùng", 5));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Clear Name Bot", 8));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Thêm tên Bot", 6));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Thêm ID Cải Trang", 7));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Set Tỉ Lệ Cải Trang", 10));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Set Tỉ Lệ Trang Bị", 11));
        player.service.openUIConfirm(NpcName.CON_MEO, "Quản lý Bot", player.getPetAvatar(), player.menus);
    }

    public void perform(int idAction, Player player, Object... p) {
        switch (idAction) {
            case 1: {
                showCreateBotInput(player);
                break;
            }
            case 9: {
                showCreateBotColdInput(player);
                break;
            }
            case 2: {
                showDeleteBotMenu(player);
                break;
            }
            case 3: {
                deleteBotsInCurrentZone(player);
                break;
            }
            case 4: {
                showBotList(player);
                break;
            }
            case 5: {
                resetUsedBotNames(player);
                break;
            }
            case 6: {
                showAddBotNameInput(player);
                break;
            }
            case 7: {
                showAddCaiTrangInput(player);
                break;
            }
            case 10: {
                showSetRatioCaitrangInput(player);
                break;
            }
            case 11: {
                showSetRatioTrangbiInput(player);
                break;
            }
            case 8: {
                clearUsedBotNames(player);
                break;
            }
            case 20: {
                int quantity = ((Integer) p[0]);
                deleteBots(player, quantity);
                break;
            }
        }
    }


    private void showCreateBotInput(Player player) {
        try {
            com.ngocrong.model.InputDialog inputDlg = new com.ngocrong.model.InputDialog(
                com.ngocrong.consts.CMDTextBox.CREATE_BOT_NEW,
                "Tạo Bot",
                new com.ngocrong.model.TextField("Nhập số lượng bot muốn tạo (1-1000):", com.ngocrong.model.TextField.INPUT_TYPE_NUMERIC)
            );
            inputDlg.setService(player.service);
            java.lang.reflect.Field field = Player.class.getDeclaredField("inputDlg");
            field.setAccessible(true);
            field.set(player, inputDlg);
            inputDlg.show();
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi mở input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showCreateBotColdInput(Player player) {
        try {
            com.ngocrong.model.InputDialog inputDlg = new com.ngocrong.model.InputDialog(
                com.ngocrong.consts.CMDTextBox.CREATE_BOT_COLD,
                "Tạo Bot Cold",
                new com.ngocrong.model.TextField("Nhập số lượng bot cold muốn tạo:", com.ngocrong.model.TextField.INPUT_TYPE_NUMERIC)
            );
            inputDlg.setService(player.service);
            java.lang.reflect.Field field = Player.class.getDeclaredField("inputDlg");
            field.setAccessible(true);
            field.set(player, inputDlg);
            inputDlg.show();
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi mở input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAddBotNameInput(Player player) {
        try {
            com.ngocrong.model.InputDialog inputDlg = new com.ngocrong.model.InputDialog(
                com.ngocrong.consts.CMDTextBox.ADD_BOT_NAME,
                "Thêm Tên Bot",
                new com.ngocrong.model.TextField("Nhập tên bot (cách nhau bởi dấu phẩy):", com.ngocrong.model.TextField.INPUT_TYPE_ANY)
            );
            inputDlg.setService(player.service);
            java.lang.reflect.Field field = Player.class.getDeclaredField("inputDlg");
            field.setAccessible(true);
            field.set(player, inputDlg);
            inputDlg.show();
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi mở input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAddCaiTrangInput(Player player) {
        try {
            com.ngocrong.model.InputDialog inputDlg = new com.ngocrong.model.InputDialog(
                com.ngocrong.consts.CMDTextBox.ADD_CAITRANG_ID,
                "Thêm ID Cải Trang",
                new com.ngocrong.model.TextField("Nhập ID cải trang (cách nhau bởi dấu phẩy):", com.ngocrong.model.TextField.INPUT_TYPE_ANY)
            );
            inputDlg.setService(player.service);
            java.lang.reflect.Field field = Player.class.getDeclaredField("inputDlg");
            field.setAccessible(true);
            field.set(player, inputDlg);
            inputDlg.show();
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi mở input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSetRatioCaitrangInput(Player player) {
        try {
            com.ngocrong.model.InputDialog inputDlg = new com.ngocrong.model.InputDialog(
                com.ngocrong.consts.CMDTextBox.SET_RATIO_CAITRANG,
                "Set Tỉ Lệ Cải Trang",
                new com.ngocrong.model.TextField("Nhập tỉ lệ cải trang (0-100):", com.ngocrong.model.TextField.INPUT_TYPE_NUMERIC)
            );
            inputDlg.setService(player.service);
            java.lang.reflect.Field field = Player.class.getDeclaredField("inputDlg");
            field.setAccessible(true);
            field.set(player, inputDlg);
            inputDlg.show();
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi mở input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSetRatioTrangbiInput(Player player) {
        try {
            com.ngocrong.model.InputDialog inputDlg = new com.ngocrong.model.InputDialog(
                com.ngocrong.consts.CMDTextBox.SET_RATIO_TRANGBI,
                "Set Tỉ Lệ Trang Bị",
                new com.ngocrong.model.TextField("Nhập tỉ lệ trang bị (0-100):", com.ngocrong.model.TextField.INPUT_TYPE_NUMERIC)
            );
            inputDlg.setService(player.service);
            java.lang.reflect.Field field = Player.class.getDeclaredField("inputDlg");
            field.setAccessible(true);
            field.set(player, inputDlg);
            inputDlg.show();
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi mở input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showDeleteBotMenu(Player player) {
        player.menus.clear();
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Xóa 1 Bot", 20, 1));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Xóa 5 Bot", 20, 5));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Xóa 10 Bot", 20, 10));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Xóa 20 Bot", 20, 20));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Xóa 50 Bot", 20, 50));
        player.menus.add(new KeyValue(CMD_Menu_Bot, "Xóa tất cả Bot", 20, -1));
        player.service.openUIConfirm(NpcName.CON_MEO, "Chọn số lượng Bot muốn xóa", player.getPetAvatar(), player.menus);
    }

    public void createBots(Player player, int quantity) {
        int created = 0;
        int totalAvailableNames = 0;

        List<BotConfigData> allConfigs = GameRepository.getInstance().botConfig.findAll();
        if (allConfigs.isEmpty()) {
            player.service.sendThongBao("Không tìm thấy config bot trong database");
            return;
        }

        for (BotConfigData config : allConfigs) {
            if (config.getBotnameJson() == null || config.getBotnameJson().isEmpty()) {
                continue;
            }
            
            try {
                String botnameJsonStr = config.getBotnameJson();
                if (botnameJsonStr == null || botnameJsonStr.trim().isEmpty()) {
                    continue;
                }
                
                JSONArray botnameArray;
                try {
                    botnameJsonStr = botnameJsonStr.trim();
                    if (!botnameJsonStr.startsWith("[")) {
                        continue;
                    }
                    botnameArray = new JSONArray(botnameJsonStr);
                } catch (Exception e) {
                    player.service.sendThongBao("Lỗi parse botnameJson (id=" + config.getId() + "): " + e.getMessage());
                    continue;
                }
                
                JSONArray usedNamesArray = new JSONArray();
                if (config.getUsedBotnames() != null && !config.getUsedBotnames().isEmpty()) {
                    try {
                        usedNamesArray = new JSONArray(config.getUsedBotnames());
                    } catch (Exception e) {
                        usedNamesArray = new JSONArray();
                    }
                }
                
                List<String> availableNames = new ArrayList<>();
                for (int j = 0; j < botnameArray.length(); j++) {
                    String configBotname = botnameArray.optString(j, "");
                    if (configBotname.isEmpty()) {
                        continue;
                    }
                    
                    boolean alreadyUsed = false;
                    for (int k = 0; k < usedNamesArray.length(); k++) {
                        if (usedNamesArray.optString(k, "").equals(configBotname)) {
                            alreadyUsed = true;
                            break;
                        }
                    }
                    
                    if (!alreadyUsed) {
                        availableNames.add(configBotname);
                    }
                }
                
                totalAvailableNames += availableNames.size();
                
                if (availableNames.isEmpty()) {
                    continue;
                }
                
                int toCreate = Math.min(quantity - created, availableNames.size());
                for (int i = 0; i < toCreate; i++) {
                    int randomIndex = Utils.nextInt(availableNames.size());
                    String botName = availableNames.remove(randomIndex);
                    
                    usedNamesArray.put(botName);
                    config.setUsedBotnames(usedNamesArray.toString());
                    GameRepository.getInstance().botConfig.save(config);
                    
                    VirtualBot_SoSinh bot = new VirtualBot_SoSinh(botName);
                    bot.setLocation(0, -1);
                    HoangAnhDz.lastCreateBot = System.currentTimeMillis();
                    created++;
                }
                
                if (created >= quantity) {
                    break;
                }
            } catch (Exception e) {
                player.service.sendThongBao("Lỗi khi tạo bot: " + e.getMessage());
                continue;
            }
        }

        if (created > 0) {
            player.service.sendThongBao("Đã tạo " + created + " Bot");
        } else {
            if (totalAvailableNames == 0) {
                player.service.sendThongBao("Tất cả tên bot đã được sử dụng. Dùng lệnh Clear Name Bot để tái sử dụng");
            } else {
                player.service.sendThongBao("Không tạo được bot. Tổng tên có sẵn: " + totalAvailableNames);
            }
        }
        if (created < quantity && totalAvailableNames > 0) {
            player.service.sendThongBao("Chỉ tạo được " + created + "/" + quantity + " bot. Còn " + totalAvailableNames + " tên có sẵn");
        }
    }

    private void deleteBots(Player player, int quantity) {
        int deleted = 0;
        List<Player> botsToDelete = new ArrayList<>();

        for (TMap map : MapManager.getInstance().maps.values()) {
            if (map == null || map.zones == null) {
                continue;
            }
            for (Zone zone : map.zones) {
                if (zone == null || zone.players == null) {
                    continue;
                }
                synchronized (zone.players) {
                    for (Player p : zone.players) {
                        if (p != null && p instanceof VirtualBot_SoSinh && !p.equals(player)) {
                            botsToDelete.add(p);
                            if (quantity > 0 && botsToDelete.size() >= quantity) {
                                break;
                            }
                        }
                    }
                }
                if (quantity > 0 && botsToDelete.size() >= quantity) {
                    break;
                }
            }
            if (quantity > 0 && botsToDelete.size() >= quantity) {
                break;
            }
        }

        for (Player bot : botsToDelete) {
            if (bot.zone != null) {
                bot.zone.leave(bot);
                bot.close();
                deleted++;
            }
        }

        player.service.sendThongBao("Đã xóa " + deleted + " Bot");
    }

    private void deleteBotsInCurrentZone(Player player) {
        if (player.zone == null) {
            return;
        }

        int deleted = 0;
        List<Player> botsToDelete = new ArrayList<>();

        synchronized (player.zone.players) {
            for (Player p : player.zone.players) {
                if (p != null && p instanceof VirtualBot_SoSinh && !p.equals(player)) {
                    botsToDelete.add(p);
                }
            }
        }

        for (Player bot : botsToDelete) {
            player.zone.leave(bot);
            bot.close();
            deleted++;
        }

        player.service.sendThongBao("Đã xóa " + deleted + " Bot trong khu hiện tại");
    }

    private void showBotList(Player player) {
        List<com.ngocrong.top.TopInfo> botList = new ArrayList<>();

        for (TMap map : MapManager.getInstance().maps.values()) {
            if (map == null || map.zones == null) {
                continue;
            }
            for (Zone zone : map.zones) {
                if (zone == null || zone.players == null) {
                    continue;
                }
                synchronized (zone.players) {
                    for (Player p : zone.players) {
                        if (p != null && p instanceof VirtualBot_SoSinh) {
                            try {
                                java.lang.reflect.Field headField = Player.class.getDeclaredField("head");
                                headField.setAccessible(true);
                                short headValue = headField.getShort(p);
                                
                                java.lang.reflect.Field bodyField = Player.class.getDeclaredField("body");
                                bodyField.setAccessible(true);
                                short bodyValue = bodyField.getShort(p);
                                
                                java.lang.reflect.Field legField = Player.class.getDeclaredField("leg");
                                legField.setAccessible(true);
                                short legValue = legField.getShort(p);
                                
                                com.ngocrong.top.TopInfo topInfo = new com.ngocrong.top.TopInfo();
                                topInfo.playerID = p.id;
                                topInfo.name = p.name;
                                topInfo.head = headValue;
                                topInfo.body = bodyValue;
                                topInfo.leg = legValue;
                                topInfo.score = p.info.power;
                                topInfo.info = String.format("Sức mạnh: %s", com.ngocrong.util.Utils.currencyFormat(p.info.power));
                                topInfo.info2 = String.format("Map: %d", zone.map.mapID);
                                topInfo.isReward = false;
                                botList.add(topInfo);
                                
                                if (botList.size() >= 50) {
                                    break;
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }
                }
                if (botList.size() >= 50) {
                    break;
                }
            }
            if (botList.size() >= 50) {
                break;
            }
        }

        if (botList.isEmpty()) {
            player.service.sendThongBao("Không có bot nào trong game");
            return;
        }

        try {
            com.ngocrong.network.Message msg = new com.ngocrong.network.Message(com.ngocrong.consts.Cmd.TOP);
            com.ngocrong.network.FastDataOutputStream ds = msg.writer();
            ds.writeByte(com.ngocrong.top.Top.TYPE_NONE);
            ds.writeUTF("Danh sách Bot");
            ds.writeByte((byte) Math.min(botList.size(), 50));
            
            int currentRank = 1;
            for (int i = 0; i < Math.min(botList.size(), 50); i++) {
                com.ngocrong.top.TopInfo info = botList.get(i);
                ds.writeInt(currentRank++);
                ds.writeInt(info.playerID);
                ds.writeShort(info.head);
                ds.writeShort(info.body);
                ds.writeShort(info.leg);
                ds.writeUTF(info.name);
                ds.writeUTF(info.info != null ? info.info : "");
                ds.writeUTF(info.info2 != null ? info.info2 : "");
            }
            
            ds.flush();
            player.service.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi hiển thị danh sách bot: " + e.getMessage());
        }
    }

    private void resetUsedBotNames(Player player) {
        try {
            List<BotConfigData> allConfigs = GameRepository.getInstance().botConfig.findAll();
            int resetCount = 0;

            for (BotConfigData config : allConfigs) {
                if (config.getIsUsed() != null && config.getIsUsed()) {
                    config.setIsUsed(false);
                    GameRepository.getInstance().botConfig.save(config);
                    resetCount++;
                }
            }

            player.service.sendThongBao("Đã reset " + resetCount + " config Bot");
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi reset config Bot");
        }
    }

    public void addBotNames(Player player, String namesStr) {
        try {
            if (namesStr == null || namesStr.trim().isEmpty()) {
                player.service.sendThongBao("Vui lòng nhập tên bot, cách nhau bởi dấu phẩy");
                return;
            }

            if (GameRepository.getInstance().botConfig == null) {
                player.service.sendThongBao("Lỗi: BotConfig repository chưa được khởi tạo");
                return;
            }

            String[] names = namesStr.split(",");
            List<BotConfigData> allConfigs = GameRepository.getInstance().botConfig.findByIsUsedFalse();
            
            BotConfigData config;
            if (allConfigs.isEmpty()) {
                config = new BotConfigData();
                config.setBotnameJson("[]");
                config.setCaitrangIdJson("[]");
                config.setRatioCaitrang(30);
                config.setRatioTrangbi(70);
                config.setIsUsed(false);
                config.setUsedBotnames("[]");
                config = GameRepository.getInstance().botConfig.save(config);
            } else {
                config = allConfigs.get(0);
            }

            JSONArray botnameArray = new JSONArray();
            
            String botnameJsonStr = config.getBotnameJson();
            if (botnameJsonStr != null && !botnameJsonStr.trim().isEmpty() && !botnameJsonStr.trim().equals("null")) {
                try {
                    botnameJsonStr = botnameJsonStr.trim();
                    if (botnameJsonStr.startsWith("[")) {
                        botnameArray = new JSONArray(botnameJsonStr);
                    }
                } catch (Exception e) {
                    player.service.sendThongBao("Lỗi parse botnameJson: " + e.getMessage());
                    return;
                }
            }

            int added = 0;
            for (String name : names) {
                name = name.trim();
                if (!name.isEmpty()) {
                    boolean exists = false;
                    for (int i = 0; i < botnameArray.length(); i++) {
                        if (botnameArray.getString(i).equals(name)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        botnameArray.put(name);
                        added++;
                    }
                }
            }

            config.setBotnameJson(botnameArray.toString());
            GameRepository.getInstance().botConfig.save(config);

            player.service.sendThongBao("Đã thêm " + added + " tên bot vào config");
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi thêm tên bot: " + e.getMessage());
        }
    }

    public void addCaiTrangIds(Player player, String idsStr) {
        try {
            if (idsStr == null || idsStr.trim().isEmpty()) {
                player.service.sendThongBao("Vui lòng nhập ID cải trang, cách nhau bởi dấu phẩy");
                return;
            }

            if (GameRepository.getInstance().botConfig == null) {
                player.service.sendThongBao("Lỗi: BotConfig repository chưa được khởi tạo");
                return;
            }

            String[] ids = idsStr.split(",");
            List<BotConfigData> allConfigs = GameRepository.getInstance().botConfig.findByIsUsedFalse();
            
            BotConfigData config;
            if (allConfigs.isEmpty()) {
                config = new BotConfigData();
                config.setBotnameJson("[]");
                config.setCaitrangIdJson("[]");
                config.setRatioCaitrang(30);
                config.setRatioTrangbi(70);
                config.setIsUsed(false);
                config.setUsedBotnames("[]");
                config = GameRepository.getInstance().botConfig.save(config);
            } else {
                config = allConfigs.get(0);
            }
            JSONArray caitrangArray = new JSONArray();
            
            String caitrangJsonStr = config.getCaitrangIdJson();
            if (caitrangJsonStr != null && !caitrangJsonStr.trim().isEmpty() && !caitrangJsonStr.trim().equals("null")) {
                try {
                    caitrangJsonStr = caitrangJsonStr.trim();
                    if (caitrangJsonStr.startsWith("[")) {
                        caitrangArray = new JSONArray(caitrangJsonStr);
                    }
                } catch (Exception e) {
                    player.service.sendThongBao("Lỗi parse caitrangIdJson: " + e.getMessage());
                    return;
                }
            }

            int added = 0;
            for (String idStr : ids) {
                idStr = idStr.trim();
                if (!idStr.isEmpty()) {
                    try {
                        int id = Integer.parseInt(idStr);
                        boolean exists = false;
                        for (int i = 0; i < caitrangArray.length(); i++) {
                            if (caitrangArray.getInt(i) == id) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            caitrangArray.put(id);
                            added++;
                        }
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }

            config.setCaitrangIdJson(caitrangArray.toString());
            GameRepository.getInstance().botConfig.save(config);

            player.service.sendThongBao("Đã thêm " + added + " ID cải trang vào config");
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi thêm ID cải trang: " + e.getMessage());
        }
    }

    private void clearUsedBotNames(Player player) {
        try {
            List<BotConfigData> allConfigs = GameRepository.getInstance().botConfig.findAll();
            int resetCount = 0;

            for (BotConfigData config : allConfigs) {
                if (config.getUsedBotnames() != null && !config.getUsedBotnames().isEmpty()) {
                    config.setUsedBotnames("[]");
                    GameRepository.getInstance().botConfig.save(config);
                    resetCount++;
                }
            }

            player.service.sendThongBao("Đã xóa tất cả tên bot đã dùng, có thể tái sử dụng");
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi xóa danh sách tên bot đã dùng");
        }
    }

    public void setRatioCaitrang(Player player, int ratio) {
        try {
            List<BotConfigData> allConfigs = GameRepository.getInstance().botConfig.findByIsUsedFalse();
            
            BotConfigData config;
            if (allConfigs.isEmpty()) {
                config = new BotConfigData();
                config.setBotnameJson("[]");
                config.setCaitrangIdJson("[]");
                config.setRatioCaitrang(ratio);
                config.setRatioTrangbi(70);
                config.setIsUsed(false);
                config.setUsedBotnames("[]");
                config = GameRepository.getInstance().botConfig.save(config);
            } else {
                config = allConfigs.get(0);
                config.setRatioCaitrang(ratio);
                GameRepository.getInstance().botConfig.save(config);
            }

            player.service.sendThongBao("Đã set tỉ lệ cải trang: " + ratio);
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi set tỉ lệ cải trang: " + e.getMessage());
        }
    }

    public void setRatioTrangbi(Player player, int ratio) {
        try {
            List<BotConfigData> allConfigs = GameRepository.getInstance().botConfig.findByIsUsedFalse();
            
            BotConfigData config;
            if (allConfigs.isEmpty()) {
                config = new BotConfigData();
                config.setBotnameJson("[]");
                config.setCaitrangIdJson("[]");
                config.setRatioCaitrang(30);
                config.setRatioTrangbi(ratio);
                config.setIsUsed(false);
                config.setUsedBotnames("[]");
                config = GameRepository.getInstance().botConfig.save(config);
            } else {
                config = allConfigs.get(0);
                config.setRatioTrangbi(ratio);
                GameRepository.getInstance().botConfig.save(config);
            }

            player.service.sendThongBao("Đã set tỉ lệ trang bị: " + ratio);
        } catch (Exception e) {
            player.service.sendThongBao("Lỗi khi set tỉ lệ trang bị: " + e.getMessage());
        }
    }
}
