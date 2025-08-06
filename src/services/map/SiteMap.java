package services.map;

import model.MultiChildTreeNode;
import model.Link;

import java.util.List;

public class SiteMap {
    private MultiChildTreeNode<String> mainNode;
    private String siteName;

    public void initialize(String mainPageLink, String siteName){
        if (mainPageLink == null || mainPageLink.isEmpty()) {
            throw new IllegalArgumentException("mainPageLink can't be empty");
        }
        mainNode = new MultiChildTreeNode<>(mainPageLink);
        this.siteName = siteName;
    }

    public MultiChildTreeNode<String> getMainNode() {
        return mainNode;
    }

    public String getSiteName(){
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
