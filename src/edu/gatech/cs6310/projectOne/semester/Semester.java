package edu.gatech.cs6310.projectOne.semester;

import edu.gatech.cs6310.projectOne.semester.constants.SemesterEnum;

/**
 * Created by dawu on 2/10/16.
 */
public class Semester {
    private Integer id;
    private String name;
    private SemesterEnum semesterEnum;
    private String start;
    private String end;

    public Semester(Integer id, String name, String start, String end) {
        this.id = id;
        this.name = name;
        this.semesterEnum = SemesterEnum.valueOf(name.split(" ")[0].toUpperCase());
        this.start = start;
        this.end = end;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SemesterEnum getSemesterEnum() {
        return semesterEnum;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

}
