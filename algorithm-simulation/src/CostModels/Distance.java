package CostModels;

import Simulation.Parameters;

import java.util.ArrayList;

public class Distance implements CostFunction{
    private int alphaCost;
    private int betaCost;
    boolean reduceScatterAllgather = false;

    private ArrayList<Integer>[] bitSizeOfNodesPerStepList;
    private ArrayList<Integer>[] congestionOfNodesPerStepList;
    private ArrayList<Integer>[] distanceOfNodesPerStepList;
    private int steps;

    public String getFunctionName() {
        return "Distance Cost Model";
    }

    @Override
    public void computeCost() {
        this.alphaCost = steps + 1;
        this.betaCost = 0;
        Parameters.printMatrices(bitSizeOfNodesPerStepList, congestionOfNodesPerStepList, distanceOfNodesPerStepList);
        for (int i = 0; i < bitSizeOfNodesPerStepList[0].size(); i++) {
            double highestTransmissionCost = 0.0;
            for (int j = 0; j < bitSizeOfNodesPerStepList.length; j++) {
                int calculatedCost = (int) (bitSizeOfNodesPerStepList[j].get(i)*congestionOfNodesPerStepList[j].get(i)*Math.pow(distanceOfNodesPerStepList[j].get(i), 1));
                if (calculatedCost > highestTransmissionCost) {
                    highestTransmissionCost = calculatedCost;
                }
            }
            Parameters.printCostPerStep("Highest cost in step " + i + ": " + highestTransmissionCost);
            betaCost += highestTransmissionCost;
        }
    }

    public void setReduceScatterAllgather(boolean reduceScatterAllgather) {
        this.reduceScatterAllgather = reduceScatterAllgather;
    }

    public void setBitSizeOfNodesPerStepList(ArrayList<Integer>[] matrix) {
        this.bitSizeOfNodesPerStepList = matrix;
    }

    public void setCongestionOfNodesPerStepList(ArrayList<Integer>[] matrix) {
        this.congestionOfNodesPerStepList = matrix;
    }

    public void setDistanceOfNodesPerStepList(ArrayList<Integer>[] matrix) {
        this.distanceOfNodesPerStepList = matrix;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    public int getAlphaCost() {
        if (this.reduceScatterAllgather) {
            return 2*this.alphaCost;
        }
        return this.alphaCost;
    }

    public int getBetaCost() {
        if (this.reduceScatterAllgather) {
            return 2*this.betaCost;
        }
        return this.betaCost;
    }
}
