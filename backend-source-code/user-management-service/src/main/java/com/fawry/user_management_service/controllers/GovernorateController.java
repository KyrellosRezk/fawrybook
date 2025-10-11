package com.fawry.user_management_service.controllers;

import com.fawry.user_management_service.payloads.responses.ObjectResponse;
import com.fawry.user_management_service.services.GovernorateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j(topic = "GovernorateController")
@RestController
@RequestMapping("v1/governorate")
@AllArgsConstructor
public class GovernorateController {

    private final GovernorateService governorateService;

    @GetMapping
    public List<ObjectResponse> getAll() {
        return this.governorateService.getAll();
    }
}
