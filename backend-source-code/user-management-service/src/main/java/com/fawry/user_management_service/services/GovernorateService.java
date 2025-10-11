package com.fawry.user_management_service.services;

import com.fawry.user_management_service.models.GovernorateEntity;
import com.fawry.user_management_service.payloads.responses.ObjectResponse;
import com.fawry.user_management_service.repositories.GovernorateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j(topic = "GovernorateService")
public class GovernorateService {

    private final GovernorateRepository governorateRepository;

    public List<ObjectResponse> getAll() {
        return this.governorateRepository.findAll().stream()
                .map(governorateEntity -> new ObjectResponse(
                        governorateEntity.getId(),
                        governorateEntity.getName()
                ))
                .collect(Collectors.toList());
    }
}
