package com.tielu.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("passenger")
public class Passenger {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String passengerName;

    private String passengerIdCard;

    private Integer passengerType;

    private String phone;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
