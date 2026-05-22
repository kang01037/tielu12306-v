package com.tielu.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tielu.common.base.exception.BusinessException;
import com.tielu.common.base.exception.ErrorCode;
import com.tielu.pay.dto.request.CreatePaymentRequest;
import com.tielu.pay.dto.response.PaymentResponse;
import com.tielu.pay.entity.PaymentRecord;
import com.tielu.pay.mapper.PaymentRecordMapper;
import com.tielu.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Map<Integer, String> STATUS_TEXT_MAP = Map.of(
            0, "待支付",
            1, "支付成功",
            2, "支付失败",
            3, "已退款"
    );

    private final PaymentRecordMapper paymentRecordMapper;

    @Autowired
    public PaymentServiceImpl(PaymentRecordMapper paymentRecordMapper) {
        this.paymentRecordMapper = paymentRecordMapper;
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        LambdaQueryWrapper<PaymentRecord> query = new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getOrderNo, request.getOrderNo())
                .eq(PaymentRecord::getStatus, 0);
        PaymentRecord existing = paymentRecordMapper.selectOne(query);
        if (existing != null) {
            return convertToResponse(existing);
        }

        String paymentNo = generatePaymentNo();

        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo(paymentNo);
        record.setOrderNo(request.getOrderNo());
        record.setUserId(request.getUserId());
        record.setAmount(request.getAmount());
        record.setPayChannel(request.getPayChannel());
        record.setStatus(0);
        paymentRecordMapper.insert(record);

        return convertToResponse(record);
    }

    @Override
    @Transactional
    public PaymentResponse handlePaymentCallback(String paymentNo, String transactionId) {
        LambdaQueryWrapper<PaymentRecord> query = new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getPaymentNo, paymentNo);
        PaymentRecord record = paymentRecordMapper.selectOne(query);
        if (record == null) {
            throw new BusinessException(ErrorCode.PAYMENT_RECORD_NOT_EXIST);
        }
        if (record.getStatus() == 1) {
            return convertToResponse(record);
        }

        record.setStatus(1);
        record.setTransactionId(transactionId);
        record.setPayTime(LocalDateTime.now());
        paymentRecordMapper.updateById(record);

        return convertToResponse(record);
    }

    @Override
    public PaymentResponse getPaymentStatus(String paymentNo) {
        LambdaQueryWrapper<PaymentRecord> query = new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getPaymentNo, paymentNo);
        PaymentRecord record = paymentRecordMapper.selectOne(query);
        if (record == null) {
            throw new BusinessException(ErrorCode.PAYMENT_RECORD_NOT_EXIST);
        }
        return convertToResponse(record);
    }

    @Override
    public PaymentRecord getPaymentByOrderNo(String orderNo) {
        LambdaQueryWrapper<PaymentRecord> query = new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getOrderNo, orderNo)
                .eq(PaymentRecord::getStatus, 1)
                .orderByDesc(PaymentRecord::getCreateTime)
                .last("LIMIT 1");
        return paymentRecordMapper.selectOne(query);
    }

    private PaymentResponse convertToResponse(PaymentRecord record) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentNo(record.getPaymentNo());
        response.setOrderNo(record.getOrderNo());
        response.setAmount(record.getAmount());
        response.setPayChannel(record.getPayChannel());
        response.setStatus(record.getStatus());
        response.setStatusText(STATUS_TEXT_MAP.getOrDefault(record.getStatus(), "未知"));
        response.setPayTime(record.getPayTime());
        return response;
    }

    private String generatePaymentNo() {
        return "P" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                String.format("%04d", (int) (Math.random() * 10000));
    }
}
