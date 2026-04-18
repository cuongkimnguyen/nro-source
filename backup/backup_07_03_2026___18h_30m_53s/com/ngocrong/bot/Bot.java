package com.ngocrong.bot;

public interface Bot {

    public abstract void attack(Object obj);

    public abstract Object targetDetect();

    public abstract void move();

    public abstract void chat(String chat);

}
