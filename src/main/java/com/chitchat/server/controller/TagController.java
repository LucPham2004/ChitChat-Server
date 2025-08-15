package com.chitchat.server.controller;

import com.chitchat.server.dto.response.ApiResponse;
import com.chitchat.server.entity.Tag;
import com.chitchat.server.service.TagService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class TagController {
    TagService tagService;

    @GetMapping("/get/all")
    public ApiResponse<Page<Tag>> getAllTags(@RequestParam(defaultValue = "0") int pageNum) {
        return ApiResponse.<Page<Tag>>builder()
            .code(1000)
            .message("Get tags successfully")
            .result(tagService.getAllTags(pageNum))
            .build();
    }
    
    @PostMapping("/create/userId/{userId}")
    public ApiResponse<Tag> createTag(@PathVariable Long userId) {
        return ApiResponse.<Tag>builder()
            .code(1000)
            .message("Create Tag successfully")
            .result(tagService.createTag(userId))
            .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ApiResponse.<Void>builder()
            .code(1000)
            .message("Delete Tag successfully")
            .build();
    }

}
