package com.ngocrong.repository;

import com.ngocrong.data.DropRateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DropRateRepository extends JpaRepository<DropRateData, Integer> {
}
