package com.fawry.user_management_service.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GOVERNORATE", schema = "BASIC_DATA")
public class GovernorateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 255)
    private String id;

    @Column(name = "NAME", nullable = false, unique = true, length = 255)
    private String name;
}