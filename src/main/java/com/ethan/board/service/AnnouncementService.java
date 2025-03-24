package com.ethan.board.service;


import com.ethan.board.model.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnnouncementService {
    Page<Announcement> getAllPaged(Pageable pageable);
    List<Announcement> getAll();
    Optional<Announcement> getById(Integer id);
    Announcement save(Announcement announcement);
    void delete(Integer id);
    void saveOrUpdateWithFiles(Integer id, String title, LocalDate startDate, LocalDate endDate,
                                      String content, Integer publisherId, MultipartFile[] files);
    void deleteAttachmentById(Integer attachmentId);
}
