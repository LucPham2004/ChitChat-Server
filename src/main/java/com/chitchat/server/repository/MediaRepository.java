package com.chitchat.server.repository;

import com.chitchat.server.entity.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepository extends PagingAndSortingRepository<Media, String> {
    Media save(Media media);

    void delete(Media media);

    Optional<Media> findById(String id);

    boolean existsById(String id);

    Page<Media> findByMessageId(String messageId, Pageable pageable);
    
    Page<Media> findByConversationId(String conversationId, Pageable pageable);

    Page<Media> findByConversationIdAndResourceType(String conversationId, String type, Pageable pageable);
    
    Page<Media> findByConversationIdAndResourceTypeNot(String conversationId, String type, Pageable pageable);

    int countByMessageId(String id);
}
