package com.fawry.user_management_service.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j(topic = "RabbitPublisher")
@Component
@RequiredArgsConstructor
public class RabbitPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(String exchange, String routingKey, Object message) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            log.info("Published message to exchange [{}] with key [{}]", exchange, routingKey);
        } catch (Exception e) {
            log.error("Failed to publish message to exchange [{}]: {}", exchange, e.getMessage());
        }
    }
}
