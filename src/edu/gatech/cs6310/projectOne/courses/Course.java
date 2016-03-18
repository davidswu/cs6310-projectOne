package edu.gatech.cs6310.projectOne.courses;

import edu.gatech.cs6310.projectOne.semester.Semester;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dawu on 2/10/16.
 */
public class Course {
    private Integer id;
    private String name;
    private String number;
    private Set<Semester> availableSemesters;
    private Set<Course> prerequisites;

    public Course(Integer id, String number, String name) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.availableSemesters = new HashSet<>();
        this.prerequisites = new HashSet<>();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public Set<Semester> getAvailableSemesters() {
        return availableSemesters;
    }

    public Set<Course> getPrerequisites() {
        return prerequisites;
    }

    public void addAvailableSemester(Semester semester) {
        this.availableSemesters.add(semester);
    }

    public void addPrerequisite(Course course) {
        this.prerequisites.add(course);
    }
}
