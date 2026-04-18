package com.ngocrong.model;

import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Server;
import com.ngocrong.util.Utils;
import com.google.gson.annotations.SerializedName;

public class Achievement {

    private transient AchievementTemplate template;

    @SerializedName("id")
    private int id;
    @SerializedName("count")
    private long count;
    @SerializedName("rewarded")
    private boolean isRewarded;

    public Achievement(int id) {
        this.id = id;
        this.count = 0;
        this.isRewarded = false;
        initTemplate();
    }

    public void initTemplate() {
        Server server = DragonBall.getInstance().getServer();
        this.template = server.achievements.get(this.id);
    }

    public void upadateCount(long count) {
        this.count = count;
    }

    public void addCount(int add) {
        this.count += add;
    }

    public void setIsRewarded(boolean isRewarded) {
        this.isRewarded = isRewarded;
    }

    public boolean isFinish() {
        return this.count >= template.maxCount;
    }

    public int getReward() {
        return template.reward;
    }

    public boolean isRewarded() {
        return this.isRewarded;
    }

    public String getName() {
        return template.name;
    }

    public String getContent() {
        return String.format(template.content + " (%s/%s)", Utils.formatNumber(count), Utils.formatNumber(template.maxCount));
    }
}
