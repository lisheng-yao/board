package com.ethan.board.service;

import com.ethan.board.model.Announcement;
import com.ethan.board.model.AnnouncementAttachment;
import com.ethan.board.model.Publisher;
import com.ethan.board.repository.AnnouncementAttachmentRepository;
import com.ethan.board.repository.AnnouncementRepository;
import com.ethan.board.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepo;

    @Autowired
    private AnnouncementAttachmentRepository attachmentRepo;

    @Autowired
    private PublisherRepository publisherRepo;

    @Value("${file.upload.dir}")
    private String uploadBaseDir;

    @Override
    public Page<Announcement> getAllPaged(Pageable pageable) {
        return announcementRepo.findAll(pageable);
    }

    @Override
    public List<Announcement> getAll() {
        return announcementRepo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public Optional<Announcement> getById(Integer id) {
        return announcementRepo.findById(id);
    }

    @Override
    public Announcement save(Announcement announcement) {
        return announcementRepo.save(announcement);
    }

    @Override
    public void delete(Integer id) {
        announcementRepo.deleteById(id);
    }

    @Override
    public void saveOrUpdateWithFiles(Integer id, String title, LocalDate startDate, LocalDate endDate,
                                      String content, Integer publisherId, MultipartFile[] files) {

        Publisher publisher = publisherRepo.findById(publisherId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 無效的 publisher ID: " + publisherId));

        Announcement announcement;

        if (id != null) {
            announcement = announcementRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("❌ 公告不存在，ID: " + id));
            System.out.println("🔄 編輯公告 ID=" + id);
            announcement.setTitle(title);
            announcement.setStartDate(startDate);
            announcement.setEndDate(endDate);
            announcement.setContent(content);
            announcement.setUpdatedAt(LocalDateTime.now());
        } else {
            announcement = new Announcement();
            System.out.println("🆕 建立新公告");
            announcement.setTitle(title);
            announcement.setStartDate(startDate);
            announcement.setEndDate(endDate);
            announcement.setContent(content);
            announcement.setPublisher(publisher);
            announcement.setCreatedAt(LocalDateTime.now());
            announcement.setUpdatedAt(LocalDateTime.now());
        }

        announcement.setPublisher(publisher);
        announcement = announcementRepo.save(announcement);
        System.out.println("📌 儲存公告成功，ID=" + announcement.getId());

        // 若沒有檔案可上傳則略過
        if (files == null || files.length == 0) {
            System.out.println("📭 沒有附件，略過上傳");
            return;
        }

        try {
            // ✅ 跨平台安全路徑建立
            Path uploadPath = Paths.get(uploadBaseDir, announcement.getId().toString());
            Files.createDirectories(uploadPath);
            System.out.println("📁 上傳路徑：" + uploadPath.toAbsolutePath());

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    System.out.println("⚠️ 空檔案略過");
                    continue;
                }

                String fileName = file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName); // 自動處理路徑分隔符

                try (InputStream in = file.getInputStream()) {
                    Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("✅ 檔案儲存完成：" + filePath);
                } catch (IOException e) {
                    System.out.println("❌ 檔案儲存失敗：" + e.getMessage());
                    e.printStackTrace();
                }

                AnnouncementAttachment attachment = new AnnouncementAttachment();
                attachment.setAnnouncement(announcement);
                attachment.setFileName(fileName);
                attachment.setFilePath(filePath.toString()); // 可存相對或絕對路徑
                attachment.setUploadTime(LocalDateTime.now());

                attachmentRepo.save(attachment);
                System.out.println("📦 附件已儲存進資料庫: " + fileName);
            }
        } catch (IOException e) {
            System.out.println("❌ 檔案儲存失敗：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAttachmentById(Integer attachmentId) {
        AnnouncementAttachment attachment = attachmentRepo.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("附件不存在，ID: " + attachmentId));

        // 刪除實體檔案
        try {
            Path path = Paths.get(attachment.getFilePath());
            Files.deleteIfExists(path);
            System.out.println("🗑️ 附件檔案已刪除：" + path);
        } catch (IOException e) {
            System.out.println("⚠️ 刪除檔案失敗：" + e.getMessage());
        }

        // 刪除資料庫紀錄
        attachmentRepo.deleteById(attachmentId);
        System.out.println("🧹 附件資料已刪除，ID=" + attachmentId);
    }


}
