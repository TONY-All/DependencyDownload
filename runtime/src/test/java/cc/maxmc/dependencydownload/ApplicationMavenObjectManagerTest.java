package cc.maxmc.dependencydownload;

import cc.maxmc.dependencydownload.dependency.BaseMavenObject;
import cc.maxmc.dependencydownload.dependency.JarMavenObject;
import cc.maxmc.dependencydownload.dependency.MavenObject;
import cc.maxmc.dependencydownload.dependency.ReleaseDependency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

public class ApplicationMavenObjectManagerTest {

    private final JarMavenObject mavenObject1 = new ReleaseDependency("a", "a-a", "", "", "", "");
    private final JarMavenObject mavenObject2 = new ReleaseDependency("b", "b-a", "", "", "", "");

    public ApplicationMavenObjectManagerTest() throws IOException {
    }

    @Test
    public void addDependencyTest() {
        ApplicationDependencyManager manager = new ApplicationDependencyManager(Paths.get("."));
        Assertions.assertEquals(1, manager.include(Collections.singleton(mavenObject1)).getDependencies().size());
    }

    @Test
    public void duplicationTest() {
        ApplicationDependencyManager manager = new ApplicationDependencyManager(Paths.get("."));
        Assertions.assertEquals(1, manager.include(Collections.singleton(mavenObject1)).getDependencies().size());
        Assertions.assertEquals(0, manager.include(Collections.singleton(mavenObject1)).getDependencies().size());
    }

    @Test
    public void multipleTest() {
        ApplicationDependencyManager manager = new ApplicationDependencyManager(Paths.get("."));
        Assertions.assertEquals(1, manager.include(Collections.singleton(mavenObject1)).getDependencies().size());
        Assertions.assertEquals(1, manager.include(Collections.singleton(mavenObject2)).getDependencies().size());
    }
}
