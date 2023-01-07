package cc.maxmc.dependencydownload.pom;

import cc.maxmc.dependencydownload.common.util.XMLUtils;
import cc.maxmc.dependencydownload.dependency.JarMavenObject;
import cc.maxmc.dependencydownload.dependency.ReleaseDependency;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PomParser {
    private final Document pom;

    public PomParser(Document pom) {
        this.pom = pom;
    }

    @NotNull
    public List<JarMavenObject> getDependencies() {
        // get Project node
        NodeList childNodes = XMLUtils.getNode(pom.getChildNodes(), "project").getChildNodes();
        // get dependencies node
        Node dependencies = XMLUtils.getNode(childNodes, "dependencies");
        if (dependencies == null) return Collections.emptyList();
        ArrayList<JarMavenObject> result = new ArrayList<>();
        XMLUtils.forEach(dependencies.getChildNodes(), (dependency) -> {
            if (dependency.getNodeName().equals("#text")) return;
            result.add(parseDependency(dependency));
        });
        return result;
    }

    public JarMavenObject parseDependency(Node dependency) {
        final AtomicReference<String> groupId = new AtomicReference<>(null);
        final AtomicReference<String> artifactId = new AtomicReference<>(null);
        final AtomicReference<String> version = new AtomicReference<>(null);
        final AtomicReference<String> scope = new AtomicReference<>(null);
        XMLUtils.forEach(dependency.getChildNodes(), (node) -> {
            if (node.getNodeName().equals("#text")) return;
            switch (node.getNodeName()) {
                case "groupId":
                    groupId.set(node.getTextContent());
                    break;
                case "artifactId":
                    artifactId.set(node.getTextContent());
                    break;
                case "version":
                    version.set(node.getTextContent());
                    break;
                case "scope":
                    scope.set(node.getTextContent());
                    break;
            }
        });
        if (scope.get() == null) {
            return new ReleaseDependency(groupId.get(), artifactId.get(), version.get(), null);
        } else {
            return new ReleaseDependency(groupId.get(), artifactId.get(), version.get(), null, null, "md5", DependencyScope.valueOf(scope.get().toUpperCase()));
        }
    }
}
