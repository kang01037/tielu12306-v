package com.tielu.ticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LockSeatRequest {

    @NotNull(message = "车次ID不能为空")
    private Long trainId;

    @NotBlank(message = "乘车日期不能为空")
    private String travelDate;

    @NotBlank(message = "席位类型不能为空")
    private String seatType;

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String seatNo;

    private String carriageNo;

    private Integer count;
}
