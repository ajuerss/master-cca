package Algorithms.Trivance;

import Algorithms.Algorithm;

public class TrivanceBandwidth implements Algorithm {

    private final boolean reduceScatterAllgather = true;
    private final boolean twoPort = true;

    public TrivanceBandwidth() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        return (int) Math.pow(3, step);
    }

    public double[] getTransmittedMessageSizePerStep(int steps, int networkSize, int messageOverhead) {
        double[] messageSizePerStep = new double[steps];
        for (int step = 0; step < steps; step++) {
            messageSizePerStep[step] = (int) ((networkSize / Math.pow(3, step + 1)) + messageOverhead);
        }
        return messageSizePerStep;
    }

    public int calculateRequiredSteps (int networkSize) {
        return (int) Math.round(Math.log(networkSize) / Math.log(3));
    }

    public boolean getReduceScatterAllgather() {
        return this.reduceScatterAllgather;
    }
    public boolean getTwoPort() {
        return this.twoPort;
    }
    
    public String getAlgorithmName() {
        return "TrivanceBandwidth";
    }
}
