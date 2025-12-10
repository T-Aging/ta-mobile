package com.example.tam.modules.sync.mq;

import java.lang.StackWalker.Option;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.tam.common.entity.Menu;
import com.example.tam.common.entity.OptionGroup;
import com.example.tam.common.entity.OptionValue;
import com.example.tam.common.entity.OrderDetail;
import com.example.tam.common.entity.OrderHeader;
import com.example.tam.common.entity.OrderOption;
import com.example.tam.common.entity.Store;
import com.example.tam.modules.menu.MenuRepository;
import com.example.tam.modules.option.repository.OptionGroupRepository;
import com.example.tam.modules.option.repository.OptionValueRepository;
import com.example.tam.modules.order.OrderHeaderRepository;
import com.example.tam.modules.store.StoreRepository;
import com.example.tam.modules.sync.dto.OrderDetailSyncMessage;
import com.example.tam.modules.sync.dto.OrderHeaderSyncMessage;
import com.example.tam.modules.sync.dto.OrderOptionSyncMessage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSyncConsumer {

    private final OrderHeaderRepository orderHeaderRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    private final OptionGroupRepository optionGroupRepository;
    private final OptionValueRepository optionValueRepository;

    @RabbitListener(queues = RabbitMqConfig.QUEUE_ORDER_SYNC)
    @Transactional
    public void handleOrderSync(OrderHeaderSyncMessage message) {
        log.info("[OrderSyncConsumer] 주문 동기화 메시지 수신: {}", message);

        // *****현재는 중앙 DB에 sourceOrderId를 별도로 저장하는 컬럼이 없어서*****
        //     '중복 체크 없이' 항상 새 주문으로 저장함.

        // 1) 매장 조회
        Store store = storeRepository.findById(message.storeId())
                .orElseThrow(() -> new IllegalStateException(
                        "[OrderSyncConsumer] store_id=" + message.storeId() + " 를 찾을 수 없습니다."
                ));

        // 2) OrderHeader 생성
        OrderHeader orderHeader = OrderHeader.builder()
                .store(store)
                .userId(message.userId())
                .sessionId(null)
                .orderDatetime(message.orderDateTime())
                .orderDate(message.orderDateTime() != null
                        ? message.orderDateTime().toLocalDate()
                        : null)
                .waitingNum(message.waitingNum())
                .totalPrice(message.totalPrice())
                .orderStatus(message.orderState())
                .build();

        // 3) 상세/옵션 매핑
        for (OrderDetailSyncMessage detailDto : message.details()) {

            Menu menu = menuRepository.findById(detailDto.menuId())
                    .orElseThrow(() -> new IllegalStateException(
                            "[OrderSyncConsumer] menu_id=" + detailDto.menuId() + " 를 찾을 수 없습니다."
                    ));

            OrderDetail detail = OrderDetail.builder()
                    .menu(menu)
                    .quantity(detailDto.quantity())
                    .temperature(detailDto.temperature())
                    .size(detailDto.size())
                    .orderDetailPrice(detailDto.orderDetailPrice())
                    .build();

            // 옵션들
            if (detailDto.options() != null) {
                for (OrderOptionSyncMessage optionDto : detailDto.options()) {
                        OptionGroup optionGroup = null;
                        OptionValue optionValue = null;

                        if (optionDto.groupId() != null) {
                                optionGroup = optionGroupRepository.findById(optionDto.groupId())
                                        .orElse(null);
                        }

                        if (optionDto.valueId() != null) {
                                optionValue = optionValueRepository.findById(optionDto.valueId())
                                        .orElse(null);
                        }

                        OrderOption option = OrderOption.builder()
                                .orderDetail(detail)
                                .optionId(optionDto.optionId())
                                .optionGroup(optionGroup)
                                .optionValue(optionValue)
                                .extraNum(optionDto.extraNum())
                                .extraPrice(optionDto.extraPrice())
                                .build();

                    detail.addOrderOption(option);
                }
            }

            orderHeader.addDetail(detail);
        }

        // 4) 저장
        orderHeaderRepository.save(orderHeader);

        log.info("[OrderSyncConsumer] 주문 동기화 완료: centralOrderId={}", orderHeader.getOrderId());
    }
}