package cc.maxmc.dependencydownload.dependency;

import cc.maxmc.dependencydownload.pom.DependencyScope;
import org.jetbrains.annotations.Nullable;

public class ReleaseDependency extends JarMavenObject {
    public ReleaseDependency(String mavenDependency) {
        this(mavenDependency, null, "md5");
    }

    public ReleaseDependency(String mavenDependency, String hash, String hashingAlgorithm) {
        super(mavenDependency, hash, hashingAlgorithm, DependencyScope.COMPILE);
    }

    public ReleaseDependency(String groupId, String artifactId, String version, String classifier) {
        this(groupId, artifactId, version, classifier, null, "md5");
    }

    public ReleaseDependency(String groupId, String artifactId, String version, String classifier, String hash, String hashingAlgorithm) {
        super(groupId, artifactId, version, classifier, hash, hashingAlgorithm, DependencyScope.COMPILE);
    }

    public ReleaseDependency(String groupId, String artifactId, String version, String classifier, String hash, String hashingAlgorithm, DependencyScope scope) {
        super(groupId, artifactId, version, classifier, hash, hashingAlgorithm, scope);
    }

    @Override
    public PomMavenObject getPom() {
        return new ReleasePom(getGroupId(), getArtifactId(), getVersion(), getClassifier(), getHash(), getHashingAlgorithm());
    }

    @Override
    public @Nullable String getSnapshotVersion() {
        return null;
    }

    @Override
    public boolean isSnapshot() {
        return false;
    }
}
