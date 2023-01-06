package cc.maxmc.dependencydownload.downloader;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Used to download dependencies and other files.
 *
 * @author tony_all
 */
public interface FileDownloader {
    /**
     * Downloads a single file.
     *
     * @param url    url of the file to download.
     * @param target where to download the file.
     * @throws IOException if file download failed.
     */
    void downloadFile(URL url, Path target) throws IOException;
}
