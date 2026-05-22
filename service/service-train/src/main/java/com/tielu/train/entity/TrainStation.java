package com.tielu.train.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("train_station")
public class TrainStation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long trainId;

    private Long stationId;

    private Integer stationSeq;

    private LocalTime arrivalTime;

    private LocalTime departureTime;

    private Integer distanceKm;

    private Integer stopMinutes;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
