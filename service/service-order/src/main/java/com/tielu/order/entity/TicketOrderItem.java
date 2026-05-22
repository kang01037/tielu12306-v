package com.tielu.order.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("ticket_order_item")
public class TicketOrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String passengerName;

    private String passengerIdCard;

    private String seatType;

    private String carriageNo;

    private String seatNo;

    private BigDecimal ticketPrice;

    private Integer ticketStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getPassengerIdCard() { return passengerIdCard; }
    public void setPassengerIdCard(String passengerIdCard) { this.passengerIdCard = passengerIdCard; }
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
    public String getCarriageNo() { return carriageNo; }
    public void setCarriageNo(String carriageNo) { this.carriageNo = carriageNo; }
    public String getSeatNo() { return seatNo; }
    public void setSeatNo(String seatNo) { this.seatNo = seatNo; }
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }
    public Integer getTicketStatus() { return ticketStatus; }
    public void setTicketStatus(Integer ticketStatus) { this.ticketStatus = ticketStatus; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
