package Algorithms;

import java.util.ArrayList;

public class SplitLastThreeSteps implements Algorithm {

    private final boolean reduceScatterAllgather = true;

    public SplitLastThreeSteps() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        int necessarySteps = (int) (Math.log(networkSize) / Math.log(2)) + 3;
        if (networkSize < 16) {
            return (int) Math.pow(-1, nodeId + step);
        }
        if (step < necessarySteps-6) {
            int target = (nodeId ^ (1 << step));
            int dRight = (target - nodeId + networkSize) % networkSize;
            int dLeft = dRight - networkSize;
            return (dRight <= networkSize / 2) ? dRight : dLeft;
        }
        if (step > necessarySteps) System.out.println("More steps required than should be necessary");
        int clusterSize = networkSize / 8;
        int positionInCluster = nodeId % clusterSize;
        int distance = clusterSize/2;
        if (step == necessarySteps-6 || step == necessarySteps-4 || step == necessarySteps-2 || step == necessarySteps) {
            return positionInCluster < (clusterSize / 2) ? -distance : distance;
        }
        return positionInCluster < (clusterSize / 2) ? distance : -distance;
    }

    public ArrayList<Integer>[] getTransmittedBitSizeMatrixForStep(int steps, int networkSize) {
        ArrayList<Integer>[] congestionOfNodesPerStepList = new ArrayList[networkSize];
        for (int i = 0; i < networkSize; i++) {
            congestionOfNodesPerStepList[i] = new ArrayList<>();
            for (int step = 0; step <= steps; step++) {
                if (step < steps-6){
                    congestionOfNodesPerStepList[i].add((int) (networkSize/(Math.pow(2, step + 1))));
                } else if (steps - step <= 1){
                    congestionOfNodesPerStepList[i].add(steps-step + 1);
                } else {
                    congestionOfNodesPerStepList[i].add(steps-step);
                }
            }
        }
        return congestionOfNodesPerStepList;
    }

    public boolean getReduceScatterAllgather() {
        return this.reduceScatterAllgather;
    }

    public String getAlgorithmName() {
        return "Split Last Three Steps";
    }
}
