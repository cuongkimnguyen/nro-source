package com.ngocrong.combine;

import com.ngocrong.consts.CMDMenu;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.item.ItemTemplate;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.server.DragonBall;
import com.ngocrong.util.Utils;

public class DoiDoKichHoat extends Combine {

    public static int[] SKHTD = new int[]{40, 40, 20};
    public static int[] SKHNM = new int[]{20, 20, 60};
    public static int[] SKHXD = new int[]{20, 60, 20};
    static final int goldRequire = 500_000_000;
    
    private static final int[] LEVEL_WEIGHTS = {
        150,
        150,
        150,
        120,
        120,
        100,
        80,
        60,
        40,
        20,
        5,
        5
    };
    
    private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 12;
    private static final int MIN_TYPE = 0;
    private static final int MAX_TYPE = 4;

    public static boolean setSKHTD(int txh, int krilin, int sgk) {
        if (txh + krilin + sgk != 100) {
            return false;
        }
        if (txh < 0 || krilin < 0 || sgk < 0) {
            return false;
        }
        SKHTD[0] = txh;
        SKHTD[1] = krilin;
        SKHTD[2] = sgk;
        return true;
    }

    public static boolean setSKHNM(int pcl, int octiu, int daimao) {
        if (pcl + octiu + daimao != 100) {
            return false;
        }
        if (pcl < 0 || octiu < 0 || daimao < 0) {
            return false;
        }
        SKHNM[0] = pcl;
        SKHNM[1] = octiu;
        SKHNM[2] = daimao;
        return true;
    }

    public static boolean setSKHXD(int kkr, int cadic, int nappa) {
        if (kkr + cadic + nappa != 100) {
            return false;
        }
        if (kkr < 0 || cadic < 0 || nappa < 0) {
            return false;
        }
        SKHXD[0] = kkr;
        SKHXD[1] = cadic;
        SKHXD[2] = nappa;
        return true;
    }

    public DoiDoKichHoat() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn 1 món đồ Hủy Diệt").append("\n");
        sb.append("Sau đó chọn 'Nâng Cấp'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép").append("\n");
        sb2.append("Biến đồ Hủy Diệt thành Đồ Kích hoạt").append("\n");
        sb2.append("Giữ nguyên loại trang bị").append("\n");
        sb2.append("Ngẫu nhiên Level 1 - 12 và Option");
        setInfo2(sb2.toString());
    }
    public int type;

    @Override
    public void confirm() {
        if (itemCombine == null) {
            return;
        }
        if (itemCombine.size() != 1) {
            player.service.dialogMessage("Số lượng trang bị không hợp lệ");
            return;
        }
        Item item = player.itemBag[itemCombine.get(0)];
        if (item == null || item.template.type >= 5 || item.template.level != 14) {
            player.service.dialogMessage("Vật phẩm không hợp lệ");
            return;
        }
        String info = "Sau khi nâng cấp, sẽ nhận được 1 trang bị Kích Hoạt cùng loại (giữ nguyên loại trang bị, ngẫu nhiên Level 1 - 12 và Option, cần 500tr vàng)";
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.COMBINE, "Đồng ý\n500tr vàng", -1));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);
    }

    private int getRandomLevel() {
        int totalWeight = 0;
        for (int weight : LEVEL_WEIGHTS) {
            totalWeight += weight;
        }
        
        int randomValue = Utils.nextInt(totalWeight);
        int currentSum = 0;
        
        for (int i = 0; i < LEVEL_WEIGHTS.length; i++) {
            currentSum += LEVEL_WEIGHTS[i];
            if (randomValue < currentSum) {
                return i + MIN_LEVEL;
            }
        }
        
        return MIN_LEVEL;
    }
    
    private int getRandomItemTemplateId(int type, int gender, int level) {
        com.ngocrong.server.Server server = DragonBall.getInstance().getServer();
        if (server == null || server.iTemplates == null) {
            return -1;
        }
        
        java.util.ArrayList<Integer> matchingIds = new java.util.ArrayList<>();
        
        for (ItemTemplate template : server.iTemplates.values()) {
            if (template.id == 693 || template.id == 691 || template.id == 692) {
                continue;
            }
            
            if (template.type == type && template.level == level) {
                if (type == 4) {
                    if (template.gender == 3 || template.gender == gender) {
                        matchingIds.add((int) template.id);
                    }
                } else {
                    if (template.gender == gender) {
                        matchingIds.add((int) template.id);
                    }
                }
            }
        }
        
        if (matchingIds.isEmpty()) {
            return -1;
        }
        
        int randomIndex = Utils.nextInt(matchingIds.size());
        return matchingIds.get(randomIndex);
    }

    public static final int[][][] OPTIONS = {
        {
            {127, 139},
            {128, 140},
            {129, 141}
        }, {
            {130, 142},
            {131, 143},
            {132, 144}
        }, {
            {133, 136},
            {134, 137},
            {135, 138}
        }
    };

    private int getRandomIndexByPercentage(int gender) {
        int[] percentages;
        switch (gender) {
            case 0:
                percentages = SKHTD;
                break;
            case 1:
                percentages = SKHNM;
                break;
            case 2:
                percentages = SKHXD;
                break;
            default:
                percentages = SKHTD;
                break;
        }

        int totalPercentage = 0;
        for (int percentage : percentages) {
            totalPercentage += percentage;
        }

        int randomValue = Utils.nextInt(totalPercentage);

        int currentSum = 0;
        for (int i = 0; i < percentages.length; i++) {
            currentSum += percentages[i];
            if (randomValue < currentSum) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        if (itemCombine.size() != 1) {
            player.service.dialogMessage("Số lượng trang bị không hợp lệ");
            return;
        }
        Item oldItem = player.itemBag[itemCombine.get(0)];
        if (oldItem == null || oldItem.template.type >= 5 || oldItem.template.level != 14) {
            player.service.dialogMessage("Vật phẩm không hợp lệ");
            return;
        }
        if (player.gold < 500000000) {
            player.service.sendThongBao("Bạn không đủ vàng");
            return;
        }
        
        int gender = oldItem.template.gender;
        int genderForOption = gender;
        if (oldItem.template.type == 4) {
            if (gender == 3) {
                gender = player.gender;
            }
            genderForOption = player.gender;
        }
        
        int itemType = oldItem.template.type;
        int randomLevel = getRandomLevel();
        
        int templateId = getRandomItemTemplateId(itemType, gender, randomLevel);
        
        if (templateId == -1) {
            player.service.dialogMessage("Không tìm thấy trang bị phù hợp. Vui lòng thử lại.");
            return;
        }
        
        player.addGold(-500000000);
        short oldIcon = oldItem.template.iconID;
        result((byte) 2, oldIcon, oldItem.template.iconID);
        
        Item newItem = new Item(templateId);
        newItem.quantity = 1;
        newItem.setDefaultOptions();

        int index = getRandomIndexByPercentage(genderForOption);
        newItem.addItemOption(new ItemOption(OPTIONS[genderForOption][index][0], 0));
        newItem.addItemOption(new ItemOption(OPTIONS[genderForOption][index][1], 0));
        newItem.addItemOption(new ItemOption(30, 0));
        
        newItem.indexUI = itemCombine.get(0);
        player.itemBag[newItem.indexUI] = newItem;
        player.service.refreshItem((byte) 1, newItem);
        update();
    }

    @Override
    public void showTab() {
        player.service.combine((byte) 0, this, (short) -1, (short) -1);
    }
}
