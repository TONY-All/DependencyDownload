package cc.maxmc.dependencydownload.path;

import cc.maxmc.dependencydownload.dependency.MavenObject;
import cc.maxmc.dependencydownload.relocation.Relocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Set;

/**
 * A {@link Path} provider for {@link MavenObject Dependencies}.
 */
public interface DependencyPathProvider {

    /**
     * Gets the path that should be used for the provided {@link MavenObject}.
     *
     * @param mavenObject  the dependency
     * @param relocations the relocations
     * @return The absolute or relative path for the provided dependency
     */
    @NotNull
    Path getDependencyPath(@NotNull MavenObject mavenObject, Set<Relocation> relocations);

    /**
     * Gets the path that should be used for the provided {@link MavenObject}.
     *
     * @param mavenObject the dependency
     * @return The absolute or relative path for the provided dependency
     */
    @NotNull
    Path getDependencyHashPath(@NotNull MavenObject mavenObject);
}
