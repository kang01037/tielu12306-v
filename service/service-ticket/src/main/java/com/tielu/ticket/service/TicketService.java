package com.tielu.ticket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tielu.ticket.dto.request.LockSeatRequest;
import com.tielu.ticket.dto.response.LockSeatResponse;
import com.tielu.ticket.dto.response.SeatAvailabilityResponse;
import com.tielu.ticket.entity.TrainSeatInventory;

public interface TicketService extends IService<TrainSeatInventory> {

    SeatAvailabilityResponse getSeatAvailability(Long trainId, String travelDate);

    LockSeatResponse lockSeat(LockSeatRequest request);

    void unlockSeat(Long orderId);

    void releaseInventory(Long trainId, String travelDate, String seatType, Integer count);
}
