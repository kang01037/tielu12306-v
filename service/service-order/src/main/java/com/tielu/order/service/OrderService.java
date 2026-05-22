package com.tielu.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tielu.order.dto.request.CreateOrderRequest;
import com.tielu.order.dto.response.OrderResponse;
import com.tielu.order.entity.TicketOrder;

import java.util.List;

public interface OrderService extends IService<TicketOrder> {

    TicketOrder createOrder(CreateOrderRequest request);

    OrderResponse getOrderDetail(String orderNo);

    List<OrderResponse> getOrderList(Long userId);

    void cancelOrder(String orderNo);

    void refundOrder(String orderNo);

    void handlePaymentCallback(String orderNo, String transactionId);

    OrderResponse changeOrder(String orderNo, CreateOrderRequest newRequest);
}
