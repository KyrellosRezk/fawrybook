package com.fawry.user_management_service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "USER_CREDENTIAL", schema = "AUTH")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 255)
    private String id;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "REFRESH_TOKEN", length = 1000)
    private String refreshToken;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "UPDATED_AT")
    private Instant updatedAt;

    public UserCredentialEntity(UserEntity userEntity, String hashedPassword) {
        this.user = userEntity;
        this.password = hashedPassword;
    }
}