package _event.newyear_2026;

import org.json.JSONArray;
import org.json.JSONException;

public class NewYear2026RankData {

    public long holdStartTime;
    public int currentRank;
    public int points;
    public int totalPoints;
    public byte freeTicketsToday;
    public long lastResetTickets;
    public long lastRewardTime; 

    private static final long DAY = 1000L * 60 * 60 * 24;

    public NewYear2026RankData() {
        this.holdStartTime = 0;
        this.currentRank = -1;
        this.points = 0;
        this.totalPoints = 0;
        this.freeTicketsToday = 3;
        this.lastResetTickets = System.currentTimeMillis();
        this.lastRewardTime = 0;
    }

    public static NewYear2026RankData fromJSON(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            NewYear2026RankData data = new NewYear2026RankData();

            if (jsonArray.length() >= 6) {
                data.holdStartTime = jsonArray.getLong(0);
                data.currentRank = jsonArray.getInt(1);
                data.points = jsonArray.getInt(2);
                data.totalPoints = jsonArray.getInt(3);
                data.freeTicketsToday = (byte) jsonArray.getInt(4);
                data.lastResetTickets = jsonArray.getLong(5);
                if (jsonArray.length() >= 7) {
                    data.lastRewardTime = jsonArray.getLong(6);
                }
            }

            resetTicketsIfNewDay(data);

            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return new NewYear2026RankData();
        }
    }

    public String toJSON() {
        return String.format("[%d,%d,%d,%d,%d,%d,%d]",
                holdStartTime,
                currentRank,
                points,
                totalPoints,
                freeTicketsToday,
                lastResetTickets,
                lastRewardTime);
    }

    public static void resetTicketsIfNewDay(NewYear2026RankData data) {
        if (data == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - data.lastResetTickets >= DAY) {
            data.freeTicketsToday = 3;
            data.lastResetTickets = now;
        }
    }

    public boolean isHoldingRank() {
        return holdStartTime > 0 && currentRank > 0 && currentRank <= 10;
    }

    public long getRemainingHoldTime() {
        if (!isHoldingRank()) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - holdStartTime;
        long remaining = EventNewYear2026.HOLD_TIME_REQUIRED - elapsed;
        return remaining > 0 ? remaining : 0;
    }

    public boolean hasHeldLongEnough() {
        if (!isHoldingRank()) {
            return false;
        }
        return System.currentTimeMillis() - holdStartTime >= EventNewYear2026.HOLD_TIME_REQUIRED;
    }
}
