package cc.maxmc.dependencydownload;

import cc.maxmc.dependencydownload.dependency.JarMavenObject;
import cc.maxmc.dependencydownload.dependency.ReleaseDependency;
import cc.maxmc.dependencydownload.downloader.SingleThreadFileDownloader;
import cc.maxmc.dependencydownload.repository.Repository;
import cc.maxmc.dependencydownload.repository.StandardRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationMavenObjectManagerTest {

    private final JarMavenObject mavenObject1 = new ReleaseDependency("a", "a-a", "", "", "", "");
    private final JarMavenObject mavenObject2 = new ReleaseDependency("b", "b-a", "", "", "", "");
    private final Executor executor = Executors.newFixedThreadPool(10);

    public ApplicationMavenObjectManagerTest() {
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        DependencyManager man = new DependencyManager(Paths.get(".", "libs"), new SingleThreadFileDownloader(), executorService);
        man.addDependency(new ReleaseDependency("org.jetbrains.kotlin:kotlin-stdlib:1.8.0"));
        List<Repository> repo = Collections.singletonList(new StandardRepository("https://repo1.maven.org/maven2"));
        man.loadTransitives(repo).join();
        man.downloadAll(repo).join();
        executorService.shutdown();
    }

    @Test
    public void addDependencyTest() {
        ApplicationDependencyManager manager = new ApplicationDependencyManager(Paths.get("."));
        Assertions.assertEquals(1, manager.include(Collections.singleton(mavenObject1), executor).getDependencies().size());
    }

    @Test
    public void duplicationTest() {
        ApplicationDependencyManager manager = new ApplicationDependencyManager(Paths.get("."));
        Assertions.assertEquals(1, manager.include(Collections.singleton(mavenObject1), executor).getDependencies().size());
        Assertions.assertEquals(0, manager.include(Collections.singleton(mavenObject1), executor).getDependencies().size());
    }

    @Test
    public void multipleTest() {
        ApplicationDependencyManager manager = new ApplicationDependencyManager(Paths.get("."));
        Assertions.assertEquals(1, manager.include(Collections.singleton(mavenObject1), executor).getDependencies().size());
        Assertions.assertEquals(1, manager.include(Collections.singleton(mavenObject2), executor).getDependencies().size());
    }
}
