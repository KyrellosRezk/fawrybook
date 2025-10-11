package com.fawry.post_management_service.controllers;

import com.fawry.post_management_service.payloads.requests.CreateCommentRequest;
import com.fawry.post_management_service.payloads.requests.UpdateMediaEntityRequest;
import com.fawry.post_management_service.payloads.responses.CreateMediaEntityResponse;
import com.fawry.post_management_service.services.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j(topic = "CommentController")
@AllArgsConstructor
@RequestMapping("v1/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CreateMediaEntityResponse createComment(
            HttpServletRequest request,
            @RequestBody CreateCommentRequest createCommentRequest
    ) {
        String userId = (String) request.getAttribute("id");
        return this.commentService.create(createCommentRequest, userId);
    }

    @PostMapping("update")
    public CreateMediaEntityResponse updateComment(
            HttpServletRequest request,
            UpdateMediaEntityRequest updateMediaEntityRequest
    ) {
        String userId = (String) request.getAttribute("id");
        return this.commentService.update(updateMediaEntityRequest, userId);
    }

    @DeleteMapping("{id}")
    public void deleteComment(
            HttpServletRequest request,
            @PathVariable("id") String id
    ) {
        String userId = (String) request.getAttribute("id");
        this.commentService.delete(id, userId);
    }
}
