package com.chitchat.server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
public class MessageReceiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    private String receiverId;

    public MessageReceiver(Message message, String receiverId) {
        this.message = message;
        this.receiverId = receiverId;
    }
}
