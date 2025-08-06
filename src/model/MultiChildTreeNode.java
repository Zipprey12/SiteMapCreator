package model;

import java.util.*;

public class MultiChildTreeNode<T> {
    private final T value;
    private final Map<T, MultiChildTreeNode<T>> children;
    private final Object lock = new Object();

    public MultiChildTreeNode(T value){
        this.value = value;
        this.children = Collections.synchronizedMap(new HashMap<>());
    }

    public T getValue(){
        return value;
    }

    public Collection<MultiChildTreeNode<T>> getChildren(){
        synchronized (lock) {
            return Collections.unmodifiableCollection(children.values());
        }
    }

    public MultiChildTreeNode<T> getByValue(T value){
        synchronized (lock){
            if(children.containsKey(value)){
                return children.get(value);
            }
        }
        return null;
    }

    public void addChild(MultiChildTreeNode<T> child){
        synchronized (lock) {
            children.put(child.value, child);
        }
    }

    public MultiChildTreeNode<T> findChildByValue(T value){
        synchronized (lock) {
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
}
