package com.tielu.order.controller;

import com.tielu.common.base.result.Result;
import com.tielu.order.dto.request.CreateOrderRequest;
import com.tielu.order.dto.response.OrderResponse;
import com.tielu.order.entity.TicketOrder;
import com.tielu.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<TicketOrder> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        TicketOrder order = orderService.createOrder(request);
        return Result.success(order);
    }

    @GetMapping("/{orderNo}")
    public Result<OrderResponse> getOrderDetail(@PathVariable String orderNo) {
        return Result.success(orderService.getOrderDetail(orderNo));
    }

    @GetMapping("/list")
    public Result<List<OrderResponse>> getOrderList(@RequestHeader("userId") Long userId) {
        return Result.success(orderService.getOrderList(userId));
    }

    @PostMapping("/{orderNo}/cancel")
    public Result<Void> cancelOrder(@PathVariable String orderNo) {
        orderService.cancelOrder(orderNo);
        return Result.success();
    }

    @PostMapping("/{orderNo}/refund")
    public Result<Void> refundOrder(@PathVariable String orderNo) {
        orderService.refundOrder(orderNo);
        return Result.success();
    }

    @PostMapping("/{orderNo}/pay/callback")
    public Result<Void> handlePaymentCallback(@PathVariable String orderNo,
                                               @RequestParam String transactionId) {
        orderService.handlePaymentCallback(orderNo, transactionId);
        return Result.success();
    }

    @PostMapping("/{orderNo}/change")
    public Result<OrderResponse> changeOrder(@PathVariable String orderNo,
                                              @Valid @RequestBody CreateOrderRequest newRequest) {
        return Result.success(orderService.changeOrder(orderNo, newRequest));
    }
}
