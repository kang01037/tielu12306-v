package com.tielu.train.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("station")
public class Station {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String stationCode;

    private String stationName;

    private String cityCode;

    private String pinyin;

    private String pinyinShort;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
