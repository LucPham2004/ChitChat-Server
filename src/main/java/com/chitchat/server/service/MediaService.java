package com.chitchat.server.service;

import com.chitchat.server.entity.Media;
import org.springframework.data.domain.Page;

public interface MediaService {
    Media createMedia(String publicId, String url, Long messageId);

    Media getById(String publicId);

    Page<Media> getMediaByMessageId(Long messageId, int pageNum);

    Page<Media> getMediasAndFilesByConversationId(Long conversationId, int pageNum);

    Page<Media> getMediasByConversationId(Long conversationId, int pageNum);

    Page<Media> getRawFilesByConversationId(Long conversationId, int pageNum);
}
