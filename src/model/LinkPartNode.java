package model;

public class LinkPartNode extends MultiChildSynchronizedTreeNode<String> {
    private boolean isPage = false;

    public LinkPartNode(String value) {
        super(value);
    }

    public boolean isPage() {
        return isPage;
    }

    public void setPage(boolean value) {
        isPage = value;
    }
}
