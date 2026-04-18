package com.ngocrong.repository;

import com.ngocrong.data.BoMongMocDiemData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoMongMocDiemRepository extends JpaRepository<BoMongMocDiemData, Integer> {

    @Query(value = "SELECT * FROM nr_bo_mong_moc_diem WHERE active = 1 ORDER BY sort_order ASC", nativeQuery = true)
    List<BoMongMocDiemData> findAllActive();

    @Query(value = "SELECT * FROM nr_bo_mong_moc_diem WHERE active = 1 AND diem_can_thiet = :diemCanThiet", nativeQuery = true)
    BoMongMocDiemData findByDiemCanThiet(@Param("diemCanThiet") Integer diemCanThiet);
}
