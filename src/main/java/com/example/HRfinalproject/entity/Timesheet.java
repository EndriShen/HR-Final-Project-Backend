package com.example.HRfinalproject.entity;

import com.example.HRfinalproject.enums.StatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "timesheet")
public class Timesheet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "fromDate", nullable = false)
    private LocalDate fromDate;

    @Column(name = "toDate", nullable = false)
    private LocalDate toDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusType status;

    @Column(name = "note")
    private String note;

    @Column(name = "createdAt", nullable = false)
    private LocalDate createdAt;

    @Column(name ="createdBy", length = 45, nullable = false)
    private String createdBy;

    @Column(name = "modifiedAt", nullable = false)
    private LocalDate modifiedAt;

    @Column(name = "modifiedBy", length = 45, nullable = false)
    private String modifiedBy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable=false)
    private User user;

    public Timesheet(LocalDate fromDate, LocalDate toDate, StatusType status, String note, User user, LocalDate createdAt, String createdBy, LocalDate modifiedAt, String modifiedBy){
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.status = status;
        this.note = note;
        this.user = user;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
    }
}
