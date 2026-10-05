package com.tokenqueue.token_queue_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "offices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 100)
    private String district;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}