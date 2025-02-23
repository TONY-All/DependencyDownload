package cc.maxmc.dependencydownload;

import cc.maxmc.dependencydownload.classpath.ClasspathAppender;
import cc.maxmc.dependencydownload.common.util.HashUtils;
import cc.maxmc.dependencydownload.dependency.JarMavenObject;
import cc.maxmc.dependencydownload.dependency.PomMavenObject;
import cc.maxmc.dependencydownload.downloader.FileDownloader;
import cc.maxmc.dependencydownload.path.CleanupPathProvider;
import cc.maxmc.dependencydownload.path.DependencyPathProvider;
import cc.maxmc.dependencydownload.path.DirectoryDependencyPathProvider;
import cc.maxmc.dependencydownload.pom.PomManager;
import cc.maxmc.dependencydownload.pom.PomParser;
import cc.maxmc.dependencydownload.relocation.DefaultRelocationProvider;
import cc.maxmc.dependencydownload.relocation.IRelocationProvider;
import cc.maxmc.dependencydownload.relocation.Relocation;
import cc.maxmc.dependencydownload.repository.Repository;
import cc.maxmc.dependencydownload.resource.DependencyDownloadResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The main class responsible for downloading, optionally relocating and loading in dependencies.
 */
@SuppressWarnings("unused") // API
public class DependencyManager {

    private final DependencyPathProvider dependencyPathProvider;
    private final FileDownloader downloader;
    private final Executor executor;

    private final List<JarMavenObject> dependencies = new CopyOnWriteArrayList<>();
    private final Set<Relocation> relocations = new CopyOnWriteArraySet<>();
    private final AtomicInteger step = new AtomicInteger(0);

    /**
     * Creates a {@link DependencyManager}, uses the {@link DirectoryDependencyPathProvider}.
     *
     * @param cacheDirectory the directory used for downloaded and relocated dependencies.
     * @param downloader     the downloader used for downloading files
     * @param executor       the executor to execute the task
     * @see DirectoryDependencyPathProvider
     */
    public DependencyManager(@NotNull Path cacheDirectory, @NotNull FileDownloader downloader, Executor executor) {
        this(new DirectoryDependencyPathProvider(cacheDirectory), downloader, executor);
    }

    /**
     * Creates a {@link DependencyManager}.
     *
     * @param dependencyPathProvider the dependencyPathProvider used for downloaded and relocated dependencies
     * @param downloader             the downloader used for downloading files
     * @param executor               the executor to execute the task
     */
    public DependencyManager(@NotNull DependencyPathProvider dependencyPathProvider, @NotNull FileDownloader downloader, Executor executor) {
        this.dependencyPathProvider = dependencyPathProvider;
        this.downloader = downloader;
        this.executor = executor;
    }

    /**
     * Adds a dependency to this {@link DependencyManager}.
     *
     * @param mavenObject the dependency to add
     * @throws IllegalStateException if this method is executed after downloading
     * @see #addDependencies(Collection)
     */
    public void addDependency(@NotNull JarMavenObject mavenObject) {
        addDependencies(Collections.singleton(mavenObject));
    }

    /**
     * Adds dependencies to this {@link DependencyManager}.
     *
     * @param dependencies the dependencies to add
     * @throws IllegalStateException if this method is executed after downloading
     * @see #addDependency(JarMavenObject)
     */
    public void addDependencies(@NotNull Collection<JarMavenObject> dependencies) {
        if (step.get() > 0) {
            throw new IllegalStateException("Cannot add dependencies after downloading");
        }
        this.dependencies.addAll(dependencies);
    }

    /**
     * Gets the dependencies in this {@link DependencyManager}.
     *
     * @return a unmodifiable list of dependencies
     */
    @NotNull
    public List<JarMavenObject> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    /**
     * Adds a relocation to this {@link DependencyManager}.
     *
     * @param relocation the relocation to add
     * @throws IllegalStateException if this method is executed after relocating
     * @see #addRelocations(Collection)
     */
    public void addRelocation(@NotNull Relocation relocation) {
        addRelocations(Collections.singleton(relocation));
    }

    /**
     * Adds relocations to this {@link DependencyManager}.
     *
     * @param relocations the relocations to add
     * @throws IllegalStateException if this method is executed after relocating
     * @see #addRelocation(Relocation)
     */
    public void addRelocations(@NotNull Collection<Relocation> relocations) {
        if (step.get() > 2) {
            throw new IllegalStateException("Cannot add relocations after relocating");
        }
        this.relocations.addAll(relocations);
    }

    /**
     * Gets the relocations in this {@link DependencyManager}.
     *
     * @return an unmodifiable set of relocations
     */
    @NotNull
    public Set<Relocation> getRelocations() {
        return Collections.unmodifiableSet(relocations);
    }

    /**
     * Gets the dependency path provider for this {@link DependencyManager}.
     *
     * @return the instance of {@link DependencyPathProvider} or {@code null}
     * @see DependencyPathProvider
     */
    @NotNull
    public DependencyPathProvider getDependencyPathProvider() {
        return dependencyPathProvider;
    }

    /**
     * Gets the file downloader for this {@link DependencyManager}.
     *
     * @return the instance of {@link FileDownloader} or {@code null}
     * @see FileDownloader
     */
    @NotNull
    public FileDownloader getDownloader() {
        return downloader;
    }

    /**
     * Loads dependencies and relocations from the resource generated by the gradle plugin.
     *
     * @param resourceURL the url to the resource
     * @throws IOException if the resource cannot be read
     */
    public void loadFromResource(@NotNull URL resourceURL) throws IOException {
        DependencyDownloadResource resource = new DependencyDownloadResource(resourceURL);
        loadFromResource(resource);
    }

    /**
     * Loads dependencies and relocations from the resource generated by the gradle plugin.
     *
     * @param fileContents the contents of the file generated by the gradle plugin as a {@link String}
     */
    public void loadFromResource(@NotNull String fileContents) {
        DependencyDownloadResource resource = new DependencyDownloadResource(fileContents);
        loadFromResource(resource);
    }

    /**
     * Loads dependencies and relocations from the resource generated by the gradle plugin.
     *
     * @param fileLines all the lines from the file generated by the gradle plugin
     */
    public void loadFromResource(@NotNull List<String> fileLines) {
        DependencyDownloadResource resource = new DependencyDownloadResource(fileLines);
        loadFromResource(resource);
    }

    /**
     * Loads dependencies and relocations from the resource provided.
     *
     * @param resource the resource
     */
    public void loadFromResource(@NotNull DependencyDownloadResource resource) {
        dependencies.addAll(resource.getDependencies());
        relocations.addAll(resource.getRelocations());
    }

    /**
     * Download all the dependencies in this {@link DependencyManager}.
     *
     * @param repositories an ordered list of repositories that will be tried one-by-one in order
     * @return a future that will complete exceptionally if a single dependency fails to download from all repositories,
     * otherwise completes when all dependencies are downloaded
     * @throws IllegalStateException if dependencies have already been queued for download once
     */
    public CompletableFuture<Void> downloadAll(@NotNull List<Repository> repositories) {
        return CompletableFuture.allOf(download(repositories));
    }

    /**
     * Download all the dependencies in this {@link DependencyManager}.
     * If one of the downloads fails, the rest will not be tried and will not get {@link CompletableFuture}s.
     *
     * @param repositories an ordered list of repositories that will be tried one-by-one, in order
     * @return an array containing a {@link CompletableFuture} for at least one dependency but up to one for each dependency
     * @throws IllegalStateException if dependencies have already been queued for download once
     */
    public CompletableFuture<Void>[] download(@NotNull List<Repository> repositories) {
        if (!step.compareAndSet(0, 1)) {
            throw new IllegalStateException("Download has already been executed");
        }
        return forEachDependency(dependency -> downloadDependency(dependency, repositories), (dependency, cause) -> new RuntimeException("Failed to download dependency " + dependency.getMavenArtifact(), cause));
    }

    /**
     * Relocates all the dependencies with the relocations in this {@link DependencyManager}. This step is not required.
     * Uses the {@link ClassLoader} that loaded this class to acquire {@code jar-relocator}.
     *
     * @return a future that will complete exceptionally if any of the dependencies fail to
     * relocate otherwise completes when all dependencies are relocated
     * @throws IllegalStateException if dependencies have already been queued for relocation once
     * @see #relocateAll(IRelocationProvider)
     * @see #relocate(IRelocationProvider)
     */
    public CompletableFuture<Void> relocateAll() {
        return CompletableFuture.allOf(relocate(new DefaultRelocationProvider(getClass().getClassLoader())));
    }

    /**
     * Relocates all the dependencies with the relocations in this {@link DependencyManager}. This step is not required.
     * Uses the {@link ClassLoader} that loaded this class to acquire {@code jar-relocator}.
     *
     * @param provider the relocation provider
     * @return a future that will complete exceptionally if any of the dependencies fail to
     * relocate otherwise completes when all dependencies are relocated
     * @throws IllegalStateException if dependencies have already been queued for relocation once
     * @see #relocateAll()
     * @see #relocate(IRelocationProvider)
     */
    public CompletableFuture<Void> relocateAll(IRelocationProvider provider) {
        return CompletableFuture.allOf(relocate(provider));
    }

    /**
     * Relocates all the dependencies with the relocations in this {@link DependencyManager}. This step is not required.
     * If one of the relocation fails, the rest will not be tried and will not get {@link CompletableFuture}s.
     *
     * @param provider the relocation helper to use
     * @return an array containing a {@link CompletableFuture} for at least one dependency but up to one for each dependency
     * @throws IllegalStateException if dependencies have already been queued for relocation once
     * @see #relocateAll(IRelocationProvider)
     */
    public CompletableFuture<Void>[] relocate(@Nullable IRelocationProvider provider) {
        int currentStep = step.get();
        if (currentStep == 0) {
            throw new IllegalArgumentException("Download hasn't been executed");
        } else if (currentStep != 1) {
            throw new IllegalArgumentException("Relocate has already been executed");
        }
        step.set(2);

        return forEachDependency(dependency -> relocateDependency(dependency, provider), (dependency, cause) -> new RuntimeException("Failed to relocate dependency " + dependency.getMavenArtifact(), cause));
    }

    /**
     * Loads all the (potentially relocated) dependencies with provided {@link ClasspathAppender}.
     *
     * @param classpathAppender the classpath appender
     * @return a future that will complete exceptionally if any of the dependencies fail to
     * be appended by the provided {@link ClasspathAppender} otherwise completes when all dependencies are relocated
     * @throws IllegalStateException if dependencies have already been queued for load once
     */
    public CompletableFuture<Void> loadAll(@NotNull ClasspathAppender classpathAppender) {
        return CompletableFuture.allOf(load(classpathAppender));
    }

    public CompletableFuture<Void> loadTransitives(@NotNull List<Repository> repositories) {
        return CompletableFuture.allOf(dependencies.stream().map((dep) -> loadTransitive(dep, repositories)).toArray(CompletableFuture[]::new));
    }

    public CompletableFuture<Void> loadTransitive(@NotNull JarMavenObject dependency, @NotNull List<Repository> repositories) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        executor.execute(() -> {
            for (Repository repository : repositories) {
                try {
                    PomMavenObject pom = dependency.getPom();
                    PomManager pomManager = new PomManager(repository, pom, this);
                    Path dependencyPath = dependencyPathProvider.getDependencyPath(pom, Collections.emptySet());
                    if (!dependencyPath.toFile().exists()) {
                        pomManager.downloadPom();
                    }
                    pomManager.checkPomHash();
                    PomParser parse = pomManager.parse();

                    List<JarMavenObject> childDependencies = parse.getDependencies();
                    if (childDependencies.isEmpty()) {
                        future.complete(null);
                        return;
                    }
                    addDependencies(childDependencies);

                    CompletableFuture<?>[] completableFutures = childDependencies.stream().map((dep) -> loadTransitive(dep, repositories)).toArray(CompletableFuture[]::new);
                    CompletableFuture<Void> futures = CompletableFuture.allOf(completableFutures);

                    futures.whenCompleteAsync((comp, thr) -> future.complete(null));

                } catch (Exception e) {
                    throw new RuntimeException("Failed to download and check pom", e);
                }
            }
        });
        return future;
    }

    /**
     * Loads all the (potentially relocated) dependencies with provided {@link ClasspathAppender}.
     * If one of the loads fails, the rest will not be tried and will not get {@link CompletableFuture}s.
     *
     * @param classpathAppender the classpath appender
     * @return an array containing a {@link CompletableFuture} for at least one dependency but up to one for each dependency
     * @throws IllegalStateException if dependencies have already been queued for load once
     */
    public CompletableFuture<Void>[] load(@NotNull ClasspathAppender classpathAppender) {
        int currentStep = step.get();
        if (currentStep == 0) {
            throw new IllegalArgumentException("Download hasn't been executed");
        }
        step.set(3);

        return forEachDependency(dependency -> loadDependency(dependency, classpathAppender, currentStep == 2), (dependency, cause) -> new RuntimeException("Failed to load dependency " + dependency.getMavenArtifact(), cause));
    }

    /**
     * Gets the {@link Path} where the given {@link JarMavenObject} will be stored once downloaded.
     *
     * @param mavenObject the dependency.
     * @param relocated   if the path should be for the relocated or unrelocated file of the Dependency
     * @return the path for the dependency
     */
    @NotNull
    public Path getPathForDependency(@NotNull JarMavenObject mavenObject, boolean relocated) {
        if (relocated) {
            return getDependencyPathProvider().getDependencyPath(mavenObject, relocations);
        } else {
            return getDependencyPathProvider().getDependencyPath(mavenObject, Collections.emptySet());
        }
    }

    /**
     * Gets {@link Path}s to all {@link JarMavenObject Dependencies} in this {@link DependencyManager},
     * optionally also including the relocated paths if {@code includeRelocated} is set to {@code true}.
     *
     * @param includeRelocated if relocated paths should also be included
     * @return paths to all dependencies (and optionally relocated dependencies)
     * @see #getPathForDependency(JarMavenObject, boolean)
     */
    @NotNull
    public Set<Path> getAllPaths(boolean includeRelocated) {
        Set<Path> paths = new HashSet<>();
        for (JarMavenObject mavenObject : dependencies) {
            paths.add(getPathForDependency(mavenObject, false));
            if (includeRelocated) {
                paths.add(getPathForDependency(mavenObject, true));
            }
        }
        return paths;
    }

    /**
     * Removes files that are not known dependencies of this {@link DependencyManager} from {@link CleanupPathProvider#getCleanupPath()} implementation.
     * <b>
     * This only accounts for dependencies that are included in this {@link DependencyManager} instance!
     * </b>
     *
     * @throws IOException           if listing files in the cache directory or deleting files in it fails
     * @throws IllegalStateException if this DependencyManager's dependencyPathProvider isn't an instance of {@link CleanupPathProvider}
     * @see #getAllPaths(boolean)
     * @see CleanupPathProvider
     */
    public void cleanupCacheDirectory() throws IOException, IllegalStateException {
        if (!(dependencyPathProvider instanceof CleanupPathProvider)) {
            throw new IllegalStateException("Cache directory cleanup is only available when dependencyPathProvider is a instance of CleanupPathProvider");
        }
        Path cacheDirectory = ((CleanupPathProvider) dependencyPathProvider).getCleanupPath();
        Set<Path> paths = getAllPaths(true);
        Set<Path> filesToDelete;
        try (Stream<Path> stream = Files.list(cacheDirectory)) {
            filesToDelete = stream
                    // Ignore directories
                    .filter(path -> !Files.isDirectory(path))
                    // Ignore files in this DependencyManager
                    .filter(path -> !paths.contains(path)).collect(Collectors.toSet());
        }

        for (Path path : filesToDelete) {
            Files.delete(path);
        }
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<Void>[] forEachDependency(ExceptionalConsumer<JarMavenObject> runnable, BiFunction<JarMavenObject, Throwable, Throwable> dependencyException) {
        int size = dependencies.size();
        CompletableFuture<Void>[] futures = new CompletableFuture[size];

        for (int index = 0; index < size; index++) {
            JarMavenObject mavenObject = dependencies.get(index);

            CompletableFuture<Void> future = new CompletableFuture<>();
            Runnable run = () -> {
                try {
                    runnable.run(mavenObject);
                    future.complete(null);
                } catch (Throwable t) {
                    future.completeExceptionally(dependencyException.apply(mavenObject, t));
                }
            };

            if (executor != null) {
                executor.execute(run);
            } else {
                run.run();
            }

            futures[index] = future;
            if (future.isCompletedExceptionally()) {
                // don't need to bother with the rest if one fails
                break;
            }
        }

        return futures;
    }

    private void downloadDependency(JarMavenObject jarDependency, List<Repository> repositories) throws IOException, NoSuchAlgorithmException {
        Path dependencyPath = getPathForDependency(jarDependency, false);
        Path dependencyHashPath = dependencyPathProvider.getDependencyHashPath(jarDependency);

        if (!Files.exists(dependencyPath.getParent())) {
            Files.createDirectories(dependencyPath.getParent());
        }

        if (Files.exists(dependencyPath) && Files.exists(dependencyHashPath)) {
            if (checkDependencyHash(jarDependency, null)) {
                // This dependency is already downloaded & the hash matches
                return;
            } else {
                Files.delete(dependencyPath);
            }
        }
        Files.deleteIfExists(dependencyPath);
        Files.createFile(dependencyPath);

        RuntimeException failure = new RuntimeException("All provided repositories failed to download dependency");
        for (Repository repository : repositories) {
            try {
                MessageDigest digest = MessageDigest.getInstance(jarDependency.getHashingAlgorithm());
                downloadFromRepository(jarDependency, repository, dependencyPath, digest);

                if (!checkDependencyHash(jarDependency, repository)) {
                    throw new RuntimeException("Failed to verify dependency (" + jarDependency + ")'s hash");
                }

                // Success
                return;
            } catch (Throwable e) {
                Files.deleteIfExists(dependencyPath);
                failure.addSuppressed(e);
            }
        }
        throw failure;
    }

    private void downloadFromRepository(JarMavenObject mavenObject, Repository repository, Path dependencyPath, MessageDigest digest) {
        try {
            downloader.downloadFile(repository.createURL(mavenObject), dependencyPath);
            byte[] buffer = new byte[4096];
            int length;
            try (FileInputStream fileInputStream = new FileInputStream(dependencyPath.toFile())) {
                while ((length = fileInputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, length);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to download dependency " + mavenObject + " from " + repository);
        }
    }

    private String downloadHashFromRepository(JarMavenObject mavenObject, Repository repository, Path path) {
        try {
            downloader.downloadFile(repository.createHashURL(mavenObject), path);
            return HashUtils.readFile(path);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to download dependency " + mavenObject + " from " + repository);
        }
    }

    private boolean checkDependencyHash(JarMavenObject mavenObject, Repository repo) throws NoSuchAlgorithmException, IOException {
        String fileHash = HashUtils.getFileHash(getPathForDependency(mavenObject, false), mavenObject.getHashingAlgorithm());
        if (mavenObject.getHash() != null) {
            return fileHash.equals(mavenObject.getHash());
        } else {
            Path hashPath = Paths.get(mavenObject.getHashFileName());
            if (hashPath.toFile().exists()) {
                return fileHash.equals(HashUtils.readFile(hashPath));
            }
            if (repo == null)
                throw new IllegalArgumentException("Hash file should be downloaded, but repository is null");
            String hash = downloadHashFromRepository(mavenObject, repo, dependencyPathProvider.getDependencyHashPath(mavenObject));
            return fileHash.equals(hash);
        }
    }

    private void relocateDependency(JarMavenObject mavenObject, IRelocationProvider helper) {

        Path dependencyFile = getPathForDependency(mavenObject, false);
        Path relocatedFile = getPathForDependency(mavenObject, true);

        if (!relocatedFile.toFile().exists()) {
            helper.run(dependencyFile, relocatedFile, relocations);
        }
    }

    private void loadDependency(JarMavenObject mavenObject, ClasspathAppender classpathAppender, boolean relocated) throws MalformedURLException {
        Path fileToLoad = relocated ? getPathForDependency(mavenObject, true) : getPathForDependency(mavenObject, false);

        classpathAppender.appendFileToClasspath(fileToLoad);
    }

    /**
     * Helper class to provide a Consumer that throws {@link Throwable}.
     *
     * @param <T> the consumable's type
     */
    @FunctionalInterface
    private interface ExceptionalConsumer<T> {

        void run(T t) throws Throwable;
    }
}
