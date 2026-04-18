package com.ngocrong.event;

import com.ngocrong.lib.RandomCollection;
import com.ngocrong.user.Player;

public abstract class Event {

    private static Event event;

    public RandomCollection<Integer> items;

    public static boolean isEvent() {
        return event != null;
    }

    public static void exchange(int type, Player _c) {
        event.action(type, _c);
    }

    public static RandomCollection<Integer> getItems() {
        return event.items;
    }

    public Event() {
        items = new RandomCollection<>();
    }

    public abstract void action(int type, Player _c);
}
