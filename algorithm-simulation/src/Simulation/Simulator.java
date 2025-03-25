package Simulation;

import Algorithms.Algorithm;
import CostModels.CostFunction;

import java.util.*;


public class Simulator {
    private final List<Node> nodes;
    private final ArrayList<Integer> maxDistancePerStep = new ArrayList<>();

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

    public void perform(Algorithm algorithm, CostFunction costFunction) throws Exception {
        ArrayList<Integer>[] distanceOfNodesPerStepList = new ArrayList[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            distanceOfNodesPerStepList[i] = new ArrayList<>();
        }
        ArrayList<Integer>[] congestionOfNodesPerStepList = new ArrayList[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            congestionOfNodesPerStepList[i] = new ArrayList<>();
        }
        int step = 0;
        while (true) {
            ArrayList<String>[] usedEdgesOfNodesPerStepList = new ArrayList[nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                usedEdgesOfNodesPerStepList[i] = new ArrayList<>();
            }
            Map<String, Integer> numberOfUsagesPerLink = new HashMap<>();
            Parameters.log("Step " + step);
            int maxDistance = 0;
            for (Node node : nodes) {
                int distance = algorithm.computeCommunicationDistance(node.getId(), step, nodes.size());
                if (Math.abs(distance) > maxDistance) maxDistance = Math.abs(distance);
                Node communicationNode = node;
                if (distance > 0) {
                    for(int j = 0; j < distance; j++) {
                        usedEdgesOfNodesPerStepList[node.getId()].add(communicationNode.getId() + "-" + communicationNode.getRight().getId());
                        numberOfUsagesPerLink.put(communicationNode.getId() + "-" + communicationNode.getRight().getId(), numberOfUsagesPerLink.getOrDefault(communicationNode.getId() + "-" + communicationNode.getRight().getId(), 0) + 1);
                        communicationNode = communicationNode.getRight();
                    }
                } else {
                    for(int j = 0; j < Math.abs(distance); j++) {
                        usedEdgesOfNodesPerStepList[node.getId()].add(communicationNode.getId() + "-" + communicationNode.getLeft().getId());
                        numberOfUsagesPerLink.put(communicationNode.getId() + "-" + communicationNode.getLeft().getId(), numberOfUsagesPerLink.getOrDefault(communicationNode.getId() + "-" + communicationNode.getLeft().getId(), 0) + 1);
                        communicationNode = communicationNode.getLeft();
                    }
                }
                Parameters.debug(node.getId() + " sends to " + communicationNode.getId());
                node.addSeenNode(communicationNode.getSeenNodeToStep(step));
                distanceOfNodesPerStepList[node.getId()].add(Math.abs(distance));
            }
            maxDistancePerStep.add(maxDistance);
            Parameters.log("Distance: " + maxDistance);
            for (Node node : nodes) {
                int maxCongestion = 0;
                for (String s : usedEdgesOfNodesPerStepList[node.getId()]) {
                    if (numberOfUsagesPerLink.get(s) > maxCongestion) maxCongestion = numberOfUsagesPerLink.get(s);
                }
                congestionOfNodesPerStepList[node.getId()].add(maxCongestion);
            }
            if (this.allNodesShared()){
                Parameters.log("All nodes shared");
                break;
            }
            step++;
        }
        costFunction.setSteps(step);
        costFunction.setBitSizeOfNodesPerStepList(algorithm.getTransmittedBitSizeMatrixForStep(step, nodes.size()));
        costFunction.setCongestionOfNodesPerStepList(congestionOfNodesPerStepList);
        costFunction.setDistanceOfNodesPerStepList(distanceOfNodesPerStepList);
        costFunction.setReduceScatterAllgather(algorithm.getReduceScatterAllgather());
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
        if (rightDist <= leftDist) {
            return rightDist;
        } else {
            return -1 * leftDist;
        }
    }


}