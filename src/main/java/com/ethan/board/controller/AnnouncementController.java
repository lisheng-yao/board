package com.ethan.board.controller;


import com.ethan.board.repository.PublisherRepository;
import com.ethan.board.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/announcements")
public class AnnouncementController {

    @Autowired
    private PublisherRepository publisherRepo;

    @Autowired
    private AnnouncementService announcementService;

    // 顯示列表頁（list.html）
    @GetMapping
    public String listPage() {
        return "announcement/list";
    }

    // 顯示新增公告表單頁（create.html）
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("publishers", publisherRepo.findAll());
        return "announcement/create";
    }

    // 顯示編輯公告頁（edit.html）
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("id", id); // 給前端抓 id
        return "announcement/edit";
    }
}
