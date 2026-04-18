package com.ngocrong.statistic;

import com.ngocrong.consts.ItemName;
import com.ngocrong.data.StatisticServerData;
import com.ngocrong.item.Item;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.user.Player;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public class StatisticService {
    
    public static void saveStatistic(Player player) {
        StatisticServerData data = buildData(player);
        upsertStatistic(data);
    }
    
    /**
     * Update if exists, Insert if not exists
     */
    private static void upsertStatistic(StatisticServerData newData) {
        // Tìm record hiện có theo playerId
        Optional<StatisticServerData> existingOpt = GameRepository.getInstance()
            .statisticServerRepository.findByPlayerId(newData.getPlayerId());
        
        if (existingOpt.isPresent()) {
            // Update existing record
            StatisticServerData existing = existingOpt.get();
            updateExistingData(existing, newData);
            GameRepository.getInstance().statisticServerRepository.save(existing);
        } else {
            // Insert new record
            GameRepository.getInstance().statisticServerRepository.save(newData);
        }
    }
    
    /**
     * Get latest statistic for player (helper method)
     */
    public static Optional<StatisticServerData> getLatestStatistic(Integer playerId) {
        // Option 1: Using Pageable (recommended)
        Pageable pageable = PageRequest.of(0, 1);
        List<StatisticServerData> results = GameRepository.getInstance()
            .statisticServerRepository.findByPlayerIdOrderByCreateDateDesc(playerId, pageable);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        
        // Option 2: Using native query (alternative)
        // return GameRepository.getInstance().statisticServerRepository.findLatestByPlayerIdNative(playerId);
    }
    
    /**
     * Update existing record with new data
     */
    private static void updateExistingData(StatisticServerData existing, StatisticServerData newData) {
        existing.setPlayerName(newData.getPlayerName());
        existing.setGoldBar(newData.getGoldBar());
        existing.setGoldBarSpentToday(newData.getGoldBarSpentToday());
        existing.setAoThanLinh(newData.getAoThanLinh());
        existing.setQuanThanLinh(newData.getQuanThanLinh());
        existing.setGangThanLinh(newData.getGangThanLinh());
        existing.setGiayThanLinh(newData.getGiayThanLinh());
        existing.setNhanThanLinh(newData.getNhanThanLinh());
        existing.setItemCap2(newData.getItemCap2());
        existing.setNr1s(newData.getNr1s());
        existing.setNr2s(newData.getNr2s());
        existing.setNr3s(newData.getNr3s());
        existing.setCreateDate(newData.getCreateDate()); // Update timestamp
    }
    
    private static StatisticServerData buildData(Player player) {
        StatisticServerData data = new StatisticServerData();
        data.setPlayerId(player.id);
        data.setPlayerName(player.name);
        data.setCreateDate(Instant.now());
        
        int goldBar = 0;
        int aoTL = 0;
        int quanTL = 0;
        int gangTL = 0;
        int giayTL = 0;
        int nhanTL = 0;
        int itemCap2 = 0;
        int nr1 = 0, nr2 = 0, nr3 = 0;
        boolean[] thienMenh = new boolean[7];
        
        Item[][] lists = new Item[][]{player.itemBag, player.itemBox, player.itemBody};
        for (Item[] arr : lists) {
            if (arr == null) continue;
            for (Item item : arr) {
                if (item == null || item.template == null) continue;
                int id = item.template.id;
                
                if (id == ItemName.THOI_VANG) {
                    goldBar += item.quantity;
                }
                if (item.template.isAoThanLinh()) aoTL += item.quantity;
                if (item.template.isQuanThanLinh()) quanTL += item.quantity;
                if (item.template.isGangThanLinh()) gangTL += item.quantity;
                if (item.template.isGiayThanLinh()) giayTL += item.quantity;
                if (item.template.isNhanThanLinh()) nhanTL += item.quantity;
                if (item.template.id == 1021 || item.template.id == 1022 || item.template.id == 1023) {
                    itemCap2 += item.quantity;
                }
                
                switch (id) {
                    case ItemName.NGOC_RONG_1_SAO:
                        nr1 += item.quantity;
                        break;
                    case ItemName.NGOC_RONG_2_SAO:
                        nr2 += item.quantity;
                        break;
                    case ItemName.NGOC_RONG_3_SAO:
                        nr3 += item.quantity;
                        break;
                }
            }
        }
        
        data.setGoldBar(goldBar);
        data.setAoThanLinh(aoTL);
        data.setQuanThanLinh(quanTL);
        data.setGangThanLinh(gangTL);
        data.setGiayThanLinh(giayTL);
        data.setNhanThanLinh(nhanTL);
        data.setItemCap2(itemCap2);
        data.setNr1s(nr1);
        data.setNr2s(nr2);
        data.setNr3s(nr3);
        
        Instant startDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Integer spent = GameRepository.getInstance().historyGoldBar.sumGoldBarUsedToday(player.name, startDay);
        if (spent == null) spent = 0;
        data.setGoldBarSpentToday(Math.abs(spent));
        
        return data;
    }
}