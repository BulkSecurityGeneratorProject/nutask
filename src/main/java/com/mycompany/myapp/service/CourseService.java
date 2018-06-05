package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Course;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.AuthorityRepository;
import com.mycompany.myapp.repository.CourseRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.CourseDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.mapper.UserMapper;
import com.mycompany.myapp.web.rest.errors.InternalServerErrorException;
import io.undertow.util.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final UserMapper userMapper;

    private final CacheManager cacheManager;

    public CourseService(
        CourseRepository courseRepository,
        UserRepository userRepository,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager, UserMapper userMapper) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
        this.userMapper = userMapper;
    }

    public CourseDTO createCourse(CourseDTO courseDTO){
        Course course = new Course();
        course.setId(courseDTO.getId());
        course.setName(courseDTO.getName());
        course.setStudents(new HashSet<>());
        for(UserDTO u : courseDTO.getStudents()){
            User user = userRepository.findOne(u.getId());
            if(user!=null){
                course.getStudents().add(user);
                user.getCourses().add(course);
            }
        }
        courseRepository.save(course);
        courseDTO = new CourseDTO(course);
        cacheManager.getCache(CourseRepository.COURSES_BY_ID).evict(course.getId());
        return courseDTO;
    }

    public CourseDTO updateCourse(CourseDTO courseDTO) throws BadRequestException {
        Course course = courseRepository.findOne(courseDTO.getId());
        if(course == null){
            throw new BadRequestException();
        }
        course.setName(courseDTO.getName());
        for(User u: course.getStudents()){
            u.getCourses().remove(course);
            userRepository.save(u);
        }
        course.setStudents(new HashSet<>());
        for(UserDTO u : courseDTO.getStudents()){
            User user = userRepository.findOne(u.getId());
            if(user!=null){
                course.getStudents().add(user);
                user.getCourses().add(course);
            }
        }
        courseRepository.save(course);
        cacheManager.getCache(CourseRepository.COURSES_BY_ID).evict(course.getId());
        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).clear();
        cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).clear();
        return courseDTO;
    }

    public List<CourseDTO> getAllCourses(Pageable pageable){
        return courseRepository
            .findAll(pageable).getContent()
            .stream().map(CourseDTO::new).collect(Collectors.toList());

    }


    public Set<CourseDTO> getCoursesOfUser(Pageable pageable, String login) throws InternalServerErrorException {
        Optional<User> user = userRepository.findOneByLogin(login);
        if(!user.isPresent()){
            throw new InternalServerErrorException("");
        }
        Set<CourseDTO> courseList= courseRepository
            .findAllByStudentsContains(pageable, user.get()).getContent()
            .stream().map(CourseDTO::new).collect(Collectors.toSet());
        return courseList;
    }

    public CourseDTO addCourse(String login, Long courseId) throws InternalServerErrorException {
        Optional<User> userRequest = userRepository.findOneByLogin(login);
        Course course = courseRepository.findOne(courseId);

        if(!userRequest.isPresent() || course == null){
            throw new InternalServerErrorException("");
        }

        User user = userRequest.get();
        user.getCourses().add(course);
        course.getStudents().add(user);
        courseRepository.save(course);
        cacheManager.getCache(CourseRepository.COURSES_BY_ID).evict(course.getId());
        return new CourseDTO(course);
    }

    public CourseDTO removeCourse(String login, Long courseId) throws InternalServerErrorException {

        Optional<User> userRequest = userRepository.findOneByLogin(login);
        Course course = courseRepository.findOne(courseId);

        if(!userRequest.isPresent() || course == null){
            throw new InternalServerErrorException("");
        }

        User user = userRequest.get();
        boolean courseRemoved = user.getCourses().remove(course);
        boolean userRemoved = course.getStudents().remove(user);
        courseRepository.save(course);
        cacheManager.getCache(CourseRepository.COURSES_BY_ID).evict(course.getId());

        return new CourseDTO(course);
    }


    public void deleteCourse(Long id) {
        courseRepository.findFirstById(id).ifPresent(course -> {
            for(User u : course.getStudents()){
                u.getCourses().remove(course);
            }
            userRepository.save(course.getStudents());
            courseRepository.delete(course);
            cacheManager.getCache(CourseRepository.COURSES_BY_ID).evict(course.getId());
            log.debug("Deleted Course: {}", course);
        });
    }
}
