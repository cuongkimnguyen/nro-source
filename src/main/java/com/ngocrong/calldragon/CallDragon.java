package com.ngocrong.calldragon;

import com.ngocrong.consts.Cmd;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.user.Player;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.map.TMap;
import com.ngocrong.network.FastDataOutputStream;
import com.ngocrong.network.Message;
import com.ngocrong.server.SessionManager;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class CallDragon {

    private static final Logger logger = Logger.getLogger(CallDragon.class);

    protected KeyValue[][] wishList;
    protected String say;
    private byte index;

    protected Player _c;
    protected short id;
    protected short avatar;
    protected KeyValue<Integer, String> select;
    protected boolean isRongNamek;
    private short x, y;
    public long time;

    public CallDragon(Player _c, short x, short y) {
        this._c = _c;
        this.index = 0;
        this.x = x;
        this.y = y;
        this.time = System.currentTimeMillis();
    }

    public void setSelect(KeyValue keyValue) {
        this.select = (KeyValue) keyValue;
    }

    public void appearDragon() {
        try {
            Zone z = _c.zone;
            TMap m = z.map;
            Message ms = new Message(Cmd.CALL_DRAGON);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(0);
            ds.writeShort(m.mapID);
            ds.writeShort(m.bgID);
            ds.writeByte(z.zoneID);
            ds.writeInt(_c.id);
            ds.writeUTF(_c.name);
            ds.writeShort(x);
            ds.writeShort(y);
            ds.writeBoolean(isRongNamek);
            ds.flush();
            SessionManager.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("appear error", ex);
        }
    }

    public void close() {
        try {
            _c.setCallDragon(null);
            Message ms = new Message(Cmd.CALL_DRAGON);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(1);
            ds.flush();
            SessionManager.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("remove error", ex);
        }
    }

    public void show() {
        _c.menus.clear();
        for (KeyValue keyValue : wishList[index]) {
            _c.menus.add(keyValue);
        }
        _c.service.openUIConfirm(id, say, avatar, _c.menus);
    }

    public void more() {
        this.index++;
        if (this.index >= wishList.length) {
            this.index = 0;
        }
        show();
    }

    public void confirm() {
        _c.menus.clear();
        _c.menus.add(new KeyValue(20001, select.value));
        _c.menus.add(new KeyValue(20002, "Từ chối"));
        _c.service.openUIConfirm(id, "Ngươi có chắc muốn ước ?", avatar, _c.menus);
    }

    public abstract void accept();

    public void deny() {
        show();
    }

    public void back(String text) {
        _c.service.sendThongBao(text);
        show();
    }
}
