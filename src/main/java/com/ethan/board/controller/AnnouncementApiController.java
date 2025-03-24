package com.ethan.board.controller;

import com.ethan.board.model.Announcement;
import com.ethan.board.model.AnnouncementAttachment;
import com.ethan.board.model.dto.AnnouncementDTO;
import com.ethan.board.repository.AnnouncementRepository;
import com.ethan.board.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/announcements/api")
public class AnnouncementApiController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementRepository announcementRepo;

    // 分頁公告
//    @GetMapping("/page")
//    public Page<Announcement> getPaged(@RequestParam(defaultValue = "0") int page,
//                                       @RequestParam(defaultValue = "5") int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
//        return announcementService.getAllPaged(pageable);
//    }


    @GetMapping("/page")
    @ResponseBody
    public Page<AnnouncementDTO> getPagedAnnouncements(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        Page<Announcement> announcementPage = announcementService.getAllPaged(pageable);

        return announcementPage.map(a -> new AnnouncementDTO(
                a.getId(),
                a.getTitle(),
                a.getStartDate(),
                a.getEndDate(),
                null, // 不回傳 content
                a.getPublisher().getId(),
                a.getPublisher().getName()
        ));
    }


    // 取得全部公告
    @GetMapping("/all")
    public List<Announcement> getAll() {
        return announcementService.getAll();
    }

    // 取得單筆公告（原始資料）
    @GetMapping("/{id}")
    public Announcement getOne(@PathVariable Integer id) {
        return announcementService.getById(id).orElse(null);
    }

    // DTO：簡易公告資料（給 edit.html 用）
    @GetMapping("/simple/{id}")
    public ResponseEntity<Map<String, Object>> getSimple(@PathVariable Integer id) {
        Announcement a = announcementRepo.findById(id).orElse(null);
        if (a == null) return ResponseEntity.notFound().build();

        Map<String, Object> dto = new HashMap<>();
        dto.put("id", a.getId());
        dto.put("title", a.getTitle());
        dto.put("startDate", a.getStartDate());
        dto.put("endDate", a.getEndDate());
        dto.put("content", a.getContent());
        dto.put("publisherId", a.getPublisher().getId());
        dto.put("publisherName", a.getPublisher().getName());

        // 加上附件資料
        List<Map<String, Object>> attachments = new ArrayList<>();
        for (AnnouncementAttachment att : a.getAttachments()) {
            Map<String, Object> file = new HashMap<>();
            file.put("id", att.getId());
            file.put("fileName", att.getFileName());
            attachments.add(file);
        }
        dto.put("attachments", attachments);

        return ResponseEntity.ok(dto);
    }

    // 建立或修改公告（JSON）
    @PostMapping
    public ResponseEntity<String> save(@RequestBody Announcement announcement) {
        announcementService.save(announcement);
        return ResponseEntity.ok("success");
    }

    // 刪除
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer id) {
        announcementService.delete(id);
    }


}
