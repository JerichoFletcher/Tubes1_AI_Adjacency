import java.util.HashSet;
import java.util.Set;

public class Tree<T> {
     private T value;
     private Tree<T> parent = null;
     private final Set<Tree<T>> children;

     public Tree(T value) {
         this.value = value;
         this.children = new HashSet<>();
     }

     public T getValue() {
         return this.value;
     }

    public void setValue(T value) {
        this.value = value;
    }

     public Tree<T> getParent() {
         return this.parent;
     }

     public Set<Tree<T>> getChildren() {
         return this.children;
     }

    public boolean hasChild(Tree<T> child) {
        return this.children.contains(child);
    }

     public void addChild(Tree<T> child) {
         if (this.children.contains(child)) return;
         if (child.parent != null) child.parent.removeChild(child);
         this.children.add(child);
     }

     public void removeChild(Tree<T> child) {
         if (!this.children.contains(child)) return;
         child.parent = null;
         this.children.remove(child);
     }
}
