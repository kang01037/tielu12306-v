package com.tielu.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("train_seat_inventory")
public class TrainSeatInventory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long trainId;

    private LocalDate travelDate;

    private String seatType;

    private Integer totalCount;

    private Integer soldCount;

    private Long version;
}
