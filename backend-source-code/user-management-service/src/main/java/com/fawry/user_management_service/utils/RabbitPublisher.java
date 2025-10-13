package com.fawry.user_management_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j(topic = "RabbitPublisher")
@Component
@RequiredArgsConstructor
public class RabbitPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;


    public void publish(String exchange, String routingKey, Object message) {
        try {
            String stringMessage = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(exchange, routingKey, stringMessage);
            log.info("Published message to exchange [{}] with key [{}]", exchange, routingKey);
        } catch (Exception e) {
            log.error("Failed to publish message to exchange [{}]: {}", exchange, e.getMessage());
        }
    }
}
