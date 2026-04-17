package com.swp391.OnlineLearning.repository;

import com.swp391.OnlineLearning.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>,
        JpaSpecificationExecutor<Course> {

    // ================= BASIC =================

    boolean existsByName(String name);

    List<Course> findAllByFeaturedTrue();

    // ================= LEARNER QUERY (QUAN TRỌNG NHẤT) =================
    // Lấy course KHÔNG bao gồm course user đã mua
    @Query("""
        SELECT c FROM Course c
        WHERE c.status = 'PUBLISHED'
        AND (:categoryId IS NULL OR c.category.id = :categoryId)
        AND (:keyword IS NULL OR c.name LIKE CONCAT('%', :keyword, '%'))
        AND c.id NOT IN (
            SELECT e.course.id FROM Enrollment e WHERE e.user.id = :userId
        )
    """)
    Page<Course> findCoursesForLearner(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}