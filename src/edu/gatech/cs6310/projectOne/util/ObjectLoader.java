package edu.gatech.cs6310.projectOne.util;

import edu.gatech.cs6310.projectOne.courses.Course;
import edu.gatech.cs6310.projectOne.semester.Semester;
import edu.gatech.cs6310.projectOne.semester.constants.SemesterEnum;
import edu.gatech.cs6310.projectOne.student.Student;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by dawu on 2/10/16.
 */
public class ObjectLoader {

    public static Map<Integer, Semester> loadSemesters() {
        Map<Integer, Semester> semesters = new HashMap<>();
        Semester semester;
        // Read Semester file
        Queue<String> file = FileReader.readFile(FileReader.SEMESTERS);
        // Remove header
        file.remove();
        String[] line;
        Integer semesterId;
        String name;
        String start;
        String end;
        while (!file.isEmpty()) {
            line = FileReader.next(file);
            semesterId = Integer.valueOf(line[0]);
            name = line[1];
            start = line[2];
            end = line[3];
            semester = new Semester(semesterId, name, start, end);
            semesters.put(semesterId, semester);
        }
        return semesters;
    }

    public static Map<Integer, Course> loadCourses(Map<Integer, Semester> semesters) {
        // Map to load prereqs and semesters into courses
        Map<Integer, Course> courses = new HashMap<>();
        Queue<String> file = FileReader.readFile(FileReader.COURSES);
        file.remove();
        String[] line;
        // constructor objects
        Integer courseId;
        String name;
        String number;
        // Object
        Course course;
        while (!file.isEmpty()) {
            line = FileReader.next(file);
            courseId = Integer.valueOf(line[0]);
            name = line[1];
            number = line[2];
            course = new Course(courseId, name, number);
            // add available semesters
            for (Semester semester : semesters.values()) {
                if (line[3].equals("1") && semester.getSemesterEnum().equals(SemesterEnum.FALL)) {
                    course.addAvailableSemester(semester);
                    continue;
                }
                if (line[4].equals("1") && semester.getSemesterEnum().equals(SemesterEnum.SPRING)) {
                    course.addAvailableSemester(semester);
                    continue;
                }
                if (line[5].equals("1") && semester.getSemesterEnum().equals(SemesterEnum.SUMMER)) {
                    course.addAvailableSemester(semester);
                    continue;
                }
            }
            courses.put(courseId, course);
        }
        // Read prereq file
        file = FileReader.readFile(FileReader.PREREQUISITES);
        file.remove();
        Integer preReqId;
        Integer dependentId;
        while (!file.isEmpty()) {
            line = FileReader.next(file);
            preReqId = Integer.valueOf(line[0]);
            dependentId = Integer.valueOf(line[1]);
            courses.get(dependentId).addPrerequisite(courses.get(preReqId));
        }
        return courses;
    }

    public static Map<Integer, Student> loadStudents(String path, Map<Integer, Course> courses) {
        Map<Integer, Student> students = new HashMap<>();
        Queue file = FileReader.readFile(path);
        file.remove();
        String[] line;
        Integer studentId;
        Integer courseId;
        Student student;
        Course course;
        while (!file.isEmpty()) {
            line = FileReader.next(file);
            studentId = Integer.valueOf(line[0]);
            courseId = Integer.valueOf(line[1]);
            if (!students.containsKey(studentId)) {
                student = new Student(studentId);
                students.put(studentId, student);
            } else {
                student = students.get(studentId);
            }
            course = courses.get(courseId);
            student.addCourse(course);
        }
        return students;
    }
}
