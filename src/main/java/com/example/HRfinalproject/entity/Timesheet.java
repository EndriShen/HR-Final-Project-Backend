package com.example.HRfinalproject.entity;

import com.example.HRfinalproject.enums.StatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "timesheet")
public class Timesheet extends BaseEntity{

    @Column(name = "fromDate", nullable = false)
    private LocalDate fromDate;

    @Column(name = "toDate", nullable = false)
    private LocalDate toDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusType status;

    @Column(name = "note")
    private String note;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable=false)
    private User user;

}
