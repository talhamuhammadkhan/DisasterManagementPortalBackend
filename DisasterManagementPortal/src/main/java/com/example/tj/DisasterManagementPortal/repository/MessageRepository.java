package com.example.tj.DisasterManagementPortal.repository;

import com.example.tj.DisasterManagementPortal.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByApplicationIdOrderByTimestampAsc(Long applicationId);

    long countByApplicationIdAndUnreadTrue(Long applicationId);
}
