package com.chitchat.server.repository;

import com.chitchat.server.entity.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {
    MessageReaction findByUserIdAndMessageId(Long userId, Long messageId);

    List<MessageReaction> findByMessageId(Long messageId);

    int countByMessageId(Long messageId);

    int countByUserId(Long id);
    
    @Query("""
            SELECT COUNT(mr) FROM MessageReaction mr
            """)
    int countAll();
}
