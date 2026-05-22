package com.tielu.train.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("train")
public class Train {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String trainNo;

    private String trainType;

    private Long startStationId;

    private Long endStationId;

    private String seatTypes;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
