package com.ngocrong.mob;

import lombok.Data;

@Data
public class MobCoordinate {

    private byte templateID;
    private short x;
    private short y;
    private long hpMax = -1;
}
