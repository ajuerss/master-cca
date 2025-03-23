package Algorithms;

import java.util.ArrayList;

public class RecursiveDoubling implements Algorithm {

    private final boolean reduceScatterAllgather = true;

    public RecursiveDoubling() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        int target = (nodeId ^ (1 << step));
        int dRight = (target - nodeId + networkSize) % networkSize;
        int dLeft = dRight - networkSize;
        return (dRight <= networkSize / 2) ? dRight : dLeft;
    }

    public ArrayList<Integer>[] getTransmittedBitSizeMatrixForStep(int steps, int networkSize) {
        ArrayList<Integer>[] congestionOfNodesPerStepList = new ArrayList[networkSize];
        for (int i = 0; i < networkSize; i++) {
            congestionOfNodesPerStepList[i] = new ArrayList<>();
            for (int step = 0; step <= steps; step++) {
                congestionOfNodesPerStepList[i].add((int) (networkSize/(Math.pow(2, step + 1))));
            }
        }
        return congestionOfNodesPerStepList;
    }

    public boolean getReduceScatterAllgather() {
        return this.reduceScatterAllgather;
    }

    public String getAlgorithmName() {
        return "Recursive Doubling";
    }
}
