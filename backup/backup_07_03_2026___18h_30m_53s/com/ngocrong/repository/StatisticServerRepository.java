package com.ngocrong.repository;

import com.ngocrong.data.StatisticServerData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticServerRepository extends JpaRepository<StatisticServerData, Integer> {
    
    /**
     * Find statistic by player ID
     */
    Optional<StatisticServerData> findByPlayerId(Integer playerId);
    
    /**
     * Check if player statistics exists
     */
    boolean existsByPlayerId(Integer playerId);
    
    /**
     * Delete statistics by player ID
     */
    void deleteByPlayerId(Integer playerId);
    
    /**
     * Find latest statistic for player using Pageable
     */
    @Query("SELECT s FROM StatisticServerData s WHERE s.playerId = :playerId ORDER BY s.createDate DESC")
    List<StatisticServerData> findByPlayerIdOrderByCreateDateDesc(@Param("playerId") Integer playerId, Pageable pageable);
    
    /**
     * Alternative: Native query approach
     */
    @Query(value = "SELECT * FROM nr_statistic_server WHERE player_id = :playerId ORDER BY create_date DESC LIMIT 1", nativeQuery = true)
    Optional<StatisticServerData> findLatestByPlayerIdNative(@Param("playerId") Integer playerId);
}