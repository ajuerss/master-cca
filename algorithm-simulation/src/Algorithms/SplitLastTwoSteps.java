package Algorithms;

import java.util.ArrayList;

public class SplitLastTwoSteps implements Algorithm {

    private final boolean reduceScatterAllgather = true;

    public SplitLastTwoSteps() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        int necessarySteps = (int) (Math.log(networkSize) / Math.log(2));
        if (networkSize < 8) {
            return (int) Math.pow(-1, nodeId + step);
        }
        if (step < necessarySteps-2) {
            int target = (nodeId ^ (1 << step));
            int dRight = (target - nodeId + networkSize) % networkSize;
            int dLeft = dRight - networkSize;
            return (dRight <= networkSize / 2) ? dRight : dLeft;
        }
        if (step > necessarySteps) System.out.println("More steps required than should be necessary");
        int clusterSize = networkSize / 4;
        int positionInCluster = nodeId % clusterSize;
        int distance = clusterSize/2;
        if (step == necessarySteps-1) {
            return positionInCluster < (clusterSize / 2) ? distance : -distance;
        }
        return positionInCluster < (clusterSize / 2) ? -distance : distance;
    }

    public ArrayList<Integer>[] getTransmittedBitSizeMatrixForStep(int steps, int networkSize) {
        ArrayList<Integer>[] congestionOfNodesPerStepList = new ArrayList[networkSize];
        for (int i = 0; i < networkSize; i++) {
            congestionOfNodesPerStepList[i] = new ArrayList<>();
            for (int step = 0; step <= steps; step++) {
                if (step < steps-2) congestionOfNodesPerStepList[i].add((int) (networkSize/(Math.pow(2, step + 1))));
                if (step == steps-2) congestionOfNodesPerStepList[i].add(2);
                if (step == steps-1) congestionOfNodesPerStepList[i].add(2);
                if (step == steps) congestionOfNodesPerStepList[i].add(1);
            }
        }
        return congestionOfNodesPerStepList;
    }

    public boolean getReduceScatterAllgather() {
        return this.reduceScatterAllgather;
    }

    public String getAlgorithmName() {
        return "Split Last Two Steps";
    }
}
