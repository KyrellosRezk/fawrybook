package com.fawry.post_management_service.services;

import com.fawry.post_management_service.models.ReactEntity;
import com.fawry.post_management_service.payloads.requests.CreateReactRequest;
import com.fawry.post_management_service.repositories.ReactRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j(topic = "ReactService")
@AllArgsConstructor
public class ReactService {
    private final ReactRepository reactRepository;

    public void react(CreateReactRequest createReactRequest, String userId) {
        Optional<ReactEntity> reactEntity = this.reactRepository.findByUserIdAndPost_Id(userId, createReactRequest.postId());
        if(reactEntity.isEmpty()){
            this.reactRepository.save(new ReactEntity(createReactRequest, userId));
        } else {
            reactEntity.get().setType(createReactRequest.type());
            this.reactRepository.save(reactEntity.get());
        }
    }

    public void removeReact(String userId, String id) {
        this.reactRepository.deleteByUserIdAndId(userId, id);
    }
}
