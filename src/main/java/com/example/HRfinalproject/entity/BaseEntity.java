package com.example.HRfinalproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "createdAt", nullable = false)
    private LocalDate createdAt;

    @Column(name ="createdBy", length = 45, nullable = false)
    private String createdBy;

    @Column(name = "modifiedAt", nullable = false)
    private LocalDate modifiedAt;

    @Column(name = "modifiedBy", length = 45, nullable = false)
    private String modifiedBy;

}
