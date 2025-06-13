package Simulation;

import java.util.*;

public class Node {
    private int id;
    private Node left;
    private Node right;
    private ArrayList<int[]> seenNodes = new ArrayList<>();
    public boolean[][] blocks;

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

    public void addBlocks(int[] reachedNodes, boolean[][] blocksFromSenderNode) {
        for (int nodes = 0; nodes < reachedNodes.length; nodes++) {
            for ( int i = 0; i < blocksFromSenderNode.length; i++) {
                if (blocksFromSenderNode[i][reachedNodes[nodes]]) {
                    this.blocks[i][reachedNodes[nodes]] = true;
                }
            }
        }
    }

}
