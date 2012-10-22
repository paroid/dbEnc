import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;


public class BplusTree {
    /**Root */
    protected Node root;
    /** M*/
    protected int order;
    /** Size */
    protected int size;
    /**Link list head*/
    protected Node head;
    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


    @SuppressWarnings("rawtypes")
    public Object get(Comparable key) {
        return root.get(key);
    }


    @SuppressWarnings("rawtypes")
    public Node getNode(Comparable key) {
        return root.getNode(key);
    }


    @SuppressWarnings("rawtypes")
    public void remove(Comparable key) {
        root.remove(key, this);
    }


    @SuppressWarnings("rawtypes")
    public void insertOrUpdate(Comparable key, Object obj) {
        root.insertOrUpdate(key, obj, this);
    }

    public BplusTree(int order) {
        if (order < 3) {
            System.out.print("order must be greater than 2");
            System.exit(0);
        }
        this.order = order;
        root = new Node(true, true);
        head = root;
        size = 0;
    }
    @SuppressWarnings("rawtypes")
    public Vector<Integer> getVals() {
        Node p = head;
        Vector<Integer> res = new Vector<Integer>();
        while(p != null) {
            for (Entry<Comparable, Object> entry : p.entries)
                res.add((Integer)entry.getValue());
            p = p.getNext();
        }
        return res;
    }
    public Vector<Integer> toInts() {
        return toInts("head", "end");
    }
    @SuppressWarnings("rawtypes")
    public Vector<Integer> toInts(String from, String to) {
        from.trim();
        Node p = head;
        int st = 0, end = 0, temp = 0;
        boolean h = from.equals("head");
        boolean e = to.equals("end");
        if(!h) {
            st = Integer.parseInt(from);
            p = (Node) getNode(st);
        }
        if(!e)
            end = Integer.parseInt(to);
        Vector<Integer> res = new Vector<Integer>();
        if(!h && !e && Integer.parseInt(from) > Integer.parseInt(to))
            return res;
        while(p != null) {
            for (Entry<Comparable, Object> entry : p.entries) {
                temp = (Integer)entry.getKey();
                if(((!h && st <= temp) || h) && ((!e && temp <= end) || e))
                    res.add(temp);
            }
            if(!e && temp > end)
                break;
            p = p.getNext();
        }
        return res;
    }
}

class Node {
    protected boolean isLeaf;
    protected boolean isRoot;
    /** Parent Node */
    protected Node parent;
    /** Previous Leaf Node*/
    protected Node previous;
    /** Next Leaf Node*/
    protected Node next;
    /** Node Key */
    @SuppressWarnings("rawtypes")
    protected List<Entry<Comparable, Object>> entries;
    /** Children Node */
    protected List<Node> children;
    @SuppressWarnings("rawtypes")
    public Node(boolean isLeaf) {
        this.isLeaf = isLeaf;
        entries = new ArrayList<Entry<Comparable, Object>>();
        if (!isLeaf) {
            children = new ArrayList<Node>();
        }
    }

    public Node(boolean isLeaf, boolean isRoot) {
        this(isLeaf);
        this.isRoot = isRoot;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" })
    public Object get(Comparable key) {
        if (isLeaf) {
            for (Entry<Comparable, Object> entry : entries) {
                if (entry.getKey().compareTo(key) == 0) {
                    return entry.getValue();
                }
            }
            return null;
        }
        else {
            //Key < Leftmost key
            if (key.compareTo(entries.get(0).getKey()) <= 0) {
                return children.get(0).get(key);
                //Key > Rightmost key
            }
            else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                return children.get(children.size() - 1).get(key);
                //search from where it's key just < key
            }
            else {
                for (int i = 0; i < entries.size(); i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0 && entries.get(i + 1).getKey().compareTo(key) > 0) {
                        return children.get(i).get(key);
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" })
    public Node getNode(Comparable key) {
        if (isLeaf)
            return this;
        else {
            //Key < Leftmost key
            if (key.compareTo(entries.get(0).getKey()) <= 0) {
                return children.get(0).getNode(key);
                //Key > Rightmost key
            }
            else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                return children.get(children.size() - 1).getNode(key);
                //search from where it's key just < key
            }
            else {
                for (int i = 0; i < entries.size(); i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0 && entries.get(i + 1).getKey().compareTo(key) > 0) {
                        return children.get(i).getNode(key);
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" })
    public void insertOrUpdate(Comparable key, Object obj, BplusTree tree) {
        if (isLeaf) {
            //No Split
            if (contains(key) || entries.size() < tree.getOrder()) {
                insertOrUpdate(key, obj);
                if (parent != null) {
                    parent.updateInsert(tree);
                }
                //Need Split
            }
            else {
                //Split
                Node left = new Node(true);
                Node right = new Node(true);
                if (previous != null) {
                    previous.setNext(left);
                    left.setPrevious(previous);
                }
                if (next != null) {
                    next.setPrevious(right);
                    right.setNext(next);
                }
                if (previous == null) {
                    tree.setHead(left);
                }
                left.setNext(right);
                right.setPrevious(left);
                previous = null;
                next = null;
                //Left & Right's Length
                int leftSize = ((tree.getOrder() + 1) >> 1) + ((tree.getOrder() + 1) & 1);
                int rightSize = (tree.getOrder() + 1) >> 1;
                //Copy key To New Node
                insertOrUpdate(key, obj);
                for (int i = 0; i < leftSize; i++) {
                    left.getEntries().add(entries.get(i));
                }
                for (int i = 0; i < rightSize; i++) {
                    right.getEntries().add(entries.get(leftSize + i));
                }
                //Not Root
                if (parent != null) {
                    //Adjust Link
                    int index = parent.getChildren().indexOf(this);
                    parent.getChildren().remove(this);
                    left.setParent(parent);
                    right.setParent(parent);
                    parent.getChildren().add(index, left);
                    parent.getChildren().add(index + 1, right);
                    setEntries(null);
                    setChildren(null);
                    parent.updateInsert(tree);
                    setParent(null);
                }
                else {
                    isRoot = false;
                    Node parent = new Node(false, true);
                    tree.setRoot(parent);
                    left.setParent(parent);
                    right.setParent(parent);
                    parent.getChildren().add(left);
                    parent.getChildren().add(right);
                    setEntries(null);
                    setChildren(null);
                    parent.updateInsert(tree);
                }
            }
        }
        else {
            if (key.compareTo(entries.get(0).getKey()) <= 0) {
                children.get(0).insertOrUpdate(key, obj, tree);
            }
            else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                children.get(children.size() - 1).insertOrUpdate(key, obj, tree);
            }
            else {
                for (int i = 0; i < entries.size(); i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0 && entries.get(i + 1).getKey().compareTo(key) > 0) {
                        children.get(i).insertOrUpdate(key, obj, tree);
                        break;
                    }
                }
            }
        }
    }

    //Update Middle Node
    @SuppressWarnings( { "unchecked", "rawtypes" })
    protected void updateInsert(BplusTree tree) {
        validate(this, tree);
        //Need Split
        if (children.size() > tree.getOrder()) {
            //Split
            Node left = new Node(false);
            Node right = new Node(false);
            int leftSize = ((tree.getOrder() + 1) >> 1) + ((tree.getOrder() + 1) & 1);
            int rightSize = (tree.getOrder() + 1) >> 1;
            for (int i = 0; i < leftSize; i++) {
                left.getChildren().add(children.get(i));
                left.getEntries().add(new SimpleEntry(children.get(i).getEntries().get(0).getKey(), null));
                children.get(i).setParent(left);
            }
            for (int i = 0; i < rightSize; i++) {
                right.getChildren().add(children.get(leftSize + i));
                right.getEntries().add(new SimpleEntry(children.get(leftSize + i).getEntries().get(0).getKey(), null));
                children.get(leftSize + i).setParent(right);
            }
            if (parent != null) {
                int index = parent.getChildren().indexOf(this);
                parent.getChildren().remove(this);
                left.setParent(parent);
                right.setParent(parent);
                parent.getChildren().add(index, left);
                parent.getChildren().add(index + 1, right);
                setEntries(null);
                setChildren(null);
                parent.updateInsert(tree);
                setParent(null);
            }
            else {
                isRoot = false;
                Node parent = new Node(false, true);
                tree.setRoot(parent);
                left.setParent(parent);
                right.setParent(parent);
                parent.getChildren().add(left);
                parent.getChildren().add(right);
                setEntries(null);
                setChildren(null);
                parent.updateInsert(tree);
            }
        }
    }

    /** Update Key*/
    @SuppressWarnings( { "unchecked", "rawtypes" })
    protected static void validate(Node node, BplusTree tree) {
        if (node.getEntries().size() == node.getChildren().size()) {
            for (int i = 0; i < node.getEntries().size(); i++) {
                Comparable key = node.getChildren().get(i).getEntries().get(0).getKey();
                if (node.getEntries().get(i).getKey().compareTo(key) != 0) {
                    node.getEntries().remove(i);
                    node.getEntries().add(i, new SimpleEntry(key, null));
                    if(!node.isRoot()) {
                        validate(node.getParent(), tree);
                    }
                }
            }
        }
        else if (node.isRoot() && node.getChildren().size() >= 2
                 || node.getChildren().size() >= tree.getOrder() >> 1
                 && node.getChildren().size() <= tree.getOrder()
                 && node.getChildren().size() >= 2) {
            node.getEntries().clear();
            for (int i = 0; i < node.getChildren().size(); i++) {
                Comparable key = node.getChildren().get(i).getEntries().get(0).getKey();
                node.getEntries().add(new SimpleEntry(key, null));
                if (!node.isRoot()) {
                    validate(node.getParent(), tree);
                }
            }
        }
    }

    protected void updateRemove(BplusTree tree) {
        validate(this, tree);
        //Need Combine
        if (children.size() < tree.getOrder() >> 1 || children.size() < 2) {
            if (isRoot) {
                if (children.size() >= 2) {
                    return;
                }
                else {
                    Node root = children.get(0);
                    tree.setRoot(root);
                    root.setParent(null);
                    root.setRoot(true);
                    setEntries(null);
                    setChildren(null);
                }
            }
            else {
                int currIdx = parent.getChildren().indexOf(this);
                int prevIdx = currIdx - 1;
                int nextIdx = currIdx + 1;
                Node previous = null, next = null;
                if (prevIdx >= 0) {
                    previous = parent.getChildren().get(prevIdx);
                }
                if (nextIdx < parent.getChildren().size()) {
                    next = parent.getChildren().get(nextIdx);
                }
                if (previous != null
                        && previous.getChildren().size() > tree.getOrder() >> 1
                        && previous.getChildren().size() > 2) {
                    int idx = previous.getChildren().size() - 1;
                    Node borrow = previous.getChildren().get(idx);
                    previous.getChildren().remove(idx);
                    borrow.setParent(this);
                    children.add(0, borrow);
                    validate(previous, tree);
                    validate(this, tree);
                    parent.updateRemove(tree);
                }
                else if (next != null
                         && next.getChildren().size() > tree.getOrder() >> 1
                         && next.getChildren().size() > 2) {
                    Node borrow = next.getChildren().get(0);
                    next.getChildren().remove(0);
                    borrow.setParent(this);
                    children.add(borrow);
                    validate(next, tree);
                    validate(this, tree);
                    parent.updateRemove(tree);
                }
                else {
                    if (previous != null
                            && (previous.getChildren().size() <= tree.getOrder() >> 1 || previous.getChildren().size() <= 2)) {
                        for (int i = previous.getChildren().size() - 1; i >= 0; i--) {
                            Node child = previous.getChildren().get(i);
                            children.add(0, child);
                            child.setParent(this);
                        }
                        previous.setChildren(null);
                        previous.setEntries(null);
                        previous.setParent(null);
                        parent.getChildren().remove(previous);
                        validate(this, tree);
                        parent.updateRemove(tree);
                    }
                    else if (next != null
                             && (next.getChildren().size() <= tree.getOrder() >> 1 || next.getChildren().size() <= 2)) {
                        for (int i = 0; i < next.getChildren().size(); i++) {
                            Node child = next.getChildren().get(i);
                            children.add(child);
                            child.setParent(this);
                        }
                        next.setChildren(null);
                        next.setEntries(null);
                        next.setParent(null);
                        parent.getChildren().remove(next);
                        validate(this, tree);
                        parent.updateRemove(tree);
                    }
                }
            }
        }
    }

    @SuppressWarnings( { "rawtypes", "unchecked" })
    public void remove(Comparable key, BplusTree tree) {
        if (isLeaf) {
            if (!contains(key)) {
                return;
            }
            if (isRoot) {
                remove(key);
            }
            else {
                if (entries.size() > tree.getOrder() >> 1 && entries.size() > 2) {
                    remove(key);
                }
                else {
                    if (previous != null
                            && previous.getEntries().size() > tree.getOrder() >> 1
                            && previous.getEntries().size() > 2
                            && previous.getParent() == parent) {
                        int size = previous.getEntries().size();
                        Entry<Comparable, Object> entry = previous.getEntries().get(size - 1);
                        previous.getEntries().remove(entry);
                        entries.add(0, entry);
                        remove(key);
                    }
                    else if (next != null
                             && next.getEntries().size() > tree.getOrder() >> 1
                             && next.getEntries().size() > 2
                             && next.getParent() == parent) {
                        Entry<Comparable, Object> entry = next.getEntries().get(0);
                        next.getEntries().remove(entry);
                        entries.add(entry);
                        remove(key);
                    }
                    else {
                        if (previous != null
                                && (previous.getEntries().size() <= tree.getOrder() >> 1 || previous.getEntries().size() <= 2)
                                && previous.getParent() == parent) {
                            for (int i = previous.getEntries().size() - 1; i >= 0; i--) {
                                entries.add(0, previous.getEntries().get(i));
                            }
                            remove(key);
                            previous.setParent(null);
                            previous.setEntries(null);
                            parent.getChildren().remove(previous);
                            if (previous.getPrevious() != null) {
                                Node temp = previous;
                                temp.getPrevious().setNext(this);
                                previous = temp.getPrevious();
                                temp.setPrevious(null);
                                temp.setNext(null);
                            }
                            else {
                                tree.setHead(this);
                                previous.setNext(null);
                                previous = null;
                            }
                        }
                        else if(next != null
                                && (next.getEntries().size() <= tree.getOrder() >> 1 || next.getEntries().size() <= 2)
                                && next.getParent() == parent) {
                            for (int i = 0; i < next.getEntries().size(); i++) {
                                entries.add(next.getEntries().get(i));
                            }
                            remove(key);
                            next.setParent(null);
                            next.setEntries(null);
                            parent.getChildren().remove(next);
                            if (next.getNext() != null) {
                                Node temp = next;
                                temp.getNext().setPrevious(this);
                                next = temp.getNext();
                                temp.setPrevious(null);
                                temp.setNext(null);
                            }
                            else {
                                next.setPrevious(null);
                                next = null;
                            }
                        }
                    }
                }
                parent.updateRemove(tree);
            }
        }
        else {
            if (key.compareTo(entries.get(0).getKey()) <= 0) {
                children.get(0).remove(key, tree);
            }
            else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
                children.get(children.size() - 1).remove(key, tree);
            }
            else {
                for (int i = 0; i < entries.size(); i++) {
                    if (entries.get(i).getKey().compareTo(key) <= 0 && entries.get(i + 1).getKey().compareTo(key) > 0) {
                        children.get(i).remove(key, tree);
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings( { "unchecked", "rawtypes" })
    protected boolean contains(Comparable key) {
        for (Entry<Comparable, Object> entry : entries) {
            if (entry.getKey().compareTo(key) == 0) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" })
    protected void insertOrUpdate(Comparable key, Object obj) {
        Entry<Comparable, Object> entry = new SimpleEntry<Comparable, Object>(key, obj);
        if (entries.size() == 0) {
            entries.add(entry);
            return;
        }
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getKey().compareTo(key) == 0) {
                entries.get(i).setValue(obj);
                return;
            }
            else if (entries.get(i).getKey().compareTo(key) > 0) {
                if (i == 0) {
                    entries.add(0, entry);
                    return;
                }
                else {
                    entries.add(i, entry);
                    return;
                }
            }
        }
        entries.add(entries.size(), entry);
    }

    @SuppressWarnings( { "unchecked", "rawtypes" })
    protected void remove(Comparable key) {
        int index = -1;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getKey().compareTo(key) == 0) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            entries.remove(index);
        }
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @SuppressWarnings("rawtypes")
    public List<Entry<Comparable, Object>> getEntries() {
        return entries;
    }

    @SuppressWarnings("rawtypes")
    public void setEntries(List<Entry<Comparable, Object>> entries) {
        this.entries = entries;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

}


