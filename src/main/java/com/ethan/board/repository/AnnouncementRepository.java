package com.ethan.board.repository;

import com.ethan.board.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    // 例如找出有效期間內的公告
//    List<Announcement> findByStartDateBeforeAndEndDateAfter(java.time.LocalDate today1, java.time.LocalDate today2);
}

