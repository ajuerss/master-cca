package Algorithms;

import java.util.ArrayList;

public interface Algorithm {
    int computeCommunicationDistance(int nodeId, int step, int networkSize);
    double[] getTransmittedMessageSizePerStep(int steps, int networkSize, int messageOverhead);
    boolean getReduceScatterAllgather();
    boolean getTwoPort();
    int calculateRequiredSteps(int networkSize);
    public AlgorithmType getAlgorithmType();
}
