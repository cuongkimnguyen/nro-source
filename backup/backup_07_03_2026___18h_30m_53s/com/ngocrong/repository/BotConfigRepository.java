package com.ngocrong.repository;

import com.ngocrong.data.BotConfigData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BotConfigRepository extends JpaRepository<BotConfigData, Integer> {
    
    @Query("SELECT b FROM BotConfigData b WHERE b.isUsed = false")
    List<BotConfigData> findByIsUsedFalse();
    
    List<BotConfigData> findAll();
}
