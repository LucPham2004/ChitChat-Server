package com.chitchat.server.repository;

import com.chitchat.server.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Override
    Page<Tag> findAll(Pageable pageable);

    @Query("""
            SELECT t from Tag t
            ORDER BY COUNT(t.message) DESC
            """)
    Page<Tag> findAllOrderByMessageDesc(Pageable pageable);
    
    @Query("""
        SELECT COUNT(t) FROM Tag t
        """)
    int countAll();
}
