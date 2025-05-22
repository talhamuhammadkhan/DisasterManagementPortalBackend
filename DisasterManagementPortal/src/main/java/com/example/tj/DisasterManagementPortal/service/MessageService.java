package com.example.tj.DisasterManagementPortal.service;

import com.example.tj.DisasterManagementPortal.model.Message;
import com.example.tj.DisasterManagementPortal.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getMessagesForApplication(Long applicationId) {
        return messageRepository.findByApplicationIdOrderByTimestampAsc(applicationId);
    }

    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    public long countUnreadMessagesForApplication(Long applicationId) {
        return messageRepository.countByApplicationIdAndUnreadTrue(applicationId);
    }

}
