package com.ngocrong.network;

import com.ngocrong.user.Player;

public interface IService {

    public abstract void setChar(Player _player);

    public abstract void close();

    public abstract void setResource();
}
