package cc.maxmc.dependencydownload.pom;

import cc.maxmc.dependencydownload.DependencyManager;
import cc.maxmc.dependencydownload.common.util.HashUtils;
import cc.maxmc.dependencydownload.dependency.PomMavenObject;
import cc.maxmc.dependencydownload.path.DependencyPathProvider;
import cc.maxmc.dependencydownload.repository.Repository;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

public class PomManager {
    private final DocumentBuilder builder;
    private final Repository repo;
    private final PomMavenObject pom;
    private final DependencyManager manager;

    {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public PomManager(Repository repo, PomMavenObject pom, DependencyManager manager) {
        this.repo = repo;
        this.pom = pom;
        this.manager = manager;
    }

    public void downloadPom() throws MalformedURLException {
        URL url = repo.createURL(pom);
        DependencyPathProvider pathProvider = manager.getDependencyPathProvider();
        try {
            manager.getDownloader().downloadFile(url, pathProvider.getDependencyPath(pom, Collections.emptySet()));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to download pom (" + pom + ")", e);
        }
    }

    public void downloadPomHash() throws MalformedURLException {
        URL url = repo.createHashURL(pom);
        DependencyPathProvider pathProvider = manager.getDependencyPathProvider();
        try {
            manager.getDownloader().downloadFile(url, pathProvider.getDependencyHashPath(pom));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to download pom (" + pom + ")", e);
        }
    }

    public boolean checkPomHash() throws NoSuchAlgorithmException, IOException {
        Path pomPath = manager.getDependencyPathProvider().getDependencyPath(pom, Collections.emptySet());
        Path pomHashPath = manager.getDependencyPathProvider().getDependencyHashPath(pom);
        String pomFileHash = HashUtils.getFileHash(pomPath.toFile(), pom.getHashingAlgorithm());

        if (pom.getHash() != null) return pomFileHash.equals(pom.getHash());

        if (!pomHashPath.toFile().exists()) {
            downloadPomHash();
        }
        String hash = HashUtils.readFile(pomHashPath);
        return pomFileHash.equals(hash);
    }

    public PomParser parse() throws IOException, SAXException {
        Document parse = builder.parse(manager.getDependencyPathProvider().getDependencyPath(pom, Collections.emptySet()).toFile());
        return new PomParser(parse);
    }
}
