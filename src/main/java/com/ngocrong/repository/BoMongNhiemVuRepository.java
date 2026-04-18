package com.ngocrong.repository;

import com.ngocrong.data.BoMongNhiemVuData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface BoMongNhiemVuRepository extends JpaRepository<BoMongNhiemVuData, Integer> {

    @Query(value = "SELECT * FROM nr_bo_mong_nhiem_vu WHERE player_id = :playerId LIMIT 1", nativeQuery = true)
    Optional<BoMongNhiemVuData> findByPlayerId(@Param("playerId") Integer playerId);

    @Modifying
    @Query(value = "DELETE FROM nr_bo_mong_nhiem_vu WHERE player_id = :playerId", nativeQuery = true)
    void deleteByPlayerId(@Param("playerId") Integer playerId);
}

