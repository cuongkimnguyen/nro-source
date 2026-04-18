package com.ngocrong.repository;

import com.ngocrong.data.GiftHistoryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCodeHistoryDataRepository extends JpaRepository<GiftHistoryData, Long> {

    List<GiftHistoryData> findByGiftCodeIdAndPlayerId(Integer giftCodeId, Integer playerId);
}
