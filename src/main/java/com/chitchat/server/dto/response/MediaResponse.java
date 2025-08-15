package com.chitchat.server.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class MediaResponse {
    String publicId;
    String url;
    String fileName;
    Long height;
    Long width;
    String resourceType;

    Long messageId;
    Long conversationId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
