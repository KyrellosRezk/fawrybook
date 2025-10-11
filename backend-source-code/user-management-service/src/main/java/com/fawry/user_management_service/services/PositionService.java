package com.fawry.user_management_service.services;

import com.fawry.user_management_service.payloads.responses.ObjectResponse;
import com.fawry.user_management_service.repositories.GovernorateRepository;
import com.fawry.user_management_service.repositories.PositionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j(topic = "PositionService")
public class PositionService {

    private final PositionRepository positionRepository;

    public List<ObjectResponse> getAll() {
        return this.positionRepository.findAll().stream()
                .map(governorateEntity -> new ObjectResponse(
                        governorateEntity.getId(),
                        governorateEntity.getName()
                ))
                .collect(Collectors.toList());
    }
}
