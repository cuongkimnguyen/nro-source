/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.top;

import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.server.mysql.MySQLConnect;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class ExportTop {

    private static final String INSERT_REWARD
            = "INSERT INTO nr_rewardtop (player_name, item, infoTop, isReward) VALUES (?, ?, ?, 0)";

    /**
     * Finalize Top Power rankings and export reward data.
     */
    public static void exportTopPower() {
        Top top = Top.getTop(Top.TOP_POWER);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardPower(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Sức mạnh sư phụ");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top Task rankings and export reward data.
     */
    public static void exportTopTask() {
        Top top = Top.getTop(Top.TOP_TASK);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardTask(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Nhiệm vụ");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top spending Gold Bar rankings and export reward data.
     */
    public static void exportTopUseGoldBar() {
        Top top = Top.getTop(Top.TOP_USING_GOLBAR);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardGoldBar(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Tiêu thỏi vàng");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top Raiti boss kill rankings and export reward data.
     */
    public static void exportTopKillRaiti() {
        Top top = Top.getTop(Top.TOP_RAITI);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardRaiti(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Săn Boss Raiti");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top Disciple power rankings and export reward data.
     */
    public static void exportTopDisciple() {
        Top top = Top.getTop(Top.TOP_DISCIPLE_POWER);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardDisciple(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Úp sức mạnh đệ tử");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top Nuoc Mia Size M rankings and export reward data.
     */
    public static void exportTopNuocMiaSizeM() {
        Top top = Top.getTop(Top.TOP_NUOCMIASIZE_M);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardNuocMiaSizeM(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Nước mía Size M");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top Nuoc Mia Size XXL rankings and export reward data.
     */
    public static void exportTopNuocMiaSizeXXL() {
        Top top = Top.getTop(Top.TOP_NUOCMIASIZE_XXL);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardNuocMiaSizeXXL(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Nước mía Size XXL");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top Hop Qua Thuong rankings and export reward data.
     */
    public static void exportTopHopQuaThuong() {
        Top top = Top.getTop(Top.TOP_HOPQUATHUONG);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardHopQuaThuong(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Hộp quà thường");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top Hop Qua VIP rankings and export reward data.
     */
    public static void exportTopHopQuaVIP() {
        Top top = Top.getTop(Top.TOP_HOPQUAVIP);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardHopQuaVIP(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Hộp quà VIP");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top Hop Qua Dac Biet rankings and export reward data.
     */
    public static void exportTopHopQuaDacBiet() {
        Top top = Top.getTop(Top.TOP_HOPQUADACBIET);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardHopQuaDacBiet(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Hộp quà đặc biệt");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top Kill Boss rankings and export reward data.
     */
    public static void exportTopKillBoss() {
        Top top = Top.getTop(Top.TOP_KILLBOSS);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardKillBoss(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Săn Boss");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top Disciple Mabu power rankings and export reward data.
     */
    public static void exportTopDisciplePowerMabu() {
        Top top = Top.getTop(Top.TOP_DT_MABU);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardDisciplePowerMabu(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Đệ tử Mabu");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top Duoc Bac rankings and export reward data.
     */
    public static void exportTopDuocBac() {
        Top top = Top.getTop(Top.TOP_DUOCBAC);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardDuocBac(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Đuốc bạc");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    /**
     * Finalize Top Duoc Vang rankings and export reward data.
     */
    public static void exportTopDuocVang() {
        Top top = Top.getTop(Top.TOP_DUOCVANG);
        if (top == null) {
            return;
        }

        List<TopInfo> list = new ArrayList<>(top.getElements());
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(INSERT_REWARD)) {
            for (int i = 0; i < list.size(); i++) {
                int rank = i + 1;
                TopInfo info = list.get(i);
                List<Item> items = itemRewardDuocVang(rank);
                if (items.isEmpty()) {
                    continue;
                }
                ps.setString(1, info.name);
                ps.setString(2, com.ngocrong.top.AutoReward.AutoReward.ItemstoJson(items));
                ps.setString(3, "Top " + rank + " Đuốc vàng");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            
        }
    }

    private static List<Item> itemRewardHopQuaThuong(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createCostumeChestReward());
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.RUONG_1_MON_KICH_HOAT_CUOI_SHOP_NGAU_NHIEN, 5));
        } else if (rank == 2) {
            list.add(createCostumeChestReward());
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.RUONG_1_MON_KICH_HOAT_CUOI_SHOP_NGAU_NHIEN, 3));
        } else if (rank == 3) {
            list.add(createCostumeChestReward());
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.RUONG_1_MON_KICH_HOAT_CUOI_SHOP_NGAU_NHIEN, 2));
        } else if (rank >= 4 && rank <= 10) {
            list.add(createCostumeChestReward());
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.RUONG_1_MON_KICH_HOAT_CUOI_SHOP_NGAU_NHIEN, 1));
        } else if (rank >= 11 && rank <= 20) {
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.RUONG_1_MON_7_SAO, 1));
            for (int id = ItemName.NGOC_RONG_1_SAO; id <= ItemName.NGOC_RONG_7_SAO; id++) {
                list.add(createItem(id, 1));
            }
            list.add(createItem(ItemName.TINH_THE, 99));
            list.add(createItem(ItemName.MA_QUAI, 99));
        } else if (rank >= 21 && rank <= 50) {
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.TINH_THE, 50));
            list.add(createItem(ItemName.MA_QUAI, 50));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.THOI_VANG, 20));
            list.add(createItem(ItemName.TINH_THE, 30));
            list.add(createItem(ItemName.MA_QUAI, 30));
        }
        return list;
    }

    private static List<Item> itemRewardHopQuaVIP(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createItem(ItemName.RUONG_1_MON_KICH_HOAT_CUOI_SHOP_NGAU_NHIEN, 10));
            list.add(createCostumeChestReward(38));
            list.add(createAmuletReward());
        } else if (rank == 2) {
            list.add(createItem(ItemName.RUONG_1_MON_KICH_HOAT_CUOI_SHOP_NGAU_NHIEN, 5));
            list.add(createCostumeChestReward(38));
            list.add(createAmuletReward());
        } else if (rank == 3) {
            list.add(createItem(ItemName.RUONG_1_MON_KICH_HOAT_CUOI_SHOP_NGAU_NHIEN, 3));
            list.add(createCostumeChestReward(38));
            list.add(createAmuletReward());
        } else if (rank >= 4 && rank <= 10) {
            list.add(createItem(ItemName.RUONG_1_MON_KICH_HOAT_CUOI_SHOP_NGAU_NHIEN, 1));
            list.add(createCostumeChestReward(38));
            list.add(createItem(ItemName.MA_QUAI, 299));
            list.add(createItem(ItemName.TINH_THE, 299));
        } else if (rank >= 11 && rank <= 15) {
            list.add(createItem(ItemName.RUONG_DO_CUOI_7_SAO, 1));
            list.add(createItem(ItemName.MA_QUAI, 199));
            list.add(createItem(ItemName.TINH_THE, 199));
            list.add(createItem(ItemName.HON_LINH_THU, 299));
        } else if (rank >= 16 && rank <= 50) {
            list.add(createItem(ItemName.MA_QUAI, 99));
            list.add(createItem(ItemName.TINH_THE, 99));
            list.add(createItem(ItemName.HON_LINH_THU, 99));
            list.add(createItem(ItemName.RUONG_DO_CUOI_5_SAO, 1));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.THOI_VANG, 20));
            list.add(createItem(ItemName.RUONG_DO_CUOI_5_SAO, 1));
        }
        return list;
    }

    private static List<Item> itemRewardHopQuaDacBiet(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createItem(ItemName.RUONG_DO_CUOI_KICH_HOAT, 1));
            list.add(createDanhHieuQuocKhanhReward());
            list.add(createItem(ItemName.HON_LINH_THU, 1000));
        } else if (rank == 2) {
            list.add(createItem(ItemName.RUONG_DO_CUOI_KICH_HOAT, 1));
            list.add(createDanhHieuQuocKhanhReward());
            list.add(createItem(ItemName.HON_LINH_THU, 700));
        } else if (rank == 3) {
            list.add(createItem(ItemName.RUONG_DO_CUOI_KICH_HOAT, 1));
            list.add(createDanhHieuQuocKhanhReward());
            list.add(createItem(ItemName.HON_LINH_THU, 500));
        } else if (rank >= 4 && rank <= 10) {
            list.add(createItem(ItemName.RUONG_SET_HUY_DIET, 1));
            list.add(createItem(ItemName.MA_QUAI, 299));
            list.add(createItem(ItemName.TINH_THE, 299));
        } else if (rank >= 11 && rank <= 20) {
            list.add(createItem(ItemName.RUONG_DO_CUOI_7_SAO, 1));
            list.add(createItem(ItemName.VE_DOI_SKILL_4, 1));
        } else if (rank >= 21 && rank <= 50) {
            list.add(createItem(ItemName.RUONG_DO_CUOI_5_SAO, 1));
            for (int id = ItemName.NGOC_RONG_1_SAO; id <= ItemName.NGOC_RONG_7_SAO; id++) {
                list.add(createItem(id, 1));
            }
            list.add(createItem(ItemName.MA_QUAI, 50));
            list.add(createItem(ItemName.TINH_THE, 50));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.THOI_VANG, 50));
        }
        return list;
    }

    private static Item createCostumeChestReward() {
        return createCostumeChestReward(35);
    }

    private static Item createCostumeChestReward(int param) {
        Item chest = createItem(ItemName.RUONG_CAI_TRANG_10_OPTION, 1);
        chest.options.add(new ItemOption(237, param));
        return chest;
    }

    private static Item createAmuletReward() {
        Item amulet = createItem(ItemName.HAC_HOA_AN, 1);
        amulet.options.clear();
        amulet.options.add(new ItemOption(77, 10));
        amulet.options.add(new ItemOption(103, 10));
        amulet.options.add(new ItemOption(50, 10));
        amulet.options.add(new ItemOption(5, 5));
        amulet.options.add(new ItemOption(30, 0));
        return amulet;
    }

    private static Item createDanhHieuQuocKhanhReward() {
        Item title = createItem(ItemName.DANH_HIEU_QUOC_KHANH, 1);
        title.options.clear();
        title.options.add(new ItemOption(77, 8));
        title.options.add(new ItemOption(103, 8));
        title.options.add(new ItemOption(50, 8));
        return title;
    }

    private static List<Item> itemRewardPower(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createItem(ItemName.RUONG_THAN_LINH, 3));
            list.add(createItem(ItemName.DANH_HIEU_TOP_1_SUC_MANH, 1));
        } else if (rank == 2) {
            list.add(createItem(ItemName.RUONG_THAN_LINH, 2));
            list.add(createItem(ItemName.DANH_HIEU_TOP_2_SUC_MANH, 1));
        } else if (rank == 3) {
            list.add(createItem(ItemName.RUONG_THAN_LINH, 2));
            list.add(createItem(ItemName.DANH_HIEU_TOP_3_SUC_MANH, 1));
        } else if (rank >= 4 && rank <= 10) {
            list.add(createItem(ItemName.RUONG_THAN_LINH, 1));
            list.add(createItem(ItemName.HONG_NGOC, 1000));
        } else if (rank >= 11 && rank <= 20) {
            list.add(createItem(ItemName.THOI_VANG, 200));
            list.add(createItem(ItemName.HONG_NGOC, 500));
            list.add(createItem(ItemName.RUONG_DA_NANG_CAP, 1000));
            list.add(createItem(ItemName.RUONG_NGOC_RONG, 10));
        } else if (rank >= 21 && rank <= 50) {
            list.add(createItem(ItemName.THOI_VANG, 100));
            list.add(createItem(ItemName.RUONG_ITEM_CAP_1, 30));
            list.add(createItem(ItemName.RUONG_NGOC_RONG, 10));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.THOI_VANG, 50));
            list.add(createItem(ItemName.RUONG_NGOC_RONG, 10));
        }
        return list;
    }

    private static List<Item> itemRewardTask(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createItem(2335, 1));
            list.add(createItem(2355, 1));
            list.add(createItem(ItemName.RUONG_THAN_LINH, 2));
            list.add(createItem(ItemName.DANH_HIEU_TOP_1_NHIEM_VU, 1));
            list.add(createItem(ItemName.VE_TICH_DIEM, 400));
        } else if (rank == 2) {
            list.add(createItem(2335, 1));
            list.add(createItem(2355, 1));
            list.add(createItem(ItemName.RUONG_THAN_LINH, 1));
            list.add(createItem(ItemName.DANH_HIEU_TOP_2_NHIEM_VU, 1));
            list.add(createItem(ItemName.VE_TICH_DIEM, 300));
        } else if (rank == 3) {
            list.add(createItem(2335, 1));
            list.add(createItem(2355, 1));
            list.add(createItem(ItemName.RUONG_THAN_LINH, 1));
            list.add(createItem(ItemName.DANH_HIEU_TOP_3_NHIEM_VU, 1));
            list.add(createItem(ItemName.VE_TICH_DIEM, 200));
        } else if (rank >= 4 && rank <= 10) {
            list.add(createItem(2335, 1));
            list.add(createItem(2355, 1));
            list.add(createItem(ItemName.RUONG_1_MON_HUY_DIET, 3));
            list.add(createItem(ItemName.VE_TICH_DIEM, 100));
        } else if (rank >= 11 && rank <= 20) {
            list.add(createItem(2355, 1));
            list.add(createItem(ItemName.DA_BAO_VE_987, 20));
            list.add(createItem(ItemName.RUONG_NGOC_RONG, 10));
        } else if (rank >= 21 && rank <= 50) {
            list.add(createItem(ItemName.THOI_VANG, 50));
            list.add(createItem(ItemName.RUONG_NGOC_RONG, 10));
            list.add(createItem(ItemName.RUONG_ITEM_CAP_2, 10));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.THOI_VANG, 30));
            list.add(createItem(ItemName.DA_BAO_VE_987, 5));
        }
        return list;
    }

    private static List<Item> itemRewardGoldBar(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createItem(ItemName.THOI_VANG, 10000));
            list.add(createItem(ItemName.DANH_HIEU_VIP_1, 1));
            list.add(createItem(ItemName.RUONG_THAN_LINH_KICH_HOAT, 1));
            list.add(createItem(2353, 1));
            list.add(createItem(ItemName.HON_LINH_THU, 10000));
        } else if (rank == 2) {
            list.add(createItem(ItemName.THOI_VANG, 7000));
            list.add(createItem(ItemName.DANH_HIEU_VIP_2, 1));
            list.add(createItem(ItemName.RUONG_THAN_LINH_KICH_HOAT, 1));
            list.add(createItem(2353, 1));
            list.add(createItem(ItemName.HON_LINH_THU, 7000));
        } else if (rank == 3) {
            list.add(createItem(ItemName.THOI_VANG, 5000));
            list.add(createItem(ItemName.DANH_HIEU_VIP_3, 1));
            list.add(createItem(ItemName.RUONG_THAN_LINH_KICH_HOAT, 1));
            list.add(createItem(2353, 1));
            list.add(createItem(ItemName.HON_LINH_THU, 5000));
        } else if (rank >= 4 && rank <= 10) {
            list.add(createItem(ItemName.RUONG_SET_HUY_DIET, 1));
            list.add(createItem(2353, 1));
        } else if (rank >= 11 && rank <= 20) {
            list.add(createItem(ItemName.DA_BAO_VE_987, 50));
            list.add(createItem(ItemName.NGOC_RONG_1_SAO, 1));
            list.add(createItem(ItemName.NGOC_RONG_2_SAO, 1));
            list.add(createItem(ItemName.NGOC_RONG_3_SAO, 1));
            list.add(createItem(ItemName.NGOC_RONG_4_SAO, 1));
            list.add(createItem(ItemName.NGOC_RONG_5_SAO, 1));
            list.add(createItem(ItemName.NGOC_RONG_6_SAO, 1));
            list.add(createItem(ItemName.NGOC_RONG_7_SAO, 1));
            list.add(createItem(ItemName.THU_BAY, 1));
            list.add(createItem(ItemName.RUONG_1_MON_HUY_DIET, 3));
        } else if (rank >= 21 && rank <= 50) {
            list.add(createItem(ItemName.DA_BAO_VE_987, 20));
            list.add(createItem(ItemName.THOI_VANG, 50));
            list.add(createItem(ItemName.THU_BAY, 1));
            list.add(createItem(ItemName.GIAP_TAP_LUYEN_CAP_4, 1));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.THU_BAY, 1));
            list.add(createItem(ItemName.RUONG_DA_NANG_CAP, 1000));
        }
        return list;
    }

    private static List<Item> itemRewardRaiti(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createItem(ItemName.DANH_HIEU_CAY_TOP_1, 1));
            list.add(createItem(ItemName.NGOC_RONG_1_SAO, 3));
            list.add(createItem(ItemName.NGOC_RONG_2_SAO, 3));
            list.add(createItem(ItemName.NGOC_RONG_3_SAO, 3));
            list.add(createItem(ItemName.NGOC_RONG_4_SAO, 3));
            list.add(createItem(ItemName.NGOC_RONG_5_SAO, 3));
            list.add(createItem(ItemName.NGOC_RONG_6_SAO, 3));
            list.add(createItem(ItemName.NGOC_RONG_7_SAO, 3));
        } else if (rank == 2) {
            list.add(createItem(ItemName.DANH_HIEU_CAY_TOP_2, 1));
            list.add(createItem(ItemName.NGOC_RONG_1_SAO, 2));
            list.add(createItem(ItemName.NGOC_RONG_2_SAO, 2));
            list.add(createItem(ItemName.NGOC_RONG_3_SAO, 2));
            list.add(createItem(ItemName.NGOC_RONG_4_SAO, 2));
            list.add(createItem(ItemName.NGOC_RONG_5_SAO, 2));
            list.add(createItem(ItemName.NGOC_RONG_6_SAO, 2));
            list.add(createItem(ItemName.NGOC_RONG_7_SAO, 2));
        } else if (rank == 3) {
            list.add(createItem(ItemName.DANH_HIEU_CAY_TOP_3, 1));
            list.add(createItem(ItemName.NGOC_RONG_1_SAO, 1));
            list.add(createItem(ItemName.NGOC_RONG_2_SAO, 1));
            list.add(createItem(ItemName.NGOC_RONG_3_SAO, 1));
            list.add(createItem(ItemName.NGOC_RONG_4_SAO, 1));
            list.add(createItem(ItemName.NGOC_RONG_5_SAO, 1));
            list.add(createItem(ItemName.NGOC_RONG_6_SAO, 1));
            list.add(createItem(ItemName.NGOC_RONG_7_SAO, 1));
        } else if (rank >= 4 && rank <= 10) {
            list.add(createItem(ItemName.THOI_VANG, 100));
        } else if (rank >= 11 && rank <= 20) {
            list.add(createItem(ItemName.THOI_VANG, 50));
        } else if (rank >= 21 && rank <= 50) {
            list.add(createItem(ItemName.THOI_VANG, 20));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.THOI_VANG, 10));
        }
        return list;
    }

    private static List<Item> itemRewardDisciple(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createItem(ItemName.DANH_HIEU_THANH_UP_DE, 1));
            list.add(createItem(2355, 1));
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.RUONG_THAN_LINH, 2));
        } else if (rank == 2) {
            list.add(createItem(ItemName.DANH_HIEU_THANH_UP_DE, 1));
            list.add(createItem(2355, 1));
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.RUONG_THAN_LINH, 1));
        } else if (rank == 3) {
            list.add(createItem(ItemName.DANH_HIEU_THANH_UP_DE, 1));
            list.add(createItem(2355, 1));
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.RUONG_THAN_LINH, 1));
        } else if (rank >= 4 && rank <= 10) {
            list.add(createItem(2355, 1));
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.THOI_VANG, 500));
        } else if (rank >= 11 && rank <= 20) {
            list.add(createItem(ItemName.THOI_VANG, 200));
            list.add(createItem(942, 1));
        } else if (rank >= 21 && rank <= 50) {
            list.add(createItem(ItemName.THOI_VANG, 50));
            list.add(createItem(942, 1));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.THOI_VANG, 30));
            list.add(createItem(ItemName.RUONG_DA_NANG_CAP, 200));
        }
        return list;
    }

    private static List<Item> itemRewardKillBoss(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createItem(ItemName.THOI_VANG, 5000));
            list.add(createItem(ItemName.DANH_HIEU_TOP_SAN_BOSS, 1));
        } else if (rank == 2) {
            list.add(createItem(ItemName.THOI_VANG, 3000));
            list.add(createItem(ItemName.DANH_HIEU_TOP_SAN_BOSS, 1));
        } else if (rank == 3) {
            list.add(createItem(ItemName.THOI_VANG, 1000));
            list.add(createItem(ItemName.DANH_HIEU_TOP_SAN_BOSS, 1));
        } else if (rank >= 4 && rank <= 10) {
            list.add(createItem(ItemName.THOI_VANG, 200));
        } else if (rank >= 11 && rank <= 20) {
            list.add(createItem(ItemName.THOI_VANG, 100));
        } else if (rank >= 21 && rank <= 50) {
            list.add(createItem(ItemName.THOI_VANG, 50));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.THOI_VANG, 20));
        }
        return list;
    }

    private static List<Item> itemRewardNuocMiaSizeM(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createItem(2301, 5));
            list.add(createItem(2255, 1));
            list.add(createItem(ItemName.DA_BAO_VE_987, 99));
        } else if (rank == 2) {
            list.add(createItem(2301, 3));
            list.add(createItem(2255, 1));
            list.add(createItem(ItemName.DA_BAO_VE_987, 50));
        } else if (rank == 3) {
            list.add(createItem(2301, 1));
            list.add(createItem(2255, 1));
            list.add(createItem(ItemName.DA_BAO_VE_987, 20));
        } else if (rank >= 4 && rank <= 10) {
            list.add(createItem(2255, 1));
            list.add(createItem(ItemName.THOI_VANG, 100));
        } else if (rank >= 11 && rank <= 50) {
            list.add(createItem(ItemName.THOI_VANG, 50));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.THOI_VANG, 20));
        }
        return list;
    }

    private static List<Item> itemRewardNuocMiaSizeXXL(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createItem(ItemName.RUONG_SKH_NGAU_NHIEN, 10));
            list.add(createItem(942, 1));
            list.add(createItem(1974, 1));
        } else if (rank == 2) {
            list.add(createItem(ItemName.RUONG_SKH_NGAU_NHIEN, 5));
            list.add(createItem(942, 1));
            list.add(createItem(1975, 1));
        } else if (rank == 3) {
            list.add(createItem(ItemName.RUONG_SKH_NGAU_NHIEN, 2));
            list.add(createItem(942, 1));
            list.add(createItem(1976, 1));
        } else if (rank >= 4 && rank <= 10) {
            list.add(createItem(ItemName.RUONG_SKH_NGAU_NHIEN, 1));
            list.add(createItem(942, 1));
        } else if (rank >= 11 && rank <= 20) {
            list.add(createItem(942, 1));
        } else if (rank >= 21 && rank <= 50) {
            list.add(createItem(ItemName.RUONG_DA_NANG_CAP, 2000));
            list.add(createItem(ItemName.THOI_VANG, 30));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.RUONG_DA_NANG_CAP, 1000));
            list.add(createItem(ItemName.THOI_VANG, 10));
        }
        return list;
    }

    private static List<Item> itemRewardDisciplePowerMabu(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createItem(ItemName.RUONG_SET_KICH_HOAT, 1));
            list.add(createItem(ItemName.DANH_HIEU_THANH_UP_DE, 1));
            list.add(createItem(ItemName.HON_LINH_THU, 3000));
            list.add(createItem(942, 1));
        } else if (rank == 2) {
            list.add(createItem(ItemName.RUONG_SET_KICH_HOAT, 1));
            list.add(createItem(ItemName.DANH_HIEU_THANH_UP_DE, 1));
            list.add(createItem(ItemName.HON_LINH_THU, 2000));
            list.add(createItem(942, 1));
        } else if (rank == 3) {
            list.add(createItem(ItemName.RUONG_SET_KICH_HOAT, 1));
            list.add(createItem(ItemName.DANH_HIEU_THANH_UP_DE, 1));
            list.add(createItem(ItemName.HON_LINH_THU, 1000));
            list.add(createItem(942, 1));
        } else if (rank == 4 || rank == 5) {
            list.add(createItem(ItemName.RUONG_SET_HUY_DIET, 1));
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.HON_LINH_THU, 300));
        } else if (rank >= 6 && rank <= 10) {
            list.add(createItem(ItemName.RUONG_1_MON_SKH, 2));
            list.add(createItem(942, 1));
        } else if (rank >= 11 && rank <= 20) {
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.RUONG_DO_CUOI_6_SAO, 1));
            list.add(createItem(ItemName.RUONG_ITEM_CAP_2, 30));
        } else if (rank >= 21 && rank <= 50) {
            list.add(createItem(942, 1));
            list.add(createItem(ItemName.THOI_VANG, 50));
            list.add(createItem(ItemName.RUONG_ITEM_CAP_2, 5));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.RUONG_DO_CUOI_5_SAO, 1));
            list.add(createItem(ItemName.THOI_VANG, 20));
        }
        return list;
    }

    private static List<Item> itemRewardDuocBac(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank >= 1 && rank <= 3) {
            list.add(createItem(ItemName.RUONG_SET_HUY_DIET, 1));
            list.add(createItem(ItemName.DANH_HIEU_NGUOI_GIU_LUA, 1));
            list.add(createItem(2289, 1));
            Item an = createItem(ItemName.HAC_HOA_AN, 1);
            an.options.clear();
            an.options.add(new ItemOption(77, 10));
            an.options.add(new ItemOption(103, 10));
            an.options.add(new ItemOption(50, 10));
            list.add(an);
        } else if (rank >= 4 && rank <= 10) {
            list.add(createItem(ItemName.RUONG_1_MON_HUY_DIET, 3));
            list.add(createItem(2289, 1));
            Item an = createItem(ItemName.HAC_HOA_AN, 1);
            an.options.clear();
            an.options.add(new ItemOption(77, 10));
            an.options.add(new ItemOption(103, 10));
            an.options.add(new ItemOption(50, 10));
            list.add(an);
        } else if (rank >= 11 && rank <= 20) {
            list.add(createItem(ItemName.NGOC_RONG_1_SAO, 14));
            Item an = createItem(ItemName.HAC_HOA_AN, 1);
            an.options.clear();
            an.options.add(new ItemOption(77, 6));
            an.options.add(new ItemOption(103, 6));
            an.options.add(new ItemOption(50, 6));
            list.add(an);
        } else if (rank >= 21 && rank <= 50) {
            list.add(createItem(ItemName.RUONG_ITEM_CAP_2, 20));
            Item an = createItem(ItemName.HAC_HOA_AN, 1);
            an.options.clear();
            an.options.add(new ItemOption(77, 6));
            an.options.add(new ItemOption(103, 6));
            an.options.add(new ItemOption(50, 6));
            list.add(an);
            list.add(createItem(ItemName.HON_LINH_THU, 50));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.THOI_VANG, 20));
        }
        return list;
    }

    private static List<Item> itemRewardDuocVang(int rank) {
        List<Item> list = new ArrayList<>();
        if (rank == 1) {
            list.add(createItem(ItemName.RUONG_THAN_LINH, 2));
            list.add(createItem(ItemName.DANH_HIEU_NGUOI_GIU_LUA, 1));
            list.add(createItem(2252, 1));
            Item halo = createItem(2353, 1);
            halo.options.clear();
            halo.options.add(new ItemOption(77, 8));
            halo.options.add(new ItemOption(103, 8));
            halo.options.add(new ItemOption(50, 8));
            list.add(halo);
        } else if (rank == 2) {
            list.add(createItem(ItemName.RUONG_THAN_LINH, 1));
            list.add(createItem(ItemName.DANH_HIEU_NGUOI_GIU_LUA, 1));
            list.add(createItem(2252, 1));
            Item halo = createItem(2353, 1);
            halo.options.clear();
            halo.options.add(new ItemOption(77, 6));
            halo.options.add(new ItemOption(103, 6));
            halo.options.add(new ItemOption(50, 6));
            list.add(halo);
        } else if (rank == 3) {
            list.add(createItem(ItemName.RUONG_THAN_LINH, 1));
            list.add(createItem(ItemName.DANH_HIEU_NGUOI_GIU_LUA, 1));
            list.add(createItem(2252, 1));
            Item halo = createItem(2353, 1);
            halo.options.clear();
            halo.options.add(new ItemOption(77, 5));
            halo.options.add(new ItemOption(103, 5));
            halo.options.add(new ItemOption(50, 5));
            list.add(halo);
        } else if (rank >= 4 && rank <= 10) {
            list.add(createItem(2304, 3));
            list.add(createItem(ItemName.HON_LINH_THU, 1000));
            list.add(createItem(2252, 1));
            Item halo = createItem(2353, 1);
            halo.options.clear();
            halo.options.add(new ItemOption(77, 5));
            halo.options.add(new ItemOption(103, 5));
            halo.options.add(new ItemOption(50, 5));
            list.add(halo);
        } else if (rank >= 11 && rank <= 20) {
            list.add(createItem(ItemName.HON_LINH_THU, 500));
            list.add(createItem(2252, 1));
            Item halo = createItem(2353, 1);
            halo.options.clear();
            halo.options.add(new ItemOption(77, 5));
            halo.options.add(new ItemOption(103, 5));
            halo.options.add(new ItemOption(50, 5));
            list.add(halo);
        } else if (rank >= 21 && rank <= 50) {
            list.add(createItem(2252, 1));
            list.add(createItem(ItemName.HON_LINH_THU, 50));
        } else if (rank >= 51 && rank <= 100) {
            list.add(createItem(ItemName.THOI_VANG, 50));
            list.add(createItem(ItemName.RUONG_ITEM_CAP_2, 10));
        }
        return list;
    }

    private static Item createItem(int id, int quantity) {
        Item item = new Item(id);
        item.quantity = quantity;
        item.setDefaultOptions();
        switch (id) {
            case 2335:
                item.options.add(new ItemOption(77, 35));
                item.options.add(new ItemOption(103, 35));
                item.options.add(new ItemOption(50, 35));
                break;
            case 2289:
                item.options.add(new ItemOption(77, 35));
                item.options.add(new ItemOption(103, 35));
                item.options.add(new ItemOption(50, 35));
                break;
            case 2355:
                item.options.add(new ItemOption(77, 15));
                item.options.add(new ItemOption(103, 15));
                item.options.add(new ItemOption(50, 15));
                break;
            case 2252:
                item.options.add(new ItemOption(77, 15));
                item.options.add(new ItemOption(103, 15));
                item.options.add(new ItemOption(50, 15));
                break;
            case 2255:
                item.options.add(new ItemOption(77, 35));
                item.options.add(new ItemOption(103, 35));
                item.options.add(new ItemOption(50, 35));
                break;
            case 942:
                item.options.clear();
                item.options.add(new ItemOption(77, 15));
                item.options.add(new ItemOption(103, 15));
                item.options.add(new ItemOption(50, 15));
                break;
            case 1974:
            case 1975:
            case 1976:
                item.options.clear();
                item.options.add(new ItemOption(77, 8));
                item.options.add(new ItemOption(103, 8));
                item.options.add(new ItemOption(50, 8));
                break;
            case 2268:
                item.options.add(new ItemOption(77, 10));
                item.options.add(new ItemOption(103, 10));
                break;
            case 2353:
                item.options.add(new ItemOption(77, 5));
                item.options.add(new ItemOption(103, 5));
                item.options.add(new ItemOption(50, 5));
                break;
            case ItemName.DANH_HIEU_TOP_SAN_BOSS:
                item.options.add(new ItemOption(77, 8));
                item.options.add(new ItemOption(103, 8));
                item.options.add(new ItemOption(50, 8));
                break;
            case ItemName.DANH_HIEU_THANH_UP_DE:
                item.options.clear();
                item.options.add(new ItemOption(77, 10));
                item.options.add(new ItemOption(103, 10));
                item.options.add(new ItemOption(50, 10));
                break;
            case ItemName.DANH_HIEU_NGUOI_GIU_LUA:
                item.options.add(new ItemOption(77, 8));
                item.options.add(new ItemOption(103, 8));
                item.options.add(new ItemOption(50, 8));
                break;
        }
        return item;
    }
}
