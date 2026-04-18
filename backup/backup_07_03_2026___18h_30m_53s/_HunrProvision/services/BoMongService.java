package _HunrProvision.services;

import com.ngocrong.util.Utils;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.data.BoMongNhiemVuConfigData;
import com.ngocrong.data.BoMongBossConfigData;
import com.ngocrong.data.BoMongConfigData;
import com.ngocrong.data.BoMongMocDiemData;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BoMongService {

    public static final int LOAI_KILL_MOB = 1;
    public static final int LOAI_NAP_TIEN = 2;
    public static final int LOAI_DAT_SM = 3;
    public static final int LOAI_KILL_BOSS = 4;
    public static final int LOAI_NANG_TRANG_BI = 5;
    public static final int LOAI_TRAIN = 6;
    public static final int LOAI_LAM_TASK = 7;

    public static final int DO_KHO_EASY = 0;
    public static final int DO_KHO_NORMAL = 1;
    public static final int DO_KHO_HARD = 2;

    public int loaiNv;
    public int doKho;
    public int yeuCau;
    public int tienDo;
    public int diemThuong;
    public String[] bossIds;
    public java.util.Set<Integer> notifiedMilestones = new java.util.HashSet<>();

    public static class CauHinhNv {
        int loaiNv;
        int doKho;
        int yeuCauMin;
        int yeuCauMax;
        int diemMin;
        int diemMax;
    }

    private static List<CauHinhNv> CAU_HINH_NV = new ArrayList<>();
    public static List<MocDiem> CAC_MOC_DIEM = new ArrayList<>();
    private static int MAX_TASK_ID = 26;
    private static int TY_LE_EASY = 50;
    private static int TY_LE_NORMAL = 35;
    private static int TY_LE_HARD = 15;

    public static class MocDiem {
        public int diemCanThiet;
        public int[] itemIds;
        public int[] quantities;

        public MocDiem(int diem, int[] items, int[] qty) {
            this.diemCanThiet = diem;
            this.itemIds = items;
            this.quantities = qty;
        }
    }

    public static void loadConfig() {
        CAU_HINH_NV.clear();

        try {
            List<BoMongNhiemVuConfigData> configList = GameRepository.getInstance().boMongNhiemVuConfig.findAllActive();
            for (BoMongNhiemVuConfigData data : configList) {
                CauHinhNv config = new CauHinhNv();
                config.loaiNv = data.loaiNv;
                config.doKho = data.doKho;
                config.yeuCauMin = data.yeuCauMin;
                config.yeuCauMax = data.yeuCauMax;
                config.diemMin = data.diemMin;
                config.diemMax = data.diemMax;
                CAU_HINH_NV.add(config);
            }

            Optional<BoMongConfigData> easyOpt = GameRepository.getInstance().boMongConfig.findByConfigKey("ty_le_easy");
            if (easyOpt.isPresent()) {
                TY_LE_EASY = Integer.parseInt(easyOpt.get().configValue);
            }

            Optional<BoMongConfigData> normalOpt = GameRepository.getInstance().boMongConfig.findByConfigKey("ty_le_normal");
            if (normalOpt.isPresent()) {
                TY_LE_NORMAL = Integer.parseInt(normalOpt.get().configValue);
            }

            Optional<BoMongConfigData> hardOpt = GameRepository.getInstance().boMongConfig.findByConfigKey("ty_le_hard");
            if (hardOpt.isPresent()) {
                TY_LE_HARD = Integer.parseInt(hardOpt.get().configValue);
            }

            Optional<BoMongConfigData> maxTaskOpt = GameRepository.getInstance().boMongConfig.findByConfigKey("max_task_id");
            if (maxTaskOpt.isPresent()) {
                MAX_TASK_ID = Integer.parseInt(maxTaskOpt.get().configValue);
            }

            loadMocDiem();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadMocDiem() {
        CAC_MOC_DIEM.clear();
        try {
            List<BoMongMocDiemData> dataList = GameRepository.getInstance().boMongMocDiem.findAllActive();
            for (BoMongMocDiemData data : dataList) {
                int[] itemIds = new int[]{0, 0, 0, 0};
                int[] quantities = new int[]{1, 1, 1, 1};
                MocDiem moc = new MocDiem(data.diemCanThiet, itemIds, quantities);
                CAC_MOC_DIEM.add(moc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MocDiem getMocDiemKhaDung(int diemHienTai) {
        for (int i = CAC_MOC_DIEM.size() - 1; i >= 0; i--) {
            MocDiem moc = CAC_MOC_DIEM.get(i);
            if (diemHienTai >= moc.diemCanThiet) {
                return moc;
            }
        }
        return null;
    }

    public static List<MocDiem> getCacMocDiemKhaDung(int diemHienTai) {
        List<MocDiem> result = new ArrayList<>();
        for (MocDiem moc : CAC_MOC_DIEM) {
            if (diemHienTai >= moc.diemCanThiet) {
                result.add(moc);
            }
        }
        return result;
    }

    public static class NoHuGroup {
        public int ratioNoHu;
        public List<Item> items;

        public NoHuGroup(int ratioNoHu, List<Item> items) {
            this.ratioNoHu = ratioNoHu;
            this.items = items;
        }
    }

    public static Item noHu(int diemSuDung) {
        BoMongMocDiemData data = GameRepository.getInstance().boMongMocDiem.findByDiemCanThiet(diemSuDung);
        if (data == null || data.active == null || data.active == 0) {
            return null;
        }

        List<NoHuGroup> groups = new ArrayList<>();
        String[] jsonFields = new String[]{
            data.itemId1,
            data.itemId2,
            data.itemId3,
            data.itemId4
        };

        for (String jsonStr : jsonFields) {
            if (jsonStr != null && !jsonStr.trim().isEmpty()) {
                try {
                    JSONObject json = new JSONObject(jsonStr);
                    int ratioNoHu = json.optInt("ratioNoHu", 0);
                    if (ratioNoHu <= 0) {
                        continue;
                    }

                    JSONArray itemsArray = json.optJSONArray("items");
                    if (itemsArray == null || itemsArray.length() == 0) {
                        continue;
                    }

                    List<Item> items = new ArrayList<>();
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemObj = itemsArray.getJSONObject(i);
                        int itemId = itemObj.getInt("id");
                        int quantity = itemObj.optInt("quantity", 1);

                        Item item = new Item(itemId);
                        item.quantity = quantity;
                        item.setDefaultOptions();

                        if (itemObj.has("expire")) {
                            int expire = itemObj.getInt("expire");
                            if (expire > 0) {
                                item.addItemOption(new ItemOption(93, expire));
                            }
                        }

                        if (itemObj.has("options")) {
                            JSONArray arrOption = itemObj.getJSONArray("options");
                            item.addItemOptions(arrOption);
                        }

                        items.add(item);
                    }

                    if (!items.isEmpty()) {
                        groups.add(new NoHuGroup(ratioNoHu, items));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (groups.isEmpty()) {
            return null;
        }

        int totalRatio = 0;
        for (NoHuGroup group : groups) {
            totalRatio += group.ratioNoHu;
        }

        if (totalRatio <= 0) {
            return null;
        }

        int randomValue = Utils.nextInt(totalRatio);
        int currentRatio = 0;
        NoHuGroup selectedGroup = null;

        for (NoHuGroup group : groups) {
            currentRatio += group.ratioNoHu;
            if (randomValue < currentRatio) {
                selectedGroup = group;
                break;
            }
        }

        if (selectedGroup == null) {
            selectedGroup = groups.get(0);
        }

        if (selectedGroup.items.isEmpty()) {
            return null;
        }

        int randomItemIndex = Utils.nextInt(selectedGroup.items.size());
        return selectedGroup.items.get(randomItemIndex);
    }

    private static boolean canDoLoaiNv(int loaiNv, long currentPower, int currentTaskId, boolean canUpgradeEquipment) {
        if (loaiNv == LOAI_LAM_TASK) {
            if (currentTaskId >= MAX_TASK_ID) {
                return false;
            }
            int taskConLai = MAX_TASK_ID - currentTaskId;
            boolean hasValidConfig = false;
            for (CauHinhNv c : CAU_HINH_NV) {
                if (c.loaiNv == LOAI_LAM_TASK && taskConLai >= c.yeuCauMin) {
                    hasValidConfig = true;
                    break;
                }
            }
            return hasValidConfig;
        }

        if (loaiNv == LOAI_DAT_SM) {
            boolean hasValidConfig = false;
            for (CauHinhNv c : CAU_HINH_NV) {
                if (c.loaiNv == LOAI_DAT_SM && currentPower < c.yeuCauMax) {
                    hasValidConfig = true;
                    break;
                }
            }
            return hasValidConfig;
        }

        if (loaiNv == LOAI_NANG_TRANG_BI) {
            return canUpgradeEquipment;
        }

        return true;
    }

    public static BoMongService randomNhiemVu(long currentPower, int currentTaskId, boolean canUpgradeEquipment) {
        if (CAU_HINH_NV.isEmpty()) {
            loadConfig();
        }

        int random = Utils.nextInt(100);
        int doKho;
        if (random < TY_LE_EASY) {
            doKho = DO_KHO_EASY;
        } else if (random < TY_LE_EASY + TY_LE_NORMAL) {
            doKho = DO_KHO_NORMAL;
        } else {
            doKho = DO_KHO_HARD;
        }

        int maxRetry = 50;
        CauHinhNv config = null;
        int loaiNv = 0;

        for (int retry = 0; retry < maxRetry; retry++) {
            loaiNv = Utils.nextInt(7) + 1;

            if (!canDoLoaiNv(loaiNv, currentPower, currentTaskId, canUpgradeEquipment)) {
                continue;
            }

            for (CauHinhNv c : CAU_HINH_NV) {
                if (c.loaiNv == loaiNv && c.doKho == doKho) {
                    if (loaiNv == LOAI_DAT_SM) {
                        if (currentPower >= c.yeuCauMax) {
                            continue;
                        }
                        if (currentPower >= c.yeuCauMin) {
                            c.yeuCauMin = (int) Math.min(currentPower + 1, c.yeuCauMax);
                        }
                    }

                    if (loaiNv == LOAI_LAM_TASK) {
                        int taskConLai = MAX_TASK_ID - currentTaskId;
                        if (taskConLai < c.yeuCauMin) {
                            continue;
                        }
                        if (taskConLai < c.yeuCauMax) {
                            c.yeuCauMax = Math.max(c.yeuCauMin, taskConLai);
                        }
                    }

                    config = c;
                    break;
                }
            }

            if (config != null) {
                break;
            }
        }

        if (config == null) {
            return null;
        }

        BoMongService nv = new BoMongService();
        nv.loaiNv = loaiNv;
        nv.doKho = doKho;
        nv.yeuCau = Utils.nextInt(config.yeuCauMax - config.yeuCauMin + 1) + config.yeuCauMin;
        nv.tienDo = 0;
        nv.diemThuong = tinhDiemThuong(loaiNv, doKho);

        if (loaiNv == LOAI_KILL_BOSS) {
            nv.bossIds = getBossNamesByDifficulty(doKho);
        }

        if (loaiNv == LOAI_DAT_SM && currentPower >= nv.yeuCau) {
            nv.tienDo = nv.yeuCau;
        }

        return nv;
    }

    public static String[] getBossNamesByDifficulty(int doKho) {
        List<String> bossList = new ArrayList<>();
        try {
            List<BoMongBossConfigData> configList = GameRepository.getInstance().boMongBossConfig.findByDoKhoAndActive(doKho);
            for (BoMongBossConfigData data : configList) {
                bossList.add(data.bossClassName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bossList.toArray(new String[0]);
    }

    public static int tinhDiemThuong(int doKho) {
        CauHinhNv config = null;
        for (CauHinhNv c : CAU_HINH_NV) {
            if (c.doKho == doKho) {
                config = c;
                break;
            }
        }
        if (config == null) {
            return 1;
        }
        return Utils.nextInt(config.diemMax - config.diemMin + 1) + config.diemMin;
    }

    public static int tinhDiemThuong(int loaiNv, int doKho) {
        for (CauHinhNv c : CAU_HINH_NV) {
            if (c.loaiNv == loaiNv && c.doKho == doKho) {
                return Utils.nextInt(c.diemMax - c.diemMin + 1) + c.diemMin;
            }
        }
        return 1;
    }
}

