package com.ngocrong.task;

import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Server;

public class TaskTemplate {

    public int id;
    public String name;
    public String[] details;
    public int[][] tasks;
    public int[][] mapTasks;
    public short[] counts;
    public String[][] subNames;
    public String[][] contents;
    public int rewardPower, rewardPotential, rewardGold, rewardGem, rewardGemLock;

    public static TaskTemplate getTaskTemplate(int taskId) {
        Server server = DragonBall.getInstance().getServer();
        for (TaskTemplate task : server.taskTemplates) {
            if (task.id == taskId) {
                return task;
            }
        }
        return null;
    }
}
