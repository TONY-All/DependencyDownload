package cc.maxmc.dependencydownload.dependency;

import cc.maxmc.dependencydownload.pom.DependencyScope;
import org.jetbrains.annotations.NotNull;

public abstract class JarMavenObject extends BaseMavenObject {
    private final DependencyScope scope;

    public JarMavenObject(String mavenDependency, String hash, String hashingAlgorithm, DependencyScope scope) {
        super(mavenDependency, hash, hashingAlgorithm);
        this.scope = scope;
    }

    public JarMavenObject(String groupId, String artifactId, String version, String classifier, String hash, String hashingAlgorithm, DependencyScope scope) {
        super(groupId, artifactId, version, classifier, hash, hashingAlgorithm);
        this.scope = scope;
    }

    public abstract PomMavenObject getPom();

    @Override
    public @NotNull String getType() {
        return "jar";
    }

    public DependencyScope getScope() {
        return scope;
    }
}
