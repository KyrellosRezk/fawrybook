package com.fawry.post_management_service.models;

import com.fawry.post_management_service.enums.ReactTypeEnum;
import com.fawry.post_management_service.payloads.requests.CreateReactRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "REACT", schema = "POST_MANAGEMENT")
public class ReactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 255)
    private String id;

    @Column(name = "USER_ID", nullable = false, length = 255)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", nullable = false)
    private PostEntity post;

    @Column(name = "TYPE", nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private ReactTypeEnum type;

    public ReactEntity(CreateReactRequest createReactRequest, String userId) {
        this.post = PostEntity.builder().id(createReactRequest.postId()).build();
        this.userId = userId;
        this.type = createReactRequest.type();
    }
}