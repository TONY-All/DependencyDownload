package cc.maxmc.dependencydownload.path;

import cc.maxmc.dependencydownload.DependencyManager;
import cc.maxmc.dependencydownload.dependency.MavenObject;
import cc.maxmc.dependencydownload.downloader.FileDownloader;
import cc.maxmc.dependencydownload.relocation.Relocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Set;

/**
 * Default dependency path provider, automatically used when using the {@link DependencyManager#DependencyManager(Path, FileDownloader)} constructor.
 */
public class DirectoryDependencyPathProvider implements CleanupPathProvider {

    private final Path cacheDirectory;

    /**
     * Creates a {@link DirectoryDependencyPathProvider}.
     *
     * @param cacheDirectory the directory used for downloaded and relocated dependencies.
     */
    public DirectoryDependencyPathProvider(Path cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    @Override
    public @NotNull Path getCleanupPath() {
        return cacheDirectory;
    }

    @Override
    public @NotNull Path getDependencyPath(@NotNull MavenObject mavenObject, Set<Relocation> relocations) {
        Path path = cacheDirectory.resolve(mavenObject.getGroupId()).resolve(mavenObject.getArtifactId()).resolve(mavenObject.getVersion());
        if (relocations.isEmpty()) {
            return path.resolve(mavenObject.getStoredFileName());
        } else {
            return path.resolve(relocations.hashCode() + "-" + mavenObject.getStoredFileName());
        }
    }

    @Override
    public @NotNull Path getDependencyHashPath(@NotNull MavenObject mavenObject) {
        Path path = cacheDirectory.resolve(mavenObject.getGroupId()).resolve(mavenObject.getArtifactId()).resolve(mavenObject.getVersion());
        return path.resolve(mavenObject.getHashFileName());
    }
}
