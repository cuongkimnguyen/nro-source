package com.ngocrong.mob;

import com.ngocrong.mob._BigBoss.Hirudegarn;

public class MobFactory {

    public static Mob getMob(MobType mobType) {
        switch (mobType) {

            case MOB:
                return new Mob();

            case BACH_TUOC:
                return new Octopus();

            case BIG_BOSS:
                return new BigBoss();

            case GUARD_ROBOT:
                return new GuardRobot();

            case NEW_BOSS:
                return new NewBoss();

            case HIRUDEGARN:
                return new Hirudegarn();

            default:
                throw new IllegalArgumentException("This mob type is unsupported");
        }

    }
}
