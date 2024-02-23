package com.example.HRfinalproject.repository;

import com.example.HRfinalproject.entity.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {

    List<Timesheet> findByUserId(Long userId);

    @Query(nativeQuery = true,value = "select * from timesheet as t where t.user_id =:user_id")
    List<Timesheet> findTimeSheetsByUserId(@Param("user_id") Long userId);
}
