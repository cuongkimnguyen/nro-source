package com.ngocrong.mob;

import com.ngocrong.consts.Cmd;
import com.ngocrong.network.FastDataOutputStream;
import com.ngocrong.network.Message;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;

public class NewBoss extends BigBoss {

    private static Logger logger = Logger.getLogger(NewBoss.class);

    @Override
    public void action(int action) {
    }

    public void setDie() {
        try {
            Message ms = new Message(Cmd.BIG_BOSS_2);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(23);
            ds.writeInt(this.mobId);
            ds.flush();
            zone.service.sendMessage(ms, null);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.debug("attack err");
        }
    }

    public void fly(short x, short y) {
        try {
            Message ms = new Message(Cmd.BIG_BOSS_2);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(21);
            ds.writeInt(this.mobId);
            ds.writeShort(this.x);
            ds.writeShort(this.y);
            ds.flush();
            zone.service.sendMessage(ms, null);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.debug("attack err");
        }
    }

    public void move(short x, short y) {
        try {
            Message ms = new Message(Cmd.BIG_BOSS_2);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(10);
            ds.writeInt(this.mobId);
            ds.writeShort(this.x);
            ds.writeShort(this.y);
            ds.flush();
            zone.service.sendMessage(ms, null);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.debug("attack err");
        }
    }
}
