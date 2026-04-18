package com.ngocrong.network;

import com.ngocrong.user.Player;

public interface IMessageHandler {

    public void setService(IService service);

    public void onMessage(Message message);

    public void setChar(Player _player);

    public void onConnectionFail();

    public void onDisconnected();

    public void onConnectOK();

    public void close();
}
