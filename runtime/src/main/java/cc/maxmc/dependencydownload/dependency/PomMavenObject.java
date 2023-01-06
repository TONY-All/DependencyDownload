package cc.maxmc.dependencydownload.dependency;

import org.jetbrains.annotations.NotNull;

public abstract class PomMavenObject extends BaseMavenObject {
    public PomMavenObject(String mavenDependency) {
        super(mavenDependency);
    }

    public PomMavenObject(String mavenDependency, String hash, String hashingAlgorithm) {
        super(mavenDependency, hash, hashingAlgorithm);
    }

    public PomMavenObject(String groupId, String artifactId, String version, String classifier) {
        super(groupId, artifactId, version, classifier);
    }

    public PomMavenObject(String groupId, String artifactId, String version, String classifier, String hash, String hashingAlgorithm) {
        super(groupId, artifactId, version, classifier, hash, hashingAlgorithm);
    }

    @Override
    public @NotNull String getType() {
        return "pom";
    }
}
