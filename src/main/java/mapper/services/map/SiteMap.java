package mapper.services.map;

import lombok.Getter;
import mapper.model.Link;
import mapper.model.LinkPartNode;
import mapper.model.MultiChildSynchronizedTreeNode;

import java.util.List;

public class SiteMap {

    @Getter
    private LinkPartNode mainNode;

    @Getter
    private String siteName;

    public void initialize(String mainPart, List<String> linkParts) {
        if (linkParts.isEmpty()) {
            throw new IllegalArgumentException("To initialize the map's main node, linkParts must not be empty");
        }

        mainNode = new LinkPartNode(mainPart);
        siteName = linkParts.getFirst();
        initializeNodes(linkParts);
    }

    private void initializeNodes(List<String> linkParts) {
        var current = mainNode;
        for (int i = 1; i < linkParts.size(); i++) {
            var newNode = new LinkPartNode(linkParts.get(i));
            current.addChild(newNode);
            current = newNode;
        }
        current.setPage(true);
    }

    public void addLink(Link link) {
        var parts = link.getParts();
        if (parts.isEmpty()) {
            return;
        }

        MultiChildSynchronizedTreeNode<String> currentNode = mainNode;
        for (int i = 0; i < parts.size(); i++) {
            var child = currentNode.getByValue(parts.get(i));
            if (child == null) {
                addLink(parts, currentNode, i);
                break;
            }
            currentNode = child;
        }
    }

    private void addLink(List<String> parts, MultiChildSynchronizedTreeNode<String> node, int startIndex) {
        var currentNode = node;
        for (int i = startIndex; i < parts.size(); i++) {
            String partText = parts.get(i);
            var newNode = new LinkPartNode(partText);
            currentNode.addChild(newNode);
            currentNode = newNode;
        }
        ((LinkPartNode) currentNode).setPage(true);
    }
}
