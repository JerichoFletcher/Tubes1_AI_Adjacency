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

     public Tree<T> getChild(T value) {
         for (Tree<T> child : this.children){
             if (child.getValue() == value) {
                 return child;
             }
         }
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
            if (child.getValue() == childValue) {
                return true;
            }
        }
        return false;
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
