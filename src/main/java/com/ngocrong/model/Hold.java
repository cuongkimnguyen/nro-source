package com.ngocrong.model;

import com.ngocrong.map.tzone.Zone;
import com.ngocrong.skill.Skill;
import com.ngocrong.mob.Mob;
import com.ngocrong.user.Player;

public class Hold extends Thread {

    public Zone zone;
    public Player holder;
    public Object detainee;
    public int seconds;
    public boolean isClosed;

    public Hold(Zone zone, Player holder, Object detainee, int seconds) {
        this.zone = zone;
        this.holder = holder;
        this.detainee = detainee;
        this.seconds = seconds;
    }

    public void update() {
        this.seconds--;
        if (this.holder.isCancelTroi()) {
            close();
        }
        if (this.seconds <= 0) {
            close();
        }
    }

    public void run() {
        while (!isClosed) {
            try {
                update();
                Thread.sleep(1000L);
            } catch (Exception e) {
                
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        super.start();
        holder.hold = this;
        holder.setHeld(true);
        if (detainee instanceof Mob) {
            Mob mob = (Mob) detainee;
            mob.hold = this;
            mob.isHeld = true;
            zone.service.setEffect(this, mob.mobId, Skill.ADD_EFFECT, Skill.MONSTER, (byte) 32);
        } else {
            Player _player = (Player) detainee;
            _player.hold = this;
            _player.setHeld(true);
            zone.service.setEffect(this, _player.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 32);
        }
    }

    public void close() {
        this.isClosed = true;
        holder.setHeld(false);
        zone.service.setEffect(null, holder.id, Skill.REMOVE_EFFECT, Skill.CHARACTER, (byte) 32);
        if (this.detainee instanceof Mob) {
            Mob mob = (Mob) this.detainee;
            mob.isHeld = false;
            mob.hold = null;
            zone.service.setEffect(null, mob.mobId, Skill.REMOVE_EFFECT, Skill.MONSTER, (byte) 32);
        } else {
            Player _player = (Player) this.detainee;
            _player.setHeld(false);
            zone.service.setEffect(null, _player.id, Skill.REMOVE_EFFECT, Skill.CHARACTER, (byte) 32);
            _player.hold = null;
        }
        holder.hold = null;
    }
}
