package com.ngocrong.repository;

import com.ngocrong.data.CCUData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CCUDataRepository extends JpaRepository<CCUData, Integer> {
}
