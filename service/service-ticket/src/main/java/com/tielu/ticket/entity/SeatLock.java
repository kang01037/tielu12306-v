package com.tielu.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("seat_lock")
public class SeatLock {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long trainId;

    private String travelDate;

    private String seatType;

    private String seatNo;

    private String carriageNo;

    private Long orderId;

    private Long userId;

    private Integer lockMinutes;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
