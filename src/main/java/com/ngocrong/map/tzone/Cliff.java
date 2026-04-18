package com.ngocrong.map.tzone;

import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.TMap;
import com.ngocrong.task.Task;

import java.util.List;

public class Cliff extends Zone {

    public ItemMap itemDuaBe;

    public Cliff(TMap map, int zoneId) {
        super(map, zoneId);
        initDuaBe();
    }

    public void initDuaBe() {
        ItemMap item = new ItemMap(this.autoIncrease++);
        item.isPickedUp = false;
        item.throwTime = System.currentTimeMillis();
        item.item = new Item(ItemName.DUA_BE);
        item.item.setDefaultOptions();
        item.x = 129;
        item.y = 288;
        item.playerID = -1;
        itemDuaBe = item;
    }

    @Override
    public List<ItemMap> getListItemMap(Task... tasks) {
        List<ItemMap> items = super.getListItemMap();
        if (tasks.length > 0) {
            for (Task task : tasks) {
                if (task != null) {
                    if (itemDuaBe != null && task.id == 3 && task.index == 1) {
                        items.add(itemDuaBe);
                    }
                }
            }
        }
        return items;
    }

    @Override
    public ItemMap findItemMapByID(int id) {
        if (itemDuaBe != null && itemDuaBe.id == id) {
            return itemDuaBe;
        }
        lockItemMap.readLock().lock();
        try {
            for (ItemMap item : items) {
                if (item.id == id) {
                    return item;
                }
            }
            return null;
        } finally {
            lockItemMap.readLock().unlock();
        }
    }
}
