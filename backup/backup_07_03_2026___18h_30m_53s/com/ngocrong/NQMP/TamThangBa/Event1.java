/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.TamThangBa;

import com.ngocrong.bot.VirtualBot;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.item.ItemTime;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

/**
 *
 * @author Administrator
 */
public class Event1 {

    static final int HOA_ANH_DAO = 2211;
    static final int HOA_SEN = 2212;
    static final int HOA_CAM_TU_CAU = 2213;
    static final int HOA_LILY = 2214;
    static final int BO_HOA_SAC_MAU = 2215; // Cần thay đổi theo ID thực tế
    static final int BO_HOA_SUM_VAY = 2216;
    static final int MANH_CAI_TRANG = 2217;

    public static byte getRadio(Player player) {
        if (player == null || player.zone == null || !player.isHuman()) {
            return 0;
        }
        byte base = 1;
        byte bonus = 1;
        if (player.itemBag[5] != null) {
            if (player.itemBag[5].template.id == 2269) {
                if (player.exitsItemTime(ItemTimeName.NUOC_MAY_MAN)) {
                    bonus = 4;
                }
                bonus = 2;
            }
        }
        if (player.exitsItemTime(ItemTimeName.NUOC_MAY_MAN)) {
            bonus = 2;
        }
        return (byte) (base * bonus);
    }

    public static void mobReward(Player player) {
        if (player == null || player.zone == null || player instanceof VirtualBot) {
            return;
        }
        Zone zone = player.zone;
        int tempId = 0;
        if (Utils.isTrue(getRadio(player), 100)) {
            if (zone.map.isNappa()) {
                tempId = HOA_ANH_DAO;
            } else if (zone.map.isFuture()) {
                tempId = HOA_SEN;
            } else if (zone.map.isCold()) {
                tempId = HOA_CAM_TU_CAU;
            } else if (zone.map.isNormalMap2()) {
                tempId = HOA_LILY;
            }

            if (tempId != 0) {

                Item item = new Item(tempId);
                item.setDefaultOptions();
                item.quantity = 1;

                ItemMap itemMap = new ItemMap(player.zone.autoIncrease++);
                itemMap.item = item;
                itemMap.playerID = Math.abs(player.id);
                itemMap.x = player.getX();
                itemMap.y = player.zone.map.collisionLand(player.getX(), player.getY());
                player.zone.addItemMap(itemMap);
                player.zone.service.addItemMap(itemMap);
            }
        }
    }

    public static void ChangeItem(Player player, byte type) {
        final int SO_LUONG_HOA = 99;
        final int SO_LUONG_THOI_VANG = 5;
        final long SO_TIEN_CAN = 500000000; // 500 triệu

        try {
            // Kiểm tra xem người chơi có đủ các vật phẩm để đổi không
            boolean duHoaAnhDao = player.getItemInBag(HOA_ANH_DAO) != null && player.getItemInBag(HOA_ANH_DAO).quantity >= SO_LUONG_HOA;
            boolean duHoaSen = player.getItemInBag(HOA_SEN) != null && player.getItemInBag(HOA_SEN).quantity >= SO_LUONG_HOA;
            boolean duHoaMaiVang = player.getItemInBag(HOA_CAM_TU_CAU) != null && player.getItemInBag(HOA_CAM_TU_CAU).quantity >= SO_LUONG_HOA;
            boolean duHoaLily = player.getItemInBag(HOA_LILY) != null && player.getItemInBag(HOA_LILY).quantity >= SO_LUONG_HOA;
            boolean duThoiVang = player.getItemInBag(457) != null && player.getItemInBag(457).quantity >= SO_LUONG_THOI_VANG;
            boolean duTien = player.gold >= SO_TIEN_CAN;
            if (type == 1) {
                if (duHoaAnhDao && duHoaSen && duHoaMaiVang && duHoaLily && duTien) {
                    if (player.getCountEmptyBag() > 0) {
                        player.removeItem(player.getItemInBag(HOA_ANH_DAO).indexUI, SO_LUONG_HOA);
                        player.removeItem(player.getItemInBag(HOA_SEN).indexUI, SO_LUONG_HOA);
                        player.removeItem(player.getItemInBag(HOA_CAM_TU_CAU).indexUI, SO_LUONG_HOA);
                        player.removeItem(player.getItemInBag(HOA_LILY).indexUI, SO_LUONG_HOA);
                        player.gold -= SO_TIEN_CAN;
                        Item boHoaSacMau = new Item(BO_HOA_SAC_MAU);
                        boHoaSacMau.setDefaultOptions();
                        boHoaSacMau.quantity = 1;
                        player.addItemBag(boHoaSacMau);
                        player.service.sendMoney();
                        player.service.sendThongBao("Bạn đã đổi thành công Bó hoa sắc màu!");
                    } else {
                        player.service.sendThongBao("Hành trang không đủ chỗ trống!");
                    }
                } else {
                    player.service.dialogMessage("Bạn chưa đủ vật phẩm để đổi \nCần có x99 Hoa anh đào,Hoa sen,Hoa cẩm tú cầu,Hoa Lily \n"
                            + "500tr vàng");
                }
            }
            if (type == 2) {
                if (duHoaAnhDao && duHoaSen && duHoaMaiVang && duHoaLily && duThoiVang) {
                    if (player.getCountEmptyBag() > 0) {
                        player.removeItem(player.getItemInBag(HOA_ANH_DAO).indexUI, SO_LUONG_HOA);
                        player.removeItem(player.getItemInBag(HOA_SEN).indexUI, SO_LUONG_HOA);
                        player.removeItem(player.getItemInBag(HOA_CAM_TU_CAU).indexUI, SO_LUONG_HOA);
                        player.removeItem(player.getItemInBag(HOA_LILY).indexUI, SO_LUONG_HOA);
                        player.removeItem(player.getItemInBag(457).indexUI, SO_LUONG_THOI_VANG);
                        Item boHoaSumVay = new Item(BO_HOA_SUM_VAY);
                        boHoaSumVay.setDefaultOptions();
                        boHoaSumVay.quantity = 1;
                        player.addItemBag(boHoaSumVay);
                        player.service.sendThongBao("Bạn đã đổi thành công Bó hoa sum vầy!");
                    } else {
                        player.service.sendThongBao("Hành trang không đủ chỗ trống!");
                    }
                } else {

                    player.service.dialogMessage("Bạn chưa đủ vật phẩm để đổi \nCần có x99 Hoa anh đào,Hoa sen,Hoa cẩm tú cầu,Hoa Lily \n"
                            + "5 thỏi vàng");
                }
            }
            if (type == 3) {
                var mth = player.getItemInBag(2237);
                var tv = player.getItemInBag(457);
                if (mth == null || tv == null || mth.quantity < 10 || tv.quantity < 5) {
                    player.service.dialogMessage("Bạn chưa đủ vật phẩm để đổi \nCần có 10 mảnh Boss Nữ Thần và 5 thỏi vàng");
                    return;
                }
                player.removeItem(mth.indexUI, 10);
                player.removeItem(tv.indexUI, 5);
                Item babythreeBox = new Item(2231);
                babythreeBox.setDefaultOptions();
                babythreeBox.quantity = 1;
                player.addItemBag(babythreeBox);
                player.service.sendThongBao("Bạn đã đổi thành công Hộp Mù Bé Ba");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean useHopQua(Player player, Item item) {
        if (player == null) {
            return false;
        }

        // ID của Hộp quà
        final int HOP_QUA_ID = 2238;

        // ID các vật phẩm có thể nhận được
        final int CHAN_MENH_THIEN_TU_ID = 2239;
        final int NGOC_BOI_ID = 2151;
        final int LINH_THU_HP_ID = 2240;
        final int LINH_THU_SD_ID = 2241;
        final int LINH_THU_KI_ID = 2242;

        // ID các option
        final int OPT_SD = 0;
        final int OPT_HP = 6;
        final int OPT_KI = 7;
        final int OPT_SD_PERCENT = 50;
        final int OPT_HP_PERCENT = 77;
        final int OPT_KI_PERCENT = 103;

        // Kiểm tra xem có phải Hộp quà không
        if (item.template.id != HOP_QUA_ID) {
            return false;
        }

        // Kiểm tra xem có đủ chỗ trống trong túi đồ không
        if (player.getCountEmptyBag() < 1) {
            player.service.sendThongBao("Hành trang không đủ chỗ trống!");
            return false;
        }

        // Xóa vật phẩm Hộp quà
        player.removeItem(item.indexUI, 1);

        try {
            // Ngẫu nhiên loại vật phẩm
            int randomValue = Utils.nextInt(100);
            Item rewardItem = null;

            if (randomValue < 15) { // 30% cơ hội nhận Chân mệnh thiên tử
                rewardItem = new Item(CHAN_MENH_THIEN_TU_ID);
                rewardItem.quantity = 1;
                rewardItem.options.add(new ItemOption(OPT_HP, 30000));

            } else if (randomValue < 30) { // 30% cơ hội nhận Chân mệnh thiên tử
                rewardItem = new Item(2272);
                rewardItem.quantity = 1;
                rewardItem.options.add(new ItemOption(OPT_KI, 30000));

            } else if (randomValue < 40) { // 30% cơ hội nhận Chân mệnh thiên tử
                rewardItem = new Item(2273);
                rewardItem.quantity = 1;
                rewardItem.options.add(new ItemOption(OPT_SD, 2000));

            } else if (randomValue < 55) { // 30% cơ hội nhận Ngọc bội
                rewardItem = new Item(NGOC_BOI_ID);
                rewardItem.quantity = 1;

                // Ngọc bội: 2-4% HP, KI, SĐ
                int bonusHP = 2 + Utils.nextInt(3); // 2-4%
                int bonusKI = 2 + Utils.nextInt(3); // 2-4%
                int bonusSD = 2 + Utils.nextInt(3); // 2-4%

                rewardItem.options.add(new ItemOption(OPT_HP_PERCENT, bonusHP));
                rewardItem.options.add(new ItemOption(OPT_KI_PERCENT, bonusKI));
                rewardItem.options.add(new ItemOption(OPT_SD_PERCENT, bonusSD));

            } else { // 40% cơ hội nhận Linh thú
                int linhThuType = Utils.nextInt(3);

                switch (linhThuType) {
                    case 0: // Linh thú: 5-10% HP
                        rewardItem = new Item(LINH_THU_HP_ID);
                        rewardItem.quantity = 1;

                        rewardItem.options.add(new ItemOption(OPT_HP_PERCENT, 4 + Utils.nextInt(0, 4))); // 5-10% HP
                        break;

                    case 1: // Linh thú: 5-10% SĐ
                        rewardItem = new Item(LINH_THU_SD_ID);
                        rewardItem.quantity = 1;

                        rewardItem.options.add(new ItemOption(OPT_SD_PERCENT, 4 + Utils.nextInt(0, 4))); // 5-10% SĐ
                        break;

                    case 2: // Linh thú: 5-10% KI
                        rewardItem = new Item(LINH_THU_KI_ID);
                        rewardItem.quantity = 1;

                        rewardItem.options.add(new ItemOption(OPT_KI_PERCENT, 4 + Utils.nextInt(0, 4))); // 5-10% KI
                        break;
                }
            }
            if (Utils.isTrue(9, 10)) {
                rewardItem.options.add(new ItemOption(93, Utils.isTrue(1, 2) ? 1 : 3)); // Option 93: số ngày HSD
            }
            // Thêm vật phẩm vào túi đồ
            player.addItemBag(rewardItem);
            player.service.sendThongBao("Bạn đã nhận được " + rewardItem.template.name);

            return true;
        } catch (Exception e) {
            player.service.sendThongBao("Có lỗi xảy ra khi sử dụng vật phẩm!");
            return false;
        }
    }

    public static boolean useBoHoaSacMau(Player player, int itemUseId) {
        if (player == null) {
            return false;
        }
        final int BO_HOA_SAC_MAU_ID = 2215; // ID Bó hoa sắc màu
        final int MANH_CAI_TRANG_ID = MANH_CAI_TRANG; // ID Mảnh cải trang
        final int MANH_THU_CUOI_ID = 2218; // ID Mảnh thú cưỡi

        if (itemUseId != BO_HOA_SAC_MAU_ID) {
            return false;
        }
        if (player.getCountEmptyBag() < 1) {
            player.service.sendThongBao("Hành trang không đủ chỗ trống!");
            return false;
        }
        var bohoa = player.getItemInBag(BO_HOA_SAC_MAU_ID);
        if (bohoa == null) {
            player.service.sendThongBao("Không tìm thấy vật phẩm!");
            return false;
        }
        player.removeItem(bohoa.indexUI, 1);
        try {
            int randomValue = Utils.nextInt(100);
            Item rewardItem = null;

            if (randomValue < 60) { // 60% xác suất nhận được cải trang hoặc mảnh cải trang
                int caiTrangType = Utils.nextInt(16); // 0-15: các loại vật phẩm khác nhau

                if (caiTrangType == 10) { // Mảnh cải trang
                    rewardItem = new Item(MANH_CAI_TRANG_ID);
                    rewardItem.quantity = 1;
                } else if (caiTrangType < 10) { // Cải trang
                    rewardItem = createCaiTrang(caiTrangType);
                } else { // Thú cưỡi
                    rewardItem = createThuCuoi(caiTrangType - 11);
                }
            } else if (randomValue < 80) { // 20% xác suất nhận được mảnh cải trang
                rewardItem = new Item(MANH_CAI_TRANG_ID);
                rewardItem.quantity = 1;
            } else { // 20% xác suất nhận được mảnh thú cưỡi
                rewardItem = new Item(MANH_THU_CUOI_ID);
                rewardItem.quantity = 1;
            }

            // Thêm vật phẩm vào túi đồ
            player.addItemBag(rewardItem);
            player.service.sendThongBao("Bạn đã nhận được " + rewardItem.template.name);
            player.pointHoaSacMau++;
            player.isChangePoint = true;
            return true;
        } catch (Exception e) {
            player.service.sendThongBao("Có lỗi xảy ra khi sử dụng vật phẩm!");
            return false;
        }
    }

    private static Item createCaiTrang(int caiTrangType) {
        // ID cải trang
        final int CAI_TRANG_ID = 2229;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;
        final int OPT_HUT_HP = 95;
        final int OPT_HUT_KI = 96;
        final int OPT_SDCM = 5;
        final int OPT_SAT_THUONG_LAZE = 197;
        final int OPT_SAT_THUONG_TU_SAT = 196;
        final int OPT_TANG_BANG_HOI = 223; // Cần thay đổi theo ID thực tế cho option tăng cho bang hội

        Item caiTrang = new Item(CAI_TRANG_ID);
        caiTrang.quantity = 1;

        // Thêm thuộc tính hạn sử dụng (HSD/VV)
        // Thêm chỉ số tùy theo loại cải trang
        switch (caiTrangType) {
            case 0: // Cải trang - 30-35% HP
                caiTrang.options.add(new ItemOption(OPT_HP, 30 + Utils.nextInt(0, 6)));
                break;
            case 1: // Cải trang - 30-35% KI
                caiTrang.options.add(new ItemOption(OPT_KI, 30 + Utils.nextInt(0, 6)));
                break;
            case 2: // Cải trang - 30-35% SĐ
                caiTrang.options.add(new ItemOption(OPT_SD, 30 + Utils.nextInt(0, 6)));
                break;
            case 3: // Cải trang - 30-35% SĐ, 10-20% hút HP, KI
                caiTrang.options.add(new ItemOption(OPT_SD, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_HUT_HP, 10 + Utils.nextInt(11)));
                caiTrang.options.add(new ItemOption(OPT_HUT_KI, 10 + Utils.nextInt(11)));
                break;
            case 4: // Cải trang - 30-35% SĐ, 10-20% SĐCM
                caiTrang.options.add(new ItemOption(OPT_SD, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_SDCM, 10 + Utils.nextInt(11)));
                break;
            case 5: // Cải trang - 30-35% KI, 10-20% sát thương laze
                caiTrang.options.add(new ItemOption(OPT_KI, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_SAT_THUONG_LAZE, 10 + Utils.nextInt(11)));
                break;
            case 6: // Cải trang - 30-35% HP, 10-20% sát thương tự sát
                caiTrang.options.add(new ItemOption(OPT_HP, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_SAT_THUONG_TU_SAT, 10 + Utils.nextInt(11)));
                break;
            case 7: // Cải trang - 30-35% HP,KI,SĐ, + thêm 3% HP cho bản thân và thành viên trong bang hội
                caiTrang.options.add(new ItemOption(OPT_HP, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_KI, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_SD, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_TANG_BANG_HOI, 3));
                break;
            case 8: // Cải trang - 30-35% HP,KI,SĐ, + thêm 3% SĐ cho bản thân và thành viên trong bang hội
                caiTrang.options.add(new ItemOption(OPT_HP, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_KI, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_SD, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_TANG_BANG_HOI + 1, 3)); // +1 để phân biệt với option HP
                break;
            case 9: // Cải trang - 30-35% HP,KI,SĐ, + thêm 3% KI cho bản thân và thành viên trong bang hội
                caiTrang.options.add(new ItemOption(OPT_HP, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_KI, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_SD, 30 + Utils.nextInt(0, 6)));
                caiTrang.options.add(new ItemOption(OPT_TANG_BANG_HOI + 2, 3)); // +2 để phân biệt với option HP và SĐ
                break;
        }
        if (Utils.isTrue(9, 10)) {
            caiTrang.options.add(new ItemOption(93, Utils.isTrue(1, 2) ? 1 : 3)); // Option 93: số ngày HSD
        }
        return caiTrang;
    }

    private static Item createThuCuoi(int thuCuoiType) {
        // ID thú cưỡi
        final int THU_CUOI_ID = 2044; // ID thú cưỡi

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;
        final int OPT_HUT_HP = 95;
        final int OPT_HUT_KI = 96;

        Item thuCuoi = new Item(THU_CUOI_ID);
        thuCuoi.quantity = 1;

        // Thêm thuộc tính hạn sử dụng (HSD/VV)
        // Thêm chỉ số tùy theo loại thú cưỡi
        switch (thuCuoiType) {
            case 0: // Thú cưỡi: 3-6% HP
                thuCuoi.options.add(new ItemOption(OPT_HP, 3 + Utils.nextInt(4)));
                break;
            case 1: // Thú cưỡi: 3-6% KI
                thuCuoi.options.add(new ItemOption(OPT_KI, 3 + Utils.nextInt(4)));
                break;
            case 2: // Thú cưỡi: 3-6% SĐ
                thuCuoi.options.add(new ItemOption(OPT_SD, 3 + Utils.nextInt(4)));
                break;
            case 3: // Thú cưỡi: 2-6% Giáp
                thuCuoi.options.add(new ItemOption(94, 2 + Utils.nextInt(5))); // Option 47: % Giáp
                break;
            case 4: // Thú cưỡi: 3-6% HP,KI,SĐ, 10-15% Hút HP,KI
                thuCuoi.options.add(new ItemOption(OPT_HP, 3 + Utils.nextInt(4)));
                thuCuoi.options.add(new ItemOption(OPT_KI, 3 + Utils.nextInt(4)));
                thuCuoi.options.add(new ItemOption(OPT_SD, 3 + Utils.nextInt(4)));
                thuCuoi.options.add(new ItemOption(OPT_HUT_HP, 10 + Utils.nextInt(0, 6)));
                thuCuoi.options.add(new ItemOption(OPT_HUT_KI, 10 + Utils.nextInt(0, 6)));
                break;
        }
        if (Utils.isTrue(9, 10)) {
            thuCuoi.options.add(new ItemOption(93, Utils.isTrue(1, 2) ? 1 : 3)); // Option 93: số ngày HSD
        }
        return thuCuoi;
    }

    public static boolean useManhCaiTrang(Player player) {
        if (player == null) {
            return false;
        }

        // ID mảnh cải trang
        final int MANH_CAI_TRANG_ID = MANH_CAI_TRANG; // ID mảnh cải trang
        var mct = player.getItemInBag(MANH_CAI_TRANG_ID);
        // Kiểm tra xem người chơi có đủ mảnh cải trang không
        if (mct == null || mct.quantity < 10) {
            player.service.sendThongBao("Bạn cần có đủ 10 mảnh cải trang để gộp thành cải trang!");
            return false;
        }

        // Kiểm tra xem có đủ chỗ trống trong túi đồ không
        if (player.getCountEmptyBag() < 1) {
            player.service.sendThongBao("Hành trang không đủ chỗ trống!");
            return false;
        }

        player.removeItem(mct.indexUI, 10);

        try {
            // Chọn loại cải trang ngẫu nhiên (chỉ số vĩnh viễn)
            int caiTrangType = Utils.nextInt(10); // 0-9: các loại cải trang khác nhau
            Item caiTrang = createCaiTrang(caiTrangType);

            // Đảm bảo cải trang từ mảnh luôn vĩnh viễn (xóa option HSD nếu có)
            caiTrang.options.removeIf(opt -> opt.id == 93);

            player.addItemBag(caiTrang);
            player.service.sendThongBao("Bạn đã nhận được 1 cải trang ngẫu nhiên vĩnh viễn!");

            return true;
        } catch (Exception e) {
            player.service.sendThongBao("Có lỗi xảy ra khi gộp mảnh cải trang!");
            return false;
        }
    }

    public static boolean useManhThuCuoi(Player player) {
        if (player == null) {
            return false;
        }

        // ID mảnh thú cưỡi
        final int MANH_THU_CUOI_ID = 2218;
        var mtc = player.getItemInBag(MANH_THU_CUOI_ID);

        // Kiểm tra xem người chơi có đủ mảnh thú cưỡi không
        if (mtc == null || mtc.quantity < 10) {
            player.service.sendThongBao("Bạn cần có đủ 10 mảnh thú cưỡi để gộp thành thú cưỡi!");
            return false;
        }

        // Kiểm tra xem có đủ chỗ trống trong túi đồ không
        if (player.getCountEmptyBag() < 1) {
            player.service.sendThongBao("Hành trang không đủ chỗ trống!");
            return false;
        }

        player.removeItem(mtc.indexUI, 10);

        try {
            // Chọn loại thú cưỡi ngẫu nhiên (chỉ số vĩnh viễn)
            int thuCuoiType = Utils.nextInt(5); // 0-4: các loại thú cưỡi khác nhau
            Item thuCuoi = createThuCuoi(thuCuoiType);

            // Đảm bảo thú cưỡi từ mảnh luôn vĩnh viễn (xóa option HSD nếu có)
            thuCuoi.options.removeIf(opt -> opt.id == 93);

            player.addItemBag(thuCuoi);
            player.service.sendThongBao("Bạn đã nhận được 1 thú cưỡi ngẫu nhiên vĩnh viễn!");

            return true;
        } catch (Exception e) {
            player.service.sendThongBao("Có lỗi xảy ra khi gộp mảnh thú cưỡi!");
            return false;
        }
    }

    public static boolean useBoHoaSumVay(Player player, int itemUseId) {
        if (player == null) {
            return false;
        }

        // ID của Bó hoa sum vầy
        final int BO_HOA_SUM_VAY_ID = 2216;

        // ID các vật phẩm tiêu thụ
        final int BANH_SEN_HONG_ID = 2219;
        final int KEO_HUONG_NHAI_ID = 2220;
        final int TRA_SEN_ID = 2221;
        final int BANH_PIA_ID = 2222;
        final int KEO_GUNG_ID = 2223;
        final int BANH_SU_KEM_ID = 2224;
        final int KEO_DEO_ID = 2225;
        final int KEO_TONG_HOP_ID = 2226;

        // ID đeo lưng, mảnh đeo lưng và hào quang
        final int DEO_LUNG_ID = 2228;
        final int MANH_DEO_LUNG_ID = 2227;
        final int HAO_QUANG_ID = 2230;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;
        final int OPT_GIAP = 94;
        final int OPT_NE_DON = 94;
        final int OPT_HUT_HP = 95;
        final int OPT_HUT_KI = 96;
        final int OPT_SDCM = 5;
        final int OPT_SAT_THUONG_LAZE = 197;
        final int OPT_SAT_THUONG_TU_SAT = 196;

        if (itemUseId != BO_HOA_SUM_VAY_ID) {
            return false;
        }

        if (player.getCountEmptyBag() < 1) {
            player.service.sendThongBao("Hành trang không đủ chỗ trống!");
            return false;
        }

        var boHoaSumVay = player.getItemInBag(BO_HOA_SUM_VAY_ID);
        if (boHoaSumVay == null) {
            player.service.sendThongBao("Không tìm thấy vật phẩm!");
            return false;
        }

        player.removeItem(boHoaSumVay.indexUI, 1);

        try {
            // Ngẫu nhiên loại vật phẩm (tiêu thụ, đeo lưng, mảnh đeo lưng, hào quang)
            int randomType = Utils.nextInt(100);
            Item rewardItem = null;

            if (randomType < 45) { // 45% vật phẩm tiêu thụ
                int foodType = Utils.nextInt(8); // 8 loại vật phẩm tiêu thụ
                int foodId = BANH_SEN_HONG_ID + foodType;
                rewardItem = new Item(foodId);
                rewardItem.quantity = 1;
            } else if (randomType < 80) { // 35% đeo lưng
                rewardItem = createDeoLung(Utils.nextInt(0, 6)); // 6 loại đeo lưng
            } else { // 10% hào quang
                rewardItem = createHaoQuang();
            }

            // Thêm vật phẩm vào túi đồ
            player.addItemBag(rewardItem);
            player.service.sendThongBao("Bạn đã nhận được " + rewardItem.template.name);
            player.pointHoaSumVay++;
            player.isChangePoint = true;
            return true;
        } catch (Exception e) {
            player.service.sendThongBao("Có lỗi xảy ra khi sử dụng vật phẩm!");
            return false;
        }
    }

    /**
     * Tạo vật phẩm Hào quang với chỉ số 3% HP, KI, SĐ
     *
     * @return Item hào quang
     */
    private static Item createHaoQuang() {
        // ID hào quang
        final int HAO_QUANG_ID = 2230;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;

        Item haoQuang = new Item(HAO_QUANG_ID);
        haoQuang.quantity = 1;

        // Thêm thuộc tính hạn sử dụng (HSD/VV)
        // Thêm chỉ số 3% HP, KI, SĐ
        haoQuang.options.add(new ItemOption(OPT_HP, 3));
        haoQuang.options.add(new ItemOption(OPT_KI, 3));
        haoQuang.options.add(new ItemOption(OPT_SD, 3));
        if (Utils.isTrue(9, 10)) {
            haoQuang.options.add(new ItemOption(93, Utils.isTrue(1, 2) ? 1 : 3)); // Option 93: số ngày HSD
        }

        return haoQuang;

    }

    /**
     * Tạo vật phẩm đeo lưng với các chỉ số ngẫu nhiên
     *
     * @param deoLungType loại đeo lưng (0-5)
     * @return Item đeo lưng
     */
    private static Item createDeoLung(int deoLungType) {
        // ID đeo lưng
        final int DEO_LUNG_ID = 2228;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;
        final int OPT_GIAP = 94;
        final int OPT_NE_DON = 108;
        final int OPT_HUT_HP = 95;
        final int OPT_HUT_KI = 96;
        final int OPT_SDCM = 5;
        final int OPT_SAT_THUONG_LAZE = 197;
        final int OPT_SAT_THUONG_TU_SAT = 196;

        Item deoLung = new Item(DEO_LUNG_ID);
        deoLung.quantity = 1;

        // Thêm thuộc tính hạn sử dụng (HSD/VV)
        // Thêm các chỉ số cơ bản cho tất cả các loại đeo lưng
        deoLung.options.add(new ItemOption(OPT_HP, 10 + Utils.nextInt(0, 5))); // 10-15% HP
        deoLung.options.add(new ItemOption(OPT_KI, 10 + Utils.nextInt(0, 5))); // 10-15% KI
        deoLung.options.add(new ItemOption(OPT_SD, 10 + Utils.nextInt(0, 5))); // 10-15% SĐ
        // 5-10% giáp

        // Thêm chỉ số đặc biệt tùy theo loại đeo lưng
        switch (deoLungType) {
            case 0: // Đeo lưng: 10-15% HP,KI,SĐ, 5-10% giáp
                deoLung.options.add(new ItemOption(OPT_GIAP, Utils.nextInt(3, 7)));
                break;
            case 1: // Đeo lưng: 10-15% HP,KI,SĐ, 5-10% né đòn
                deoLung.options.add(new ItemOption(OPT_NE_DON, 5 + Utils.nextInt(0, 5))); // 5-10% né đòn
                break;
            case 2: // Đeo lưng: 10-15% HP,KI,SĐ, 10-15% Hút hp, ki
                deoLung.options.add(new ItemOption(OPT_HUT_HP, 10 + Utils.nextInt(0, 5))); // 10-15% hút HP
                deoLung.options.add(new ItemOption(OPT_HUT_KI, 10 + Utils.nextInt(0, 5))); // 10-15% hút KI
                break;
            case 3: // Đeo lưng: 10-15% HP,KI,SĐ, 2-4% SĐCM
                deoLung.options.add(new ItemOption(OPT_SDCM, 2 + Utils.nextInt(3))); // 2-4% SĐCM
                break;
            case 4: // Đeo lưng: 10-15% HP,KI,SĐ, 2-4% Sát thương laze
                deoLung.options.add(new ItemOption(OPT_SAT_THUONG_LAZE, 2 + Utils.nextInt(3))); // 2-4% Sát thương laze
                break;
            case 5: // Đeo lưng: 10-15% HP,KI,SĐ, 2-4% Sát thương tự sát
            default:
                deoLung.options.add(new ItemOption(OPT_SAT_THUONG_TU_SAT, 2 + Utils.nextInt(3))); // 2-4% Sát thương tự sát
                break;

        }
        if (Utils.isTrue(9, 10)) {
            deoLung.options.add(new ItemOption(93, Utils.isTrue(1, 2) ? 1 : 3)); // Option 93: số ngày HSD
        }
        return deoLung;
    }

    /**
     * Xử lý khi người chơi sử dụng Mảnh đeo lưng (gộp 10 mảnh)
     *
     * @param player người chơi sử dụng vật phẩm
     * @return true nếu sử dụng thành công, false nếu thất bại
     */
    public static boolean useManhDeoLung(Player player) {
        if (player == null) {
            return false;
        }

        // ID mảnh đeo lưng
        final int MANH_DEO_LUNG_ID = 2227; // Cần thay đổi theo ID thực tế

        var manhDeoLung = player.getItemInBag(MANH_DEO_LUNG_ID);

        // Kiểm tra xem người chơi có đủ mảnh đeo lưng không
        if (manhDeoLung == null || manhDeoLung.quantity < 10) {
            player.service.sendThongBao("Bạn cần có đủ 10 mảnh đeo lưng để gộp thành đeo lưng!");
            return false;
        }

        // Kiểm tra xem có đủ chỗ trống trong túi đồ không
        if (player.getCountEmptyBag() < 1) {
            player.service.sendThongBao("Hành trang không đủ chỗ trống!");
            return false;
        }

        player.removeItem(manhDeoLung.indexUI, 10);

        try {
            // Chọn loại đeo lưng ngẫu nhiên (chỉ số vĩnh viễn)
            int deoLungType = Utils.nextInt(0, 5); // 0-5: các loại đeo lưng khác nhau
            Item deoLung = createDeoLung(deoLungType);

            // Đảm bảo đeo lưng từ mảnh luôn vĩnh viễn (xóa option HSD nếu có)
            deoLung.options.removeIf(opt -> opt.id == 93);

            player.addItemBag(deoLung);
            player.service.sendThongBao("Bạn đã nhận được 1 đeo lưng ngẫu nhiên vĩnh viễn!");

            return true;
        } catch (Exception e) {
            player.service.sendThongBao("Có lỗi xảy ra khi gộp mảnh đeo lưng!");
            return false;
        }
    }

    public static boolean useHopMuBeBa(Player player, Item item) {
        if (player == null) {
            return false;
        }

        // ID của Hộp Mù Bé Ba
        final int HOP_MU_BE_BA_ID = 2231;

        // Kiểm tra xem có phải Hộp Mù Bé Ba không
        if (item.template.id != HOP_MU_BE_BA_ID) {
            return false;
        }

        // Kiểm tra xem có đủ chỗ trống trong túi đồ không
        if (player.getCountEmptyBag() < 1) {
            player.service.sendThongBao("Hành trang không đủ chỗ trống!");
            return false;
        }

        // Xóa vật phẩm Hộp Mù Bé Ba
        player.removeItem(item.indexUI, 1);

        try {
            // Ngẫu nhiên loại vật phẩm (pet hoặc thú cưỡi)
            int randomValue = Utils.nextInt(100);
            Item rewardItem = null;
            rewardItem = createRandomPet();
            player.addItemBag(rewardItem);
            player.service.sendThongBao("Bạn đã nhận được " + rewardItem.template.name);

            return true;
        } catch (Exception e) {
            player.service.sendThongBao("Có lỗi xảy ra khi sử dụng vật phẩm!");
            return false;
        }
    }

    /**
     * Tạo ngẫu nhiên một pet từ danh sách có sẵn
     *
     * @return Item pet
     */
    private static Item createRandomPet() {
        // ID các pet
        final int PET_BUNNY_BABY_ID = 2209;
        final int PET_BABY_RONG_XANH_ID = 2210;
        final int PET_PANDA_CHAN_1_ID = 2235;
        final int PET_PANDA_CHAN_2_ID = 2236;
        final int PET_BUNNY_BABY_SECRET_ID = 2234;
        final int PET_BEARCHU_SECRET_ID = 2233;
        final int PET_BABY_RONG_XANH_SECRET_ID = 2232;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;
        final int OPT_GIAP = 94;
        final int OPT_NE_DON = 108;
        final int OPT_SDCM = 5;
        final int OPT_CM = 14; // Chí mạng
        final int OPT_SAT_THUONG_LAZE = 197;
        final int OPT_SAT_THUONG_TU_SAT = 196;

        // Chọn ngẫu nhiên một pet
        int petType = Utils.nextInt(7);
        Item pet = null;

        switch (petType) {
            case 0: // Pet Bunny Baby: 12-15% HP, 2-6% giáp, 5-10% né đòn
                pet = new Item(PET_BUNNY_BABY_ID);
                pet.quantity = 1;

                pet.options.add(new ItemOption(OPT_HP, 2 + Utils.nextInt(8))); // 12-15% HP
                pet.options.add(new ItemOption(OPT_KI, 2 + Utils.nextInt(8))); // 2-6% giáp
                pet.options.add(new ItemOption(OPT_SD, 2 + Utils.nextInt(8))); // 5-10% né đòn
                break;

            case 1: // Pet Baby Rồng Xanh: 12-15% Sức đánh, 2-6% SĐCM, 5-10% né đòn
                pet = new Item(PET_BABY_RONG_XANH_ID);
                pet.quantity = 1;

                pet.options.add(new ItemOption(OPT_HP, 2 + Utils.nextInt(8))); // 12-15% HP
                pet.options.add(new ItemOption(OPT_KI, 2 + Utils.nextInt(8))); // 2-6% giáp
                pet.options.add(new ItemOption(OPT_SD, 2 + Utils.nextInt(8))); // 5-10% né đòn
                pet.options.add(new ItemOption(OPT_CM, 2 + Utils.nextInt(8)));
                break;

            case 2: // Pet Panda Chan 1: 12-15% KI, 2-6% Sát thương Laze, 5-10% né đòn
                pet = new Item(PET_PANDA_CHAN_1_ID);
                pet.quantity = 1;

                pet.options.add(new ItemOption(OPT_HP, 2 + Utils.nextInt(8))); // 12-15% HP
                pet.options.add(new ItemOption(OPT_KI, 2 + Utils.nextInt(8))); // 2-6% giáp
                pet.options.add(new ItemOption(OPT_SD, 2 + Utils.nextInt(8)));
                pet.options.add(new ItemOption(OPT_NE_DON, 2 + Utils.nextInt(8))); // 5-10% né đòn
                break;

            case 3: // Pet Panda Chan 2: 12-15% HP, 2-6% Sát thương chiêu Tự Sát, 5-10% né đòn
                pet = new Item(PET_PANDA_CHAN_2_ID);
                pet.quantity = 1;
                pet.options.add(new ItemOption(OPT_HP, 2 + Utils.nextInt(8))); // 12-15% HP
                pet.options.add(new ItemOption(OPT_KI, 2 + Utils.nextInt(8))); // 2-6% giáp
                pet.options.add(new ItemOption(OPT_SD, 2 + Utils.nextInt(8)));
                pet.options.add(new ItemOption(95, 2 + Utils.nextInt(8)));
                pet.options.add(new ItemOption(96, 2 + Utils.nextInt(8)));
                break;

            case 4: // Pet Bunny Baby Secret: 15-18% HP, Sức đánh, 10% Sức đánh chí mạng, 5-10% Chí mạng
                pet = new Item(PET_BUNNY_BABY_SECRET_ID);
                pet.quantity = 1;
                pet.options.add(new ItemOption(OPT_HP, 2 + Utils.nextInt(10))); // 12-15% HP
                pet.options.add(new ItemOption(OPT_KI, 2 + Utils.nextInt(10))); // 2-6% giáp
                pet.options.add(new ItemOption(OPT_SD, 2 + Utils.nextInt(10)));
                pet.options.add(new ItemOption(OPT_SDCM, 2 + Utils.nextInt(10))); // 10% Sức đánh chí mạng
                break;

            case 5: // Pet BearChu Secret: 15-18% Sức đánh, KI, 10% Sát thương chiêu Laze, 5-10% chí mạng
                pet = new Item(PET_BEARCHU_SECRET_ID);
                pet.quantity = 1;
                pet.options.add(new ItemOption(OPT_HP, 2 + Utils.nextInt(8))); // 12-15% HP
                pet.options.add(new ItemOption(OPT_KI, 2 + Utils.nextInt(8))); // 2-6% giáp
                pet.options.add(new ItemOption(OPT_SD, 2 + Utils.nextInt(8)));
                break;

            case 6: // Pet Baby Rồng Xanh Secret: 15-18% HP, 5% giáp, 10% Sát thương chiêu tự sát
                pet = new Item(PET_BABY_RONG_XANH_SECRET_ID);
                pet.quantity = 1;
                pet.options.add(new ItemOption(OPT_HP, 2 + Utils.nextInt(8))); // 12-15% HP
                pet.options.add(new ItemOption(OPT_KI, 2 + Utils.nextInt(8))); // 2-6% giáp
                pet.options.add(new ItemOption(OPT_SD, 2 + Utils.nextInt(8)));
                break;
        }
        return pet;
    }

    /**
     * Tạo ngẫu nhiên một thú cưỡi Hỏa Long Thần Vương
     *
     * @return Item thú cưỡi
     */
    private static Item createRandomHoaLong() {
        // ID thú cưỡi Hỏa Long Thần Vương
        final int HOA_LONG_THAN_VUONG_ID = 2044;

        // ID options
        final int OPT_HP = 77;
        final int OPT_KI = 103;
        final int OPT_SD = 50;
        final int OPT_GIAP = 94;
        final int OPT_SDCM = 5;
        final int OPT_SAT_THUONG_LAZE = 197;
        final int OPT_SAT_THUONG_TU_SAT = 196;
        final int OPT_HUT_HP = 95;
        final int OPT_HUT_KI = 96;
        final int OPT_TNSM = 101; // Tăng nhanh sinh mệnh (TNSM)

        Item hoaLong = new Item(HOA_LONG_THAN_VUONG_ID);
        hoaLong.quantity = 1;

        // Thêm thuộc tính hạn sử dụng (HSD/VV)
        // Thêm thuộc tính cơ bản 5-8% HP, KI, SĐ cho tất cả các loại
        hoaLong.options.add(new ItemOption(OPT_HP, 5 + Utils.nextInt(4))); // 5-8% HP
        hoaLong.options.add(new ItemOption(OPT_KI, 5 + Utils.nextInt(4))); // 5-8% KI
        hoaLong.options.add(new ItemOption(OPT_SD, 5 + Utils.nextInt(4))); // 5-8% SĐ

        // Ngẫu nhiên loại thú cưỡi Hỏa Long
        int hoaLongType = Utils.nextInt(7);

        switch (hoaLongType) {
            case 0: // 5-8% HP,KI,SĐ - 2-4% Sức đánh chí mạng
                hoaLong.options.add(new ItemOption(OPT_SDCM, 2 + Utils.nextInt(3))); // 2-4% Sức đánh chí mạng
                break;

            case 1: // 5-8% HP,KI,SĐ - 2-4% Sát thương laze
                hoaLong.options.add(new ItemOption(OPT_SAT_THUONG_LAZE, 2 + Utils.nextInt(3))); // 2-4% Sát thương laze
                break;

            case 2: // 5-8% HP,KI,SĐ - 2-4% sát thương chiêu tự sát
                hoaLong.options.add(new ItemOption(OPT_SAT_THUONG_TU_SAT, 2 + Utils.nextInt(3))); // 2-4% sát thương chiêu tự sát
                break;

            case 3: // 5-8% HP,KI,SĐ - 2-4% giáp
                hoaLong.options.add(new ItemOption(OPT_GIAP, 2 + Utils.nextInt(3))); // 2-4% giáp
                break;

            case 4: // 5-8% HP,KI,SĐ - 15% TNSM
                hoaLong.options.add(new ItemOption(OPT_TNSM, 15)); // 15% TNSM
                break;

            case 5: // 5-8% HP,KI,SĐ - 2-4% sức đánh chí mạng, giáp, laze, tự sát, 15% HÚT HP,KI
                hoaLong.options.add(new ItemOption(OPT_SDCM, 2 + Utils.nextInt(3))); // 2-4% sức đánh chí mạng
                hoaLong.options.add(new ItemOption(OPT_GIAP, 2 + Utils.nextInt(3))); // 2-4% giáp
                hoaLong.options.add(new ItemOption(OPT_SAT_THUONG_LAZE, 2 + Utils.nextInt(3))); // 2-4% Sát thương laze
                hoaLong.options.add(new ItemOption(OPT_SAT_THUONG_TU_SAT, 2 + Utils.nextInt(3))); // 2-4% sát thương chiêu tự sát
                hoaLong.options.add(new ItemOption(OPT_HUT_HP, 15)); // 15% HÚT HP
                hoaLong.options.add(new ItemOption(OPT_HUT_KI, 15)); // 15% HÚT KI
                break;

            case 6: // 5-8% HP,KI,SĐ - 2-4% sức đánh chí mạng, giáp, laze, tự sát, 15% HÚT HP,KI, TNSM
                hoaLong.options.add(new ItemOption(OPT_SDCM, 2 + Utils.nextInt(3))); // 2-4% sức đánh chí mạng
                hoaLong.options.add(new ItemOption(OPT_GIAP, 2 + Utils.nextInt(3))); // 2-4% giáp
                hoaLong.options.add(new ItemOption(OPT_SAT_THUONG_LAZE, 2 + Utils.nextInt(3))); // 2-4% Sát thương laze
                hoaLong.options.add(new ItemOption(OPT_SAT_THUONG_TU_SAT, 2 + Utils.nextInt(3))); // 2-4% sát thương chiêu tự sát
                hoaLong.options.add(new ItemOption(OPT_HUT_HP, 15)); // 15% HÚT HP
                hoaLong.options.add(new ItemOption(OPT_HUT_KI, 15)); // 15% HÚT KI
                hoaLong.options.add(new ItemOption(OPT_TNSM, 15)); // 15% TNSM
                break;
        }
        if (hoaLong != null && hoaLong.options != null && Utils.isTrue(9, 10)) {
            hoaLong.options.add(new ItemOption(93, Utils.isTrue(1, 2) ? 1 : 3)); // Option 93: số ngày HSD
        }

        return hoaLong;
    }

    /**
     * Cập nhật hàm useItem để xử lý thêm các vật phẩm mới
     *
     * @param player người chơi sử dụng vật phẩm
     * @return true nếu sử dụng thành công, false nếu thất bại
     */
    public static boolean useItem(Player player, Item item) {
        if (player == null || item.template.id <= 0) {
            return false;
        }

        // ID của các vật phẩm
        final int BO_HOA_SAC_MAU_ID = 2215;
        final int BO_HOA_SUM_VAY_ID = 2216;
        final int MANH_CAI_TRANG_ID = MANH_CAI_TRANG;
        final int MANH_THU_CUOI_ID = 2218;
        final int MANH_DEO_LUNG_ID = 2227;
        final int BANH_SEN_HONG_ID = 2219;
        final int KEO_TONG_HOP_ID = 2226;
        final int HOP_MU_BE_BA_ID = 2231;
        final int HOP_QUA_ID = 2238;
        final int NUOC_GIAI_KHAT = 2251;
        final int NUOC_MA_THUAT = 2243;
        final int KEM_TRAI_CAY = 2261;
        var itemUse = player.getItemInBag(item.template.id);
        if (itemUse == null || itemUse.quantity < 1) {
            return false;
        }
        // Xử lý dựa vào loại vật phẩm
        if (item.template.id == BO_HOA_SAC_MAU_ID) {
            return useBoHoaSacMau(player, item.template.id);
        } else if (item.template.id == BO_HOA_SUM_VAY_ID) {
            return useBoHoaSumVay(player, item.template.id);
        } else if (item.template.id == MANH_CAI_TRANG_ID) {
            return useManhCaiTrang(player);
        } else if (item.template.id == MANH_THU_CUOI_ID) {
            return useManhThuCuoi(player);
        } else if (item.template.id == MANH_DEO_LUNG_ID) {
            return useManhDeoLung(player);
        } else if (item.template.id >= BANH_SEN_HONG_ID && item.template.id <= KEO_TONG_HOP_ID) {

            player.removeItem(itemUse.indexUI, 1);
            byte[] itemTimes = new byte[]{
                ItemTimeName.BANH_SEN_HONG,
                ItemTimeName.KEO_HUONG_NHAI,
                ItemTimeName.TRA_SEN,
                ItemTimeName.BANH_PIA,
                ItemTimeName.KEO_GUNG,
                ItemTimeName.BANH_SU_KEM,
                ItemTimeName.KEO_DEO,
                ItemTimeName.KEO_TONG_HOP
            };
            player.setItemTime(itemTimes[item.template.id - BANH_SEN_HONG_ID], item.template.iconID, true, 60 * 10);
            return true;
        } else if (item.template.id == HOP_MU_BE_BA_ID) {
            return useHopMuBeBa(player, item);
        } else if (item.template.id == HOP_QUA_ID) {
            return useHopQua(player, item);
        } else if (item.template.id == NUOC_GIAI_KHAT) {
            player.removeItem(itemUse.indexUI, 1);
            player.setItemTime(ItemTimeName.NUOC_GIAI_KHAT, item.template.iconID, true, 60 * 10);
        } else if (item.template.id == KEM_TRAI_CAY) {
            player.removeItem(itemUse.indexUI, 1);
            player.setItemTime(ItemTimeName.KEM_TRAI_CAY, item.template.iconID, true, 60 * 10);
        }
        return false;
    }
}
