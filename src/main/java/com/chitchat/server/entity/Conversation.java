package com.chitchat.server.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "conversations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String color;

    @Column(name = "avatar_public_id")
    private String avatarPublicId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private String emoji;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "conversation_participants", 
                     joinColumns = @JoinColumn(name = "conversation_id"))
    private Set<Long> participantIds;

    private Long ownerId;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "conversation_messages")
    private Set<Message> messages;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "conversation_medias")
    private Set<Media> medias;

    // Flags

    private boolean isGroup;

    private boolean isRead;

    private boolean isMuted;

    private boolean isPinned;

    private boolean isArchived;

    private boolean isDeleted;

    private boolean isBlocked;

    private boolean isReported;

    private boolean isSpam;

    private boolean isMarkedAsUnread;

    private boolean isMarkedAsRead;
    
    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
