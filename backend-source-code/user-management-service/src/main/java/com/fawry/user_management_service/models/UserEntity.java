package com.fawry.user_management_service.models;

import com.fawry.user_management_service.enums.UserRoleEnum;
import com.fawry.user_management_service.enums.UserStatusEnum;
import com.fawry.user_management_service.payloads.requests.CreateUserRequest;
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
@Table(name = "USER", schema = "USER_MANAGEMENT")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 255)
    private String id;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 255)
    private String username;

    @Column(name = "FIRST_NAME", nullable = false, length = 255)
    private String firstName;

    @Column(name = "MIDDLE_NAME", nullable = false, length = 255)
    private String middleName;

    @Column(name = "LAST_NAME", nullable = false, length = 255)
    private String lastName;

    @Column(name = "PHONE_NUMBER", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "LNG", nullable = true)
    private Double lng;

    @Column(name = "LAT", nullable = true)
    private Double lat;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserCredentialEntity userCredential;

    @ManyToOne(fetch =  FetchType.EAGER)
    @JoinColumn(name = "GOVERNORATE_ID", nullable = false)
    private GovernorateEntity governorate;

    @Column(name = "ROLE", nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    @ManyToMany
    @JoinTable(
            name = "FOLLOWING",
            schema = "USER",
            joinColumns = @JoinColumn(name = "FOLLOWER_ID"),
            inverseJoinColumns = @JoinColumn(name = "FOLLOWED_ID")
    )
    private Set<UserEntity> following = new HashSet<>();

    @ManyToMany(mappedBy = "following")
    private Set<UserEntity> followers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "FOLLOWING_REQUEST",
            schema = "USER",
            joinColumns = @JoinColumn(name = "SENDER_ID"),
            inverseJoinColumns = @JoinColumn(name = "RECEIVER_ID")
    )
    private Set<UserEntity> sentFollowRequests = new HashSet<>();

    @ManyToMany(mappedBy = "sentFollowRequests")
    private Set<UserEntity> receivedFollowRequests = new HashSet<>();

    @Column(name = "STATUS", nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private UserStatusEnum status = UserStatusEnum.UNVERIFIED;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POSITION_ID", nullable = false)
    private PositionEntity position;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "UPDATED_AT")
    private Instant updatedAt;

    @Column(name = "DELETED_AT")
    private Instant deletedAt;

    public UserEntity(CreateUserRequest createUserRequest) {
        this.username = createUserRequest.username();
        this.firstName = createUserRequest.firstName();
        this.middleName = createUserRequest.middleName();
        this.lastName = createUserRequest.lastName();
        this.phoneNumber = createUserRequest.phoneNumber();
        this.email = createUserRequest.email();
        this.lng = createUserRequest.lng();
        this.lat = createUserRequest.lat();
        this.role = createUserRequest.role();
        this.governorate = GovernorateEntity.builder().id(createUserRequest.governorateId()).build();
        this.position = PositionEntity.builder().id(createUserRequest.positionId()).build();
    }
}