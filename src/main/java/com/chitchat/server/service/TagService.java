package com.chitchat.server.service;

import com.chitchat.server.entity.Tag;
import org.springframework.data.domain.Page;

public interface TagService {
    Page<Tag> getAllTags(int pageNum);

    Page<Tag> getAllTagsSortedByMessageCount(int pageNum);

    Tag createTag(String userId);

    void deleteTag(Long id);
}
