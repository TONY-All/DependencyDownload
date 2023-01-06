package cc.maxmc.dependencydownload.classloader;

import cc.maxmc.dependencydownload.DependencyManager;
import cc.maxmc.dependencydownload.classpath.ClasspathAppender;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 * Utility {@link ClassLoader} to load classes onto a separate classpath as the main application.
 * Extends {@link ClasspathAppender} for use with {@link DependencyManager}.
 */
@SuppressWarnings("unused") // API
public class IsolatedClassLoader extends URLClassLoader implements ClasspathAppender {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public IsolatedClassLoader() {
        super(new URL[0], ClassLoader.getSystemClassLoader().getParent());
    }

    @Override
    public void appendFileToClasspath(@NotNull Path path) throws MalformedURLException {
        addURL(path.toUri().toURL());
    }
}
