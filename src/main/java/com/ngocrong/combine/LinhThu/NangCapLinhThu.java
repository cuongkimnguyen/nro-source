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

/**
 *
 * @author Administrator
 */
public class NangCapLinhThu extends Combine {

    public int count;

    public NangCapLinhThu() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang\n");
        sb.append("Chọn Linh thú bậc 1\n");
        sb.append("và Hồn linh thú\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        setInfo2("Ta sẽ nâng cấp linh thú giúp ngươi");
    }

    @Override
    public void confirm() {
        if (itemCombine == null) {
            return;
        }
        Item linhthu = null;
        Item soul = null;
        for (byte idx : itemCombine) {
            Item item = player.itemBag[idx];
            if (item != null) {
                if (item.template.type == Item.TYPE_PET_BAY_BAC_1) {
                    linhthu = item;
                } else if (item.template.id == ItemName.HON_LINH_THU) {
                    soul = item;
                }
            }
        }
        String info = String.format("Cần 1 Linh thú bậc 1 và Hồn linh thú");
        boolean ok = linhthu != null && soul != null;
        player.menus.clear();
        if (ok) {
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng 1 lần", 1));
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng 10 lần", 10));
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng 50 lần", 50));
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng 100 lần", 100));
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng 500 lần", 500));
        }
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        Item linhthu = null;
        Item soul = null;
        for (byte idx : itemCombine) {
            Item item = player.itemBag[idx];
            if (item != null) {
                if (item.template.type == Item.TYPE_PET_BAY_BAC_1) {
                    linhthu = item;
                } else if (item.id == ItemName.HON_LINH_THU) {
                    soul = item;
                }
            }
        }
        if (linhthu == null || soul == null) {
            player.service.sendThongBao("Vật phẩm không đủ");
            return;
        }
        EXPLinhThu(linhthu, soul, count);
    }

    public void EXPLinhThu(Item linhthubac1, Item honlinhthu, int soluong) {
        if (linhthubac1 == null || honlinhthu == null) {
            return;
        }
        if (soluong > 0) {
            soluong = GetMaxEXP(linhthubac1) - GetEXP(linhthubac1) >= soluong ? soluong : GetMaxEXP(linhthubac1) - GetEXP(linhthubac1);
            Item item = honlinhthu;
            if (item.quantity < soluong) {
                player.service.dialogMessage("Thiếu vật phẩm để thực hiện ");
                return;
            }
            Item itemStart = SetEXP(linhthubac1, soluong);
            if (itemStart != null) {
                player.removeItem(honlinhthu.indexUI, soluong);
                player.service.setItemBag();
                result((byte) 5, honlinhthu.template.iconID);
                update();
            }
        }
    }

    private static int GetMaxEXP(Item item) {
        for (ItemOption op : item.options) {
            switch (op.optionTemplate.id) {
                case 227: {
                    return 30;
                }
                case 228: {
                    return 50;
                }
                case 229: {
                    return 100;
                }
                case 230: {
                    return 200;
                }
                case 231: {
                    return 400;
                }
                case 232: {
                    return 750;
                }
                case 233: {
                    return 1500;
                }
            }
        }
        return 30;
    }

    private static int GetEXP(Item item) {
        for (ItemOption op : item.options) {
            if (op.optionTemplate.id >= 227 && op.optionTemplate.id <= 233) {
                return op.param;
            }
        }
        return 0;
    }

    private Item SetEXP(Item linhthu, int EXPUp) {
        if (FindLevel(linhthu) == 7) {
            player.service.sendThongBao("Linh thú đã đạt cấp tối đa , Hãy tiến hành nâng bậc ");
            return null;
        }
        if (!FindOption(linhthu)) {
            ItemOption opadd = new ItemOption(227, EXPUp);
            linhthu.options.add(opadd);
            if (EXPUp >= 30) {
                UpdateOption(linhthu);
            }
            return linhthu;
        }
        if (GetEXP(linhthu) + EXPUp < GetMaxEXP(linhthu)) {
            PlusEXP(linhthu, EXPUp);

            return linhthu;
        }
        if (GetEXP(linhthu) + EXPUp >= GetMaxEXP(linhthu)) {
            UpdateOption(linhthu);

            return linhthu;
        }
        return null;
    }

    private static void PlusEXP(Item linhthu, int exp) {
        for (ItemOption op : linhthu.options) {
            if (op.optionTemplate.id >= 227 && op.optionTemplate.id <= 233) {
                op.param += exp;
            }
        }
    }

    private static void UpdateOption(Item linhthu) {
        ItemOption opUpdate = null;
        ItemOption opLevel = null;

        for (ItemOption op : linhthu.options) {
            if (op.optionTemplate.id >= 227 && op.optionTemplate.id <= 233) {
                opUpdate = op;
            }
            if (op.optionTemplate.id == 72) {
                opLevel = op;
            }
        }
        if (opLevel != null) {
            linhthu.options.remove(opLevel);
        }
        linhthu.options.add(new ItemOption(72, GetLevel(linhthu)));
        if (opUpdate != null) {
            linhthu.options.remove(opUpdate);
            for (ItemOption op : linhthu.options) {
                if (op.optionTemplate.id != 72) {
                    op.param += 1;
                }
            }
            if (opUpdate.optionTemplate.id <= 232) {
                linhthu.options.add(new ItemOption(opUpdate.optionTemplate.id + 1, 0));
            }
        }
    }

    private static int GetLevel(Item item) {
        for (ItemOption op : item.options) {
            switch (op.optionTemplate.id) {
                case 227: {
                    return 1;
                }
                case 228: {
                    return 2;
                }
                case 229: {
                    return 3;
                }
                case 230: {
                    return 4;
                }
                case 231: {
                    return 5;
                }
                case 232: {
                    return 6;
                }
                case 233: {
                    return 7;
                }
            }
        }
        return 0;
    }

    private static int FindLevel(Item item) {
        for (ItemOption op : item.options) {
            if (op.optionTemplate.id == 72) {
                return op.param;
            }
        }
        return -1;
    }

    private static boolean FindOption(Item item) {
        for (ItemOption op : item.options) {
            if (op.optionTemplate.id >= 227 && op.optionTemplate.id <= 233) {
                return true;
            }
        }
        return false;
    }
}
