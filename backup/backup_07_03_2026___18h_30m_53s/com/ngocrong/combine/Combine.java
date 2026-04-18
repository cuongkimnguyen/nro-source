package com.ngocrong.combine;

import com.ngocrong.consts.CMDMenu;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.user.Player;
import com.ngocrong.model.Npc;
import lombok.Data;

import java.util.ArrayList;

@Data
public abstract class Combine {

    private String info;
    private String info2;
    protected ArrayList<Byte> itemCombine;
    protected Player player;
    protected Npc npc;

    public void showTab() {
        player.service.combine((byte) 0, this, (short) -1, (short) -1);
    }

    public void setItemCombine(ArrayList<Byte> items) {
        this.itemCombine = items;
    }

    protected void update() {
        player.service.combine((byte) 1, this, (short) -1, (short) -1);
    }

    protected void result(byte type, short... iconID) {
        short iconID1 = -1;
        if (iconID.length >= 1) {
            iconID1 = iconID[0];
        }
        short iconID2 = -1;
        if (iconID.length >= 2) {
            iconID2 = iconID[1];
        }
        player.service.combine(type, this, iconID1, iconID2);
    }

    public void showCancel(String info) {
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);
    }

    public abstract void confirm();

    public abstract void combine();
}
