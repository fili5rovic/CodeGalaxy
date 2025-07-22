package fili5rovic.codegalaxy.util.downloader;

public enum JDTLSRelease {
    V1_48_0("https://www.eclipse.org/downloads/download.php?file=/jdtls/milestones/1.48.0/jdt-language-server-1.48.0-202506271502.tar.gz");

    private final String url;

    JDTLSRelease(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}

