package Simulation;


import Algorithms.AlgorithmType;

public class CostFunction {
    private double alphaCost;
    private double betaCost;
    boolean reduceScatterAllgather = false;

    private int messageOverhead;

    private double[] bitSizeOfNodesPerStepList;
    private int[] congestionOfNodesPerStepList;
    private int[] distanceOfNodesPerStepList;
    private int steps;

    private AlgorithmType alg;

    private final boolean includeCongestion;

    public CostFunction(int messageOverhead, boolean includeCongestion) {
        this.messageOverhead = messageOverhead;
        this.includeCongestion = includeCongestion;
    }

    public String getFunctionName() {
        return "Congestion Cost Model";
    }

    public void computeCost() {
        this.alphaCost = steps;
        this.betaCost = 0;
        Parameters.printMatrices(bitSizeOfNodesPerStepList, congestionOfNodesPerStepList, distanceOfNodesPerStepList);
        if (bitSizeOfNodesPerStepList.length != congestionOfNodesPerStepList.length || congestionOfNodesPerStepList.length != distanceOfNodesPerStepList.length) {
            throw new ArithmeticException("Matrices do not align in size");
        }
        for (int i = 0; i < steps; i++) {
            double calculatedCost;
            if (includeCongestion){
                calculatedCost = bitSizeOfNodesPerStepList[i]*congestionOfNodesPerStepList[i];
            } else {
                calculatedCost = bitSizeOfNodesPerStepList[i];
            }
            Parameters.printCostPerStep("Highest cost in step " + i + ": " + calculatedCost);
            betaCost += calculatedCost;
        }
    }

    public void setReduceScatterAllgather(boolean reduceScatterAllgather) {
        this.reduceScatterAllgather = reduceScatterAllgather;
    }

    public void setTransferedBitSizePerStep(double[] matrix) {
        this.bitSizeOfNodesPerStepList = matrix;
    }
    public void setAlgorithmType(AlgorithmType alg) {
        this.alg = alg;
    }
    public void setCongestionPerStep(int[] matrix) {
        this.congestionOfNodesPerStepList = matrix;
    }

    public void setDistancePerStep(int[] matrix) {
        this.distanceOfNodesPerStepList = matrix;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    public double getAlphaCost() {
        if (this.reduceScatterAllgather) {
            return 2*this.alphaCost;
        }
        return this.alphaCost;
    }

    public double getBetaCost() {
        if (this.reduceScatterAllgather) {
            return 2*this.betaCost;
        }
        return this.betaCost;
    }

    public int getMessageOverhead() {
        return this.messageOverhead;
    }

    public AlgorithmType getAlgorithmType() {
        return this.alg;
    }

}
