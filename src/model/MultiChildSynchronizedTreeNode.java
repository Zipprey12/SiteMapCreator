package model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MultiChildSynchronizedTreeNode<T> {
    private final T value;
    private final Map<T, MultiChildSynchronizedTreeNode<T>> children;

    public MultiChildSynchronizedTreeNode(T value) {
        this.value = value;
        this.children = Collections.synchronizedMap(new HashMap<>());
    }

    public T getValue() {
        return value;
    }

    public synchronized Collection<MultiChildSynchronizedTreeNode<T>> getChildren() {
        return Collections.unmodifiableCollection(children.values());
    }

    public synchronized MultiChildSynchronizedTreeNode<T> getByValue(T value) {
        if (children.containsKey(value)) {
            return children.get(value);
        }
        return null;
    }

    public synchronized void addChild(MultiChildSynchronizedTreeNode<T> child) {
        children.put(child.value, child);
    }

    public synchronized MultiChildSynchronizedTreeNode<T> findChildByValue(T value) {
        if (children.containsKey(value)) {
            return children.get(value);
        }
        for (var child : children.values()) {
            var node = child.findChildByValue(value);
            if (node != null) {
                return node;
            }
        }
        return null;
    }
}
