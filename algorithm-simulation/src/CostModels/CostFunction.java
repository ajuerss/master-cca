package CostModels;

import java.util.ArrayList;

public interface CostFunction {
    String getFunctionName();
    void computeCost();
    void setReduceScatterAllgather(boolean reduceScatterAllgather);
    void setBitSizeOfNodesPerStepList(ArrayList<Integer>[] matrix);
    void setCongestionOfNodesPerStepList(ArrayList<Integer>[] matrix);
    void setDistanceOfNodesPerStepList(ArrayList<Integer>[] matrix);
    void setSteps(int steps);
    int getSteps();
    int getAlphaCost();
    int getBetaCost();

}
