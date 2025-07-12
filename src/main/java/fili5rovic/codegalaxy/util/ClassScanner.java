package fili5rovic.codegalaxy.util;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.classgraph.ClassInfo;

import java.util.ArrayList;
import java.util.List;

// NOT YET USED
// !!!!!!!!! I DON'T WANT TO USE THIS BECAUSE IT'S USING COMPILED CLASSES
// the reason why I am using this library and not lsp
// is because lsp can't get out system classes
public class ClassScanner {
    /**
     * Scans the specified classpath output directory for all class names.
     *
     * @return List of fully-qualified class names.
     */
    public static String[] getAllProjectClasses() {
        String outputDir = MetaDataHelper.getClasspathPath("output");
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

        return classNames.toArray(new String[0]);
    }

}
