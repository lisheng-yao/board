# 公告事項管理系統 Bulletin Board

這是一個以 Spring Boot 為後端、Bootstrap 5 為前端的公告管理系統。支援公告新增、編輯、刪除、附件上傳與下載等功能，並具備分頁功能與 JSON API。

於CentOS 部屬網站：  https://top-yeti-gladly.ngrok-free.app/  

(如有連線問題請來電來信告知，謝謝)

## 📌 1.系統功能
- 公告新增、修改、刪除

- 公告列表支援分頁

- 支援多附件上傳、刪除、下載

- 支援 CKEditor 編輯公告內容

- 使用者介面採 Bootstrap 響應式設計

- API 接口支援 JSON 串接

---

## 📌 2.使用技術

| 類別       | 技術                        |
|------------|-----------------------------|
| 語言       | Java 17                     |
| 後端框架   | Spring Boot 3.4.4 (MVC)     |
| ORM        | Hibernate + Spring Data JPA |
| 資料庫     | MySQL                       |
| 前端       | Bootstrap 5 + HTML5         |
| 富文本編輯 | CKEditor 4.22.1             |

---

## 📌 3.專案結構說明：
controller/：負責路由、接收前端請求並調用 service。

model/：包含公告、發佈者、附件的 Entity 與 DTO。

repository/：JPA 介面，用於與資料庫溝通。

service/：包含資料儲存、查詢、附件處理等商業邏輯。

static/：HTML + JS 前端畫面，靜態放置於 Spring Boot 預設目錄。

uploads/：使用者上傳的檔案將以 uploads/{announcementId}/ 儲存。

## 📌 4.相關 API
| 方法     | 路徑                                      | 說明                     | 參數說明                             |
|----------|-------------------------------------------|--------------------------|-------------------------------------|
| GET      | /announcements/api/page                   | 取得公告分頁               | `page`、`size`（Query 參數）        |
| GET      | /announcements/api/all                    | 取得所有公告               | 無                                  |
| GET      | /announcements/api/{id}                   | 取得完整公告資料            | `id`：公告 ID（Path 參數）           |
| GET      | /announcements/api/simple/{id}            | 取得簡化公告資料（DTO）     | `id`：公告 ID（Path 參數）           |
| GET      | /announcements/download/{attachmentId}    | 下載附件                   | `attachmentId`：附件 ID（path 參數） |
| DELETE   | /announcements/delete/{id}                | 刪除附件                   | `id`：附件 ID（path 參數）           |
| POST     | /announcements/upload                     | 上傳公告及附件（新增或修改） | 使用 `multipart/form-data` 傳送：<br>- `id`（選填）<br>- `title`<br>- `startDate`<br>- `endDate`<br>- `content`<br>- `publisherId`<br>- `files`（可選，多檔） |



## 📌 5. 資料庫結構
系統使用 MySQL，資料庫名稱：bulletin_board，共包含 3 張主要資料表：

### 5-1 publisher（公告發佈者）
| 欄位名稱     | 資料型別        | 說明                  |
|--------------|------------------|-----------------------|
| id           | INT (PK, AI)     | 主鍵，自動遞增        |
| name         | VARCHAR(100)     | 發佈者名稱            |
| email        | VARCHAR(100)     | 電子郵件              |
| role         | VARCHAR(50)      | 使用者角色（如 admin）|
| created_at   | TIMESTAMP        | 建立時間，預設當下時間 |

### 5-2 announcement（公告事項）
| 欄位名稱       | 資料型別        | 說明                            |
|----------------|------------------|---------------------------------|
| id             | INT (PK, AI)     | 主鍵，自動遞增                  |
| title          | VARCHAR(255)     | 公告標題                        |
| publisher_id   | INT (FK)         | 發佈者 ID，關聯 publisher.id     |
| start_date     | DATE             | 公告開始日期                    |
| end_date       | DATE             | 公告截止日期                    |
| content        | TEXT             | 公告內容（HTML 格式）           |
| created_at     | TIMESTAMP        | 建立時間                        |
| updated_at     | TIMESTAMP        | 更新時間，自動更新              |

🔗 一對多：一位發佈者可擁有多筆公告。

### 5-3 announcement_attachment（公告附件）
| 欄位名稱          | 資料型別        | 說明                                 |
|-------------------|------------------|--------------------------------------|
| id                | INT (PK, AI)     | 主鍵，自動遞增                       |
| announcement_id   | INT (FK)         | 所屬公告 ID，關聯 announcement.id    |
| file_name         | VARCHAR(255)     | 上傳時的原始檔案名稱                |
| file_path         | VARCHAR(500)     | 檔案儲存路徑                         |
| upload_time       | TIMESTAMP        | 上傳時間                             |

🔗 一對多：一則公告可上傳多個附件。

## 📌 6.MySQL 資料表建置

```sql
CREATE DATABASE bulletin_board DEFAULT CHARSET=utf8mb4;

USE bulletin_board;

CREATE TABLE publisher (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO publisher (name, email, role) VALUES ('Administrator', 'admin@example.com', 'admin');

CREATE TABLE announcement (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    publisher_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (publisher_id) REFERENCES publisher(id)
);

CREATE TABLE announcement_attachment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    announcement_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (announcement_id) REFERENCES announcement(id) ON DELETE CASCADE
);

-- 新增資料
INSERT INTO publisher (name, email, role) VALUES
('Alice', 'alice@example.com', 'editor'),
('Bob', 'bob@example.com', 'publisher'),
('Charlie', 'charlie@example.com', 'editor');

INSERT INTO announcement (title, publisher_id, start_date, end_date, content) VALUES
('系統維護公告', 1, '2025-03-25', '2025-03-27', '系統將於 3/25 晚間進行維護，期間暫停服務。'),
('新功能上線通知', 2, '2025-03-20', '2025-04-01', '我們已上線留言功能，歡迎大家使用！'),
('清明連假公告', 3, '2025-04-01', '2025-04-05', '清明連假期間客服暫停服務，造成不便敬請見諒。');
