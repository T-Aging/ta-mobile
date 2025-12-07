package com.example.tam.modules.sync.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.tam.modules.sync.dto.OrderHeaderSyncMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderSyncConsumer {
    
    @RabbitListener(queues = RabbitMqConfig.QUEUE_ORDER_SYNC)
    public void handleOrderSync(OrderHeaderSyncMessage message){
        log.info("[OrderSyncConsumer] 주문 동기화 메시지 수신: {}", message);

        
    }
}
