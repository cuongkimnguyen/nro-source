package com.ngocrong.map.expansion;

import com.ngocrong.map.TMap;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Expansion {

    public boolean isRunning;
    public static int autoIncrease;
    public int id;
    public ArrayList<TMap> maps;
    public long startTime;
    public long endTime;
    public long updateTime;
    public long LIMIT_TIME;
    public boolean isClose;
    public Lock lock = new ReentrantLock();

    public Expansion(long limitTime) {
        id = autoIncrease++;
        maps = new ArrayList<>();
        LIMIT_TIME = limitTime;
        startTime = System.currentTimeMillis();
        endTime = startTime + LIMIT_TIME;
    }

    public abstract void close();

    public abstract void update();
}
