package com.swp391.OnlineLearning.service.impl;

import com.swp391.OnlineLearning.model.Blog;
import com.swp391.OnlineLearning.model.dto.BlogDTO;
import com.swp391.OnlineLearning.repository.BlogRepository;
import com.swp391.OnlineLearning.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Page<BlogDTO> getPaginatedPublishedBlogsByCategorySlug(String blogCategorySlug, Pageable pageable) {
        return this.blogRepository.findAllPublishedByCategory_Slug(blogCategorySlug, pageable);
    }

    @Override
    public BlogDTO getBlogById(Long blogId) {
        return this.blogRepository.findPublishedBlogById(blogId);
    }

    @Override
    public BlogDTO getBlogByIdForMarketing(Long blogId) {
        return this.blogRepository.findBlogById(blogId);
    }

    @Override
    public List<BlogDTO> findLatestBlogs(int i) {
        return this.blogRepository.findLatestPublishedBlogs(PageRequest.of(0, i));
    }

    @Override
    public Page<BlogDTO> getBlogsForMarketing(String keyword, String statusStr, int page, int size) {
        Blog.BlogStatus status = null;
        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                status = Blog.BlogStatus.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());
        return this.blogRepository.findAllBlogsForMarketing(keyword, status, pageable);
    }
}
