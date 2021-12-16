import java.util.ArrayList;

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    private HeapNode min;
    private int size;
    private HeapNode head;
    private int marksCount;
    private int treesCount;
    private static int cutCount;
    private static int linksCount;

    /**
     * public boolean isEmpty()
     *
     * Returns true if and only if the heap is empty.
     *
     */
    public boolean isEmpty()
    {
        return this.size == 0; // should be replaced by student code
    }

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     *
     * Returns the newly created node.
     */
    public HeapNode insert(int key)
    {
        HeapNode node = new HeapNode(key);
        if (this.isEmpty()){
            this.min = node;
            this.head = node;
            node.setNext(node);
            node.setPrev(node);
            this.treesCount++;
        }
        else {
            if (this.min.getKey() > node.getKey()) this.min = node;
            insertNode(node);
        }
        this.size++;
        return node; // should be replaced by student code
    }

    // Inserts a node at the beginning of the tree, updates min and tree count. doesn't update size (assumes it's being called after cut)
    // Time complexity: O(1)

    public void insertNode (HeapNode node){
        node.setPrev(this.head.prev);
        this.head.prev.setNext(node);
        node.setNext(this.head);
        this.head.setPrev(node);
        this.head = node;
        this.treesCount++;
    }

    /**
     * public void deleteMin()
     *
     * Deletes the node containing the minimum key.
     *
     */
    public void deleteMin()
    {
        if (this.isEmpty()) return; //Heap is empty
        this.size--;
        if (this.size == 0){ //Min is the only HeapNode in the heap (and it has no children)
            this.head = null;
            this.min = null;
            this.treesCount--;
            return;
        }
        if (this.min.getRank() == 0) { //Min has no children, and it's not the only Node in the heap
            if (this.min.getKey() == this.head.getKey()) { // Min is the head of the heap
                this.head = this.min.getNext();
            }
            this.min.getPrev().setNext(min.getNext());
            this.min.getNext().setPrev(min.getPrev());
        }

        else if (this.treesCount == 1){ // Min is the only HeapNode in the heap (and it has children for sure)
            this.head = this.min.getChild();
            this.resetChildren(this.min);
        }

        else {
            if (this.min.getKey() == this.head.getKey()) {this.head = this.min.getChild();}
            HeapNode prev = this.min.getPrev();
            HeapNode next = this.min.getNext();
            HeapNode firstChild = this.min.getChild();
            HeapNode lastChild = firstChild.getPrev();
            this.resetChildren(this.min);
            firstChild.setPrev(prev);
            prev.setNext(firstChild);
            lastChild.setNext(next);
            next.setPrev(lastChild);

        }
        this.consolidate();
    }

    /**
     * public void resetChildren(HeapNode parent)
     *
     * Deletes the parent field for all the children which their parent is being deleted
     * Also, turns all children mark to be false
     *
     */
    public void resetChildren(HeapNode parent){
        HeapNode child = parent.getChild();
        int firstChildKey = child.getKey();
        child.setParent(null);
        if (child.getMarked()){
            child.setMarked(false);
            this.marksCount--;
        }
        child = child.getNext();
        while (child.getKey() != firstChildKey){
            child.setParent(null);
            if (child.getMarked()){
                child.setMarked(false);
                this.marksCount--;
            }
            child = child.getNext();
        }

    }


    /**
     * public void cut(HeapNode child, HeapNode parent)
     *
     * Cuts child from its parent y
     * Time Complexity: O(1)
     */

    public void cut(HeapNode child, HeapNode parent){
        this.treesCount++;
        cutCount++;
        child.setParent(null);
        child.setMarked(false);
        parent.setRank(parent.getRank() - 1);
        if (child.next.getKey() == child.getKey()){
            parent.child = null;
        }
        else {
            if (parent.child.getKey() == child.getKey()) { parent.child = child.getNext();} //child is the direct child of parent
            child.getNext().setPrev(child.getPrev());
            child.getPrev().setNext(child.getNext());
            this.insertNode(child);
        }
    }

    /**
     * public void cascadingCut(HeapNode child, HeapNode parent)
     *
     * Cuts child from its parent y
     * Time Complexity: O(1)
     */
    public void cascadingCuts(HeapNode child, HeapNode parent){
        this.cut(child,parent);
        if (parent.getParent() != null){
            if (!parent.marked) {
                parent.setMarked(true);
                marksCount++;
            }
            else cascadingCuts(parent,parent.getParent());
        }
    }



    /**
     * public HeapNode findMin()
     *
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     *
     */
    public HeapNode findMin()
    {
        return this.min;// should be replaced by student code
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     *
     */
    public void meld (FibonacciHeap heap2)
    {
        this.size = this.size + heap2.size();
        this.treesCount = this.treesCount + heap2.treesCount;
        if (this.isEmpty() || heap2.isEmpty()){
            if (this.isEmpty() && heap2.isEmpty()) return;
            if (this.isEmpty()) {
                this.head = heap2.head;
                this.min = heap2.min;
            }
            return;
        }
        if (this.min.getKey() > heap2.min.getKey()) {this.min = heap2.min;}
        HeapNode last = this.head.prev;
        heap2.head.getPrev().setNext(this.head);
        this.head.setPrev(heap2.head.getPrev());
        last.setNext(heap2.head);
        heap2.head.setPrev(last);

    }

    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     *
     */
    public int size()
    {
        return this.size; // should be replaced by student code
    }

    /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * Note: The size of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
     *
     */

    // Worst case time complexity: O(n)
    public int[] countersRep()
    {
        if (this.isEmpty()) {return new int[] {};}
        HeapNode current = this.head;
        int max = this.head.getRank();
        current = current.getNext();
        while (current.getKey() != this.head.getKey()){
            if (max < current.getRank()) max = current.getRank();
            current = current.getNext();
        }

        int[] arr = new int[max];
        arr[current.getRank()]++;
        current = current.getNext();
        while (current.getKey() != this.head.getKey()){
            arr[current.getRank()]++;
        }
        return arr; //	 to be replaced by student code
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     *
     */
    public void delete(HeapNode x)
    {
        return; // should be replaced by student code
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta)
    {
        x.setKey(x.getKey() - delta);
        if (this.min.getKey() > x.getKey()) { this.min = x;}
        if (x.getParent() != null) {
            if (x.getKey() < x.getParent().getKey()) {
                cascadingCuts(x, x.getParent());
            }
        }
    }
    public void consolidate() {
        HeapNode[] list = this.toBuckets();
        this.fromBuckets(list);
    }
    public HeapNode[] toBuckets () {
        HeapNode[] arr = new HeapNode[(int)Math.round(5*Math.log10(this.size+2))];
        HeapNode current = this.head;
        current.getPrev().setNext(null);
        HeapNode next;
        while (current != null){
            next = current.next;
            while(arr[current.getRank()] != null) {
                current = this.link(current,arr[current.getRank()]);
                arr[current.getRank() - 1] = null;
            }
            arr[current.getRank()] = current;
            current = next;
        }
        return arr;
    }

    public void fromBuckets(HeapNode[] list){
        this.head = null;
        this.treesCount = 0;
        boolean isFirst = true;
        HeapNode first = null;
        HeapNode prev = null;
        HeapNode min = null;
        for (HeapNode node: list){
            if (node == null) continue;
            this.treesCount++;
            if (isFirst) {
                this.head = node;
                isFirst = false;
                first = node;
                min = node;
            }
            else{
                if (node.getKey() < min.getKey()) min = node;
                node.setPrev(prev);
                prev.setNext(node);
            }
            prev = node;
        }
        first.setPrev(prev);
        prev.setNext(first);
        this.min = min;

    }

    public HeapNode link (HeapNode child , HeapNode parent){
        linksCount++;
        // We want y to save the "child" and y to save the parent
        if (child.getKey() < parent.getKey()) {
            HeapNode temp = child;
            child = parent;
            parent = temp;
        }
        parent.addChild(child);
        return parent;
    }

    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     *
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     */
    public int potential()
    {
        return this.treesCount + 2*this.marksCount;
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
    public static int totalLinks()
    {
        return linksCount; // should be replaced by student code
    }

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts()
    {
        return cutCount; // should be replaced by student code
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     *
     * ###CRITICAL### : you are NOT allowed to change H.
     */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        int[] arr = new int[100];
        return arr; // should be replaced by student code
    }

    /**
     * public class HeapNode
     *
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in another file.
     *
     */
    public static class HeapNode{

        public int key;
        private boolean marked;
        private HeapNode parent, child, next, prev;
        private int rank;

        public void setMarked(Boolean flag) {this.marked = flag;}
        public boolean getMarked() {return this.marked;}

        public boolean isRoot () {return this.parent == null;}

        public HeapNode(int key) {this.key = key;}

        public int getKey() {return this.key;}
        public void setKey(int k) { this.key = k;}

        // Given a node, adds the node as a child to 'this'
        // Time Complexity: O(1)
        public void addChild (HeapNode node) {
            node.parent = this;
            if (this.child == null) { // There is no child to the current node
                this.child = node;
                node.prev = node;
                node.next = node;
                this.rank++;
            }
            else {
                this.child.addSibling(node);
            }

        }
        // Given a node, adds the node as a last sibling to 'this'
        // Time Complexity: O(1)
        public void addSibling (HeapNode node) {
            if (this.parent != null) {
                this.parent.rank++;
                this.parent.child = node;
            }
            HeapNode last = this.prev;
            last.next = node;
            node.next = this;
            node.prev = last;
            this.prev = node;
        }


        public HeapNode getParent() {return this.parent;}
        public void setParent(HeapNode node) {this.parent = node;}

        public HeapNode getChild() {return this.child;}

        public HeapNode getNext() {return this.next;}
        public void setNext(HeapNode node) {this.next = node;}
        public HeapNode getPrev() {return this.prev;}
        public void setPrev(HeapNode node) {this.prev = node;}
        public int getRank() {return this.rank;}
        public void setRank(int k) {this.rank = k;}

    }
}
