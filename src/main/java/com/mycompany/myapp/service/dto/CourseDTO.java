package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mycompany.myapp.domain.Course;

import java.util.Set;
import java.util.stream.Collectors;

public class CourseDTO {

    private Long id;

    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<UserDTO> students;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer studentsAmount;


    public CourseDTO() {
    }

    public CourseDTO(Course course) {
        this.id = course.getId();
        this.name = course.getName();
        if(course.getStudents()!=null){
            this.students = course.getStudents().stream().map(UserDTO::new).collect(Collectors.toSet());
        }
        if(this.students!=null){
            this.studentsAmount = this.students.size();
        }
    }
    public static CourseDTO getCourseDTOfromUserDTO(Course course) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(course.getId());
        courseDTO.setName(course.getName());
        return courseDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserDTO> getStudents() {
        return students;
    }

    public void setStudents(Set<UserDTO> students) {
        this.students = students;
    }

    public Integer getStudentsAmount() {
        return studentsAmount;
    }

    public void setStudentsAmount(Integer studentsAmount) {
        this.studentsAmount = studentsAmount;
    }
}
