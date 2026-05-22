package com.tielu.order.feign;

import com.tielu.common.base.result.Result;
import com.tielu.order.dto.request.LockSeatRequest;
import com.tielu.order.dto.response.LockSeatResponse;
import com.tielu.order.dto.response.SeatAvailabilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "service-ticket")
public interface TicketFeignClient {

    @GetMapping("/api/ticket/availability/{trainId}")
    Result<SeatAvailabilityResponse> getSeatAvailability(
            @PathVariable("trainId") Long trainId,
            @RequestParam("travelDate") String travelDate);

    @PostMapping("/api/ticket/lock")
    Result<LockSeatResponse> lockSeat(@RequestBody LockSeatRequest request);

    @DeleteMapping("/api/ticket/unlock/{orderId}")
    Result<Void> unlockSeat(@PathVariable("orderId") Long orderId);

    @PutMapping("/api/ticket/release")
    Result<Void> releaseInventory(@RequestParam("trainId") Long trainId,
                                  @RequestParam("travelDate") String travelDate,
                                  @RequestParam("seatType") String seatType,
                                  @RequestParam("count") Integer count);
}
