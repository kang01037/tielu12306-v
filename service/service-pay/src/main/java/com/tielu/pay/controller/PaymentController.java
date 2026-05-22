package com.tielu.pay.controller;

import com.tielu.common.base.result.Result;
import com.tielu.pay.dto.request.CreatePaymentRequest;
import com.tielu.pay.dto.response.PaymentResponse;
import com.tielu.pay.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pay")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public Result<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return Result.success(paymentService.createPayment(request));
    }

    @PostMapping("/callback")
    public Result<PaymentResponse> handleCallback(@RequestParam String paymentNo,
                                                   @RequestParam String transactionId) {
        return Result.success(paymentService.handlePaymentCallback(paymentNo, transactionId));
    }

    @GetMapping("/status/{paymentNo}")
    public Result<PaymentResponse> getPaymentStatus(@PathVariable String paymentNo) {
        return Result.success(paymentService.getPaymentStatus(paymentNo));
    }

    @GetMapping("/order/{orderNo}")
    public Result<PaymentResponse> getPaymentByOrderNo(@PathVariable String orderNo) {
        return Result.success(paymentService.getPaymentByOrderNo(orderNo) != null
                ? convertToResponse(paymentService.getPaymentByOrderNo(orderNo))
                : null);
    }

    private PaymentResponse convertToResponse(com.tielu.pay.entity.PaymentRecord record) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentNo(record.getPaymentNo());
        response.setOrderNo(record.getOrderNo());
        response.setAmount(record.getAmount());
        response.setPayChannel(record.getPayChannel());
        response.setStatus(record.getStatus());
        response.setPayTime(record.getPayTime());
        return response;
    }
}
