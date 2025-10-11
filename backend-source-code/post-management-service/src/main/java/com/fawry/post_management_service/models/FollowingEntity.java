package com.fawry.post_management_service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "FOLLOWING", schema = "FOLLOW_MANAGEMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowingEntity {

    @Id
    @Column(name = "USER_ID", length = 255)
    private String userId;

    @Column(name = "FOLLOWED_USER_IDS", columnDefinition = "varchar(255)[]", nullable = false)
    private Set<String> followedUserIds;
}