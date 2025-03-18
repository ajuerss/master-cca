package algorithms;

public interface Algorithm {
    String getAlgorithmName();
    int compute_communication_partner_node(int nodeId, int step);
}
