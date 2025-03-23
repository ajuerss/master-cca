package Algorithms;

import java.util.ArrayList;

public class Swing implements Algorithm {

    private final boolean reduceScatterAllgather = true;

    public Swing() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        int distance = (1 - (int) Math.pow(-2, step + 1)) / 3;
        return nodeId % 2 == 0 ? distance % networkSize : -(distance % networkSize);
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
        return "Swing";
    }
}
