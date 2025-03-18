package algorithms;

public class RecursiveDoubling implements Algorithm {
    private final String algorithmName = "Recursive Doubling";


    public RecursiveDoubling() {
    }

    public int compute_communication_partner_node(int nodeId, int step) {
        return nodeId ^ (1 << step);
    }

    public String getAlgorithmName() {
        return algorithmName;
    }
}
