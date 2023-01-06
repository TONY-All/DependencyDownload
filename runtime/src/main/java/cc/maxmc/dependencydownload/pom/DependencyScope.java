package cc.maxmc.dependencydownload.pom;

/**
 * The scope of a dependency
 *
 * @author Zach Deibert
 * @since 1.0.0
 */
public enum DependencyScope {

    /**
     * The dependency is needed when the code is being compiled, so it will be
     * downloaded while resolving dependencies at runtime.
     *
     * @since 1.0.0
     */
    COMPILE(true),

    /**
     * The dependency is provided by the runtime environment, so it does not
     * need to be downloaded while resolving dependencies at runtime.
     *
     * @since 1.0.0
     */
    PROVIDED(false),

    /**
     * The dependency is needed when the application is running, so it will be
     * downloaded while resolving dependencies at runtime.
     *
     * @since 1.0.0
     */
    RUNTIME(true),

    /**
     * The dependency is needed for compiling and running the unit tests, so it
     * does not need to be downloaded while resolving dependencies at runtime.
     *
     * @since 1.0.0
     */
    TEST(false),

    /**
     * The dependency should be on the system already, so it does not need to be
     * downloaded while resolving dependencies at runtime.
     *
     * @since 1.0.0
     */
    SYSTEM(false),

    /**
     * The dependency is actually just a pom and not a jar, so we do not need to
     * download it at all.
     *
     * @since 1.0.0
     */
    IMPORT(false);

    private final boolean download;

    DependencyScope(boolean download) {
        this.download = download;
    }

    public boolean isDownload() {
        return download;
    }
}
