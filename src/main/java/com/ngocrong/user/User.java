package com.ngocrong.user;

import _HunrProvision.ConfigStudio;
import com.ngocrong.data.PlayerData;
import com.ngocrong.data.UserData;
import com.ngocrong.model.Achievement;
import com.ngocrong.network.Session;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.server.*;
import com.ngocrong.item.Item;
import com.ngocrong.model.MagicTree;
import com.google.common.base.CharMatcher;
import com.google.gson.Gson;
import com.ngocrong.item.ItemOption;
import lombok.Data;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class User {

    private static Logger logger = Logger.getLogger(User.class);
    private static final int[][] HAIR_ID = {{64, 30, 31}, {9, 29, 32}, {6, 27, 28}};
    private static final int[][] LOCATION = {{21, 100, 336}, {22, 100, 336}, {23, 100, 336}};

    private int id;
    private String username;
    private String password;
    private int status;
    private int goldBar;
    private int role;
    private int activated;
    private Session session;
    private Timestamp lockTime;

    public User(String username, String password, Session session) {
        this.username = username.toLowerCase();
        this.password = password;
        this.session = session;
    }

    public void BanAccount() {
        GameRepository.getInstance().user.setBanAccount(username);
    }

    public int login() throws SQLException {
        Server server = DragonBall.getInstance().getServer();
        if (server.isMaintained) {
            return 5;
        }
         if (!username.startsWith("@guest.ingame_")) {
             CharMatcher allowedChars = CharMatcher.inRange('a', 'z')
                    .or(CharMatcher.inRange('A', 'Z'))
                    .or(CharMatcher.inRange('0', '9'))
                    .or(CharMatcher.is('.'))
                    .or(CharMatcher.is('@'))
                    .or(CharMatcher.is('_'));
            if (!allowedChars.matchesAllOf(username)) {
                return 6;
            }
        }
        List<UserData> userDataList = GameRepository.getInstance().user.findByUsernameAndPassword(username, password);
        if (userDataList.isEmpty()) {
            return 0;
        }

        UserData userData = userDataList.get(0);
 
        if (ConfigStudio.MODE_COMINGSOON && userData.getRole() != 1 && 
            LocalDateTime.now().isBefore(LocalDateTime.of(
                ConfigStudio.COMINGSOON_YEAR, 
                ConfigStudio.COMINGSOON_MONTH, 
                ConfigStudio.COMINGSOON_DAY, 
                ConfigStudio.COMINGSOON_HOUR, 
                ConfigStudio.COMINGSOON_MINUTE))) {
            return 7;
        }

        if (ConfigStudio.MODE_ADMIN && userData.getRole() != 1) {
            return 8;
        }
 
        setId(userData.id);
        setStatus(userData.status);
        setLockTime(userData.lockTime);
        setGoldBar(userData.goldBar);
        setRole(userData.role);
        setActivated(userData.activated);
        List<User> userList = SessionManager.findUser(username);
        if (!userList.isEmpty()) {
            for (User user : userList) {
                user.getSession().disconnect();
            }
            return 3;
        }
        if (status == 1) {
            return 2;
        }
        if (lockTime != null) {
            return 4;
        }
        return 1;
    }

    public int getActivated() {
        //RadioTest
//        if (Config.serverID() == 2) {
//            return 1;
//        }
        return this.activated;
    }

    public byte createChar(String name, byte gender, short hair) {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            int lent = name.length();
            if (lent < 5 || lent > 15) {
                return 1;
            }
            if (name.contains("admin") || name.contains("server")) {
                return 5;
            }
            Pattern p = Pattern.compile("^[a-z0-9]+$");
            Matcher m1 = p.matcher(name);
            if (!m1.find()) {
                return 2;
            }
            if (!GameRepository.getInstance().player.findByName(name).isEmpty()) {
                return 4;
            }
            if (gender < 0 || gender > 2) {
                gender = 0;
            }
            int[] h = HAIR_ID[gender];
            int tmp = h[0];
            for (int i = 1; i < h.length; i++) {
                if (h[i] == hair) {
                    tmp = h[i];
                    break;
                }
            }
            hair = (short) tmp;
            long power = 1200;
            long potential = 0;
            ArrayList<Item> itemBodys = new ArrayList<>();
            ArrayList<Item> itemBoxs = new ArrayList<>();
            ArrayList<Item> itemBag = new ArrayList<>();
            Gson g = new Gson();
            if (gender == 0) {
                Item item = new Item(0);
                item.indexUI = 0;
                item.quantity = 1;
                item.setDefaultOptions();
                itemBodys.add(item);

                Item item2 = new Item(6);
                item2.indexUI = 1;
                item2.quantity = 1;
                item2.setDefaultOptions();
                itemBodys.add(item2);
            }
            if (gender == 1) {
                Item item = new Item(1);
                item.indexUI = 0;
                item.quantity = 1;
                item.setDefaultOptions();
                itemBodys.add(item);

                Item item2 = new Item(7);
                item2.indexUI = 1;
                item2.quantity = 1;
                item2.setDefaultOptions();
                itemBodys.add(item2);
            }
            if (gender == 2) {
                Item item = new Item(2);
                item.indexUI = 0;
                item.quantity = 1;
                item.setDefaultOptions();
                itemBodys.add(item);

                Item item2 = new Item(8);
                item2.indexUI = 1;
                item2.quantity = 1;
                item2.setDefaultOptions();
                itemBodys.add(item2);
            }
            Item item = new Item(12);
            item.indexUI = 0;
            item.quantity = 1;
            item.setDefaultOptions();
            itemBoxs.add(item);

            Item tv = new Item(457);
            tv.indexUI = 1;
            tv.quantity = 10;
            tv.setDefaultOptions();
            tv.addItemOption(new ItemOption(30, 0));
            itemBoxs.add(tv);

            Item pean = new Item(595);
            pean.indexUI = 2;
            pean.quantity = 999_999;
            pean.setDefaultOptions();
            itemBoxs.add(pean);
            Item bongtai = new Item(454);
            bongtai.indexUI = 3;
            bongtai.quantity = 1;
            bongtai.setDefaultOptions();
            itemBoxs.add(bongtai);

            Item csl = new Item(194);
            csl.indexUI = 0;
            csl.quantity = 1;
            csl.setDefaultOptions();
            itemBag.add(csl);
            MagicTree magicTree = new MagicTree();
            magicTree.level = 10;
            Info info = new Info(gender);
            info.recovery(Info.ALL, 100, false);
            info.setStamina();
            info.power = info.potential = 1_500_000;
            PlayerData data = new PlayerData();
            data.userId = getId();
            data.serverId = config.getServerID();
            data.name = name;
            data.gender = gender;
            data.classId = gender;
            data.head = hair;
            data.task = "{\"id\":0,\"index\":2,\"count\":0,\"lastTask\":0}";
            data.gold = 10_000_000_000L;
            data.diamond = 500000;
            data.diamondLock = 0;
            data.itemBag = g.toJson(itemBag);
            data.itemBody = g.toJson(itemBodys);
            data.itemBox = g.toJson(itemBoxs);
            data.boxCrackBall = "[]";
            data.map = g.toJson(LOCATION[gender]);
            data.skill = "[]";
            data.info = g.toJson(info);
            data.clan = -1;
            data.shortcut = "[-1,-1,-1,-1,-1,-1,-1,-1,-1,-1]";
            data.magicTree = g.toJson(magicTree);
            data.numberCellBag = 100;
            data.numberCellBox = 100;
            data.friend = "[]";
            data.enemy = "[]";
            data.ship = 0;
            data.fusion = 1;
            data.porata = 0;
            data.itemTime = "[]";
            data.amulet = "[]";
            List<Achievement> achievements = new ArrayList<>();
            for (int i = 0; i < server.achievements.size(); i++) {
                achievements.add(new Achievement(i));
            }
            data.achievement = g.toJson(achievements);
            data.timePlayed = 0;
            data.typeTrainning = 0;
            data.online = 0;
            data.timeAtSplitFusion = 0L;
            data.head2 = -1;
            data.body = -1;
            data.leg = -1;
            data.collectionBook = "[]";
            data.dataDHVT23 = "[0,0,0,0,0,0,0,0,0,0,0,0,0]";
            data.thoivang = 0;
            data.dropItem = "{}";
            data.countNumberOfSpecialSkillChanges = 0;
            data.createTime = data.resetTime = new Timestamp(System.currentTimeMillis());
            GameRepository.getInstance().player.save(data);
            return 0;
        } catch (Exception ex) {
            
            System.err.println("Error at 5");
            logger.error("failed!", ex);

        }
        return 3;
    }

    public void close() {
        session = null;
        password = null;
        username = null;
    }
}
