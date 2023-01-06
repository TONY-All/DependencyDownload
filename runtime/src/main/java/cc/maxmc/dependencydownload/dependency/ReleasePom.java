package cc.maxmc.dependencydownload.dependency;

import org.jetbrains.annotations.Nullable;

public class ReleasePom extends PomMavenObject {
    public ReleasePom(String mavenDependency) {
        super(mavenDependency);
    }

    public ReleasePom(String mavenDependency, String hash, String hashingAlgorithm) {
        super(mavenDependency, hash, hashingAlgorithm);
    }

    public ReleasePom(String groupId, String artifactId, String version, String classifier) {
        super(groupId, artifactId, version, classifier);
    }

    public ReleasePom(String groupId, String artifactId, String version, String classifier, String hash, String hashingAlgorithm) {
        super(groupId, artifactId, version, classifier, hash, hashingAlgorithm);
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
