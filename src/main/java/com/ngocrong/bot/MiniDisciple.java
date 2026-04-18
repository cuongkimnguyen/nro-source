package com.ngocrong.bot;

import com.ngocrong.item.Item;
import com.ngocrong.network.Service;
import com.ngocrong.user.Player;
import com.ngocrong.user.Info;
import com.ngocrong.util.Utils;

import java.util.ArrayList;

public class MiniDisciple extends Player {

    private short pHead, pBody, pLeg;
    public Item item;
    private final Player owner;

    public MiniDisciple(Item item, Player _owner) {
        super();
        this.id = -(((Utils.nextInt(100) * 1000) + Utils.nextInt(100) * 100)) + Utils.nextInt(100);
        this.item = item;
        this.owner = _owner;
        this.name = "";
        setPart();
        info = new Info(this);
        info.setPowerLimited();
        info.setStamina();
        info.setInfo();
        info.recovery(Info.ALL, 100, false);
        service = new Service(this);
        effects = new ArrayList<>();
        itemTimes = new ArrayList<>();
        idMount = -1;
        setDefaultPart();
    }

    private void setPart() {
        this.pHead = item.template.head;
        this.pBody = item.template.body;
        this.pLeg = item.template.leg;
    }

    public void move() {
        short x = (short) (owner.getX() + Utils.nextInt(-50, 50));
        short y = owner.getY();
        moveTo(x, y);
    }

    public void moveTo(short x, short y) {
        setX(x);
        setY(y);
        if (zone != null) {
            zone.service.move(this);
        }
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
    public boolean isDisciple() {
        return false;
    }

    @Override
    public boolean isMiniDisciple() {
        return true;
    }

    @Override
    public boolean isEscort() {
        return false;
    }

    @Override
    public void setDefaultLeg() {
        setLeg(this.pLeg);
    }

    @Override
    public void setDefaultBody() {
        setBody(this.pBody);
    }

    @Override
    public void setDefaultHead() {
        setHead(this.pHead);
    }

    @Override
    public void updateEveryFiveSeconds() {
        move();
    }

}
