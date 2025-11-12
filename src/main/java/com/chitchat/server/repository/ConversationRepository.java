package com.chitchat.server.repository;

import com.chitchat.server.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ConversationRepository extends PagingAndSortingRepository<Conversation, String> {
    Conversation save(Conversation conversation);

    void delete(Conversation conversation);

    void deleteById(String id);

    Optional<Conversation> findById(String id);

    boolean existsById(String id);

    @Query("""
            SELECT c FROM Conversation c
            WHERE :userAId MEMBER OF c.participantIds
            AND :userBId MEMBER OF c.participantIds
            AND c.isGroup = false
            """)
    Optional<Conversation> findDirectMessage(@Param("userAId") String userAId, @Param("userBId") String userBId);
    
    @Query("""
            SELECT c.id FROM Conversation c
            WHERE :userAId MEMBER OF c.participantIds
            AND :userBId MEMBER OF c.participantIds
            AND c.isGroup = false
            """)
    String findDirectMessageId(@Param("userAId") String userAId, @Param("userBId") String userBId);

    @Query("""
            SELECT c FROM Conversation c WHERE :userId MEMBER OF c.participantIds
            AND c.messages IS NOT EMPTY
            ORDER BY (SELECT MAX(m.createdAt) FROM Message m WHERE m.conversation = c) DESC
            """)
    Page<Conversation> findByParticipantIdsContaining(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT c FROM Conversation c WHERE :userId MEMBER OF c.participantIds AND EXISTS (SELECT id FROM c.participantIds pid WHERE pid IN :targetUserIds)")
    Page<Conversation> findByParticipantIdsContainingAndParticipantIn(@Param("userId") String userId, @Param("targetUserIds") List<String> targetUserIds, Pageable pageable);

    // Search conversations
    @Query("SELECT c FROM Conversation c WHERE :userId MEMBER OF c.participantIds AND c.name IS NOT NULL AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Conversation> findByParticipantIdsContainingAndNameContainingIgnoreCase(@Param("userId") String userId, String keyword, Pageable pageable);
    
    Page<Conversation> findByOwnerId(String userId, Pageable pageable);

    int countByOwnerId(String userId);
}
