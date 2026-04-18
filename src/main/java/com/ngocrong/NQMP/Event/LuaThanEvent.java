package com.ngocrong.NQMP.Event;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.MapName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.mob.Mob;
import com.ngocrong.server.Config;
import com.ngocrong.server.SessionManager;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Sự kiện Lửa Thần - chế tạo và mở đuốc lễ hội
 */
public class LuaThanEvent {

    private static final int[] WOOD_MAPS = {17, 18, 27, 28, 35, 36};
    private static final Set<Player> PARTICIPANTS = new HashSet<>();
    public static int torchCount = 0;

    private static boolean contains(int[] arr, int val) {
        for (int i : arr) {
            if (i == val) {
                return true;
            }
        }
        return false;
    }

    private static void dropItem(Player player, Mob mob, Item item) {
        item.setDefaultOptions();
        item.quantity = 1;
        ItemMap map = new ItemMap(player.zone.autoIncrease++);
        map.item = item;
        map.playerID = Math.abs(player.id);
        map.x = player.getX();
        map.y = player.zone.map.collisionLand(player.getX(), player.getY());
        player.zone.addItemMap(map);
        mob.items.add(map);
    }

    public static void mobReward(Player player, Mob mob) {
        if (true) {
            return;
        }
        if (player == null || player.zone == null || mob == null) {
            return;
        }
        int mapId = player.zone.map.mapID;
        if (contains(WOOD_MAPS, mapId) && Utils.isTrue(1, 100)) {
            dropItem(player, mob, new Item(ItemName.CUI_KHO));
        }
        if ((mapId == MapName.NGU_HANH_SON || mapId == MapName.NGU_HANH_SON_2 || mapId == MapName.NGU_HANH_SON_3)
                && Utils.isTrue(1, 300)) {
            dropItem(player, mob, new Item(ItemName.NGON_LUA));
        }
    }

    public static void bossReward(Player player, Boss boss) {
        if (true) {
            return;
        }
        if (player == null || boss == null || boss.zone == null) {
            return;
        }
        Item it = new Item(ItemName.NGON_DUOC);
        it.setDefaultOptions();
        it.quantity = Utils.nextInt(5, 10);
        ItemMap map = new ItemMap(boss.zone.autoIncrease++);
        map.item = it;
        map.playerID = Math.abs(player.id);
        map.x = boss.getX();
        map.y = boss.zone.map.collisionLand(boss.getX(), boss.getY());
        boss.zone.addItemMap(map);
        boss.zone.service.addItemMap(map);
    }

    public static void combine(Player player, byte type) {
        if (player == null) {
            return;
        }
        if (player.getCountEmptyBag() == 0) {
            player.service.dialogMessage("Hành trang đã đầy");
            return;
        }
        Item cui = player.getItemInBag(ItemName.CUI_KHO);
        Item lua = player.getItemInBag(ItemName.NGON_LUA);
        Item duoc = player.getItemInBag(ItemName.NGON_DUOC);
        if (type == 1) {
            if (cui == null || lua == null || duoc == null || cui.quantity < 5 || lua.quantity < 5 || duoc.quantity < 5
                    || player.gold < 500_000_000L) {
                player.service.sendThongBao("Bạn không đủ nguyên liệu");
                return;
            }
            player.removeItem(cui.indexUI, 5);
            player.removeItem(lua.indexUI, 5);
            player.removeItem(duoc.indexUI, 5);
            player.subGold(500_000_000L);
            Item item = new Item(ItemName.NGOC_DUOC_LE_HOI_BAC);
            item.setDefaultOptions();
            player.addItem(item);
            player.service.sendThongBao("Bạn nhận được " + item.template.name);
        } else if (type == 2) {
            Item bar = player.getItemInBag(ItemName.THOI_VANG);
            if (cui == null || lua == null || duoc == null || bar == null || cui.quantity < 10 || lua.quantity < 10
                    || duoc.quantity < 10 || bar.quantity < 5) {
                player.service.sendThongBao("Bạn không đủ nguyên liệu");
                return;
            }
            player.removeItem(cui.indexUI, 10);
            player.removeItem(lua.indexUI, 10);
            player.removeItem(duoc.indexUI, 10);
            player.removeItem(bar.indexUI, 5);
            Item item = new Item(ItemName.NGON_DUOC_LE_HOI_VANG);
            item.setDefaultOptions();
            player.addItem(item);
            player.service.sendThongBao("Bạn nhận được " + item.template.name);
        }
    }

    public static boolean useItem(Player player, Item item) {
        if (player == null || item == null) {
            return false;
        }
        if (player.getCountEmptyBag() == 0) {
            player.service.dialogMessage("Hành trang đã đầy");
            return false;
        }
        if (item.template.id == ItemName.NGOC_DUOC_LE_HOI_BAC) {
            player.removeItem(item.indexUI, 1);
            openSilver(player);
            registerUse(player);
            return true;
        }
        if (item.template.id == ItemName.NGON_DUOC_LE_HOI_VANG) {
            player.removeItem(item.indexUI, 1);
            openGold(player);
            registerUse(player);
            return true;
        }
        return false;
    }

    private static void registerUse(Player p) {
        if (torchCount == -1) {
            return;
        }
        PARTICIPANTS.add(p);
        torchCount++;
        if (torchCount >= 2000) {
            for (Player pl : PARTICIPANTS) {
                if (pl != null) {
                    Item goldbar = new Item(ItemName.THOI_VANG);
                    goldbar.quantity = Utils.nextInt(1, 5);
                    goldbar.setDefaultOptions();
                    pl.addItem(goldbar);
                    pl.service.sendThongBao("Bạn nhận được x" + goldbar.quantity + " Thỏi vàng");
                }
            }
            SessionManager.chatVip("Mừng server đạt 2000 ngọc đuốc được thắp , phần thưởng gửi tới mọi người");
            PARTICIPANTS.clear();
            torchCount = -1;
        }
    }

    private static void openSilver(Player p) {

//        p.duocBac++;
//        p.isChangePoint = true;
        int r = Utils.nextInt(100);
        Item reward;

        // Chia đều 8 nhóm, mỗi nhóm 12.5%
        if (r < 11) { // 12% - Ngọc rồng 4-7 sao
            reward = new Item(Utils.nextInt(ItemName.NGOC_RONG_4_SAO, ItemName.NGOC_RONG_7_SAO));
            reward.setDefaultOptions();
        } else if (r < 22) { // 13% - Vật phẩm kháng Xinbato
            reward = new Item(ItemName._VAT_PHAM_KHANG_XINBATO);
            reward.setDefaultOptions();
        } else if (r < 33) { // 12% - Vật phẩm kháng Drabula
            reward = new Item(ItemName.VAT_PHAM_KHANG_DRABULA);
            reward.setDefaultOptions();
        } else if (r < 44) { // 13% - Đá bảo vệ 987
            reward = new Item(ItemName.DA_BAO_VE_987);
            reward.setDefaultOptions();
        } else if (r < 55) { // 12% - Băng thạch
            reward = new Item(ItemName.BANG_THACH);
            reward.setDefaultOptions();
        } else if (r < 66) { // 13% - Cải trang
            reward = createCaiTrang();
        } else if (r < 77) { // 12% - Items 381-385
            int[] arr = {381, 382, 383, 384, 385};
            reward = new Item(arr[Utils.nextInt(arr.length)]);
            reward.setDefaultOptions();
        } else if (r < 88) { // 12% - Items 381-385
            reward = createPet();
        } else { // 13% - Hồn lửa
            reward = new Item(ItemName.HON_LUA);
            reward.setDefaultOptions();
        }

        p.addItem(reward);
        p.service.sendThongBao("Bạn nhận được " + reward.template.name);
    }

    private static void openGold(Player p) {
        int r = Utils.nextInt(100);
        Item reward;
//        p.duocVang++;
//        p.isChangePoint = true;
        // Chia đều 9 nhóm, mỗi nhóm ~11.11%
        if (r < 12) { // 11% - Items 1021-1023
            int[] arr = {1021, 1022, 1023};
            reward = new Item(arr[Utils.nextInt(arr.length)]);
            reward.setDefaultOptions();
        } else if (r < 25) { // 11% - Ngọc rồng 3 sao
            reward = new Item(ItemName.NGOC_RONG_3_SAO);
            reward.setDefaultOptions();
        } else if (r < 37) { // 11% - Tinh thạch
            reward = new Item(ItemName.TINH_THACH);
            reward.setDefaultOptions();
        } else if (r < 50) { // 11% - Hỏa thạch
            reward = new Item(ItemName.HOA_THACH);
            reward.setDefaultOptions();
        } else if (r < 62) { // 11% - Ngọc thạch
            reward = new Item(ItemName.NGOC_THACH);
            reward.setDefaultOptions();
        } else if (r < 75) { // 11% - Kim thạch
            reward = new Item(ItemName.KIM_THACH);
            reward.setDefaultOptions();
        } else if (r < 87) { // 11% - Huyết thạch
            reward = new Item(ItemName.HUYET_THACH);
            reward.setDefaultOptions();
        } else { // 12% - Hắc hỏa án
            reward = new Item(ItemName.HAC_HOA_AN);
            reward.options.add(new ItemOption(77, Utils.nextInt(1, 5)));
            reward.options.add(new ItemOption(103, Utils.nextInt(1, 5)));
            reward.options.add(new ItemOption(50, Utils.nextInt(1, 5)));
            int[] opt = {5, 196, 197};
            reward.options.add(new ItemOption(opt[Utils.nextInt(opt.length)], Utils.nextInt(5, 10)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 3)));
            }
        }

        p.addItem(reward);
        p.service.sendThongBao("Bạn nhận được " + reward.template.name);
    }

    static Item createCaiTrang() {
        Item item = new Item(ItemName.CAI_TRANG_VEGETA);
        item.options.add(new ItemOption(77, Utils.nextInt(30, 35)));
        item.options.add(new ItemOption(103, Utils.nextInt(30, 35)));
        item.options.add(new ItemOption(50, Utils.nextInt(30, 35)));
        if (Utils.isTrue(90, 100)) {
            item.options.add(new ItemOption(93, Utils.nextInt(1, 3)));
        }
        return item;
    }

    static Item createPet() {
        int[] array = new int[]{2252, ItemName.PET_CA_XANH, ItemName.PET_CA_CAM};
        int[] options = new int[]{5, 196, 197};
        int index = Utils.nextInt(array.length);
        Item item = new Item(array[index]);
        item.options.add(new ItemOption(77, Utils.nextInt(10, 15)));
        item.options.add(new ItemOption(103, Utils.nextInt(10, 15)));
        item.options.add(new ItemOption(50, Utils.nextInt(10, 15)));
        item.options.add(new ItemOption(options[index], Utils.nextInt(5, 10)));
        if (Utils.isTrue(95, 100)) {
            item.options.add(new ItemOption(93, Utils.nextInt(1, 3)));
        }
        return item;
    }
}
