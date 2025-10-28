package com.chitchat.server.service;

import com.chitchat.server.entity.Media;
import org.springframework.data.domain.Page;

public interface MediaService {
    Media createMedia(String publicId, String url, String messageId);

    Media getById(String publicId);

    Page<Media> getMediaByMessageId(String messageId, int pageNum);

    Page<Media> getMediasAndFilesByConversationId(String conversationId, int pageNum);

    Page<Media> getMediasByConversationId(String conversationId, int pageNum);

    Page<Media> getRawFilesByConversationId(String conversationId, int pageNum);
}
