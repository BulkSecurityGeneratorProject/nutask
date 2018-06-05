package com.mycompany.myapp.web.rest;


import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.Course;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.CourseRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.CourseService;
import com.mycompany.myapp.service.dto.CourseDTO;
import com.mycompany.myapp.web.rest.errors.InternalServerErrorException;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import io.undertow.util.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CourseResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);
    private final CourseService courseService;
    private final CourseRepository courseRepository;

    public CourseResource(CourseService courseService, CourseRepository courseRepository) {
        this.courseService = courseService;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/courses/my")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public Set<CourseDTO> getMyCourses(Pageable pageable){
        final String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
        Set<CourseDTO> courseDTOSet = courseService.getCoursesOfUser(pageable, userLogin);
        courseDTOSet.forEach(courseDTO -> courseDTO.setStudents(null));
        return courseDTOSet;
    }

    @GetMapping("/courses/{id}")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public CourseDTO getCourse(@PathVariable Long id){
        final String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
        CourseDTO courseDTO = new CourseDTO(courseRepository.findOne(id));
        return courseDTO;
    }

    @PostMapping("/courses")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTOReq) throws URISyntaxException {
        CourseDTO courseDTO = courseService.createCourse(courseDTOReq);
        return ResponseEntity.ok(courseDTO);
    }

    @PutMapping("/courses")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN})
    public ResponseEntity<CourseDTO> updateCourse(@RequestBody CourseDTO courseDTOReq) throws URISyntaxException {
        CourseDTO courseDTO = null;
        try {
            courseDTO = courseService.updateCourse(courseDTOReq);
        } catch (BadRequestException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(courseDTO);
    }

    @GetMapping("/courses")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<List<CourseDTO>> getAllCourses(Pageable pageable){
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }

    @PostMapping("/courses/add")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<CourseDTO> addCourse(@RequestBody CourseDTO courseDTO){
        final String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
        Course course = courseRepository.findOne(courseDTO.getId());
        if(course == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        CourseDTO courseDTO1 = courseService.addCourse(userLogin, course.getId());
        return ResponseEntity.ok(courseService.createCourse(courseDTO1));
    }

    @PostMapping("/courses/remove")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<CourseDTO> removeCourse(@RequestBody CourseDTO courseDTOReq){
        final String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
        Course course = courseRepository.findOne(courseDTOReq.getId());
        if(course == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        CourseDTO courseDTO = courseService.removeCourse(userLogin, course.getId());
        return ResponseEntity.ok(courseDTO);
    }

    @DeleteMapping("/courses/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id){
        courseService.deleteCourse(id);
        return ResponseEntity.ok().build();
    }
}
