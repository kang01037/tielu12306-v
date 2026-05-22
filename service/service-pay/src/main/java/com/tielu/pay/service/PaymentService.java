package com.tielu.pay.service;

import com.tielu.pay.dto.request.CreatePaymentRequest;
import com.tielu.pay.dto.response.PaymentResponse;
import com.tielu.pay.entity.PaymentRecord;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request);

    PaymentResponse handlePaymentCallback(String paymentNo, String transactionId);

    PaymentResponse getPaymentStatus(String paymentNo);

    PaymentRecord getPaymentByOrderNo(String orderNo);
}
