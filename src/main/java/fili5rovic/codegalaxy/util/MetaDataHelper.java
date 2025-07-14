package fili5rovic.codegalaxy.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import fili5rovic.codegalaxy.settings.IDESettings;
import org.w3c.dom.*;

import java.io.File;
import java.nio.file.Paths;

public class MetaDataHelper {

    public static String getClasspathPath(String kind) {
        try {
            String projectPath = IDESettings.getRecentInstance().get("lastProjectPath");
            if (projectPath == null) {
                System.err.println("Project path not set in properties.");
                return null;
            }
            String classpathFilePath = projectPath + "/.classpath";

            File classpathFile = new File(classpathFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(classpathFile);
            doc.getDocumentElement().normalize();

            NodeList entries = doc.getElementsByTagName("classpathentry");

            for (int i = 0; i < entries.getLength(); i++) {
                Node node = entries.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String classPathKind = element.getAttribute("kind");

                    if (kind.equals(classPathKind)) {
                        return Paths.get(projectPath).resolve(element.getAttribute("path")).toString();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error reading classpath file: " + e.getMessage());
        }
        return null;
    }

}

