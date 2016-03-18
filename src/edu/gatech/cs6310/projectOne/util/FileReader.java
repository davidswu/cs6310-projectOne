package edu.gatech.cs6310.projectOne.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by dawu on 2/10/16.
 */
public class FileReader {

    private static final String PATH_TO_STATIC_RESOURCES = "../resources/static/";

    private static final String splitRegex = ",";

    public static final String PREREQUISITES = PATH_TO_STATIC_RESOURCES + "course_dependencies.csv";
    public static final String COURSES = PATH_TO_STATIC_RESOURCES + "courses.csv";
    public static final String SEMESTERS = PATH_TO_STATIC_RESOURCES + "semesters.csv";

    public static Queue<String> readFile(String name) {
        Queue<String> parsedFile = new LinkedList<String>();
        BufferedReader br = null;
        String line;
        try {
            Path file = Paths.get(name).toAbsolutePath();
            br = new BufferedReader(new java.io.FileReader(file.toFile()));
            line = br.readLine();
            while (line != null) {
                parsedFile.add(line);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return parsedFile;
    }

    public static String[] next(Queue<String> file) {
        String line = file.remove();
        return split(line);
    }

    private static String[] split(String line) {
        return line.split(splitRegex);
    }
}
