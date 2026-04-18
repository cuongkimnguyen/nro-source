package com.ngocrong.repository;

import com.ngocrong.data.CCUData;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameEventRepository extends JpaRepository<CCUData, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE PlayerData p SET p.dataDHVT23 = '[0,0,0,0]'")
    void resetDHVT();

    @Modifying
    @Transactional
    @Query("UPDATE DHVTSieuHangData p SET p.isReward = '0'")
    void resetDHVT_SieuHang();

}
