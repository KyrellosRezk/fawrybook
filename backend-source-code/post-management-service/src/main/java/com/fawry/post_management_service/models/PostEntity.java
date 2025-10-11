package com.fawry.post_management_service.models;

import com.fawry.post_management_service.payloads.requests.CreatePostRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "POST", schema = "POST_MANAGEMENT")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 255)
    private String id;

    @Column(name = "USER_ID", nullable = false, length = 255)
    private String userId;

    @Column(name = "HAS_MEDIA", nullable = false)
    private Boolean hasMedia;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "UPDATED_AT")
    private Instant updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReactEntity> reacts = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentEntity> comments = new HashSet<>();

    public PostEntity(CreatePostRequest createPostRequest, String userId) {
        this.content = createPostRequest.content();
        this.userId = userId;
        this.hasMedia = createPostRequest.hasMedia();
    }
}