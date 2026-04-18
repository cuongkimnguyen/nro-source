package com.ngocrong.bot;

import com.ngocrong.map.tzone.Zone;
import com.ngocrong.network.Service;
import com.ngocrong.user.Player;
import com.ngocrong.user.Info;
import com.ngocrong.util.Utils;

import java.util.ArrayList;

public class TrongTai extends Player {

    public TrongTai() {
        super();
        this.id = -(((Utils.nextInt(100) * 1000) + Utils.nextInt(100) * 100)) + Utils.nextInt(100);
        this.name = "Trọng tài";
        info = new Info(this);
        info.setInfo();
        info.recovery(Info.ALL, 100, false);
        service = new Service(this);
        effects = new ArrayList();
        itemTimes = new ArrayList();
        idMount = -1;
        this.zone = zone;
        setDefaultPart();
    }

    public TrongTai(Zone zone) {
        super();
        this.id = -(((Utils.nextInt(100) * 1000) + Utils.nextInt(100) * 100)) + Utils.nextInt(100);
        this.name = "Trọng tài";
        info = new Info(this);
        info.setInfo();
        info.recovery(Info.ALL, 100, false);
        service = new Service(this);
        effects = new ArrayList();
        itemTimes = new ArrayList();
        idMount = -1;
        this.zone = zone;
        setDefaultPart();
    }

    @Override
    public boolean isBoss() {
        return false;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public boolean isEscort() {
        return false;
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 116);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 115);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 114);
    }

    public void chat(String chat) {
        if (zone != null) {
            zone.service.chat(this, chat);
        }
    }
}
