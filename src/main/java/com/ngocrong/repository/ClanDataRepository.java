package com.ngocrong.repository;

import com.ngocrong.data.ClanData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Transactional
@Repository
public interface ClanDataRepository extends JpaRepository<ClanData, Integer> {

    @Modifying
    @Query("UPDATE ClanData c SET c.abbreviation = :abbreviation, "
            + "c.leaderId = :leaderId, "
            + "c.leaderName = :leaderName, "
            + "c.slogan = :slogan, "
            + "c.imageId = :imageId, "
            + "c.level = :level, "
            + "c.clanPoint = :clanPoint, "
            + "c.powerPoint = :powerPoint, "
            + "c.maxMember = :maxMember, "
            + "c.itemBox = :itemBox  "
            + "WHERE c.id = :id")
    void saveData(Integer id, String abbreviation, Integer leaderId, String leaderName,
            String slogan, Byte imageId, Byte level, Integer clanPoint,
            Long powerPoint, Byte maxMember, String itemBox);
}
