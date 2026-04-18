/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.TamThangBa;

import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import com.ngocrong.consts.ItemName;

/**
 *
 * @author Administrator
 */
public class newVQTD {

    public static boolean reward(Player player) {
        if (player == null) {
            return false;
        }

        // Kiểm tra xem có đủ chỗ trống trong túi đồ không
        if (player.getCountEmptyBag() < 1) {
            player.service.sendThongBao("Hành trang không đủ chỗ trống!");
            return false;
        }

        try {
            // Lựa chọn phần thưởng ngẫu nhiên theo tỉ lệ
            int randomValue = Utils.nextInt(10000);
            int cumulativeProbability = 0;

            // 1. Đá nâng cấp 5 loại - 19%
            cumulativeProbability += 1900;
            if (randomValue < cumulativeProbability) {
                Item item = createDNC();
                player.boxCrackBall.add(item);
                return true;
            }

            // 2. Tôm chiên giòn - 1%
            cumulativeProbability += 100;
            if (randomValue < cumulativeProbability) {
                Item item = createTomChienGion();
                player.boxCrackBall.add(item);
                return true;
            }

            // 3. Cải trang Vegeta Hủy Diệt - 3%
            cumulativeProbability += 300;
            if (randomValue < cumulativeProbability) {
                Item item = createCaiTrangVegeta();
                player.boxCrackBall.add(item);
                return true;
            }

            // 4. Rương cải trang - 5%
            cumulativeProbability += 500;
            if (randomValue < cumulativeProbability) {
                Item item = createRuongCaiTrang();
                player.boxCrackBall.add(item);
                return true;
            }

            // 5. Ngọc rồng 5-7 sao - 20%
            cumulativeProbability += 1700;
            if (randomValue < cumulativeProbability) {
                Item item = createNR57();
                player.boxCrackBall.add(item);
                return true;
            }

            // 6. Đeo lưng - 3%
            cumulativeProbability += 300;
            if (randomValue < cumulativeProbability) {
                Item item = createDeoLung();
                player.boxCrackBall.add(item);
                return true;
            }

            // 7. Pet Mèo Hoàng Thượng - 3%
            cumulativeProbability += 300;
            if (randomValue < cumulativeProbability) {
                Item item = createPetMeoHoangThuong();
                player.boxCrackBall.add(item);
                return true;
            }

            // 8. Đùi gà nướng - 3%
            cumulativeProbability += 300;
            if (randomValue < cumulativeProbability) {
                Item item = createDuiGaNuong();
                player.boxCrackBall.add(item);
                return true;
            }

            // 9. Mảnh sổ sưu tầm - 16%
            cumulativeProbability += 1600;
            if (randomValue < cumulativeProbability) {
                Item item = createManhSuuTam();
                player.boxCrackBall.add(item);
                return true;
            }

            // 10. Đá bảo vệ x1 - 3%
            cumulativeProbability += 300;
            if (randomValue < cumulativeProbability) {
                Item item = createDaBaoVe();
                player.boxCrackBall.add(item);
                return true;
            }
            // 10. SPL - 2%
            cumulativeProbability += 200;
            if (randomValue < cumulativeProbability) {
                Item item = createSPL();
                player.boxCrackBall.add(item);
                return true;
            }
            // 11. Cục vàng 5tr-20tr - 14%
            cumulativeProbability += 1400;
            if (randomValue < cumulativeProbability) {
                Item item = createGold();
                player.boxCrackBall.add(item);
                return true;
            }

            // 12. Ngọc rồng 4 sao - 7%
            cumulativeProbability += 700;
            if (randomValue < cumulativeProbability) {
                Item item = createNR4();
                player.boxCrackBall.add(item);
                return true;
            }

            cumulativeProbability += 100;
            if (randomValue < cumulativeProbability) {
                Item item = createNR3();
                player.boxCrackBall.add(item);
                return true;
            }

            cumulativeProbability += 300;
            if (randomValue < cumulativeProbability) {
                Item item = createCaiTrangVegetaThoiTrang();
                player.boxCrackBall.add(item);
                return true;
            }

            // Trường hợp mặc định
            player.boxCrackBall.add(createGold());
            return true;

        } catch (Exception e) {
            player.service.sendThongBao("Có lỗi xảy ra khi nhận phần thưởng!");
            return false;
        }
    }

    /**
     * Tạo cải trang Bạch Ngọc Tiên
     *
     * @return Cải trang Bạch Ngọc Tiên
     */
    private static Item createBachNgocTien() {
        // ID cải trang Bạch Ngọc Tiên
        final int BACH_NGOC_TIEN_ID = 2246;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;
        final int OPT_TNSM = 101; // Sửa từ 19 thành 101 theo yêu cầu

        Item bachNgocTien = new Item(BACH_NGOC_TIEN_ID);
        bachNgocTien.quantity = 1;

        // Thêm chỉ số
        bachNgocTien.options.add(new ItemOption(OPT_HP, 10)); // 10% HP
        bachNgocTien.options.add(new ItemOption(OPT_KI, 10)); // 10% KI
        bachNgocTien.options.add(new ItemOption(OPT_SD, 10)); // 10% SĐ
        bachNgocTien.options.add(new ItemOption(OPT_TNSM, 30 + Utils.nextInt(71))); // 30-100% TNSM

        // 100% HSD (1,3,5,7 ngày)
        int[] days = {1, 3, 5, 7};
        int dayIndex = Utils.nextInt(days.length);
        bachNgocTien.options.add(new ItemOption(93, days[dayIndex])); // Option 93: số ngày HSD

        return bachNgocTien;
    }

    /**
     * Tạo cải trang Hắc Long Saiyan
     *
     * @return Cải trang Hắc Long Saiyan
     */
    private static Item createHacLongSaiyan() {
        // ID cải trang Hắc Long Saiyan
        final int HAC_LONG_SAIYAN_ID = 2247;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;
        final int OPT_GIAP = 94;

        Item hacLongSaiyan = new Item(HAC_LONG_SAIYAN_ID);
        hacLongSaiyan.quantity = 1;

        // Thêm chỉ số
        hacLongSaiyan.options.add(new ItemOption(OPT_HP, 24 + Utils.nextInt(7))); // 24-30% HP
        hacLongSaiyan.options.add(new ItemOption(OPT_KI, 24 + Utils.nextInt(7))); // 24-30% KI
        hacLongSaiyan.options.add(new ItemOption(OPT_SD, 24 + Utils.nextInt(7))); // 24-30% SĐ
        hacLongSaiyan.options.add(new ItemOption(OPT_GIAP, 10 + Utils.nextInt(6))); // 10-15% Giáp
        // 10% VV, 90% HSD
        if (Utils.isTrue(10, 100)) {
            // Vĩnh viễn - không thêm option HSD
        } else {
            // HSD ngẫu nhiên 1-30 ngày
            hacLongSaiyan.options.add(new ItemOption(93, getDays())); // Option 93: số ngày HSD
        }

        return hacLongSaiyan;
    }

    /**
     * Tạo cải trang Thiên Tài Công Nghệ
     *
     * @return Cải trang Thiên Tài Công Nghệ
     */
    private static Item createThienTaiCongNghe() {
        // ID cải trang Thiên Tài Công Nghệ
        final int THIEN_TAI_CONG_NGHE_ID = 2248;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;
        final int OPT_CHI_MANG = 14;

        Item thienTaiCongNghe = new Item(THIEN_TAI_CONG_NGHE_ID);
        thienTaiCongNghe.quantity = 1;

        // Thêm chỉ số
        thienTaiCongNghe.options.add(new ItemOption(OPT_HP, 30 + Utils.nextInt(6))); // 24-30% HP
        thienTaiCongNghe.options.add(new ItemOption(OPT_KI, 30 + Utils.nextInt(6))); // 24-30% KI
        thienTaiCongNghe.options.add(new ItemOption(OPT_SD, 30 + Utils.nextInt(6))); // 24-30% SĐ
//        thienTaiCongNghe.options.add(new ItemOption(226, 2));
        // 10% VV, 90% HSD
        if (Utils.isTrue(10, 100)) {
            // Vĩnh viễn - không thêm option HSD
        } else {
            // HSD ngẫu nhiên 1-30 ngày
            thienTaiCongNghe.options.add(new ItemOption(93, getDays())); // Option 93: số ngày HSD
        }

        return thienTaiCongNghe;
    }

    /**
     * (Thú cưỡi Hắc Kỳ Lân)
     *
     * @return Hắc Kỳ Lân
     */
    private static Item createHacKyLan() {
        // ID Saiyan Cuồng Nộ
        final int HAC_KY_LAN_ID = 2259;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;

        Item saiyancuongno = new Item(HAC_KY_LAN_ID);
        saiyancuongno.quantity = 1;

        // Thêm chỉ số
        saiyancuongno.options.add(new ItemOption(OPT_HP, 4 + Utils.nextInt(5))); // 4-8% HP
        saiyancuongno.options.add(new ItemOption(OPT_KI, 4 + Utils.nextInt(5))); // 4-8% KI
        saiyancuongno.options.add(new ItemOption(OPT_SD, 4 + Utils.nextInt(5))); // 4-8% SĐ

        // 10% VV, 90% HSD
        if (Utils.isTrue(10, 100)) {
            // Vĩnh viễn - không thêm option HSD
        } else {
            // HSD ngẫu nhiên 1-30 ngày
            saiyancuongno.options.add(new ItemOption(93, getDays())); // Option 93: số ngày HSD
        }

        return saiyancuongno;
    }

    /**
     * Tạo Thần trả hủy diệt (Pet Tiểu Xà Vương)
     *
     * @return Thần trả hủy diệt
     */
    private static Item createTieuXaVuong() {
        // ID Thần trả hủy diệt
        final int THAN_TRA_HUY_DIET_ID = 2252;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;
        final int OPT_NE_DON = 108;

        Item thantrahuydiett = new Item(THAN_TRA_HUY_DIET_ID);
        thantrahuydiett.quantity = 1;

        // Thêm chỉ số
        thantrahuydiett.options.add(new ItemOption(OPT_HP, 12 + Utils.nextInt(5))); // 12-16% HP
        thantrahuydiett.options.add(new ItemOption(OPT_KI, 12 + Utils.nextInt(5))); // 12-16% KI
        thantrahuydiett.options.add(new ItemOption(OPT_SD, 12 + Utils.nextInt(5))); // 12-16% SĐ
        thantrahuydiett.options.add(new ItemOption(OPT_NE_DON, 10)); // 10% né đòn

        // 10% VV, 90% HSD
        if (Utils.isTrue(10, 100)) {
            // Vĩnh viễn - không thêm option HSD
        } else {
            // HSD ngẫu nhiên 1-30 ngày

            thantrahuydiett.options.add(new ItemOption(93, getDays())); // Option 93: số ngày HSD
        }

        return thantrahuydiett;
    }

    static int getDays() {
        return Utils.nextInt(1, 3);
    }

    /**
     * Tạo đeo lưng Cờ Ngọc Rồng 2 Sao
     *
     * @return Đeo lưng Cờ Ngọc Rồng 2 Sao
     */
    private static Item createCoNgocRong() {
        // ID đeo lưng Cờ Ngọc Rồng 2 Sao
        final int CO_NGOC_RONG_ID = 2249;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;
        final int OPT_HUT_HP = 95;
        final int OPT_HUT_KI = 96;

        Item coNgocRong = new Item(CO_NGOC_RONG_ID);
        coNgocRong.quantity = 1;

        // Thêm chỉ số
        coNgocRong.options.add(new ItemOption(OPT_HP, 10 + Utils.nextInt(6))); // 10-15% HP
        coNgocRong.options.add(new ItemOption(OPT_KI, 10 + Utils.nextInt(6))); // 10-15% KI
        coNgocRong.options.add(new ItemOption(OPT_SD, 10 + Utils.nextInt(6))); // 10-15% SĐ
        coNgocRong.options.add(new ItemOption(OPT_HUT_HP, 5 + Utils.nextInt(6))); // 5-10% hút HP
        coNgocRong.options.add(new ItemOption(OPT_HUT_KI, 5 + Utils.nextInt(6))); // 5-10% hút KI

        // 10% VV, 90% HSD
        if (Utils.isTrue(10, 100)) {
            // Vĩnh viễn - không thêm option HSD
        } else {
            // HSD ngẫu nhiên 1-30 ngày
            coNgocRong.options.add(new ItemOption(93, getDays())); // Option 93: số ngày HSD
        }

        return coNgocRong;
    }

    /**
     * Tạo đeo lưng Khỉ con vui vẻ
     *
     * @return Đeo lưng Khỉ con vui vẻ
     */
    private static Item createKhiConVuiVe() {
        // ID đeo lưng Khỉ con vui vẻ
        final int KHI_CON_VUI_VE_ID = 2250;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;
        final int OPT_GIAP = 94;

        Item khiConVuiVe = new Item(KHI_CON_VUI_VE_ID);
        khiConVuiVe.quantity = 1;

        // Thêm chỉ số
        khiConVuiVe.options.add(new ItemOption(OPT_HP, 10 + Utils.nextInt(6))); // 10-15% HP
        khiConVuiVe.options.add(new ItemOption(OPT_KI, 10 + Utils.nextInt(6))); // 10-15% KI
        khiConVuiVe.options.add(new ItemOption(OPT_SD, 10 + Utils.nextInt(6))); // 10-15% SĐ
        khiConVuiVe.options.add(new ItemOption(OPT_GIAP, 3 + Utils.nextInt(5))); // 3-7% Giáp

        // 10% VV, 90% HSD
        if (Utils.isTrue(10, 100)) {
            // Vĩnh viễn - không thêm option HSD
        } else {
            // HSD ngẫu nhiên 1-30 ngày
            khiConVuiVe.options.add(new ItemOption(93, getDays())); // Option 93: số ngày HSD
        }

        return khiConVuiVe;
    }

    /**
     * Tạo nước giải khát
     *
     * @return Nước giải khát
     */
    private static Item createKemTraiCay() {
        // ID nước giải khát
        final int KEM_TRAI_CAY_ID = 2261;

        Item nuocMaThuat = new Item(KEM_TRAI_CAY_ID);
        nuocMaThuat.quantity = 1;

        // Đây là vật phẩm tiêu thụ, không cần thêm option
        return nuocMaThuat;
    }

    private static Item createNuocGiaiKhat() {
        // ID nước giải khát
        final int NUOC_GIAI_KHAT_ID = 2251;

        Item nuocGiaiKhat = new Item(NUOC_GIAI_KHAT_ID);
        nuocGiaiKhat.quantity = 1;

        // Đây là vật phẩm tiêu thụ, không cần thêm option
        return nuocGiaiKhat;
    }

    /**
     * Tạo linh thú Tiểu Miêu Linh
     *
     * @return Linh thú Tiểu Miêu Linh
     */
    private static Item createTieuMieuLinh() {
        // ID linh thú Tiểu Miêu Linh
        final int TIEU_MIEU_LINH_ID = 2258;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;

        Item tieuMieuLinh = new Item(TIEU_MIEU_LINH_ID);
        tieuMieuLinh.quantity = 1;

        // Thêm chỉ số
        tieuMieuLinh.options.add(new ItemOption(OPT_HP, 4 + Utils.nextInt(5))); // 4-8% HP
        tieuMieuLinh.options.add(new ItemOption(OPT_KI, 4 + Utils.nextInt(5))); // 4-8% KI
        tieuMieuLinh.options.add(new ItemOption(OPT_SD, 4 + Utils.nextInt(5))); // 4-8% SĐ

        // 10% VV, 90% HSD
        if (Utils.isTrue(10, 100)) {
            // Vĩnh viễn - không thêm option HSD
        } else {
            // HSD ngẫu nhiên 1-30 ngày
            tieuMieuLinh.options.add(new ItemOption(93, getDays())); // Option 93: số ngày HSD
        }

        return tieuMieuLinh;
    }

    private static Item createSPL() {
        var itemID = Utils.nextInt(ItemName.SAO_PHA_LE_DAME_TO_HP, ItemName.SAO_PHA_LE_TNSM);
        Item item = new Item(itemID);
        item.setDefaultOptions();
        return item;
    }

    private static Item createNR() {
        var itemID = Utils.nextInt(ItemName.NGOC_RONG_4_SAO, ItemName.NGOC_RONG_7_SAO);
        Item item = new Item(itemID);
        item.setDefaultOptions();
        return item;
    }

    private static Item createDNC() {
        var itemID = Utils.nextInt(ItemName.DA_LUC_BAO, ItemName.DA_THACH_ANH_TIM);
        Item item = new Item(itemID);
        item.setDefaultOptions();
        return item;
    }

    // ---------- New reward items ----------
    private static Item createTomChienGion() {
        Item item = new Item(ItemName.TOM_CHIEN_GION);
        item.quantity = 1;
        return item;
    }

    private static Item createCaiTrangVegeta() {
        Item item = new Item(ItemName.CAI_TRANG_GOHAN_BEAST);
        item.quantity = 1;
        item.options.add(new ItemOption(77, Utils.nextInt(30, 35))); // HP 30-35%
        item.options.add(new ItemOption(103, Utils.nextInt(30, 35))); // KI 30-35%
        item.options.add(new ItemOption(50, Utils.nextInt(30, 35))); // SĐ 30-35%
        item.options.add(new ItemOption(94, Utils.nextInt(15, 20))); // Giáp 15-20%
        if (!Utils.isTrue(10, 100)) {
            item.options.add(new ItemOption(93, getDays()));
        }
        return item;
    }

    private static Item createRuongCaiTrang() {
        Item ctTDB = new Item(ItemName.CAI_TRANG_THO_DAU_BAC);
        ctTDB.addItemOption(new ItemOption(101, Utils.nextInt(30, 100)));
        int days = Utils.isTrue(1, 3) ? 3 : 1;
        ctTDB.addItemOption(new ItemOption(93, days));
        // item.options.add(new ItemOption(93, getDays()));
        return ctTDB;
    }

    private static Item createNR57() {
        var itemID = Utils.nextInt(ItemName.NGOC_RONG_5_SAO, ItemName.NGOC_RONG_7_SAO);
        Item item = new Item(itemID);
        item.setDefaultOptions();
        return item;
    }

    private static Item createDeoLung() {
        Item item = new Item(ItemName.LUOI_HAI_THAN_CHET);
        item.quantity = 1;
        item.options.add(new ItemOption(77, 10 + Utils.nextInt(6))); // HP 10-15%
        item.options.add(new ItemOption(103, 10 + Utils.nextInt(6))); // KI 10-15%
        item.options.add(new ItemOption(50, 10 + Utils.nextInt(6))); // SĐ 10-15%
        item.options.add(new ItemOption(94, 5 + Utils.nextInt(6)));  // Giáp 5-10%
        if (!Utils.isTrue(10, 100)) {
            item.options.add(new ItemOption(93, getDays()));
        }
        return item;
    }

    private static Item createPetMeoHoangThuong() {
        Item item = new Item(ItemName.PET_MEO_HOANG_THUONG);
        item.quantity = 1;
        item.options.add(new ItemOption(77, 10 + Utils.nextInt(6))); // HP 10-15%
        item.options.add(new ItemOption(103, 10 + Utils.nextInt(6))); // KI 10-15%
        item.options.add(new ItemOption(50, 10 + Utils.nextInt(6))); // SĐ 10-15%
        if (!Utils.isTrue(10, 100)) {
            item.options.add(new ItemOption(93, getDays()));
        }
        return item;
    }

    private static Item createDuiGaNuong() {
        Item item = new Item(ItemName.DUI_GA_THOM_NGON);
        item.quantity = 1;
        return item;
    }

    private static Item createManhSuuTam() {
        int[] ids = {
            ItemName.MANH_KHUNG_LONG,
            ItemName.MANH_LON_LOI,
            ItemName.MANH_QUY_DAT,
            ItemName.MANH_KHUNG_LONG_ME,
            ItemName.MANH_LON_LOI_ME,
            ItemName.MANH_QUY_DAT_ME,
            ItemName.MANH_THAN_LAN_BAY,
            ItemName.MANH_PHI_LONG,
            ItemName.MANH_QUY_BAY,
            ItemName.MANH_LINH_DOC_NHAN,
            ItemName.MANH_LINH_DOC_NHAN2,
            ItemName.MANH_SOI_XAM,
            ItemName.MANH_TRUNG_UY_TRANG,
            ItemName.MANH_NINJA_AO_TIM,
            ItemName.MANH_TRUNG_UY_XANH_LO,
            ItemName.MANH_DOC_NHAN
        };
        int id = ids[Utils.nextInt(ids.length)];
        Item item = new Item(id);
        item.setDefaultOptions();
        return item;
    }

    private static Item createDaBaoVe() {
        return new Item(ItemName.DA_BAO_VE_987);
    }

    private static Item createGold() {
        Item item = new Item(ItemName.VANG);
        item.quantity = 5_000_000 + Utils.nextInt(15_000_001);
        return item;
    }

    private static Item createNR4() {
        Item item = new Item(ItemName.NGOC_RONG_4_SAO);
        item.setDefaultOptions();
        return item;
    }

    private static Item createNR3() {
        Item item = new Item(ItemName.NGOC_RONG_3_SAO);
        item.setDefaultOptions();
        return item;
    }

    private static Item createCaiTrangVegetaThoiTrang() {
        Item item = new Item(ItemName.CAI_TRANG_VEGETA_THOITRANG);
        item.quantity = 1;
        item.options.add(new ItemOption(77, Utils.nextInt(30, 35))); // HP 30-35%
        item.options.add(new ItemOption(103, Utils.nextInt(30, 35))); // KI 30-35%
        item.options.add(new ItemOption(50, Utils.nextInt(30, 35))); // SĐ 30-35%
        item.options.add(new ItemOption(5, Utils.nextInt(1, 15))); // Giáp 15-20%
        if (!Utils.isTrue(10, 100)) {
            item.options.add(new ItemOption(93, getDays()));
        }
        return item;
    }

}
