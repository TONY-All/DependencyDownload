package cc.maxmc.dependencydownload.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class BaseMavenObject implements MavenObject {

    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String classifier;
    private final String hash;
    private final String hashingAlgorithm;


    public BaseMavenObject(String mavenDependency, String hash, String hashingAlgorithm) {
        String[] split = mavenDependency.split(":");
        try {
            if (split.length == 4) {
                this.groupId = split[0];
                this.artifactId = split[1];
                this.version = split[2];
                this.classifier = split[3];
                this.hash = hash;
                this.hashingAlgorithm = hashingAlgorithm;
            } else {
                this.groupId = split[0];
                this.artifactId = split[1];
                this.version = split[2];
                this.classifier = null;
                this.hash = hash;
                this.hashingAlgorithm = hashingAlgorithm;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Maven dependency " + mavenDependency + " is not illegal.");
        }
    }

    public BaseMavenObject(String groupId, String artifactId, String version, String classifier, String hash, String hashingAlgorithm) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.classifier = classifier;
        this.hash = hash;
        this.hashingAlgorithm = hashingAlgorithm;
    }

    @Override
    public @NotNull String getGroupId() {
        return groupId;
    }

    @Override
    public @NotNull String getArtifactId() {
        return artifactId;
    }

    @Override
    public @NotNull String getVersion() {
        return version;
    }

    @Override
    public String getClassifier() {
        return classifier;
    }

    @Override
    public @Nullable String getHash() {
        return hash;
    }

    @Override
    public @NotNull String getHashingAlgorithm() {
        return hashingAlgorithm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseMavenObject that = (BaseMavenObject) o;
        return Objects.equals(groupId, that.groupId) && Objects.equals(artifactId, that.artifactId) && Objects.equals(version, that.version) && Objects.equals(classifier, that.classifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version, classifier);
    }
}
