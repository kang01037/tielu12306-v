package com.tielu.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tielu.common.base.exception.BusinessException;
import com.tielu.common.base.exception.ErrorCode;
import com.tielu.common.base.result.Result;
import com.tielu.order.dto.TrainDTO;
import com.tielu.order.dto.request.CreateOrderRequest;
import com.tielu.order.dto.request.LockSeatRequest;
import com.tielu.order.dto.response.LockSeatResponse;
import com.tielu.order.dto.response.OrderResponse;
import com.tielu.order.dto.response.SeatAvailabilityResponse;
import com.tielu.order.entity.TicketOrder;
import com.tielu.order.entity.TicketOrderItem;
import com.tielu.order.feign.PayFeignClient;
import com.tielu.order.feign.TicketFeignClient;
import com.tielu.order.feign.TrainFeignClient;
import com.tielu.order.mapper.TicketOrderItemMapper;
import com.tielu.order.mapper.TicketOrderMapper;
import com.tielu.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<TicketOrderMapper, TicketOrder> implements OrderService {

    private final TicketOrderMapper orderMapper;
    private final TicketOrderItemMapper orderItemMapper;
    private final TicketFeignClient ticketFeignClient;
    private final TrainFeignClient trainFeignClient;
    private final PayFeignClient payFeignClient;

    @Autowired
    public OrderServiceImpl(TicketOrderMapper orderMapper,
                            TicketOrderItemMapper orderItemMapper,
                            TicketFeignClient ticketFeignClient,
                            TrainFeignClient trainFeignClient,
                            PayFeignClient payFeignClient) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.ticketFeignClient = ticketFeignClient;
        this.trainFeignClient = trainFeignClient;
        this.payFeignClient = payFeignClient;
    }

    private static final Map<Integer, String> STATUS_TEXT_MAP = Map.of(
            0, "待支付",
            1, "已支付",
            2, "已取消",
            3, "已退票",
            4, "已改签"
    );

    @Override
    @Transactional
    public TicketOrder createOrder(CreateOrderRequest request) {
        Result<TrainDTO> trainResult = trainFeignClient.getTrainEntity(request.getTrainId());
        TrainDTO train = trainResult.getData();
        if (train == null) {
            throw new BusinessException(ErrorCode.TRAIN_NOT_EXIST);
        }

        String travelDateStr = request.getTravelDate().toString();
        Result<SeatAvailabilityResponse> availabilityResult = ticketFeignClient.getSeatAvailability(
                request.getTrainId(), travelDateStr);
        SeatAvailabilityResponse availability = availabilityResult.getData();
        if (availability == null || availability.getAvailableSeats() == null || availability.getAvailableSeats() <= 0) {
            throw new BusinessException(ErrorCode.TICKET_NOT_ENOUGH);
        }

        String orderNo = generateOrderNo();

        LockSeatRequest lockRequest = new LockSeatRequest();
        lockRequest.setTrainId(request.getTrainId());
        lockRequest.setTravelDate(travelDateStr);
        lockRequest.setSeatType(request.getSeatType());
        lockRequest.setCount(1);

        TicketOrder order = new TicketOrder();
        order.setOrderNo(orderNo);
        order.setUserId(request.getUserId());
        order.setTrainId(request.getTrainId());
        order.setTravelDate(request.getTravelDate());
        order.setFromStationId(request.getFromStationId());
        order.setToStationId(request.getToStationId());
        order.setAmount(request.getAmount());
        order.setStatus(0);
        orderMapper.insert(order);

        lockRequest.setOrderId(order.getId());

        Result<LockSeatResponse> lockResult = ticketFeignClient.lockSeat(lockRequest);
        LockSeatResponse lockResponse = lockResult.getData();
        if (lockResponse == null || Boolean.FALSE.equals(lockResponse.getSuccess())) {
            orderMapper.deleteById(order.getId());
            throw new BusinessException(ErrorCode.SEAT_LOCK_FAILED);
        }

        TicketOrderItem orderItem = new TicketOrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setPassengerName(request.getPassengerName());
        orderItem.setPassengerIdCard(request.getPassengerIdCard());
        orderItem.setSeatType(request.getSeatType());
        orderItem.setTicketPrice(request.getAmount());
        orderItem.setTicketStatus(0);
        orderItemMapper.insert(orderItem);

        return order;
    }

    @Override
    public OrderResponse getOrderDetail(String orderNo) {
        LambdaQueryWrapper<TicketOrder> query = new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getOrderNo, orderNo);
        TicketOrder order = orderMapper.selectOne(query);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        return convertToResponse(order);
    }

    @Override
    public List<OrderResponse> getOrderList(Long userId) {
        LambdaQueryWrapper<TicketOrder> query = new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getUserId, userId)
                .orderByDesc(TicketOrder::getCreateTime);
        List<TicketOrder> orders = orderMapper.selectList(query);
        return orders.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelOrder(String orderNo) {
        LambdaQueryWrapper<TicketOrder> query = new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getOrderNo, orderNo);
        TicketOrder order = orderMapper.selectOne(query);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        order.setStatus(2);
        orderMapper.updateById(order);

        ticketFeignClient.releaseInventory(order.getTrainId(), order.getTravelDate().toString(), "", 1);
    }

    @Override
    @Transactional
    public void refundOrder(String orderNo) {
        LambdaQueryWrapper<TicketOrder> query = new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getOrderNo, orderNo);
        TicketOrder order = orderMapper.selectOne(query);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }

        order.setStatus(3);
        orderMapper.updateById(order);

        ticketFeignClient.releaseInventory(order.getTrainId(), order.getTravelDate().toString(), "", 1);
    }

    @Override
    @Transactional
    public void handlePaymentCallback(String orderNo, String transactionId) {
        LambdaQueryWrapper<TicketOrder> query = new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getOrderNo, orderNo);
        TicketOrder order = orderMapper.selectOne(query);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now().toString());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public OrderResponse changeOrder(String orderNo, CreateOrderRequest newRequest) {
        LambdaQueryWrapper<TicketOrder> query = new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getOrderNo, orderNo);
        TicketOrder oldOrder = orderMapper.selectOne(query);
        if (oldOrder == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (oldOrder.getStatus() != 0 && oldOrder.getStatus() != 1) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }

        Result<TrainDTO> trainResult = trainFeignClient.getTrainEntity(newRequest.getTrainId());
        TrainDTO train = trainResult.getData();
        if (train == null) {
            throw new BusinessException(ErrorCode.TRAIN_NOT_EXIST);
        }

        String travelDateStr = newRequest.getTravelDate().toString();
        Result<SeatAvailabilityResponse> availabilityResult = ticketFeignClient.getSeatAvailability(
                newRequest.getTrainId(), travelDateStr);
        SeatAvailabilityResponse availability = availabilityResult.getData();
        if (availability == null || availability.getAvailableSeats() == null || availability.getAvailableSeats() <= 0) {
            throw new BusinessException(ErrorCode.TICKET_NOT_ENOUGH);
        }

        ticketFeignClient.releaseInventory(oldOrder.getTrainId(), oldOrder.getTravelDate().toString(), "", 1);

        oldOrder.setTrainId(newRequest.getTrainId());
        oldOrder.setTravelDate(newRequest.getTravelDate());
        oldOrder.setFromStationId(newRequest.getFromStationId());
        oldOrder.setToStationId(newRequest.getToStationId());
        oldOrder.setAmount(newRequest.getAmount());
        oldOrder.setStatus(4);
        orderMapper.updateById(oldOrder);

        return convertToResponse(oldOrder);
    }

    private OrderResponse convertToResponse(TicketOrder order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setUserId(order.getUserId());
        response.setTrainId(order.getTrainId());
        response.setTravelDate(order.getTravelDate());
        response.setFromStationId(order.getFromStationId());
        response.setToStationId(order.getToStationId());
        response.setAmount(order.getAmount());
        response.setStatus(order.getStatus());
        response.setStatusText(STATUS_TEXT_MAP.getOrDefault(order.getStatus(), "未知"));
        response.setPayTime(order.getPayTime());
        response.setCreateTime(order.getCreateTime());
        return response;
    }

    private String generateOrderNo() {
        return "T" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                String.format("%04d", (int) (Math.random() * 10000));
    }
}
