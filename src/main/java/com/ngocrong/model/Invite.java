package com.ngocrong.model;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Invite {

    public static final byte GIAO_DICH = 0;
    public static final byte THACH_DAU = 1;
    public static final byte INVITE_CLAN = 2;

    private ArrayList<CharInvite> list = new ArrayList();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public void addCharInvite(byte type, int id, long wait) {
        lock.writeLock().lock();
        try {
            list.add(new CharInvite(id, type, wait));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public CharInvite findCharInvite(byte type, int id) {
        lock.readLock().lock();
        try {
            for (CharInvite in : list) {
                if (in.type == type && in.id == id) {
                    return in;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }

    public void update() {
        long now = System.currentTimeMillis();
        lock.writeLock().lock();
        try {
            list.removeIf(c -> now > c.end);
        } finally {
            lock.writeLock().unlock();

        }
    }

    public class CharInvite {

        public CharInvite(int id, byte type, long wait) {
            this.id = id;
            this.start = System.currentTimeMillis();
            this.end = this.start + wait;
            this.type = type;
        }

        private int id;
        private byte type;
        public long start;
        public long end;
    }
}
