package cc.maxmc.dependencydownload.dependency;

import org.jetbrains.annotations.Nullable;

public class ReleaseDependency extends JarMavenObject {
    public ReleaseDependency(String mavenDependency) {
        super(mavenDependency);
    }

    public ReleaseDependency(String mavenDependency, String hash, String hashingAlgorithm) {
        super(mavenDependency, hash, hashingAlgorithm);
    }

    public ReleaseDependency(String groupId, String artifactId, String version, String classifier) {
        super(groupId, artifactId, version, classifier);
    }

    public ReleaseDependency(String groupId, String artifactId, String version, String classifier, String hash, String hashingAlgorithm) {
        super(groupId, artifactId, version, classifier, hash, hashingAlgorithm);
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
