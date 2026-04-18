/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _HunrProvision;

import _HunrProvision.services.DaiHoiVoThuat_23Service;
import _HunrProvision.minigame.ConSoMayMan;
import com.ngocrong.NQMP.DHVT_SH.RewardSuperRank;
import com.ngocrong.NQMP.DHVT_SH.SuperRank;
import static com.ngocrong.NQMP.DHVT_SH.SuperRank.HalfDay;
import com.ngocrong.NQMP.DHVT_SH.Top_SieuHang;
import com.ngocrong.NQMP.Mabu14H.Bu_Map;
import static com.ngocrong.NQMP.Mabu14H.Bu_Map.list;
import com.ngocrong.NQMP.Whis.RewardWhis;
import com.ngocrong.bot.BotCold;
import com.ngocrong.bot.VirtualBot;
import com.ngocrong.bot.VirtualBot_SoSinh;
import com.ngocrong.clan.ClanImage;
import com.ngocrong.consts.MapName;
import com.ngocrong.data.OsinCheckInData;
import com.ngocrong.event.OsinCheckInEvent;
import com.ngocrong.item.Item;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.SQLStatement;
import com.ngocrong.server.Server;
import com.ngocrong.server.ServerMaintenance;
import com.ngocrong.server.SessionManager;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.top.Top;
import com.ngocrong.top.TopInfo;
import com.ngocrong.user.Player;
import static com.ngocrong.user.Player.generateCharacterName;
import com.ngocrong.util.Utils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.json.JSONArray;

/**
 *
 * @author Administrator
 */
public class MainUpdate implements Runnable {

    static boolean isResetDHVT = true;
    static boolean isRewardDHVT = true;
    public static LocalDateTime now;

    public static void updateMabu14H() {
        Bu_Map.initBoss();
        if (list != null && !list.isEmpty()) {
       
            List<Bu_Map> currentBuList = new ArrayList<>(list);
            for (Bu_Map bu : currentBuList) {
                if (bu != null) { // Safety check
                    bu.update();
                }
            }
        }
    }

    public static void ResetDHVT() {

        try {
            GameRepository.getInstance().gameEventRepository.resetDHVT();
            GameRepository.getInstance().gameEventRepository.resetDHVT_SieuHang();
            for (Player player : SessionManager.getPlayers()) {
                player.roundDHVT23 = 0;
                player.timesOfDHVT23 = 0;
                player.setGetChest(false);
                player.countDhvtSieuHang = 1;
            }
        } catch (Exception ex) {
            
            logger.error("failed!", ex);

        }
    }

    public static void RewardDHVT23() {
        Top topDHVT = Top.getTop(Top.TOP_DHVT_SIEU_HANG);
        for (TopInfo info : topDHVT.getElements()) {
            Player player = SessionManager.findChar(info.playerID);
            if (player != null && player.zone != null && System.currentTimeMillis() - player.lastLogin >= 5000) {
                if (!info.isReward) {
                    info.isReward = true;
                    short goldReward;
                    if (info.rank == 1) {
                        goldReward = 200;
                    } else if (info.rank < 10) {
                        goldReward = 50;
                    } else if (info.rank < 50) {
                        goldReward = 10;
                    } else {
                        goldReward = 0;
                    }
                    if (goldReward > 0) {
                        GameRepository.getInstance().dhvtSieuHangRepository.setReward(info.playerID, 1);
                        Item thoivang = new Item(457);
                        thoivang.quantity = goldReward;
                        player.addItem(thoivang);
                        player.service.dialogMessage(String.format(
                                "Bạn đạt Top %d ở Giải Đấu Siêu Hạng\n"
                                + "Bạn nhận được %d thỏi vàng", info.rank, goldReward));
                    }
                }
            }
        }
    }

    private static final Logger logger = Logger.getLogger(MainUpdate.class);
    static boolean isSupportMisson;

    static long initCold = System.currentTimeMillis();
    static long initBot = System.currentTimeMillis();
    static long lastRewardSieuHang = System.currentTimeMillis();

    public static void update() {
        try {
            now = LocalDateTime.now();
            isSupportMisson = now.getHour() == 18;
            DaiHoiVoThuat_23Service.update();
            ConSoMayMan.update();
            checkKickOut();
            autoRegEvent();
            if (!isResetDHVT && now.getHour() == 0 && now.getMinute() == 0 && now.getSecond() == 0) {
                isResetDHVT = true;
                ResetDHVT();
            }
            if (now.getHour() >= 20) {
                RewardDHVT23();
            }
//            if (System.currentTimeMillis() - UtilsNQMP.lastCreateBot >= 5000) {
//                VirtualBot_SoSinh bot = new VirtualBot_SoSinh(generateCharacterName());
//                bot.setLocation(0, -1);
//                UtilsNQMP.lastCreateBot = System.currentTimeMillis();
//            }
//            if (System.currentTimeMillis() - initCold >= 15000 && BotCold.TotalBotCold < 50) {
//                initCold = System.currentTimeMillis();
//                int[] map = new int[]{105, 106, 107, 108, 109, 110};
//                TMap map2 = MapManager.getInstance().getMap(map[Utils.nextInt(map.length)]);
//                UtilsNQMP.createBotCold(1, map2.mapID);
//            }
//            if (System.currentTimeMillis() - initBot >= 5000 && VirtualBot.TotalBot < 250) {
//                initBot = System.currentTimeMillis();
//                int[] map = new int[]{0, 7, 14, 5};
//                VirtualBot.TotalBot++;
//                UtilsNQMP.createBot(1, map[Utils.nextInt(map.length)]);
//            }
            if (System.currentTimeMillis() - lastRewardSieuHang >= 60000 && now.getHour() >= 20) {
                List<Player> list = new ArrayList<>(SessionManager.getPlayers());
                for (var player : list) {
                    if (player != null && player.superrank != null && player.superrank.rank < 100) {
                        if (System.currentTimeMillis() - player.superrank.lastAttack >= 30 * 60000 && System.currentTimeMillis() - player.superrank.lastReward >= HalfDay) {
                            SuperRank.checkReward(player);
                        }
                    }
                }
//
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.err.println("Hour:"+now.getHour());
    }

    static void setBaoTri() {
//        MainUpdate.runTaskDay(() -> {
//            ServerMaintenance.BaoTri(3 * 60);
//        }, "17:00");
    }

    static void setRewardWhis() {
//        MainUpdate.runTaskDay(() -> {
//            RewardWhis.reward();
//        }, "00:00");
    }

    static void setRewardSuperRank() {
        MainUpdate.runTaskDay(() -> {
            RewardSuperRank.reward();
        }, "20:01");
    }

    static void setMabu14h() {
        MainUpdate.runTaskDayInWindow(() -> {
            try {
                logger.info("Attempting Mabu 14H event: Initializing bosses...");
                Bu_Map.initBoss();
                logger.info("Mabu 14H event: Successfully initialized bosses.");
            } catch (Exception e) {
                logger.error("Error during Mabu 14H initialization: ", e);
            }
        }, "14:01", "15:00");
    }

    @Override
    public void run() {
        Server server = DragonBall.getInstance().getServer();
        setBaoTri();
        setRewardWhis();
        setRewardSuperRank();
        setMabu14h();
        while (server.start) {
            try {
                Thread.sleep(100);
                update();
            } catch (InterruptedException ex) {
                
                logger.error("failed!", ex);
            }
        }
    }

    public static boolean CanEnterZoneSupportMisson(Player player, Zone zone) {
//        if (zone.map.mapID == 79 || zone.map.mapID == 82 || zone.map.mapID == 83) {
//            if (player.taskMain != null && player.taskMain.id != 20 && !zone.getBossInZone().isEmpty()) {                
//                return false;
//            }
//            if (player.taskMain != null && player.taskMain.id == 20 && !zone.getBossInZone().isEmpty() && player.taskMain.index == 6) {                
//                return false;
//            }
//        }
        if (!isSupportMisson) {
            return true;
        }
        if (zone.map.mapID == 80) {
            if (player.taskMain != null && player.taskMain.id != 21 && !zone.getBossInZone().isEmpty()) {
                return false;
            }
        }
        if (zone.map.mapID == 97) {
            if (player.taskMain != null && player.taskMain.id != 24 && !zone.getBossInZone().isEmpty()) {
                return false;
            }
        }
        if (zone.map.mapID == 100) {
            if (player.taskMain != null && player.taskMain.id != 25 && !zone.getBossInZone().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static void checkKickOut() {
        try {
            int hour = now.getHour();
            if (hour != 14) {
                TMap map = MapManager.getInstance().getMap(MapName.CONG_PHI_THUYEN_2);
                if (map == null) {
                    return;
                }

                // Tạo danh sách tạm để tránh ConcurrentModificationException
                List<Player> playersToKick = new ArrayList<>();

                // Thu thập người chơi cần kick
                for (Zone zone : map.zones) {
                    if (zone != null) {
                        playersToKick.addAll(zone.players.stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()));
                    }
                }

                // Thực hiện teleport cho từng người chơi
                for (Player player : playersToKick) {
                    try {
                        if (player != null && player.getSession() != null && player.getSession().user.getRole() != 1) {
                            player.teleport(0);
                        }
                    } catch (Exception e) {
                        

                    }
                }
            }
            if (hour != 22) {
                TMap map = MapManager.getInstance().getMap(126);
                if (map == null) {
                    return;
                }

                // Tạo danh sách tạm để tránh ConcurrentModificationException
                List<Player> playersToKick = new ArrayList<>();

                // Thu thập người chơi cần kick
                for (Zone zone : map.zones) {
                    if (zone != null) {
                        playersToKick.addAll(zone.players.stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()));
                    }
                }

                // Thực hiện teleport cho từng người chơi
                for (Player player : playersToKick) {
                    try {
                        if (player != null && player.getSession() != null && player.getSession().user.getRole() != 1) {
                            player.teleport(0);
                        }
                    } catch (Exception e) {
                        

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetDay() {
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement("update nr_player set drop_item = '{}'");
            try {
                int updated = ps.executeUpdate();
            } catch (Exception e) {
                
            } finally {
                ps.close();
            }
        } catch (Exception ex) {
            
            logger.error("Lỗi kết nối khi resetDay", ex);
        }

    }

    public static void runTaskDay(Runnable task, String time) {
        try {
            String callerMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
            String[] timeParts = time.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);

            // Nếu thời gian hôm nay đã qua, lên lịch cho ngày mai
            if (now.isAfter(nextRun)) {
                nextRun = nextRun.plusDays(1);
            }

            Duration duration = Duration.between(now, nextRun);
            long initialDelay = duration.getSeconds();

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(task, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);

            System.out.printf("Đã lên lịch task '%s' chạy lúc %s mỗi ngày%n", callerMethodName, time);
            System.out.printf("Lần chạy tiếp theo: %s (sau %d giây)%n", nextRun, initialDelay);

        } catch (Exception e) {
            String callerMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
            System.err.printf("Lỗi khi lên lịch task '%s': %s%n", callerMethodName, e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getCallerMethodName() {
        try {
            return Thread.currentThread().getStackTrace()[3].getMethodName();
        } catch (Exception e) {
            return "UnnamedTask";
        }
    }

    public static void runTaskDayInWindow(Runnable task, String timeStart, String timeEnd) {
        try {
            String callerMethodName = getCallerMethodName();

            String[] startParts = timeStart.split(":");
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);

            String[] endParts = timeEnd.split(":");
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            LocalDateTime now = LocalDateTime.now();
            LocalTime currentTime = now.toLocalTime();
            LocalTime startTime = LocalTime.of(startHour, startMinute);
            LocalTime endTime = LocalTime.of(endHour, endMinute);

            // Kiểm tra và chạy ngay lập tức nếu trong khung thời gian
            if (isTimeInWindow(currentTime, startTime, endTime)) {
                System.out.printf("Task '%s' đang chạy ngay lập tức (thời gian hiện tại %s nằm trong khoảng %s-%s)%n",
                        callerMethodName, currentTime, timeStart, timeEnd);
                executeTaskSafely(task, callerMethodName);
            }

            // Lên lịch chạy hàng ngày vào thời gian bắt đầu
            scheduleDaily(task, now, startHour, startMinute, callerMethodName, timeStart);

        } catch (Exception e) {
            String callerMethodName = getCallerMethodName();
            System.err.printf("Lỗi nghiêm trọng khi thiết lập runTaskDayInWindow cho '%s': %s%n",
                    callerMethodName, e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isTimeInWindow(LocalTime currentTime, LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            // Trường hợp qua đêm (ví dụ: 22:00 - 06:00)
            return !currentTime.isBefore(startTime) || currentTime.isBefore(endTime);
        } else {
            // Trường hợp cùng ngày (ví dụ: 08:00 - 18:00)
            return !currentTime.isBefore(startTime) && currentTime.isBefore(endTime);
        }
    }

    /**
     * Thực thi task một cách an toàn với xử lý lỗi
     */
    private static void executeTaskSafely(Runnable task, String taskName) {
        try {
            task.run();
        } catch (Exception e) {
            System.err.printf("Lỗi khi thực thi task '%s' ngay lập tức: %s%n", taskName, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lên lịch task chạy hàng ngày
     */
    private static void scheduleDaily(Runnable task, LocalDateTime now, int hour, int minute,
            String taskName, String timeStart) {
        LocalDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);

        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        Duration initialDelayDuration = Duration.between(now, nextRun);
        long initialDelaySeconds = Math.max(0, initialDelayDuration.getSeconds());

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(task, initialDelaySeconds, 24 * 60 * 60, TimeUnit.SECONDS);

        System.out.printf("Đã lên lịch task '%s' để chạy hàng ngày vào lúc %s%n", taskName, timeStart);
        System.out.printf("Lần chạy theo lịch tiếp theo: %s (sau %d giây)%n", nextRun, initialDelaySeconds);
    }

    static long lastReg;
    static long nextTime = 3000;
    static int numReg = Utils.nextInt(500, 650);

    public static void autoRegEvent() {
        if (System.currentTimeMillis() - lastReg >= nextTime) {
            lastReg = System.currentTimeMillis();
            nextTime = Utils.nextInt(2000,5000);
            if (OsinCheckInEvent.getTotalTodayCheckIns() % 100 != 99 && OsinCheckInEvent.getTotalTodayCheckIns() < numReg) {
                OsinCheckInData newData = new OsinCheckInData();
                newData.setPlayerId(-1);
                newData.setCheckinDate(Instant.now());
                newData.setRewarded((byte) 0);
                newData.setIs_rewarded(0);
                GameRepository.getInstance().osinCheckInRepository.save(newData);
            }
        }
    }
}
