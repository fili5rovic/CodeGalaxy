package fili5rovic.codegalaxy.util;

import fili5rovic.codegalaxy.settings.IDESettings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

public class MetaDataHelper {

    private static volatile String srcPathCache = null;
    private static volatile String outputPathCache = null;
    private static final AtomicBoolean isCachePopulated = new AtomicBoolean(false);


    public static String getSrcPath() {
        return getClasspathPath("src");
    }

    public static String getOutputPath() {
        return getClasspathPath("output");
    }

    private static String getClasspathPath(String kind) {
        if (!isCachePopulated.get()) {
            populateCache();
        }

        if ("src".equals(kind)) {
            return srcPathCache;
        } else if ("output".equals(kind)) {
            return outputPathCache;
        }

        return null;
    }

    private static synchronized void populateCache() {
        if (isCachePopulated.get()) {
            return;
        }

        String projectPath = IDESettings.getRecentInstance().get("lastProjectPath");
        if (projectPath == null) {
            System.err.println("Project path not set in IDE settings.");
            isCachePopulated.set(true);
            return;
        }

        File classpathFile = new File(projectPath, ".classpath");
        if (!classpathFile.exists()) {
            System.err.println(".classpath file does not exist at: " + classpathFile.getPath());
            isCachePopulated.set(true);
            return;
        }

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(classpathFile);
            doc.getDocumentElement().normalize();

            NodeList entries = doc.getElementsByTagName("classpathentry");

            for (int i = 0; i < entries.getLength(); i++) {
                Node node = entries.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String entryKind = element.getAttribute("kind");
                    String entryPath = element.getAttribute("path");

                    // Only cache the "src" and "output" kinds.
                    if ("src".equals(entryKind)) {
                        srcPathCache = Paths.get(projectPath).resolve(entryPath).toString();
                    } else if ("output".equals(entryKind)) {
                        outputPathCache = Paths.get(projectPath).resolve(entryPath).toString();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading or parsing .classpath file: " + e.getMessage());
        } finally {
            isCachePopulated.set(true);
        }
    }
}