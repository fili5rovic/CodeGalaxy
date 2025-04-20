package fili5rovic.codegalaxy.util;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.classgraph.ClassInfo;

import java.util.ArrayList;
import java.util.List;

// the reason why I am using this library and not lsp
// is because lsp can't get out system classes
public class ClassScanner {

    /**
     * Scans the specified classpath output directory for all class names.
     * @param outputDir The output directory (like "bin" or "target/classes").
     * @return List of fully-qualified class names.
     */
    public static List<String> getAllProjectClasses(String outputDir) {
        List<String> classNames = new ArrayList<>();

        try (ScanResult scanResult = new ClassGraph()
                .overrideClasspath(outputDir)
                .enableClassInfo()
//                .enableSystemJarsAndModules() // If I do this, I need to check if they are imported first ( there are too many )
                .scan()) {

            for (ClassInfo classInfo : scanResult.getAllClasses()) {
                classNames.add(classInfo.getName());
            }
        }

        return classNames;
    }

    public static void main(String[] args) {
        List<String> classes = getAllProjectClasses("D:/MY_WORKSPACE/Sex/bin");

        for (String className : classes) {
            System.out.println(className);
        }
    }
}
