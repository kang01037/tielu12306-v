package com.tielu.order.feign;

import com.tielu.common.base.result.Result;
import com.tielu.order.dto.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-pay")
public interface PayFeignClient {

    @PostMapping("/api/pay/callback")
    Result<PaymentResponse> handlePaymentCallback(
            @RequestParam("paymentNo") String paymentNo,
            @RequestParam("transactionId") String transactionId);

    @GetMapping("/api/pay/status/{paymentNo}")
    Result<PaymentResponse> getPaymentStatus(@PathVariable("paymentNo") String paymentNo);

    @GetMapping("/api/pay/order/{orderNo}")
    Result<PaymentResponse> getPaymentByOrderNo(@PathVariable("orderNo") String orderNo);
}
