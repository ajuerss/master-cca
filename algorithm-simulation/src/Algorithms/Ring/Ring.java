package Algorithms.Ring;

import Algorithms.Algorithm;
import Algorithms.AlgorithmType;

public class Ring implements Algorithm {

    private final boolean reduceScatterAllgather = false;
    private final boolean twoPort = false;
    private AlgorithmType type = AlgorithmType.RING;

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
    public AlgorithmType getAlgorithmType() {
        return this.type;
    }
}
