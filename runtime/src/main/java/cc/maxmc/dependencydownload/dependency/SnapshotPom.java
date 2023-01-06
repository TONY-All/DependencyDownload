package cc.maxmc.dependencydownload.dependency;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SnapshotPom extends PomMavenObject {
    private final String snapshotVersion;

    public SnapshotPom(String mavenDependency, String snapshotVersion) {
        super(mavenDependency);
        this.snapshotVersion = snapshotVersion;
    }

    public SnapshotPom(String mavenDependency, String hash, String snapshotVersion, String hashingAlgorithm) {
        super(mavenDependency, hash, hashingAlgorithm);
        this.snapshotVersion = snapshotVersion;
    }

    public SnapshotPom(String groupId, String artifactId, String version, String snapshotVersion, String classifier) {
        super(groupId, artifactId, version, classifier);
        this.snapshotVersion = snapshotVersion;
    }

    public SnapshotPom(String groupId, String artifactId, String version, String snapshotVersion, String classifier, String hash, String hashingAlgorithm) {
        super(groupId, artifactId, version, classifier, hash, hashingAlgorithm);
        this.snapshotVersion = snapshotVersion;
    }

    @Override
    public String getSnapshotVersion() {
        return snapshotVersion;
    }

    @Override
    public boolean isSnapshot() {
        return true;
    }

    @Override
    public @NotNull String getFileName() {
        String classifier = getClassifier();
        return getArtifactId() + '-' + getSnapshotVersion() + (classifier != null ? '-' + classifier : "") + "." + getType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SnapshotPom that = (SnapshotPom) o;
        return Objects.equals(snapshotVersion, that.snapshotVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), snapshotVersion);
    }
}
