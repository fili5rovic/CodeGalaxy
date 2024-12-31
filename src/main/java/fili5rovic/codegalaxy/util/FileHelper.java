package fili5rovic.codegalaxy.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileHelper {

    public static String readFromFile(String path) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
        } catch (IOException e) {
            System.out.println("Couldn't read file " + path);
        }
        return sb.toString();
    }

}
