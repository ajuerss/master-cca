package Algorithms;

import java.util.ArrayList;

public interface Algorithm {
    String getAlgorithmName();
    int computeCommunicationDistance(int nodeId, int step, int networkSize);
    ArrayList<Integer>[] getTransmittedBitSizeMatrixForStep(int steps, int networkSize);
    boolean getReduceScatterAllgather();
}
