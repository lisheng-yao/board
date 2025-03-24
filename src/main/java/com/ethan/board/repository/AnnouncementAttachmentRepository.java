package com.ethan.board.repository;

import com.ethan.board.model.AnnouncementAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementAttachmentRepository extends JpaRepository<AnnouncementAttachment, Integer> {
//    List<AnnouncementAttachment> findByAnnouncementId(Integer announcementId);
}
