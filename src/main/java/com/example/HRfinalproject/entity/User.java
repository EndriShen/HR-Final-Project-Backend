package com.example.HRfinalproject.entity;

import com.example.HRfinalproject.enums.UserRoles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "firstName", length = 45, nullable = false)
    private String firstName;

    @Column(name = "lastName", length = 45, nullable = false)
    private String lastName;

    @Column(name = "username", length = 45, nullable = false)
    private String username;

    @Column(name = "password", length = 45, nullable = false)
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRoles role = UserRoles.USER;

    @Column(name = "daysOff", nullable = false)
    @Min(value = 0)
    private int daysOff;

    @Column(name = "createdAt")
    private LocalDate createdAt;

    @Column(name = "createdBy", length = 45)
    private String createdBy;

    @Column(name = "modifiedAt")
    private LocalDate modifiedAt;

    @Column(name = "modifiedBy", length = 45)
    private String modifiedBy;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Timesheet> timesheets;

    public User(String firstName, String lastName, String username, String password, UserRoles role, int daysOff, LocalDate createdAt, String createdBy, LocalDate modifiedAt, String modifiedBy) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.daysOff = daysOff;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
    }
}
