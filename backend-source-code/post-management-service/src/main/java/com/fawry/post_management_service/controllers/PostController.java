package com.fawry.post_management_service.controllers;

import com.fawry.post_management_service.payloads.requests.CreatePostRequest;
import com.fawry.post_management_service.payloads.requests.PaginationRequest;
import com.fawry.post_management_service.payloads.requests.UpdateMediaEntityRequest;
import com.fawry.post_management_service.payloads.responses.CreateMediaEntityResponse;
import com.fawry.post_management_service.payloads.responses.PostPagination;
import com.fawry.post_management_service.services.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/post")
@Slf4j(topic = "PostController")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public CreateMediaEntityResponse createPost(
            @RequestBody CreatePostRequest createPostRequest,
            HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("id");
        return this.postService.create(createPostRequest, userId);
    }

    @PostMapping("update")
    public CreateMediaEntityResponse updatePost(
            HttpServletRequest request,
            @RequestBody UpdateMediaEntityRequest updateMediaEntityRequest
    ) {
        String userId = (String) request.getAttribute("id");
        return this.postService.update(updateMediaEntityRequest, userId);
    }

    @DeleteMapping("{id}")
    public void delete(
            HttpServletRequest request,
            @PathVariable("id") String id
    ) {
        String userId = (String) request.getAttribute("id");
        this.postService.delete(id, userId);
    }

    @PostMapping("pagination")
    public Page<PostPagination> getFeed(
            HttpServletRequest request,
            @RequestBody PaginationRequest paginationRequest
    ) {
        String userId = (String) request.getAttribute("id");
        return this.postService.getFeed(userId, paginationRequest);
    }
}
