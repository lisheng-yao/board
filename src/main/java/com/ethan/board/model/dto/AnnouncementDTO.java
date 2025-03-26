package com.ethan.board.model.dto;

import java.time.LocalDate;

public class AnnouncementDTO {

    private Integer id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String content;
    private Integer publisherId;
    private String publisherName;

    // 建構子
    public AnnouncementDTO(Integer id, String title, LocalDate startDate, LocalDate endDate,
                           String content, Integer publisherId, String publisherName) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.content = content;
        this.publisherId = publisherId;
        this.publisherName = publisherName;
    }


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getPublisherId() { return publisherId; }
    public void setPublisherId(Integer publisherId) { this.publisherId = publisherId; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }
}
