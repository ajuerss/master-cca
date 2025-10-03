package Algorithms.RecursiveDoubling;

import Algorithms.Algorithm;
import Algorithms.AlgorithmType;

public class RecursiveDoublingBandwidthTwoPort implements Algorithm {

    private final boolean reduceScatterAllgather = true;
    private final boolean twoPort = true;
    private AlgorithmType type = AlgorithmType.RECURSIVE_DOUBLING_BANDWIDTH_TWO_PORT;

    public RecursiveDoublingBandwidthTwoPort() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        return (int) Math.pow(2, step);
    }

    public double[] getTransmittedMessageSizePerStep(int steps, int networkSize, int messageOverhead) {
        double[] messageSizePerStep = new double[steps];
        for (int step = 0; step < steps; step++) {
            messageSizePerStep[step] = (double) (networkSize/(Math.pow(2, step + 1)))/2 + messageOverhead;
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
    public AlgorithmType getAlgorithmType() {
        return this.type;
    }
}
