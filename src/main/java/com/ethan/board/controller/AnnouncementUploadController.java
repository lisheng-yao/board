package com.ethan.board.controller;

import com.ethan.board.model.AnnouncementAttachment;
import com.ethan.board.repository.AnnouncementAttachmentRepository;
import com.ethan.board.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/announcements")
public class AnnouncementUploadController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementAttachmentRepository attachmentRepo;

    @Value("${file.upload.dir}")
    private String uploadBaseDir;

    // === 上傳與儲存公告 + 附件（新增 / 編輯） ===
    @PostMapping("/upload")
    public ResponseEntity<String> createOrUpdateWithAttachment(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam("title") String title,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("content") String content,
            @RequestParam("publisherId") Integer publisherId,
            @RequestParam(name = "files", required = false) MultipartFile[] files
    ) {
        if (files == null) files = new MultipartFile[0];
        announcementService.saveOrUpdateWithFiles(id, title, startDate, endDate, content, publisherId, files);
        return ResponseEntity.ok("success");
    }

    // === 附件下載 ===
    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer attachmentId) {
        AnnouncementAttachment attachment = attachmentRepo.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("找不到附件 ID=" + attachmentId));

        try {
            Path path = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("找不到實體檔案：" + attachment.getFilePath());
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException("檔案讀取失敗：" + e.getMessage(), e);
        }
    }

    // === 附件刪除 ===
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAttachment(@PathVariable Integer id) {
        announcementService.deleteAttachmentById(id);
        return ResponseEntity.ok("deleted");
    }
}


