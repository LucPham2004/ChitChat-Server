package com.chitchat.server.repository;

import com.chitchat.server.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MessageRepository extends PagingAndSortingRepository<Message, String> {
    Message save(Message Message);

    void delete(Message Message);

    Optional<Message> findById(String id);

    boolean existsById(String id);

    Page<Message> findByConversationId(String conversationId, Pageable pageable);

    Page<Message> findBySenderId(String senderId, Pageable pageable);

    // Search messages by keyword in a conversation
    @Query(value = "SELECT * FROM messages WHERE conversation_id = :conversationId AND LOWER(content) LIKE LOWER(CONCAT('%', :contentKeyword, '%'))", 
        nativeQuery = true)
    Page<Message> findByConversationIdAndContentContainingIgnoreCase(@Param("conversationId") String conversationId, @Param("contentKeyword") String contentKeyword, Pageable pageable);

    int countBySenderId(String senderId);

    @Query("""
            SELECT COUNT(m) FROM Message m
            """)
    int countAll();
}
