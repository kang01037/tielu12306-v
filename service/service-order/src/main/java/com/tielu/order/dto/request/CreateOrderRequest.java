package com.tielu.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateOrderRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "车次ID不能为空")
    private Long trainId;

    @NotNull(message = "乘车日期不能为空")
    private LocalDate travelDate;

    @NotNull(message = "出发站ID不能为空")
    private Long fromStationId;

    @NotNull(message = "到达站ID不能为空")
    private Long toStationId;

    @NotBlank(message = "席位类型不能为空")
    private String seatType;

    @NotNull(message = "订单金额不能为空")
    private BigDecimal amount;

    @NotBlank(message = "乘车人姓名不能为空")
    private String passengerName;

    @NotBlank(message = "乘车人身份证号不能为空")
    private String passengerIdCard;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }
    public LocalDate getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }
    public Long getFromStationId() { return fromStationId; }
    public void setFromStationId(Long fromStationId) { this.fromStationId = fromStationId; }
    public Long getToStationId() { return toStationId; }
    public void setToStationId(Long toStationId) { this.toStationId = toStationId; }
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getPassengerIdCard() { return passengerIdCard; }
    public void setPassengerIdCard(String passengerIdCard) { this.passengerIdCard = passengerIdCard; }
}
