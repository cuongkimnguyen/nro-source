/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.model;

import _HunrProvision.HoangAnhDz;
import com.ngocrong.network.Message;
import com.ngocrong.network.Service;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.data.MabuEggData;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Optional;

/**
 *
 * @author Administrator
 */
public class MabuEgg {
    
    private static final long DEFAULT_TIME_DONE = 30000L;
    
    private Player player;
    public long lastTimeCreate;
    public long timeDone;
    
    private final short id = 50;
    
    public MabuEgg(Player player, long lastTimeCreate, long timeDone) {
        this.player = player;
        this.lastTimeCreate = lastTimeCreate;
        this.timeDone = timeDone;
    }
    
    public static void createMabuEgg(Player player) {
        long currentTime = System.currentTimeMillis();

        // Tính thời gian đến 00:00 ngày 2/8/2025 UTC+7
        ZoneId utcPlus7 = ZoneId.of("UTC+07:00");
        ZonedDateTime targetDateTime = ZonedDateTime.of(2025, 8, 2, 19, 0, 0, 0, utcPlus7);
        long targetTimeMillis = targetDateTime.toInstant().toEpochMilli();

        // Tính thời gian còn lại từ hiện tại đến target time
        long timeUntilTarget = targetTimeMillis - currentTime;

        // Lấy max giữa DEFAULT_TIME_DONE và thời gian còn lại đến target
        long timeDone = timeUntilTarget;

        //timeDone = 60000;
        // Đảm bảo timeDone không âm (nếu đã qua ngày 2/8/2025 UTC+7)
        if (timeDone < 0) {
            timeDone = 10000;
        }
        
        player.mabuEgg = new MabuEgg(player, currentTime, timeDone);
        player.mabuEgg.sendMabuEgg();
        player.mabuEgg.save();
    }
    
    public void sendMabuEgg() {
        Message msg;
        try {
            msg = new Message(-122);
            msg.writer().writeShort(this.id);
            msg.writer().writeByte(1);
            msg.writer().writeShort(4664);
            msg.writer().writeByte(0);
            msg.writer().writeInt(this.getSecondDone());
            this.player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int getSecondDone() {
        int seconds = (int) ((lastTimeCreate + timeDone - System.currentTimeMillis()) / 1000);
        return seconds > 0 ? seconds : 0;
    }
    
    public void openEgg(int gender) {
        if (!player.isEmptyDiscipleBody()) {
            player.service.dialogMessage("Hãy tháo trang bị của đệ tử");
            return;
        }
        Utils.setTimeout(()
                -> {
            try {
                destroyEgg();
                Thread.sleep(3000);
                player.teleport(player.gender * 7);
                player.openMabuEgg();
                player.mabuEgg = null;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 100);
    }
    
    public void destroyEgg() {
        try {
            Message msg = new Message(-117);
            msg.writer().writeByte(101);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.delete();
        this.player.mabuEgg = null;
    }
    
    public void subTimeDone(int d, int h, int m, int s) {
        this.timeDone -= ((d * 24 * 60 * 60 * 1000) + (h * 60 * 60 * 1000) + (m * 60 * 1000) + (s * 1000));
        this.sendMabuEgg();
        this.save();
    }
    
    public void save() {
        MabuEggData data = new MabuEggData();
        data.setPlayerId(this.player.id);
        data.setLastTimeCreate(this.lastTimeCreate);
        data.setTimeDone(this.timeDone);
        Optional<MabuEggData> old = GameRepository.getInstance().mabuEggRepository.findByPlayerId(this.player.id);
        old.ifPresent(d -> data.setId(d.getId()));
        GameRepository.getInstance().mabuEggRepository.save(data);
    }
    
    public static MabuEgg load(Player player) {
        Optional<MabuEggData> data = GameRepository.getInstance().mabuEggRepository.findByPlayerId(player.id);
        if (data.isPresent()) {
            MabuEggData d = data.get();
            return new MabuEgg(player, d.getLastTimeCreate(), d.getTimeDone());
        }
        return null;
    }
    
    public void delete() {
        Optional<MabuEggData> data = GameRepository.getInstance().mabuEggRepository.findByPlayerId(this.player.id);
        data.ifPresent(d -> GameRepository.getInstance().mabuEggRepository.delete(d));
    }
    
    public void dispose() {
        this.player = null;
    }
}
