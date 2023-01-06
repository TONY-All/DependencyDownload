package cc.maxmc.dependencydownload.path;

import cc.maxmc.dependencydownload.downloader.FileDownloader;
import cc.maxmc.dependencydownload.DependencyManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * An interface extending {@link DependencyPathProvider} to provide a cleanup path for the {@link DependencyManager}.
 * {@link DependencyManager#cleanupCacheDirectory()} requires the use of this interface in {@link DependencyManager#DependencyManager(DependencyPathProvider, FileDownloader)}  DependencyManager}.
 */
public interface CleanupPathProvider extends DependencyPathProvider {

    /**
     * Gets the path that should be used for removing unused files using {@link DependencyManager#cleanupCacheDirectory}.
     *
     * @return The absolute or relative path use for cleanup directory (should be the directory where the dependencies are stored)
     */
    @NotNull
    Path getCleanupPath();

}
