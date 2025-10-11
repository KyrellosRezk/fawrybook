package com.fawry.user_management_service.controllers;

import com.fawry.user_management_service.payloads.responses.ObjectResponse;
import com.fawry.user_management_service.services.PositionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j(topic = "PositionController")
@AllArgsConstructor
@RequestMapping("v1/position")
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public List<ObjectResponse> getAll() {
        return this.positionService.getAll();
    }
}
