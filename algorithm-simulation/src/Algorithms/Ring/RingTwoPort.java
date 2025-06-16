package Algorithms.Ring;

import Algorithms.Algorithm;
import Algorithms.AlgorithmType;

public class RingTwoPort implements Algorithm {

    private final boolean reduceScatterAllgather = true;
    private final boolean twoPort = true;
    private AlgorithmType type = AlgorithmType.RING_TWO_PORT;

    public RingTwoPort() {}

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
        return (int) Math.floor(networkSize / 2.0);
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
