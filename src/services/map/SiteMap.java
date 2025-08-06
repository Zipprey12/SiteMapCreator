package services.map;

import model.Link;
import model.MultiChildTreeNode;

import java.util.List;

public class SiteMap {
    private MultiChildTreeNode<String> mainNode;
    private String siteName;
    private String initialPage;

    public void initialize(String initialPage, List<String> linkParts) {
        if(linkParts.isEmpty()){
            throw new IllegalArgumentException("To initialize the map's main node, linkParts must not be empty");
        }
        this.initialPage = initialPage;
        this.siteName = linkParts.getFirst();
        initializeNodes(linkParts);
    }

    private void initializeNodes(List<String> linkParts){
        mainNode = new MultiChildTreeNode<>(initialPage);
        var current = mainNode;
        for (int i = 1; i < linkParts.size(); i++){
            var newNode = new MultiChildTreeNode<>(linkParts.get(i));
            current.addChild(newNode);
            current = newNode;
        }
    }

    public MultiChildTreeNode<String> getMainNode() {
        return mainNode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void addLink(Link link) {
        var parts = link.getParts();
        if (parts.isEmpty()) {
            return;
        }

        var currentNode = mainNode;
        for (int i = 0; i < parts.size(); i++) {
            var child = currentNode.getByValue(parts.get(i));
            if (child == null) {
                addLink(parts, currentNode, i);
                break;
            }
            currentNode = child;
        }
    }

    private void addLink(List<String> parts, MultiChildTreeNode<String> node, int startIndex) {
        var currentNode = node;
        for (int i = startIndex; i < parts.size(); i++) {
            String partText = parts.get(i);
            var newNode = new MultiChildTreeNode<>(partText);
            currentNode.addChild(newNode);
            currentNode = newNode;
        }
    }
}
