package algorithms;

public class RecursiveDoubling implements Algorithm {
    private final int necessarySteps;
    private final String algorithmName = "Recursive Doubling";


    public RecursiveDoubling(int maxNodes) {
        this.necessarySteps = (int) (Math.log(maxNodes) / Math.log(2));
    }

    public int compute_communication_partner_node(int nodeId, int step) {
        return nodeId ^ (1 << step);
    }

    public int getNecessarySteps() {
        return necessarySteps;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }
}
