package com.fawry.post_management_service.models;

import com.fawry.post_management_service.payloads.requests.CreateCommentRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "COMMENT", schema = "POST_MANAGEMENT")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 255)
    private String id;

    @Column(name = "USER_ID", nullable = false, length = 255)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", nullable = false)
    private PostEntity post;

    @Column(name = "HAS_MEDIA", nullable = false)
    private Boolean hasMedia;

    @Column(name = "CONTENT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMMENT_ID")
    private CommentEntity parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentEntity> replies = new HashSet<>();

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "UPDATED_AT")
    private Instant updatedAt;

    public CommentEntity(CreateCommentRequest createCommentRequest, String userId) {
        this.userId = userId;
        this.post = PostEntity.builder().id(createCommentRequest.postId()).build();
        this.hasMedia = false;
        //this.hasMedia = createCommentRequest.hasMedia();
        this.content = createCommentRequest.content();
        this.parentComment = null;
        //this.parentComment = createCommentRequest.parentCommentId() != null ?
        //    CommentEntity.builder().id(createCommentRequest.parentCommentId()).build() : null;
    }
}