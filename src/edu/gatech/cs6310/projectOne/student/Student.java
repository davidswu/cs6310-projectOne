package edu.gatech.cs6310.projectOne.student;

import edu.gatech.cs6310.projectOne.courses.Course;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dawu on 2/10/16.
 */
public class Student {
    private Integer id;
    private Set<Course> courses;

    public Student(Integer id) {
        this.id = id;
        this.courses = new HashSet<>();
    }

    public Integer getId() {
        return id;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }
}
