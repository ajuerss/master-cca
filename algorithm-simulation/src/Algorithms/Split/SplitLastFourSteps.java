package Algorithms.Split;

import Algorithms.Algorithm;

public class SplitLastFourSteps implements Algorithm {

    private final boolean reduceScatterAllgather = true;
    private final boolean twoPort = false;

    public SplitLastFourSteps() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        int necessarySteps = (int) (Math.log(networkSize) / Math.log(2)) + 10;
        if (networkSize < 32) {
            return (int) Math.pow(-1, nodeId + step);
        }
        if (step < necessarySteps-14) {
            int target = (nodeId ^ (1 << step));
            int dRight = (target - nodeId + networkSize) % networkSize;
            int dLeft = dRight - networkSize;
            return (dRight <= networkSize / 2) ? dRight : dLeft;
        }
        if (step > necessarySteps) System.out.println("More steps required than should be necessary");
        int clusterSize = networkSize / 16;
        int positionInCluster = nodeId % clusterSize;
        int distance = clusterSize/2;
        if ((necessarySteps-step) % 2 == 0) {
            return positionInCluster < (clusterSize / 2) ? -distance : distance;
        }
        return positionInCluster < (clusterSize / 2) ? distance : -distance;
    }

    public double[] getTransmittedMessageSizePerStep(int steps, int networkSize, int messageOverhead) {
        double[] messageSizePerStep = new double[steps];
        return messageSizePerStep;
    }

    public int calculateRequiredSteps (int networkSize) {
        return (int)(Math.log(networkSize) / Math.log(2));
    }

    public boolean getReduceScatterAllgather() {
        return this.reduceScatterAllgather;
    }
    public boolean getTwoPort() {
        return this.twoPort;
    }

    public String getAlgorithmName() {
        return "Split Last Four Steps";
    }
}
