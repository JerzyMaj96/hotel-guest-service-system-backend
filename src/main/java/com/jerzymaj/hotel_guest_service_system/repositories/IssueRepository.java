package com.jerzymaj.hotel_guest_service_system.repositories;

import com.jerzymaj.hotel_guest_service_system.models.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query("""
            SELECT i
            FROM Issue i
            WHERE i.user.email = :userEmail
            ORDER BY i.creationDate DESC
            """)
    List<Issue> findAllByUserEmailSortedByDate(@Param("userEmail") String userEmail);
}
