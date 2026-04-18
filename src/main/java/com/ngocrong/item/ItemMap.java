package com.ngocrong.item;

import com.ngocrong.user.Player;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ItemMap {

    public int id;
    public Item item;
    public short x;
    public short y;
    public int r;
    public Player owner;
    public boolean isPickedUp;
    public int playerID;
    public boolean isDragonBallNamec;
    public boolean isBarrack;
    public long throwTime;
    public boolean isThrowFromMob;
    public boolean killerIsHuman;
    public int mobLevel;
    public int countDown;
    public Lock lock = new ReentrantLock();

    public ItemMap(short id) {
        this.id = id;
        this.isPickedUp = false;
        this.throwTime = System.currentTimeMillis();
    }
}
