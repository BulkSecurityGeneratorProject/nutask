package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Course;
import com.mycompany.myapp.domain.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CourseRepository extends JpaRepository<Course, Long> {

    String COURSES_BY_ID = "coursesById";


    Page<Course> findAllByStudentsContains(Pageable pageable, User student);
    
    @Cacheable(cacheNames = COURSES_BY_ID)
    Course findOne(Long id);

    @Cacheable(cacheNames = COURSES_BY_ID)
    Optional<Course> findFirstById(Long id);
}
