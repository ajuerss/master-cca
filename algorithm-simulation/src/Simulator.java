import algorithms.Algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Simulator {
    private final List<Node> nodes;
    private final ArrayList<Integer> maxDistancePerStep = new ArrayList<>();
    private int requiredSteps;

    public Simulator(int power) {
        int maxNodeNumber = (int) Math.pow(2, power);
        nodes = new ArrayList<Node>();
        Node temp = null;
        for(int i = 0; i < maxNodeNumber; i++) {
            Node n = new Node(i);
            if (temp != null) {
                temp.setRight(n);
                n.setLeft(temp);
            }
            temp = n;
            nodes.add(n);
        }
        Node first = nodes.get(0);
        Node last = nodes.get(nodes.size() - 1);
        first.setLeft(last);
        last.setRight(first);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public int getRequiredSteps() {return requiredSteps;}

    public void perform(Algorithm algorithm) throws Exception {
        requiredSteps = algorithm.getNecessarySteps();
        for (int step = 0; step < algorithm.getNecessarySteps(); step++) {
            int maxDistance = 0;
            for (Node node : nodes) {
                int indexOfCommunicatingNode = algorithm.compute_communication_partner_node(node.getId(), step);
                int distance = this.getDistanceBetweenNodes(node.getId(), indexOfCommunicatingNode);
                if (Math.abs(distance) > maxDistance) maxDistance = Math.abs(distance);
                Node communicationNode = node;
                if (distance > 0) {
                    for(int j = 0; j < distance; j++) {
                        communicationNode = communicationNode.getRight();
                    }
                } else {
                    for(int j = 0; j < Math.abs(distance); j++) {
                        communicationNode = communicationNode.getLeft();
                    }
                }
                node.addSeenNode(communicationNode.getSeenNodeToStep(step));
            }
            maxDistancePerStep.add(maxDistance);
        }
        if (!this.allNodesShared()){
            throw new Exception("algorithm is not correct");
        }
    }

    public boolean allNodesShared() {
        Set<Integer> allNodeIds = new HashSet<>();

        for (Node node : nodes) {
            allNodeIds.add(node.getId());
        }

        for (Node node : nodes) {
            Set<Integer> seenSet = new HashSet<>();

            for (int[] seenArray : node.getSeenNodes()) {
                for (int id : seenArray) {
                    seenSet.add(id);
                }
            }

            for (int id : allNodeIds) {
                if (id != node.getId() && !seenSet.contains(id)) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getDistanceBetweenNodes(int a, int b) {
        int rightDist = (b-a+nodes.size())% nodes.size();
        int leftDist = (a-b+nodes.size())% nodes.size();
        if (rightDist >= leftDist) {
            return rightDist;
        } else {
            return -1 * leftDist;
        }
    }
}