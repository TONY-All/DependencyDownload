package cc.maxmc.dependencydownload.common.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A helper class to get standard hashes from {@link MessageDigest}s and {@link File}s.
 */
public final class HashUtils {

    private HashUtils() {
    }

    /**
     * Gets the hash of the provided file
     *
     * @param path      the file path
     * @param algorithm the hashing algorithm (used on {@link MessageDigest#getInstance(String)})
     * @return the file's hash in standard format
     * @throws NoSuchAlgorithmException if the provided algorithm couldn't be found
     * @throws IOException              if reading the file was unsuccessful
     */
    public static String getFileHash(Path path, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);

        try (InputStream inputStream = Files.newInputStream(path)) {
            byte[] buffer = new byte[1024];
            int total;
            while ((total = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, total);
            }
        }

        return getHash(digest);
    }

    /**
     * Gets the hash from the provided {@link MessageDigest}.
     *
     * @param digest the message digest
     * @return the hash in standard format
     */
    public static String getHash(MessageDigest digest) {
        StringBuilder result = new StringBuilder();
        for (byte b : digest.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public static String readFile(Path path) {
        try (FileReader reader = new FileReader(path.toFile())) {
            char[] buffer = new char[32];
            int length;
            StringBuilder result = new StringBuilder();
            while ((length = reader.read(buffer)) != -1) {
                result.append(buffer, 0, length);
            }
            return result.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + path);
        }
    }
}
