/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.combine.LinhThu;

import com.ngocrong.combine.Combine;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;
import java.util.*;

public class NangChiSoLinhThu extends Combine {

    // Constants
    private static final long REQUIRE_GOLD = 500_000_000L;
    private static final int[] OPTION_IDS = {234, 235, 236};
    private static final int[] OPTION_PARAMS = {5, 5, 3};
    private static final int MAX_COUNT = 8;
    
    // Pity System Configuration
    private static final int BASE_WEIGHT = 100;        // Trọng số cơ bản
    private static final int PITY_MULTIPLIER = 75;     // Bonus cho mỗi lần thiếu hụt
    private static final int MIN_WEIGHT = 25;          // Trọng số tối thiểu
    private static final boolean ENABLE_PROGRESSIVE_PITY = true; // Pity tăng dần theo cấp độ

    public NangChiSoLinhThu() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang\n");
        sb.append("Chọn Linh thú bậc 2\n");
        sb.append("và Đá ma thuật\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        setInfo2("Ta sẽ nâng cấp linh thú giúp ngươi với hệ thống cân bằng");
    }

    @Override
    public void confirm() {
        if (itemCombine == null) {
            return;
        }
        
        Item pet = null;
        Item stone = null;
        
        // Tìm kiếm items cần thiết
        for (byte idx : itemCombine) {
            Item item = player.itemBag[idx];
            if (item != null) {
                if (item.template.type == Item.TYPE_PET_BAY_BAC_2) {
                    pet = item;
                } else if (item.id == ItemName.DA_MA_THUAT) {
                    stone = item;
                }
            }
        }
        
        // Kiểm tra điều kiện
        if (pet == null) {
            showCancel("Không tìm thấy Linh thú bậc 2");
            return;
        }
        if (stone == null) {
            showCancel("Không tìm thấy Đá ma thuật");
            return;
        }

        if (getCount(pet) >= MAX_COUNT) {
            showCancel("Linh thú đã đạt giới hạn nâng chỉ số");
            return;
        }
        
        // Hiển thị thông tin dự đoán cho người chơi
        showPityInfo(pet);
        
        // Hiển thị menu xác nhận
        String info = "Cần Linh thú bậc 2, Đá ma thuật và 500tr vàng\n";
        boolean ok = pet != null && stone != null && getCount(pet) < MAX_COUNT 
                    && player.gold >= REQUIRE_GOLD && stone.quantity >= 1;
        
        player.menus.clear();
        if (ok) {
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng chỉ số"));
        }
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        
        Item pet = null;
        Item stone = null;
        
        // Tìm kiếm items
        for (byte idx : itemCombine) {
            Item item = player.itemBag[idx];
            if (item != null) {
                if (item.template.type == Item.TYPE_PET_BAY_BAC_2) {
                    pet = item;
                } else if (item.id == ItemName.DA_MA_THUAT) {
                    stone = item;
                }
            }
        }
        
        // Kiểm tra lại điều kiện
        if (pet == null || stone == null || stone.quantity < 1 
            || player.gold < REQUIRE_GOLD || getCount(pet) >= MAX_COUNT) {
            player.service.sendThongBao("Vật phẩm hoặc vàng không đủ");
            return;
        }

        // Trừ items và vàng
        player.removeItem(stone.indexUI, 1);
        player.addGold(-REQUIRE_GOLD);

        // Cập nhật counter
        int count = getCount(pet) + 1;
        updateCounterOption(pet, 107, count);
        updateCounterOption(pet, 102, count);

        // SỬ DỤNG PITY SYSTEM ALGORITHM
        int selectedIndex = selectOptionWithPity(pet);
        int optId = OPTION_IDS[selectedIndex];
        int param = OPTION_PARAMS[selectedIndex];
        
        // Áp dụng option được chọn
        ItemOption op = pet.getItemOption(optId);
        if (op != null) {
            op.param += param;
        } else {
            pet.addItemOption(new ItemOption(optId, param));
        }

        // Cập nhật UI và kết quả
        player.service.refreshItem((byte) 1, pet);
        result((byte) 2);
        update();
        
        // Log kết quả và hiển thị thông tin pity
        logUpgradeResult(pet, optId, param, selectedIndex);
    }

    /**
     * PITY SYSTEM ALGORITHM - Thuật toán chính
     * Tăng dần xác suất cho option ít xuất hiện, đảm bảo công bằng lâu dài
     */
    private int selectOptionWithPity(Item pet) {
        int totalUpgrades = getCount(pet);
        
        // Nếu chưa nâng cấp lần nào, random thuần
        if (totalUpgrades == 0) {
            return Utils.nextInt(OPTION_IDS.length);
        }
        
        // Đếm số lần mỗi option đã xuất hiện
        int[] optionCounts = new int[OPTION_IDS.length];
        for (int i = 0; i < OPTION_IDS.length; i++) {
            ItemOption op = pet.getItemOption(OPTION_IDS[i]);
            optionCounts[i] = op != null ? (op.param / OPTION_PARAMS[i]) : 0;
        }
        
        // Tính pity weights cho mỗi option
        int[] pityWeights = calculatePityWeights(optionCounts, totalUpgrades);
        int totalWeight = Arrays.stream(pityWeights).sum();
        
        // Đảm bảo có trọng số hợp lệ
        if (totalWeight <= 0) {
            return Utils.nextInt(OPTION_IDS.length);
        }
        
        // Random theo pity weights
        int randomValue = Utils.nextInt(totalWeight);
        int currentSum = 0;
        
        for (int i = 0; i < pityWeights.length; i++) {
            currentSum += pityWeights[i];
            if (randomValue < currentSum) {
                return i;
            }
        }
        
        // Fallback
        return Utils.nextInt(OPTION_IDS.length);
    }

    /**
     * Tính toán pity weights cho mỗi option
     * Công thức: BaseWeight + (Deficit × PityMultiplier × ProgressiveBonus)
     */
    private int[] calculatePityWeights(int[] optionCounts, int totalUpgrades) {
        int[] weights = new int[OPTION_IDS.length];
        
        // Tính số lần lý tưởng mỗi option nên xuất hiện
        double idealCountPerOption = (double) totalUpgrades / OPTION_IDS.length;
        
        for (int i = 0; i < OPTION_IDS.length; i++) {
            // Tính độ thiếu hụt (deficit)
            double expectedCount = idealCountPerOption;
            int actualCount = optionCounts[i];
            int deficit = Math.max(0, (int) Math.ceil(expectedCount - actualCount));
            
            // Base weight
            int weight = BASE_WEIGHT;
            
            // Pity bonus
            if (deficit > 0) {
                int pityBonus = deficit * PITY_MULTIPLIER;
                
                // Progressive pity - thiếu càng nhiều thì bonus càng lớn
                if (ENABLE_PROGRESSIVE_PITY && deficit > 1) {
                    // Công thức: bonus tăng theo cấp số nhân nhẹ
                    pityBonus = (int) (pityBonus * (1 + Math.log(deficit) * 0.3));
                }
                
                weight += pityBonus;
            }
            
            // Đảm bảo trọng số tối thiểu
            weights[i] = Math.max(weight, MIN_WEIGHT);
        }
        
        return weights;
    }

    /**
     * Hiển thị thông tin pity cho người chơi (optional)
     */
    private void showPityInfo(Item pet) {
        try {
            int totalUpgrades = getCount(pet);
            if (totalUpgrades == 0) return;
            
            StringBuilder info = new StringBuilder();
            info.append("=== THÔNG TIN NÂNG CẤP ===\n");
            
            // Hiển thị phân bố hiện tại
            int[] optionCounts = new int[OPTION_IDS.length];
            String[] optionNames = {"Tấn công", "Phòng thủ", "HP"}; // Có thể thay đổi
            
            for (int i = 0; i < OPTION_IDS.length; i++) {
                ItemOption op = pet.getItemOption(OPTION_IDS[i]);
                optionCounts[i] = op != null ? (op.param / OPTION_PARAMS[i]) : 0;
                info.append(String.format("%s: %d lần\n", optionNames[i], optionCounts[i]));
            }
            
            // Hiển thị xác suất tiếp theo
            int[] weights = calculatePityWeights(optionCounts, totalUpgrades);
            int totalWeight = Arrays.stream(weights).sum();
            
            info.append("\n=== XÁC SUẤT LẦN TIẾP ===\n");
            for (int i = 0; i < weights.length; i++) {
                double percentage = (double) weights[i] / totalWeight * 100;
                info.append(String.format("%s: %.1f%%\n", optionNames[i], percentage));
            }
            
            // Log thông tin (có thể hiển thị trong game nếu cần)
            System.out.println(info.toString());
            
        } catch (Exception e) {
            System.err.println("Error showing pity info: " + e.getMessage());
        }
    }

    /**
     * Lấy thông tin pity để hiển thị trong confirm dialog
     */
    private String getPityDisplayInfo(Item pet) {
        try {
            int totalUpgrades = getCount(pet);
            if (totalUpgrades == 0) {
                return "Lần đầu nâng cấp - xác suất đều nhau";
            }
            
            // Tìm option ít xuất hiện nhất
            int[] optionCounts = new int[OPTION_IDS.length];
            String[] optionNames = {"ATK", "DEF", "HP"};
            
            int minCount = Integer.MAX_VALUE;
            List<String> leastOptions = new ArrayList<>();
            
            for (int i = 0; i < OPTION_IDS.length; i++) {
                ItemOption op = pet.getItemOption(OPTION_IDS[i]);
                optionCounts[i] = op != null ? (op.param / OPTION_PARAMS[i]) : 0;
                
                if (optionCounts[i] < minCount) {
                    minCount = optionCounts[i];
                    leastOptions.clear();
                    leastOptions.add(optionNames[i]);
                } else if (optionCounts[i] == minCount) {
                    leastOptions.add(optionNames[i]);
                }
            }
            
            return String.format("Ưu tiên: %s (ít nhất: %d lần)", 
                String.join(", ", leastOptions), minCount);
                
        } catch (Exception e) {
            return "Hệ thống cân bằng hoạt động";
        }
    }

    /**
     * Kiểm tra xem pet đã cân bằng chưa
     */
    private boolean isBalanced(Item pet) {
        int[] optionCounts = new int[OPTION_IDS.length];
        
        for (int i = 0; i < OPTION_IDS.length; i++) {
            ItemOption op = pet.getItemOption(OPTION_IDS[i]);
            optionCounts[i] = op != null ? (op.param / OPTION_PARAMS[i]) : 0;
        }
        
        // Coi là cân bằng nếu chênh lệch không quá 1
        int min = Arrays.stream(optionCounts).min().orElse(0);
        int max = Arrays.stream(optionCounts).max().orElse(0);
        
        return (max - min) <= 1;
    }

    /**
     * Lấy tên option cho hiển thị
     */
    private String getOptionName(int optionId) {
        switch (optionId) {
            case 234: return "HP";
            case 235: return "MP";
            case 236: return "KI";
            default: return "Chỉ số";
        }
    }

    /**
     * Cập nhật option counter
     */
    private void updateCounterOption(Item pet, int optionId, int value) {
        ItemOption countOp = pet.getItemOption(optionId);
        if (countOp == null) {
            pet.addItemOption(new ItemOption(optionId, value));
        } else {
            countOp.param = value;
        }
    }

    /**
     * Lấy số lần đã nâng cấp của pet
     */
    private int getCount(Item item) {
        ItemOption op = item.getItemOption(107);
        return op == null ? 0 : op.param;
    }

    /**
     * Log kết quả nâng cấp để debug và monitor
     */
    private void logUpgradeResult(Item pet, int optionId, int param, int selectedIndex) {
        try {
            int upgradeCount = getCount(pet);
            
            System.out.println(String.format(
                "[PITY_UPGRADE] Player: %s, Count: %d/%d, Option: %s (+%d), Balanced: %s",
                player.name, upgradeCount, MAX_COUNT, getOptionName(optionId), param, isBalanced(pet)
            ));
            
            // Log phân bố hiện tại
            StringBuilder distribution = new StringBuilder();
            String[] names = {"ATK", "DEF", "HP"};
            for (int i = 0; i < OPTION_IDS.length; i++) {
                ItemOption op = pet.getItemOption(OPTION_IDS[i]);
                int current = op != null ? op.param : 0;
                int count = current / OPTION_PARAMS[i];
                distribution.append(String.format("%s:%d(%d) ", names[i], current, count));
            }
            
            System.out.println(String.format(
                "[PITY_DISTRIBUTION] %s", distribution.toString()
            ));
            
        } catch (Exception e) {
            System.err.println("Error logging upgrade result: " + e.getMessage());
        }
    }

    /**
     * Method để test pity system
     */
    public void testPitySystem(Item pet, int iterations) {
        System.out.println("=== PITY SYSTEM TEST ===");
        int[] results = new int[OPTION_IDS.length];
        
        for (int i = 0; i < iterations; i++) {
            int selected = selectOptionWithPity(pet);
            results[selected]++;
            
            // Giả lập việc thêm option vào pet
            ItemOption op = pet.getItemOption(OPTION_IDS[selected]);
            if (op != null) {
                op.param += OPTION_PARAMS[selected];
            } else {
                pet.addItemOption(new ItemOption(OPTION_IDS[selected], OPTION_PARAMS[selected]));
            }
        }
        
        System.out.println("Results after " + iterations + " iterations:");
        for (int i = 0; i < results.length; i++) {
            System.out.println(String.format("Option %d: %d times (%.1f%%)", 
                OPTION_IDS[i], results[i], (double)results[i] / iterations * 100));
        }
    }
}