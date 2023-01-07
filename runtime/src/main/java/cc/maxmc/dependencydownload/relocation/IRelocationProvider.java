package cc.maxmc.dependencydownload.relocation;

import java.nio.file.Path;
import java.util.Set;

public interface IRelocationProvider {

    /**
     * relocate a jar file with give {@link Relocation}s.
     *
     * @param from        where is the jar file
     * @param to          where will the relocated jar output
     * @param relocations relocation to relocate the jar
     */
    void run(Path from, Path to, Set<Relocation> relocations);
}
