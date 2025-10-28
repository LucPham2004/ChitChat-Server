package com.chitchat.server.service.impl;

import com.chitchat.server.entity.Tag;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.repository.TagRepository;
import com.chitchat.server.service.TagService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class TagServiceImpl implements TagService {
    TagRepository tagRepository;

    static int TAGS_PER_PAGE = 20;

    public Page<Tag> getAllTags(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, TAGS_PER_PAGE);
        return tagRepository.findAll(pageable);
    }

    public Page<Tag> getAllTagsSortedByMessageCount(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, TAGS_PER_PAGE);
        return tagRepository.findAllOrderByMessageDesc(pageable);
    }
    
    public Tag createTag(String userId) {
        Tag tag = new Tag();
        tag.setUserId(userId);
        return tagRepository.save(tag);
    }

    public void deleteTag(Long id) {
        if(tagRepository.existsById(id)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        tagRepository.delete(tagRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED)));
    }
}
