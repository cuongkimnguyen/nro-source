package com.ngocrong.event;

import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.data.OsinCheckInData;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemTime;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.repository.OsinCheckInRepository;
import com.ngocrong.server.SessionManager;
import com.ngocrong.data.PlayerData;
import com.ngocrong.repository.PlayerDataRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngocrong.server.Config;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class OsinCheckInEvent {

    private static OsinCheckInRepository repo() {
        return GameRepository.getInstance().osinCheckInRepository;
    }

    public static final int[] MILESTONES = {2000, 1000, 500, 400, 300, 200};

    /**
     * Lấy tổng số lượt điểm danh hôm nay
     */
    public static int getTotalTodayCheckIns() {
        return repo().countToday();
    }

    /**
     * Xử lý điểm danh của người chơi
     */
    public static void checkIn(Player player) {
        if (player == null) {
            return;
        }

        // Kiểm tra kích hoạt thành viên
        if (player.getSession().user.getActivated() == 0) {
            player.service.sendThongBao("Bạn cần kích hoạt thành viên để tham gia tính năng này");
            return;
        }

        // Kiểm tra đã điểm danh hôm nay chưa
        Optional<OsinCheckInData> existingCheckInOpt = repo().findTodayByPlayer(player.id);
        if (existingCheckInOpt.isPresent()) {
            OsinCheckInData existingCheckInData = existingCheckInOpt.get();
            if (existingCheckInData.getRewarded() != null && existingCheckInData.getRewarded() == 1) {
                player.service.sendThongBao("Bạn đã điểm danh và nhận quà hôm nay rồi.");
            } else {
                player.service.sendThongBao("Bạn đã điểm danh hôm nay. Chờ đạt mốc để nhận quà.");
            }
            return;
        }

        // Kiểm tra sự kiện còn slot không
        int totalBeforeThisCheckIn = getTotalTodayCheckIns();
        if (totalBeforeThisCheckIn >= MILESTONES[0]) {
            player.service.sendThongBao("Hoạt động đã kết thúc");
            return;
        }

        // Lưu dữ liệu điểm danh
        OsinCheckInData newData = new OsinCheckInData();
        newData.setPlayerId(player.id);
        newData.setCheckinDate(Instant.now());
        newData.setRewarded((byte) 0);
        newData.setIs_rewarded(0);
        repo().save(newData);

        player.service.sendThongBao("Điểm danh thành công!");

        // Xử lý mốc thưởng
        int totalAfterThisCheckIn = getTotalTodayCheckIns();
        processGlobalMilestoneRewards(totalBeforeThisCheckIn, totalAfterThisCheckIn);

    }

    /**
     * Xử lý mốc thưởng toàn server - CHỈ cho những người điểm danh từ trước
     */
    private static void processGlobalMilestoneRewards(int previousTotal, int currentTotal) {
        int currentHighestAchievedMilestone = getHighestAchievedMilestone(currentTotal);
        if (currentHighestAchievedMilestone == 0) {
            return; // Chưa đạt mốc nào cả
        }

        int previousHighestAchievedMilestone = getHighestAchievedMilestone(previousTotal);

        // Thông báo nếu đạt mốc mới
        if (currentHighestAchievedMilestone > previousHighestAchievedMilestone) {
            String announcement = String.format("Chúc mừng! Mốc điểm danh %d người đã đạt! Hãy tham gia điểm danh ngay", currentHighestAchievedMilestone);
            SessionManager.addThongBaoAll(announcement);
        }

        // Trao thưởng milestone CHỈ cho những người đã điểm danh từ trước và chưa nhận quà ở mốc này
        List<OsinCheckInData> candidates = repo().findAllTodayUnrewarded(currentHighestAchievedMilestone);
        for (OsinCheckInData checkInData : candidates) {
            giveMilestoneRewardToPlayer(null, checkInData, currentTotal);

        }

    }

    /**
     * LOGIC MỚI: Trao buff toàn server (x2 TNSM) cho người vừa điểm danh
     */
    private static void giveServerBuffToPlayer(Player player, int currentTotal) {
        if (player == null) {
            return;
        }
        int countX2 = MILESTONES[2];
        if (currentTotal < countX2) {
            return;
        }
        int currentHighestMilestone = getHighestAchievedMilestone(currentTotal);
        ItemTime existingBuff = player.getItemTime(ItemTimeName.PHIEU_X2_TNSM);
        if (existingBuff == null) {
            Item base = new Item(ItemName.PHIEU_X2_TNSM);
            player.setItemTime(ItemTimeName.PHIEU_X2_TNSM, base.template.iconID, true, Utils.getSecondsUntilEndOfDay());
            player.service.sendThongBao("Bạn nhận được x2 TNSM trong ngày do server đã đạt mốc " + currentHighestMilestone + " người điểm danh!");
        }
    }

    /**
     * LOGIC MỚI: Trao thưởng milestone (thỏi vàng + vật phẩm) - CHỈ cho người
     * điểm danh từ trước
     */
    private static void giveMilestoneRewardToPlayer(Player playerToReward, OsinCheckInData checkInData, int currentMilestone) {
        // Kiểm tra đã nhận thưởng mốc này chưa
        if (checkInData.getRewarded() != null && checkInData.getIs_rewarded() >= currentMilestone) {
            return; // Đã nhận quà rồi
        }

        Player p = playerToReward;
        if (p == null) {
            p = SessionManager.findChar(checkInData.getPlayerId());
        }

        if (p == null) {
            return; // Không tìm thấy người chơi online
        }
        giveServerBuffToPlayer(p, currentMilestone);
        boolean hasGivenAnyReward = false;
        StringBuilder rewardMessage = new StringBuilder();

        // 1. Trao thưởng đặc biệt cho các mốc cụ thể (thỏi vàng)
        Item specialReward = getSpecialRewardForMilestone(currentMilestone);
        if (specialReward != null) {
            if (p.addItem(specialReward)) {
                rewardMessage.append(String.format("%s x%d", specialReward.template.name, specialReward.quantity));
                hasGivenAnyReward = true;
            } else {
                p.service.sendThongBao("Hành trang không đủ chỗ trống để nhận một phần quà điểm danh.");
            }
        }

        if (hasGivenAnyReward) {
            checkInData.setRewarded((byte) 1);
            checkInData.setIs_rewarded(currentMilestone);
            repo().save(checkInData);

            // Thông báo tổng hợp
            if (rewardMessage.length() > 0) {
                p.service.sendThongBao(String.format("Bạn nhận được quà điểm danh mốc %d: %s",
                        currentMilestone, rewardMessage.toString()));
            }
        }
    }

    /**
     * DEPRECATED: Hàm cũ được thay thế bởi 2 hàm riêng biệt
     */
    /**
     * Lấy thưởng đặc biệt cho mốc cụ thể (thỏi vàng)
     */
    private static Item getSpecialRewardForMilestone(int milestone) {
        Item reward = null;
        final int x = MILESTONES[0];
        switch (milestone) {
            case 200:
            case 300:
                reward = new Item(ItemName.VANG_190);
                reward.quantity = 500_000_000;
                break;
            case 400:
                int[] satellites = {342, 343, 344, 345};
                reward = new Item(satellites[Utils.nextInt(satellites.length)]);
                reward.quantity = 3;
                break;
            case 500:
                reward = new Item(ItemName.VANG_190);
                reward.quantity = 1_000_000_000;
                break;
            case 1000:
                reward = new Item(Utils.nextInt(1021, 1023));
                reward.quantity = 1;
                break;
            case 2000:
                reward = new Item(ItemName.THOI_VANG);
                reward.quantity = 5;
                break;
        }

        if (reward != null) {
            reward.setDefaultOptions();
        }
        return reward;
    }

    /**
     * Lấy thưởng thông thường theo mốc
     */
    /**
     * Xác định mốc cao nhất đã đạt được dựa trên tổng số lượt điểm danh
     */
    private static int getHighestAchievedMilestone(int totalCheckIns) {
        for (int milestone : MILESTONES) {
            if (totalCheckIns >= milestone) {
                return milestone;
            }
        }
        return 0;
    }

    /**
     * Kiểm tra và xử lý x2 TNSM pending
     */
    public static void checkPendingReward(Player player) {
        if (player == null || player.itemTimes == null || player.itemTimes.isEmpty()) {
            return;
        }
        Utils.setTimeout(() -> {
            var itemTime = player.getItemTime(ItemTimeName.PHIEU_X2_TNSM);
            if (itemTime != null) {
                final int count = MILESTONES[2];
                if (getTotalTodayCheckIns() >= count) {
                    player.setTimeForItemtime(ItemTimeName.PHIEU_X2_TNSM, Utils.getSecondsUntilEndOfDay());
                } else {
                    player.setTimeForItemtime(ItemTimeName.PHIEU_X2_TNSM, 1);
                }
            }
        }, 3000);
    }

    /**
     * Lấy thông tin trạng thái sự kiện (cho debug/admin)
     */
    public static String getEventStatus() {
        int total = getTotalTodayCheckIns();
        int currentMilestone = getHighestAchievedMilestone(total);

        StringBuilder status = new StringBuilder();
        status.append("=== TRẠNG THÁI SỰ KIỆN ĐIỂM DANH ===\n");
        status.append(String.format("Tổng số người đã điểm danh: %d\n", total));
        status.append(String.format("Mốc hiện tại: %d\n", currentMilestone));
        status.append("Các mốc: ");
        for (int milestone : MILESTONES) {
            if (total >= milestone) {
                status.append(String.format("[✓%d] ", milestone));
            } else {
                status.append(String.format("[✗%d] ", milestone));
            }
        }
        status.append("\n");

        return status.toString();
    }
}
