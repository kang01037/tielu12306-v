package com.tielu.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddPassengerRequest {

    @NotBlank(message = "乘车人姓名不能为空")
    private String passengerName;

    @NotBlank(message = "身份证号不能为空")
    private String passengerIdCard;

    private Integer passengerType;

    private String phone;
}
