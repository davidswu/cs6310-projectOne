package edu.gatech.cs6310.projectOne;

import edu.gatech.cs6310.projectOne.courses.Course;
import edu.gatech.cs6310.projectOne.semester.Semester;
import edu.gatech.cs6310.projectOne.student.Student;
import edu.gatech.cs6310.projectOne.util.ObjectLoader;
import gurobi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dawu on 2/11/16.
 */
public class Scheduler {
    public static float calculateSchedule(String path) {
        float result = 0;
        // Load all semesters
        Map<Integer, Semester> semestersMap = ObjectLoader.loadSemesters();
        List<Semester> semesters = new ArrayList<>(semestersMap.values());
        // Load all courses
        Map<Integer, Course> coursesMap = ObjectLoader.loadCourses(semestersMap);
        List<Course> courses = new ArrayList<>(coursesMap.values());
        // Load all students
        Map<Integer, Student> studentsMap = ObjectLoader.loadStudents(path, coursesMap);
        List<Student> students = new ArrayList<>(studentsMap.values());

        try {
            GRBEnv env = new GRBEnv("mip1.log");
            env.set(GRB.IntParam.LogToConsole, 0);
            int i = students.size();
            int j = courses.size();
            int k = semesters.size();
            GRBVar[][][] yijk = new GRBVar[i][j][k];
            Student student;
            Course course;
            Semester semester;

            // Add all to model
            GRBModel model = new GRBModel(env);
            for (i = 0; i < students.size(); i++) {
                for (j = 0; j < courses.size(); j++) {
                    for (k = 0; k < semesters.size(); k++) {
                        GRBVar grbVar = model.addVar(0, 1, 0.0, GRB.BINARY, "");
                        yijk[i][j][k] = grbVar;
                    }
                }
            }

            // We want to minimize this variable
            GRBVar X = model.addVar(0, GRB.INFINITY, 0, GRB.CONTINUOUS, "X");
            model.update();

            GRBLinExpr obj = new GRBLinExpr();
            obj.addTerm(1, X);
            model.setObjective(obj, GRB.MINIMIZE);

            // Total students taking course per semester is less than X
            for (j = 0; j < courses.size(); j++) {
                for (k = 0; k < semesters.size(); k++) {
                    GRBLinExpr total = new GRBLinExpr();
                    for (i = 0; i < students.size(); i++) {
                        total.addTerm(1, yijk[i][j][k]);
                    }
                    String cname = "TOTAL_Couse" + j + "_Semester" + k;
                    model.addConstr(total, GRB.LESS_EQUAL, X, cname);
                }
            }

            // Student must take desired course
            for (i = 0; i < students.size(); i++) {
                student = students.get(i);
                for (j = 0; j < courses.size(); j++) {
                    course = courses.get(j);
                    if (student.getCourses().contains(course)) {
                        GRBLinExpr mustEnroll = new GRBLinExpr();
                        for (k = 0; k < semesters.size(); k++) {
                            mustEnroll.addTerm(1, yijk[i][j][k]);
                        }
                        String cname = "MUSTENROLL_Student" + i + "_Course" + j;
                        model.addConstr(mustEnroll, GRB.EQUAL, 1, cname);
                    }
                }
            }

            // Limit to 2 courses a semester
            for (i = 0; i < students.size(); i++) {
                for (k = 0; k < semesters.size(); k++) {
                    GRBLinExpr maxCourses = new GRBLinExpr();
                    for (j = 0; j < courses.size(); j++) {
                        maxCourses.addTerm(1, yijk[i][j][k]);
                    }
                    String cname = "MAXCOURSES_Student" + i + "_SEMESTER" + k;
                    model.addConstr(maxCourses, GRB.LESS_EQUAL, 2, cname);
                }
            }

            // If course is unavailable that semester, nobody can enroll
            for (j = 0; j < courses.size(); j++) {
                course = courses.get(j);
                for (k = 0; k < semesters.size(); k++) {
                    semester = semesters.get(k);
                    if (!course.getAvailableSemesters().contains(semester)) {
                        GRBLinExpr unavailable = new GRBLinExpr();
                        for (i = 0; i < students.size(); i++) {
                            unavailable.addTerm(1, yijk[i][j][k]);
                        }
                        String cname = "UNAVAILABLE_COURSE" + j + "_SEMESTER" + k;
                        model.addConstr(unavailable, GRB.EQUAL, 0, cname);
                    }
                }
            }

            // Prerequisites
            for (i = 0; i < students.size(); i++) {
                for (j = 0; j < courses.size(); j++) {
                    course = courses.get(j);
                    // if course has prereq
                    if (course.getPrerequisites().size() > 0) {
                        GRBLinExpr prereqLHS = new GRBLinExpr();
                        for (k = 1; k < semesters.size() - 1; k++) {
                            prereqLHS.addTerm(1, yijk[i][j][k + 1]);
                        }
                        // for each prereq, add all semesters for that student
                        for (Course c : course.getPrerequisites()) {
                            // find index in array
                            int j0 = courses.indexOf(c);
                            GRBLinExpr prereqRHS = new GRBLinExpr();
                            for (k = 1; k < semesters.size() - 1; k++) {
                                prereqRHS.addTerm(1, yijk[i][j0][k]);
                            }
                            String cname = "PREREQ_STUDENT" + i + "_COURSE" + j;
                            // course must be enrolled <= prereq
                            model.addConstr(prereqLHS, GRB.LESS_EQUAL, prereqRHS, cname);
                        }
                    }
                }
            }

            // Any course with prereq cannot be taken the first semester
            for (k = 0; k < semesters.size(); k++) {
                semester = semesters.get(k);
                for (j = 0; j < courses.size(); j++) {
                    course = courses.get(j);
                    if (course.getPrerequisites().size() > 0 && semester.getId().equals(Integer.valueOf("1"))) {
                        GRBLinExpr firstSemester = new GRBLinExpr();
                        for (i = 0; i < students.size(); i++) {
                            firstSemester.addTerm(1, yijk[i][j][k]);
                        }
                        String cname = "FIRSTSEMESTER_Semester" + k + "_Couse" + j;
                        model.addConstr(firstSemester, GRB.EQUAL, 0, cname);
                    }
                }
            }

            model.optimize();
            result = (float) model.get(GRB.DoubleAttr.ObjVal);
        } catch (GRBException e) {
            e.printStackTrace();
        }
        return result;
    }
}
