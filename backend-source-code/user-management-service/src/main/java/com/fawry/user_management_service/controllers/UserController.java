package com.fawry.user_management_service.controllers;

import com.fawry.user_management_service.payloads.requests.PaginationRequest;
import com.fawry.user_management_service.payloads.requests.RequestActionRequest;
import com.fawry.user_management_service.payloads.responses.SuggestionFriend;
import com.fawry.user_management_service.payloads.responses.UserBasicDataResponse;
import com.fawry.user_management_service.payloads.responses.UserProfile;
import com.fawry.user_management_service.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Slf4j(topic = "UserController")
@RestController
@RequestMapping("v1/user/")
public class UserController {

    private final UserService userService;

    @PostMapping("suggestions")
    public Page<SuggestionFriend> getSuggestions(
            @RequestBody PaginationRequest paginationRequest,
            HttpServletRequest request
    ) throws Exception {
        String userId = (String) request.getAttribute("id");
        return this.userService.getSuggestions(paginationRequest, userId);
    }

    @GetMapping("id")
    public UserProfile getUserProfile(
            HttpServletRequest request,
            @RequestParam(value = "id", required = false) String profileId
    ) throws Exception {
        String userId = (String) request.getAttribute("id");
        return this.userService.getUserProfile(profileId, userId);
    }
}
