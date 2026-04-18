package com.ngocrong.data;

import com.google.gson.Gson;
import com.ngocrong.item.Item;
import com.ngocrong.shop.ConsignmentItemStatus;
import com.ngocrong.user.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "nr_consignment_shop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsignmentItemData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "seller_id")
    public Integer sellerId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    public ConsignmentItemStatus status;

    @Column(name = "item_id")
    public Integer itemId;

    @Column(name = "quantity")
    public Integer quantity;

    @Column(name = "options")
    public String options;

    @Column(name = "buyer_id")
    public Integer buyerId;

    @Column(name = "price")
    public Integer price;

    @Column(name = "buy_time")
    public Timestamp buyTime;

    @Column(name = "receive_time")
    public Timestamp receiveTime;

    @Column(name = "create_time")
    public Timestamp createTime;

    public ConsignmentItemData(Player player, Item item, int quantity, int price) {
        status = ConsignmentItemStatus.ON_SALE;
        sellerId = player.id;
        buyerId = -1;
        this.price = price;
        createTime = new Timestamp(System.currentTimeMillis());
        itemId = (int) item.template.id;
        this.quantity = quantity;
        this.price = price;
        options = new Gson().toJson(item.options);
    }
}
