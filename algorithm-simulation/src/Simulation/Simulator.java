package Simulation;

import Algorithms.Algorithm;
import Algorithms.Trivance.TrivanceBandwidth;

import java.util.*;


public class Simulator {
    private final List<Node> nodes;
    private final ArrayList<Integer> maxDistancePerStep = new ArrayList<>();
    private int requiredSteps = 0;
    private boolean twoPort;

    public Simulator(int size) {
        nodes = new ArrayList<>();
        Node temp = null;
        for(int i = 0; i < size; i++) {
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

    public void perform(Algorithm algorithm, CostFunction costFunction) throws Exception {
        if (algorithm.getAlgorithmName() == "TrivanceBandwidth") {
            for (Node node : nodes) {
                node.blocks = new boolean[nodes.size()][nodes.size()];
                Arrays.fill(node.blocks[node.getId()], true);
            }
        }
        requiredSteps = algorithm.calculateRequiredSteps(nodes.size());
        twoPort = algorithm.getTwoPort();
        int[] distancePerStep = new int[requiredSteps];
        int[] congestionPerStep = new int[requiredSteps];

        for (int step = 0; step < requiredSteps; step++){
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
                if (twoPort) {
                    for(int j = 0; j < Math.abs(distance); j++) {
                        usedEdgesOfNodesPerStepList[node.getId()].add(communicationNode.getId() + "-" + communicationNode.getRight().getId());
                        numberOfUsagesPerLink.put(communicationNode.getId() + "-" + communicationNode.getRight().getId(), numberOfUsagesPerLink.getOrDefault(communicationNode.getId() + "-" + communicationNode.getRight().getId(), 0) + 1);
                        communicationNode = communicationNode.getRight();
                    }
                    if (algorithm.getAlgorithmName() == "TrivanceBandwidth") {
                        int[] reachedNodes = this.getReachedNodes(communicationNode.getId(), step, nodes.size());
                        communicationNode.addBlocks(reachedNodes, node.blocks);
                    }
                    Parameters.debug(node.getId() + " sends to " + communicationNode.getId());
                    int[] seenNodesFromRight = communicationNode.getSeenNodeToStep(step);

                    communicationNode = node;
                    for(int j = 0; j < Math.abs(distance); j++) {
                        usedEdgesOfNodesPerStepList[node.getId()].add(communicationNode.getId() + "-" + communicationNode.getLeft().getId());
                        numberOfUsagesPerLink.put(communicationNode.getId() + "-" + communicationNode.getLeft().getId(), numberOfUsagesPerLink.getOrDefault(communicationNode.getId() + "-" + communicationNode.getLeft().getId(), 0) + 1);
                        communicationNode = communicationNode.getLeft();
                    }
                    if (algorithm.getAlgorithmName() == "TrivanceBandwidth") {
                        int[] reachedNodes = this.getReachedNodes(communicationNode.getId(), step, nodes.size());
                        communicationNode.addBlocks(reachedNodes, node.blocks);
                    }
                    Parameters.debug(node.getId() + " sends to " + communicationNode.getId());
                    int[] seenNodesFromLeft = communicationNode.getSeenNodeToStep(step);
                    int[] totalSeenNodes = new int[seenNodesFromLeft.length + seenNodesFromRight.length];
                    System.arraycopy(seenNodesFromLeft, 0, totalSeenNodes, 0, seenNodesFromLeft.length);
                    System.arraycopy(seenNodesFromRight, 0, totalSeenNodes, seenNodesFromLeft.length, seenNodesFromRight.length);
                    node.addSeenNode(totalSeenNodes);
                } else {
                    if (distance > 0) {
                        for(int j = 0; j < Math.abs(distance); j++) {
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
                }
                distancePerStep[step] = (Math.abs(distance));
            }
            maxDistancePerStep.add(maxDistance);
            Parameters.log("Distance: " + maxDistance);
            for (Node node : nodes) {
                int maxCongestion = 0;
                for (String s : usedEdgesOfNodesPerStepList[node.getId()]) {
                    if (numberOfUsagesPerLink.get(s) > maxCongestion) maxCongestion = numberOfUsagesPerLink.get(s);
                }
                congestionPerStep[step] = maxCongestion;
            }
        }
        if (algorithm.getAlgorithmName() == "TrivanceBandwidth") {
            if(!this.allBlocksReceived()) {
                throw new Exception("For the TrivanceBandwidth algorithm, not every node has received all blocks necessary for allreduce");
            }
        }
        if (!this.allNodesShared()){
            throw new Exception("Within the required steps, some nodes have not been shared");
        }
        Parameters.log("All nodes shared");
        costFunction.setSteps(requiredSteps);
        costFunction.setTransferedBitSizePerStep(algorithm.getTransmittedMessageSizePerStep(requiredSteps, nodes.size(), costFunction.getMessageOverhead()));
        costFunction.setCongestionPerStep(congestionPerStep);
        costFunction.setDistancePerStep(distancePerStep);
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
                    System.out.println("node " + node.getId() + " has not seen " + id);
                    return false;
                }
            }
        }

        return true;
    }

    public boolean allBlocksReceived() {
        for (Node node : nodes) {
            for (int i = 0; i < node.blocks.length; i++) {
                if (!node.blocks[i][node.getId()]){
                    return false;
                }
            }
        }
        return true;
    }

    public int[] getReachedNodes(int nodeId, int step, int networkSize){
        int s = (int) Math.round(Math.log(networkSize) / Math.log(3));
        Set<Integer> result = new HashSet<>();
        result.add(nodeId);
        for (int i = step+1; i < s; i++) {
            Set<Integer> toAdd = new HashSet<>();
            for(Integer n : result){
                toAdd.add((int) ((n + Math.pow(3,i)) % networkSize + networkSize) % networkSize);
                toAdd.add((int) ((n - Math.pow(3,i)) % networkSize + networkSize) % networkSize);
            }
            result.addAll(toAdd);
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }


}