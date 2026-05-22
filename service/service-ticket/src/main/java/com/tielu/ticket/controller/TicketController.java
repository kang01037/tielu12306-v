package com.tielu.ticket.controller;

import com.tielu.common.base.result.Result;
import com.tielu.ticket.dto.request.LockSeatRequest;
import com.tielu.ticket.dto.response.LockSeatResponse;
import com.tielu.ticket.dto.response.SeatAvailabilityResponse;
import com.tielu.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/availability/{trainId}")
    public Result<SeatAvailabilityResponse> getSeatAvailability(
            @PathVariable Long trainId,
            @RequestParam String travelDate) {
        return Result.success(ticketService.getSeatAvailability(trainId, travelDate));
    }

    @PostMapping("/lock")
    public Result<LockSeatResponse> lockSeat(@Valid @RequestBody LockSeatRequest request) {
        return Result.success(ticketService.lockSeat(request));
    }

    @DeleteMapping("/unlock/{orderId}")
    public Result<Void> unlockSeat(@PathVariable Long orderId) {
        ticketService.unlockSeat(orderId);
        return Result.success();
    }

    @PutMapping("/release")
    public Result<Void> releaseInventory(
            @RequestParam Long trainId,
            @RequestParam String travelDate,
            @RequestParam String seatType,
            @RequestParam Integer count) {
        ticketService.releaseInventory(trainId, travelDate, seatType, count);
        return Result.success();
    }
}
