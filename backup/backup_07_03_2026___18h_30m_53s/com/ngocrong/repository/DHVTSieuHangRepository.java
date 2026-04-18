package com.ngocrong.repository;

import com.ngocrong.data.DHVTSieuHangData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface DHVTSieuHangRepository extends JpaRepository<DHVTSieuHangData, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE DHVTSieuHangData p SET p.isReward = :reward WHERE p.playerId = :playerId")
    void setReward(Integer playerId, Integer reward);

    @Query(value = "SELECT * from nr_top_dhvt_sieu_hang_1 where player_id = :playerId order by id desc limit 1", nativeQuery = true)
    Optional<DHVTSieuHangData> findFirstByPlayerId(Integer playerId);

    @Modifying
    @Transactional
    @Query("UPDATE DHVTSieuHangData p SET p.point = :point WHERE p.playerId = :playerId")
    void updatePoint(Integer playerId, Integer point);

    @Modifying
    @Transactional
    @Query("UPDATE DHVTSieuHangData p SET p.point = :point WHERE p.playerId = :playerId and p.point = :oldpoint")
    void updatePoint(Integer playerId, Integer point, Integer oldpoint);

    @Query(value = "SELECT * from nr_top_dhvt_sieu_hang_1 order by top DESC limit 50", nativeQuery = true)
    List<DHVTSieuHangData> findTopReward();
}
