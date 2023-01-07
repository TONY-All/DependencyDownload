package cc.maxmc.dependencydownload.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public class SingleThreadFileDownloader implements FileDownloader {
    @Override
    public void downloadFile(URL url, Path target) throws IOException {
        System.out.println("Downloading " + url);
        target.toFile().getParentFile().mkdirs();
        URLConnection connection = url.openConnection();

        byte[] buffer = new byte[4096];
        try (BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream())) {
            try (BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(target))) {
                int total;
                while ((total = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, total);
                }
            }
        }
    }
}
