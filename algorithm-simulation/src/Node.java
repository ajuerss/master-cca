import java.util.*;

public class Node {
    private int id;
    private Node left;
    private Node right;
    private ArrayList<int[]> seenNodes = new ArrayList<>();

    public Node(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public ArrayList<int[]> getSeenNodes() {
        return seenNodes;
    }

    public void addSeenNode(int[] newSeenNodes) {
        if (newSeenNodes != null ) {
            seenNodes.add(newSeenNodes);
        }
    }

    public int[] getSeenNodeToStep(int step) {
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < step; i++) {
            for (int node : this.seenNodes.get(i)) {
                seen.add(node);
            }
        }
        seen.add(this.id);
        return seen.stream().mapToInt(Integer::intValue).toArray();
    }

}
