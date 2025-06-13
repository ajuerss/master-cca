package Algorithms.Ring;

import Algorithms.Algorithm;

public class Ring implements Algorithm {

    private final boolean reduceScatterAllgather = true;
    private final boolean twoPort = false;

    public Ring() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        return 1;
    }

    public double[] getTransmittedMessageSizePerStep(int steps, int networkSize, int messageOverhead) {
        double[] messageSizePerStep = new double[steps];
        for (int step = 0; step < steps; step++) {
            messageSizePerStep[step] = 1 + messageOverhead;
        }
        return messageSizePerStep;
    }

    public int calculateRequiredSteps (int networkSize) {
        return networkSize-1;
    }

    public boolean getReduceScatterAllgather() {
        return this.reduceScatterAllgather;
    }
    public boolean getTwoPort() {
        return this.twoPort;
    }

    public String getAlgorithmName() {
        return "Ring";
    }
}
