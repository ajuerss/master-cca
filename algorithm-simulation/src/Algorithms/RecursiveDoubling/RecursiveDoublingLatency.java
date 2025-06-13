package Algorithms.RecursiveDoubling;

import Algorithms.Algorithm;

public class RecursiveDoublingLatency implements Algorithm {

    private final boolean reduceScatterAllgather = false;
    private final boolean twoPort = false;

    public RecursiveDoublingLatency() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        int target = (nodeId ^ (1 << step));
        int dRight = (target - nodeId + networkSize) % networkSize;
        int dLeft = dRight - networkSize;
        return (dRight <= networkSize / 2) ? dRight : dLeft;
    }

    public double[] getTransmittedMessageSizePerStep(int steps, int networkSize, int messageOverhead) {
        double[] messageSizePerStep = new double[steps];
        for (int step = 0; step < steps; step++) {
            messageSizePerStep[step] = networkSize + messageOverhead;
        }
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
        return "Recursive Doubling";
    }
}
