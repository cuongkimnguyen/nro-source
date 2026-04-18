package com.ngocrong.repository;

import com.ngocrong.data.OsinCheckInData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OsinCheckInRepository extends JpaRepository<OsinCheckInData, Integer> {

    @Query(value = "SELECT COUNT(*) FROM nr_event_checkin WHERE DATE(checkin_date) = CURDATE()", nativeQuery = true)
    int countToday();

    @Query(value = "SELECT COUNT(*) FROM nr_event_checkin WHERE player_id = :playerId AND DATE(checkin_date) = CURDATE()", nativeQuery = true)
    int countTodayByPlayer(@Param("playerId") int playerId);

    @Query(value = "SELECT COUNT(*) FROM nr_event_checkin WHERE DATE(checkin_date) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)", nativeQuery = true)
    int countYesterday();

    @Query(value = "SELECT * FROM nr_event_checkin WHERE player_id = :playerId AND DATE(checkin_date) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) LIMIT 1", nativeQuery = true)
    Optional<OsinCheckInData> findYesterday(@Param("playerId") int playerId);

    @Query(value = "SELECT * FROM nr_event_checkin WHERE player_id = :playerId AND DATE(checkin_date) = CURDATE() LIMIT 1", nativeQuery = true)
    Optional<OsinCheckInData> findTodayByPlayer(@Param("playerId") int playerId);

    @Query(value = "SELECT * FROM nr_event_checkin WHERE DATE(checkin_date) = CURDATE() AND is_rewarded = 0", nativeQuery = true)
    List<OsinCheckInData> findAllTodayUnrewarded();
    
    @Query(value = "SELECT * FROM nr_event_checkin WHERE DATE(checkin_date) = CURDATE() AND is_rewarded < :count", nativeQuery = true)
    List<OsinCheckInData> findAllTodayUnrewarded(@Param("count") int count);

    @Query(value = "SELECT * FROM nr_event_checkin WHERE player_id = :playerId ORDER BY checkin_date DESC", nativeQuery = true)
    List<OsinCheckInData> findByPlayerId(@Param("playerId") int playerId);

    @Query(value = "SELECT * FROM nr_event_checkin WHERE player_id = :playerId AND DATE(checkin_date) = DATE(:date) LIMIT 1", nativeQuery = true)
    Optional<OsinCheckInData> findByPlayerIdAndDate(@Param("playerId") int playerId, @Param("date") Date date);
}
