package fili5rovic.codegalaxy.util.downloader;

import java.nio.file.Paths;

public class Test {
    public static void main(String[] args) {
        try {
            JDTLSDownloader downloader = new JDTLSDownloader(
                    JDTLSRelease.V1_48_0,
                    Paths.get("C:/Users/fili5/OneDrive/Desktop/TestLSP"),
                    "jdtls.tar.gz"
            );
            downloader.download();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
