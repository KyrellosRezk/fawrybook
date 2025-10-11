package com.fawry.user_management_service.controllers;

import com.fawry.user_management_service.payloads.requests.PaginationRequest;
import com.fawry.user_management_service.payloads.requests.RequestActionRequest;
import com.fawry.user_management_service.payloads.requests.SendRequestRequest;
import com.fawry.user_management_service.payloads.responses.UserBasicDataResponse;
import com.fawry.user_management_service.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("v1/request")
@Slf4j(topic = "RequestController")
public class RequestController {
    private final UserService userService;

    @PostMapping("pagination")
    public Page<UserBasicDataResponse> getRequests(
            HttpServletRequest request,
            @RequestBody PaginationRequest paginationRequest
    ) {
        String id = (String) request.getAttribute("id");
        return this.userService.getRequests(paginationRequest, id);
    }

    @PostMapping("reply")
    public void takeActionOnRequest(
            HttpServletRequest request,
            @RequestBody RequestActionRequest requestActionRequest
    ) {
        String id = (String) request.getAttribute("id");
        this.userService.takeActionOnRequest(requestActionRequest, id);
    }

    @PostMapping("action")
    public void sendRequest(
            HttpServletRequest request,
            @RequestBody SendRequestRequest sendRequestRequest
    ) {
        String id = (String) request.getAttribute("id");
        this.userService.sendRequest(sendRequestRequest, id);
    }
}
