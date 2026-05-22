package com.tielu.order.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tielu.order.entity.TicketOrder;
import com.tielu.order.feign.TicketFeignClient;
import com.tielu.order.mapper.TicketOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class OrderTimeoutTask {

    private static final Logger log = LoggerFactory.getLogger(OrderTimeoutTask.class);

    private static final String ORDER_TIMEOUT_KEY = "order:timeout:";

    private final TicketOrderMapper orderMapper;
    private final TicketFeignClient ticketFeignClient;
    private final StringRedisTemplate redisTemplate;

    @Value("${order.timeout-minutes:30}")
    private int timeoutMinutes;

    @Autowired
    public OrderTimeoutTask(TicketOrderMapper orderMapper,
                            TicketFeignClient ticketFeignClient,
                            StringRedisTemplate redisTemplate) {
        this.orderMapper = orderMapper;
        this.ticketFeignClient = ticketFeignClient;
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRate = 60000)
    public void checkTimeoutOrders() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(timeoutMinutes);

        LambdaQueryWrapper<TicketOrder> query = new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getStatus, 0)
                .lt(TicketOrder::getCreateTime, timeoutThreshold)
                .last("LIMIT 100");

        List<TicketOrder> timeoutOrders = orderMapper.selectList(query);

        for (TicketOrder order : timeoutOrders) {
            String lockKey = ORDER_TIMEOUT_KEY + order.getOrderNo();
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 5, TimeUnit.MINUTES);
            if (Boolean.FALSE.equals(acquired)) {
                continue;
            }

            try {
                order.setStatus(2);
                orderMapper.updateById(order);

                ticketFeignClient.releaseInventory(
                        order.getTrainId(),
                        order.getTravelDate().toString(),
                        "",
                        1);

                log.info("订单超时自动取消: orderNo={}, userId={}", order.getOrderNo(), order.getUserId());
            } catch (Exception e) {
                log.error("订单超时取消失败: orderNo={}", order.getOrderNo(), e);
                redisTemplate.delete(lockKey);
            }
        }
    }

    public void addOrderTimeout(String orderNo) {
        String key = ORDER_TIMEOUT_KEY + orderNo;
        redisTemplate.opsForValue().set(key, "1", timeoutMinutes + 5, TimeUnit.MINUTES);
    }

    public void removeOrderTimeout(String orderNo) {
        String key = ORDER_TIMEOUT_KEY + orderNo;
        redisTemplate.delete(key);
    }
}
