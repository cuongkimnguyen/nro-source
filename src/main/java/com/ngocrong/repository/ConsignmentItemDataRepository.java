package com.ngocrong.repository;

import com.ngocrong.data.ConsignmentItemData;
import com.ngocrong.shop.ConsignmentItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Transactional
@Repository
public interface ConsignmentItemDataRepository extends JpaRepository<ConsignmentItemData, Long> {

    List<ConsignmentItemData> findByStatusIn(ConsignmentItemStatus[] statusList);

    @Modifying
    @Query("UPDATE ConsignmentItemData i SET i.buyerId = :buyerId, "
            + "i.status = :status, "
            + "i.options = :options, "
            + "i.buyTime = :buyTime, "
            + "i.receiveTime = :receiveTime "
            + "WHERE i.id = :id")
    void saveData(Long id, Integer buyerId, ConsignmentItemStatus status, String options, Timestamp buyTime, Timestamp receiveTime);
}
