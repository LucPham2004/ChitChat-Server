package com.chitchat.server.service.impl;

import com.chitchat.server.entity.Media;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.repository.ConversationRepository;
import com.chitchat.server.repository.MediaRepository;
import com.chitchat.server.repository.MessageRepository;
import com.chitchat.server.service.MediaService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MediaServiceImpl implements MediaService {

    MediaRepository mediaRepository;
    MessageRepository messageRepository;
    ConversationRepository conversationRepository;

    static int MEDIAS_PER_PAGE = 20;

    public Media createMedia(String publicId, String url, String messageId) {
        Media media = new Media();

        media.setUrl(url);
        media.setPublicId(publicId);
        media.setMessage(messageRepository.findById(messageId).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED)));

        return mediaRepository.save(media);
    }

    public Media getById(String publicId) {
        return mediaRepository.findById(publicId).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
    }

    public Page<Media> getMediaByMessageId(String messageId, int pageNum) {
        if (!messageRepository.existsById(messageId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Pageable pageable = PageRequest.of(pageNum, MEDIAS_PER_PAGE, Sort.by(Sort.Direction.DESC, "createdAt"));

        return mediaRepository.findByMessageId(messageId, pageable);
    }
    
    public Page<Media> getMediasAndFilesByConversationId(String conversationId, int pageNum) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Pageable pageable = PageRequest.of(pageNum, MEDIAS_PER_PAGE, Sort.by(Sort.Direction.DESC, "createdAt"));

        return mediaRepository.findByConversationId(conversationId, pageable);
    }
    
    public Page<Media> getMediasByConversationId(String conversationId, int pageNum) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Pageable pageable = PageRequest.of(pageNum, MEDIAS_PER_PAGE, Sort.by(Sort.Direction.DESC, "createdAt"));
        String type = "raw";
        return mediaRepository.findByConversationIdAndResourceTypeNot(conversationId, type, pageable);
    }
    
    public Page<Media> getRawFilesByConversationId(String conversationId, int pageNum) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Pageable pageable = PageRequest.of(pageNum, MEDIAS_PER_PAGE, Sort.by(Sort.Direction.DESC, "createdAt"));
        String type = "raw";
        return mediaRepository.findByConversationIdAndResourceType(conversationId, type, pageable);
    }
}
