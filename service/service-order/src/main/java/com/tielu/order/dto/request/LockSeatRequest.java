package com.tielu.order.dto.request;

import jakarta.validation.constraints.NotNull;

public class LockSeatRequest {

    @NotNull(message = "车次ID不能为空")
    private Long trainId;

    @NotNull(message = "出行日期不能为空")
    private String travelDate;

    @NotNull(message = "席位类型不能为空")
    private String seatType;

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "锁定数量不能为空")
    private Integer count;

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }
    public String getTravelDate() { return travelDate; }
    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}
