package com.example.HRfinalproject.repository;

import com.example.HRfinalproject.entity.Timesheet;
import com.example.HRfinalproject.entity.User;
import com.example.HRfinalproject.enums.StatusType;
import com.example.HRfinalproject.enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    List<Timesheet> findByUser(User user);

    List<Timesheet> findByUserId(Long userId);

//    @Query("SELECT t FROM timesheet t " +
//            "WHERE t.user.id = :userId " +
//            "AND ((:fromDate BETWEEN t.fromDate AND t.toDate) " +
//            "OR (:toDate BETWEEN t.fromDate AND t.toDate))")
//    List<Timesheet> findOverlappingTimesheets(@Param("userId") Long userId,
//                                              @Param("fromDate") LocalDate fromDate,
//                                              @Param("toDate") LocalDate toDate);

    @Query(nativeQuery = true,value = "select * from timesheet as t where t.user_id =:user_id")
    List<Timesheet> findTimeSheetsByUserId(@Param("user_id") Long userId);
}
