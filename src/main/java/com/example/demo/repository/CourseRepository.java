package com.example.demo.repository;

import com.example.demo.model.Course;
import com.example.demo.model.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR c.name LIKE %:keyword% OR c.description LIKE %:keyword%) " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:categoryId IS NULL OR c.category.id = :categoryId) " +
           "AND (:expertId IS NULL OR c.expert.id = :expertId) " +
           "AND (:isFeatured IS NULL OR c.isFeatured = :isFeatured)")
    Page<Course> findForAdmin(@Param("keyword") String keyword,
                              @Param("status") CourseStatus status,
                              @Param("categoryId") Long categoryId,
                              @Param("expertId") Long expertId,
                              @Param("isFeatured") Boolean isFeatured,
                              Pageable pageable);

    Page<Course> findByExpertId(Long expertId, Pageable pageable);

    Page<Course> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Course> findByStatus(CourseStatus status, Pageable pageable);
}
