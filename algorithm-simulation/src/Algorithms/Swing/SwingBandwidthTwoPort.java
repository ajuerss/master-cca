package Algorithms.Swing;

import Algorithms.Algorithm;
import Algorithms.AlgorithmType;

public class SwingBandwidthTwoPort implements Algorithm {

    private final boolean reduceScatterAllgather = true;
    private final boolean twoPort = true;
    private final AlgorithmType type = AlgorithmType.SWING_BANDWIDTH_TWO_PORT;

    public SwingBandwidthTwoPort() {}

    public int computeCommunicationDistance(int nodeId, int step, int networkSize) {
        int distance = (1 - (int) Math.pow(-2, step + 1)) / 3;
        return nodeId % 2 == 0 ? distance % networkSize : -(distance % networkSize);
    }

    public double[] getTransmittedMessageSizePerStep(int steps, int networkSize, int messageOverhead) {
        double[] messageSizePerStep = new double[steps];
        for (int step = 0; step < steps; step++) {
            messageSizePerStep[step] = (networkSize/(Math.pow(2, step + 1)))/2 + messageOverhead;
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
