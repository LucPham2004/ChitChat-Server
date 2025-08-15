package com.chitchat.server.repository;

import com.chitchat.server.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByAuthority(String authority);
    Optional<Role> findById(String id);
    
    @Query("""
            SELECT COUNT(ro) FROM Role ro
            """)
    int countAll();
}
