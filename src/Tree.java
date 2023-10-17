import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

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

     public Tree<T> getChild(T value) {
         return this.getChild(child -> child.getValue().equals(value));
     }

     public Tree<T> getChild(Predicate<Tree<T>> predicate) {
         for (Tree<T> child : this.children)
             if (predicate.test(child)) return child;
         return null;
     }

     public boolean hasParent(){
         return (parent != null);
     }

    public boolean hasChild(Tree<T> child) {
        return this.children.contains(child);
    }

    public boolean hasChildTValue(T childValue){
        for (Tree<T> child : this.children) {
            if (child.getValue().equals(childValue)) {
                return true;
            }
        }
        return false;
    }

     public void addChild(Tree<T> child) {
         if (this.children.contains(child)) return;
         if (child.parent != null) child.parent.removeChild(child);
         child.parent = this;
         this.children.add(child);
     }

     public void removeChild(Tree<T> child) {
         if (!this.children.contains(child)) return;
         child.parent = null;
         this.children.remove(child);
     }
}
