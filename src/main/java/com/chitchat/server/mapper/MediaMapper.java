package com.chitchat.server.mapper;

import com.chitchat.server.dto.response.MediaResponse;
import com.chitchat.server.entity.Media;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal = true)
public class MediaMapper {
    

    public MediaResponse toResponse(Media media) {
        MediaResponse response = new MediaResponse();

        response.setPublicId(media.getPublicId());
        response.setUrl(media.getUrl());
        response.setFileName(media.getFileName());
        response.setHeight(media.getHeight());
        response.setWidth(media.getWidth());
        response.setResourceType(media.getResourceType());
        response.setConversationId(media.getConversation().getId());
        response.setMessageId(media.getMessage().getId());
        response.setCreatedAt(media.getCreatedAt());
        response.setUpdatedAt(media.getUpdatedAt());

        return response;
    }
}
