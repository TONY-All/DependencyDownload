package cc.maxmc.dependencydownload.relocation;

import java.nio.file.Path;
import java.util.Set;

public interface IRelocationProvider {
    void run(Path from, Path to, Set<Relocation> relocations);
}
