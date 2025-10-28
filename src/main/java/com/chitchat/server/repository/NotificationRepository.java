package com.chitchat.server.repository;

import com.chitchat.server.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NotificationRepository extends PagingAndSortingRepository<Notification, Long> {
    Notification save(Notification notification);

    void delete(Notification notification);

    void deleteById(Long id);
    
    Notification findById(Long id);

    boolean existsById(Long id);

    Page<Notification> findByUserId(String userId, Pageable pageable);
    
    @Query("""
            SELECT COUNT(n) FROM Notification n
            """)
    int countAll();
}
