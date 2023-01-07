package cc.maxmc.dependencydownload.common.util;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class XMLUtils {

    public static void forEach(NodeList nodeList, Consumer<Node> iterator) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            iterator.accept(nodeList.item(i));
        }
    }

    @Nullable
    public static Node getNode(NodeList nodeList, String name) {
        AtomicReference<Node> ret = new AtomicReference<>(null);
        forEach(nodeList, (node) -> {
            if (node.getNodeName().equals(name)) {
                ret.set(node);
            }
        });
        return ret.get();
    }
}
