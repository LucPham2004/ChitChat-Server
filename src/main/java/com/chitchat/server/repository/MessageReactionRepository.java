package com.chitchat.server.repository;

import com.chitchat.server.entity.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {
    MessageReaction findByUserIdAndMessageId(String userId, String messageId);

    List<MessageReaction> findByMessageId(String messageId);

    int countByMessageId(String messageId);

    int countByUserId(String id);
    
    @Query("""
            SELECT COUNT(mr) FROM MessageReaction mr
            """)
    int countAll();
}
