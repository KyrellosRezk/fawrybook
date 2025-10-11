package com.fawry.post_management_service.controllers;

import com.fawry.post_management_service.payloads.requests.CreateReactRequest;
import com.fawry.post_management_service.services.ReactService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Slf4j(topic = "ReactController")
@RestController
@RequestMapping("v1/react")
public class ReactController {

    private final ReactService reactService;

    @PostMapping
    public void react(
            @RequestBody CreateReactRequest createReactRequest,
            HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("id");
        this.reactService.react(createReactRequest, userId);
    }

    @DeleteMapping("{id}")
    public void removeReact(
            HttpServletRequest request,
            @PathVariable("id") String id
    ) {
        String userId = (String) request.getAttribute("id");
        this.reactService.removeReact(userId, id);
    }
}
