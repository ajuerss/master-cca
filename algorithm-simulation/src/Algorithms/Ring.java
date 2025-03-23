package Algorithms;

import java.util.ArrayList;

public class Ring implements Algorithm {

    private final boolean reduceScatterAllgather = true;

    public Ring() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        return 1;
    }

    public ArrayList<Integer>[] getTransmittedBitSizeMatrixForStep(int steps, int networkSize) {
        ArrayList<Integer>[] congestionOfNodesPerStepList = new ArrayList[networkSize];
        for (int i = 0; i < networkSize; i++) {
            congestionOfNodesPerStepList[i] = new ArrayList<>();
            for (int step = 0; step <= steps; step++) {
                congestionOfNodesPerStepList[i].add(1);
            }
        }
        return congestionOfNodesPerStepList;
    }

    public boolean getReduceScatterAllgather() {
        return this.reduceScatterAllgather;
    }

    public String getAlgorithmName() {
        return "Ring";
    }
}
