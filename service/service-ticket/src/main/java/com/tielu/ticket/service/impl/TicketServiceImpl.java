package com.tielu.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tielu.common.base.exception.BusinessException;
import com.tielu.common.base.exception.ErrorCode;
import com.tielu.ticket.dto.request.LockSeatRequest;
import com.tielu.ticket.dto.response.LockSeatResponse;
import com.tielu.ticket.dto.response.SeatAvailabilityResponse;
import com.tielu.ticket.entity.SeatLock;
import com.tielu.ticket.entity.TrainSeatInventory;
import com.tielu.ticket.mapper.SeatLockMapper;
import com.tielu.ticket.mapper.TrainSeatInventoryMapper;
import com.tielu.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl extends ServiceImpl<TrainSeatInventoryMapper, TrainSeatInventory> implements TicketService {

    private final TrainSeatInventoryMapper inventoryMapper;
    private final SeatLockMapper seatLockMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String INVENTORY_KEY_PREFIX = "ticket:inventory:";
    private static final String LOCK_KEY_PREFIX = "ticket:lock:";
    private static final int LOCK_TIMEOUT_MINUTES = 30;

    @Override
    public SeatAvailabilityResponse getSeatAvailability(Long trainId, String travelDate) {
        String cacheKey = INVENTORY_KEY_PREFIX + trainId + ":" + travelDate;
        Map<Object, Object> cached = redisTemplate.opsForHash().entries(cacheKey);

        if (!cached.isEmpty()) {
            SeatAvailabilityResponse response = new SeatAvailabilityResponse();
            response.setTrainId(trainId);
            response.setTravelDate(travelDate);
            Map<String, Integer> availability = new HashMap<>();
            cached.forEach((k, v) -> availability.put(k.toString(), Integer.parseInt(v.toString())));
            response.setSeatAvailability(availability);
            return response;
        }

        LambdaQueryWrapper<TrainSeatInventory> query = new LambdaQueryWrapper<TrainSeatInventory>()
                .eq(TrainSeatInventory::getTrainId, trainId)
                .eq(TrainSeatInventory::getTravelDate, LocalDate.parse(travelDate));
        List<TrainSeatInventory> inventories = inventoryMapper.selectList(query);

        Map<String, Integer> availability = new HashMap<>();
        for (TrainSeatInventory inventory : inventories) {
            availability.put(inventory.getSeatType(), inventory.getTotalCount() - inventory.getSoldCount());
        }

        redisTemplate.opsForHash().putAll(cacheKey,
                availability.entrySet().stream()
                        .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString())));
        redisTemplate.expire(cacheKey, 5, TimeUnit.MINUTES);

        SeatAvailabilityResponse response = new SeatAvailabilityResponse();
        response.setTrainId(trainId);
        response.setTravelDate(travelDate);
        response.setSeatAvailability(availability);
        return response;
    }

    @Override
    @Transactional
    public LockSeatResponse lockSeat(LockSeatRequest request) {
        String lockKey = LOCK_KEY_PREFIX + request.getTrainId() + ":" + request.getTravelDate() + ":" + request.getSeatType();
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, request.getOrderId().toString(), LOCK_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(acquired)) {
            LockSeatResponse response = new LockSeatResponse();
            response.setOrderId(request.getOrderId());
            response.setSuccess(false);
            response.setMessage("座位锁定失败，请稍后重试");
            return response;
        }

        TrainSeatInventory inventory = inventoryMapper.selectOne(new LambdaQueryWrapper<TrainSeatInventory>()
                .eq(TrainSeatInventory::getTrainId, request.getTrainId())
                .eq(TrainSeatInventory::getTravelDate, LocalDate.parse(request.getTravelDate()))
                .eq(TrainSeatInventory::getSeatType, request.getSeatType()));

        if (inventory == null) {
            redisTemplate.delete(lockKey);
            throw new BusinessException(ErrorCode.TICKET_NOT_ENOUGH);
        }

        int available = inventory.getTotalCount() - inventory.getSoldCount();
        int count = request.getCount() != null ? request.getCount() : 1;
        if (available < count) {
            redisTemplate.delete(lockKey);
            throw new BusinessException(ErrorCode.TICKET_NOT_ENOUGH);
        }

        int updated = inventoryMapper.increaseSoldCount(
                request.getTrainId(), request.getTravelDate(), request.getSeatType(), count, inventory.getVersion());

        if (updated == 0) {
            redisTemplate.delete(lockKey);
            throw new BusinessException(ErrorCode.SEAT_ALREADY_SOLD);
        }

        String cacheKey = INVENTORY_KEY_PREFIX + request.getTrainId() + ":" + request.getTravelDate();
        redisTemplate.delete(cacheKey);

        LockSeatResponse response = new LockSeatResponse();
        response.setOrderId(request.getOrderId());
        response.setSuccess(true);
        response.setMessage("座位锁定成功");
        return response;
    }

    @Override
    @Transactional
    public void unlockSeat(Long orderId) {
        SeatLock seatLock = seatLockMapper.selectOne(new LambdaQueryWrapper<SeatLock>()
                .eq(SeatLock::getOrderId, orderId));
        if (seatLock == null) {
            return;
        }

        inventoryMapper.decreaseSoldCount(
                seatLock.getTrainId(), seatLock.getTravelDate(), seatLock.getSeatType(), 1, 0L);

        String cacheKey = INVENTORY_KEY_PREFIX + seatLock.getTrainId() + ":" + seatLock.getTravelDate();
        redisTemplate.delete(cacheKey);

        seatLockMapper.deleteById(seatLock.getId());
    }

    @Override
    @Transactional
    public void releaseInventory(Long trainId, String travelDate, String seatType, Integer count) {
        inventoryMapper.decreaseSoldCount(trainId, travelDate, seatType, count, 0L);

        String cacheKey = INVENTORY_KEY_PREFIX + trainId + ":" + travelDate;
        redisTemplate.delete(cacheKey);
    }
}
