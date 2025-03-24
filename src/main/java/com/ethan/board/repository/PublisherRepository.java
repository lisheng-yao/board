package com.ethan.board.repository;

import com.ethan.board.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
    // 你也可以加上自訂方法，例如：
//    Publisher findByEmail(String email);
}
