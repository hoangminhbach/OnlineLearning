package com.example.demo.service;

import com.example.demo.model.CourseCategory;
import com.example.demo.repository.CourseCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CourseCategoryService {
    private final CourseCategoryRepository repository;

    public CourseCategoryService(CourseCategoryRepository repository) {
        this.repository = repository;
    }

    public Page<CourseCategory> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public CourseCategory findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public CourseCategory save(CourseCategory category) {
        return repository.save(category);
    }

    public CourseCategory update(CourseCategory category) {
        return repository.save(category);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
