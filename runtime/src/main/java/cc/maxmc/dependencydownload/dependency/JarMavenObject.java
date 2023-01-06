package cc.maxmc.dependencydownload.dependency;

import org.jetbrains.annotations.NotNull;

public abstract class JarMavenObject extends BaseMavenObject {
    public abstract PomMavenObject getPom();

    public JarMavenObject(String mavenDependency) {
        super(mavenDependency);
    }

    public JarMavenObject(String mavenDependency, String hash, String hashingAlgorithm) {
        super(mavenDependency, hash, hashingAlgorithm);
    }

    public JarMavenObject(String groupId, String artifactId, String version, String classifier) {
        super(groupId, artifactId, version, classifier);
    }

    public JarMavenObject(String groupId, String artifactId, String version, String classifier, String hash, String hashingAlgorithm) {
        super(groupId, artifactId, version, classifier, hash, hashingAlgorithm);
    }

    @Override
    public @NotNull String getType() {
        return "jar";
    }
}
