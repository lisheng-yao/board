package com.ethan.board.repository;

import com.ethan.board.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {

//    List<Announcement> findByStartDateBeforeAndEndDateAfter(java.time.LocalDate today1, java.time.LocalDate today2);
}

