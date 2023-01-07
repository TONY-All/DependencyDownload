package cc.maxmc.dependencydownload.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A maven dependency.
 */
@SuppressWarnings("unused") // API
public interface MavenObject {

    String MAVEN_PATH_FORMAT = "%s/%s/%s/%s";

    /**
     * The type of the maven object.
     *
     * @return the type of the maven object in lower case
     */
    @NotNull String getType();

    /**
     * The group id of the dependency.
     *
     * @return the dependency's group id
     */
    @NotNull String getGroupId();

    /**
     * The artifact id of the dependency.
     *
     * @return the dependency's artifact id
     */
    @NotNull String getArtifactId();

    /**
     * The version of the dependency.
     *
     * @return the dependency's version
     */
    @NotNull String getVersion();

    /**
     * The classifier for the dependency artifact, if any.
     *
     * @return the dependency artifact's classifier or {@code null}.
     */
    @Nullable String getClassifier();

    /**
     * The timestamped snapshot version.
     *
     * @return the timestamped snapshot version or {@code null} if this isn't a snapshot dependency.
     * @see #isSnapshot()
     */
    @Nullable String getSnapshotVersion();

    /**
     * The hash of the dependency, this is checked against the downloaded file.
     *
     * @return the hash of the dependency archive
     * @see #getHashingAlgorithm()
     */
    @Nullable String getHash();

    /**
     * The hashing algorithm used for the {@link #getHash()}.
     *
     * @return the hashing algorithm used for the dependency archive's hash
     * @see #getHash()
     */
    @NotNull String getHashingAlgorithm();

    /**
     * If this is a snapshot dependency.
     *
     * @return true if this dependency is a snapshot
     */
    boolean isSnapshot();

    /**
     * Returns the file name for the end of the maven path.
     *
     * @return the file name for the dependency
     */
    @NotNull
    default String getFileName() {
        String classifier = getClassifier();
        return getArtifactId() + '-' + getVersion() + (classifier != null ? '-' + classifier : "") + "." + getType();
    }

    /**
     * Returns the hash file name for the dependency jar.
     *
     * @return the hash file name for the dependency jar
     */
    @NotNull
    default String getHashFileName() {
        return getFileName() + "." + getHashingAlgorithm().toLowerCase();
    }

    /**
     * Returns the file name when stored to disk.
     *
     * @return the file name for storing the dependency
     */
    @NotNull
    default String getStoredFileName() {
//        String classifier = getClassifier();
//        return getGroupId() + '-' + getArtifactId() + '-' + getVersion() + (classifier != null ? '-' + classifier : "") + ".jar";
        return getFileName();
    }

    /**
     * The path to this dependency on a maven repository, without the protocol, domain or slash at the beginning.
     *
     * @return the path to this dependency's jar file on a maven repository
     */
    @NotNull
    default String getMavenPath() {
        return String.format(MAVEN_PATH_FORMAT, getGroupId().replace('.', '/'), getArtifactId(), getVersion(), getFileName());
    }

    /**
     * The path to this dependency's hash on a maven repository, without the protocol, domain or slash at the beginning.
     *
     * @return the path to this dependency's hash file on a maven repository
     */
    @NotNull
    default String getHashMavenPath() {
        return String.format(MAVEN_PATH_FORMAT, getGroupId().replace('.', '/'), getArtifactId(), getVersion(), getHashFileName());
    }

    /**
     * Gets the group id, artifact id, version and classifier (if specified) seperated by semicolons.
     *
     * @return the maven artifact's GAV and classifier parameters seperated by semicolons (<code>:</code>)
     */
    @NotNull
    default String getMavenArtifact() {
        String classifier = getClassifier();
        return getGroupId() + ":" + getArtifactId() + ":" + getVersion() + (classifier != null ? ":" + classifier : "");
    }
}
