package com.example.HRfinalproject.repository;

import com.example.HRfinalproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);

//    @Query(nativeQuery = true, value = "SELECT u FROM user u WHERE u.username = :username AND u.password = :password")
//    Optional<User> findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

}
