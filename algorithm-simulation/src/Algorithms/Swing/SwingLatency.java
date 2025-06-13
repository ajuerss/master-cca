package Algorithms.Swing;

import Algorithms.Algorithm;

public class SwingLatency implements Algorithm {

    private final boolean reduceScatterAllgather = false;
    private final boolean twoPort = false;

    public SwingLatency() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        int distance = (1 - (int) Math.pow(-2, step + 1)) / 3;
        return nodeId % 2 == 0 ? distance % networkSize : -(distance % networkSize);
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
        return "SwingLatency";
    }
}
