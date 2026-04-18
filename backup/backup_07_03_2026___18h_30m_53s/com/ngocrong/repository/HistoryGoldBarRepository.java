package com.ngocrong.repository;

import com.ngocrong.data.HistoryGoldBar;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.time.Instant;

public interface HistoryGoldBarRepository extends CrudRepository<HistoryGoldBar, Integer> {

    @Query("SELECT COALESCE(SUM(h.numberGold),0) FROM HistoryGoldBar h WHERE h.playerName = :name AND h.createDate >= :time AND h.numberGold < 0")
    Integer sumGoldBarUsedToday(@Param("name") String name, @Param("time") Instant time);
}