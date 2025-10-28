package com.chitchat.server.controller;

import com.chitchat.server.dto.response.ApiResponse;
import com.chitchat.server.dto.response.MediaResponse;
import com.chitchat.server.entity.Media;
import com.chitchat.server.mapper.MediaMapper;
import com.chitchat.server.service.MediaService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/medias")
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class MediaController {
    
    MediaService mediaService;
    MediaMapper mediaMapper;

    @GetMapping("/get/{id}")
    public ApiResponse<MediaResponse> getMediaById(@PathVariable String id) {
       Media media = mediaService.getById(id);
        return ApiResponse.<MediaResponse>builder()
            .code(1000)
            .message("Get media with id: " + id + " successfully")
            .result(mediaMapper.toResponse(media))
            .build();
    }

    @GetMapping("/get/message")
    public ApiResponse<Page<MediaResponse>> getMediaByMessageId(
                @RequestParam String messageId,
                @RequestParam int pageNum) {
        Page<Media> medias = mediaService.getMediaByMessageId(messageId, pageNum);
        return ApiResponse.<Page<MediaResponse>>builder()
            .code(1000)
            .message("Get medias by message with id: " + messageId + " successfully")
            .result(medias.map(mediaMapper::toResponse))
            .build();
    }

    @GetMapping("/get/conversation")
    public ApiResponse<Page<MediaResponse>> getMediasAndFilesByConversationId(
                @RequestParam String conversationId,
                @RequestParam int pageNum) {
        Page<Media> medias = mediaService.getMediasAndFilesByConversationId(conversationId, pageNum);
        return ApiResponse.<Page<MediaResponse>>builder()
            .code(1000)
            .message("Get medias by message with id: " + conversationId + " successfully")
            .result(medias.map(mediaMapper::toResponse))
            .build();
    }
    
    @GetMapping("/get/conversation/media")
    public ApiResponse<Page<MediaResponse>> getMediasByConversationId(
                @RequestParam String conversationId,
                @RequestParam int pageNum) {
        Page<Media> medias = mediaService.getMediasByConversationId(conversationId, pageNum);
        return ApiResponse.<Page<MediaResponse>>builder()
            .code(1000)
            .message("Get medias by message with id: " + conversationId + " successfully")
            .result(medias.map(mediaMapper::toResponse))
            .build();
    }
    
    @GetMapping("/get/conversation/raw")
    public ApiResponse<Page<MediaResponse>> getRawFilesByConversationId(
                @RequestParam String conversationId,
                @RequestParam int pageNum) {
        Page<Media> medias = mediaService.getRawFilesByConversationId(conversationId, pageNum);
        return ApiResponse.<Page<MediaResponse>>builder()
            .code(1000)
            .message("Get medias by message with id: " + conversationId + " successfully")
            .result(medias.map(mediaMapper::toResponse))
            .build();
    }

}
