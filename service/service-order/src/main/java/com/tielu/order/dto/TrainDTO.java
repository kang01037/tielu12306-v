package com.tielu.order.dto;

public class TrainDTO {
    private Long id;
    private String trainNo;
    private String trainType;
    private Long startStationId;
    private Long endStationId;
    private String seatTypes;
    private Integer status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTrainNo() { return trainNo; }
    public void setTrainNo(String trainNo) { this.trainNo = trainNo; }
    public String getTrainType() { return trainType; }
    public void setTrainType(String trainType) { this.trainType = trainType; }
    public Long getStartStationId() { return startStationId; }
    public void setStartStationId(Long startStationId) { this.startStationId = startStationId; }
    public Long getEndStationId() { return endStationId; }
    public void setEndStationId(Long endStationId) { this.endStationId = endStationId; }
    public String getSeatTypes() { return seatTypes; }
    public void setSeatTypes(String seatTypes) { this.seatTypes = seatTypes; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
