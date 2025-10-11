package com.fawry.post_management_service.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fawry.post_management_service.payloads.dtos.FollowingMessageDto;
import com.fawry.post_management_service.services.FollowingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FollowingListener {

    private final ObjectMapper objectMapper;
    private final FollowingService followingService;

    public FollowingListener(ObjectMapper objectMapper, FollowingService followingService) {
        this.objectMapper = objectMapper;
        this.followingService = followingService;
    }

    @RabbitListener(queues = "${rabbit.following.queue}")
    public void handleNewFollow(String message) throws JsonProcessingException {
        FollowingMessageDto dto = objectMapper.readValue(message, FollowingMessageDto.class);
        followingService.follow(dto);
        System.out.println("Processed new follow: " + dto);
    }

    @RabbitListener(queues = "${rabbit.unfollowing.queue}")
    public void handleUnfollow(String message) throws JsonProcessingException {
        FollowingMessageDto dto = objectMapper.readValue(message, FollowingMessageDto.class);
        followingService.unfollow(dto);
        System.out.println("Processed unfollow: " + dto);
    }
}