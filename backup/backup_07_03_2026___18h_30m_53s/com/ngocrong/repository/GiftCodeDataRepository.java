package com.ngocrong.repository;

import com.ngocrong.data.GiftCodeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCodeDataRepository extends JpaRepository<GiftCodeData, Integer> {

    List<GiftCodeData> findByCode(String code);
}
