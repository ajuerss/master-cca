package algorithms;

public interface Algorithm {
    int getNecessarySteps();
    String getAlgorithmName();
    int compute_communication_partner_node(int nodeId, int step);
}
