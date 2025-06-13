package Algorithms.Trivance;

import Algorithms.Algorithm;

public class TrivanceLatency implements Algorithm {

    private final boolean reduceScatterAllgather = false;
    private final boolean twoPort = true;

    public TrivanceLatency() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        return (int) Math.pow(3, step);
    }

    public double[] getTransmittedMessageSizePerStep(int steps, int networkSize, int messageOverhead) {
        double[] messageSizePerStep = new double[steps];
        for (int step = 0; step < steps; step++) {
            messageSizePerStep[step] = networkSize + messageOverhead;
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
        return "TrivanceLatency";
    }
}
