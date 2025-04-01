package Simulation;

import java.util.ArrayList;

public class Parameters {
    public static boolean logging = false;
    public static ArrayList<Integer> networkSizes = new ArrayList<>();

    public static boolean debug = false;
    public static boolean costPerStep = false;
    public static boolean matrices = false;

    public static boolean ringAlgorithm = false;
    public static boolean recursiveDoublingAlgorithm = false;
    public static boolean swingAlgorithm = false;
    public static boolean splitLastTwoStepsAlgorithm = false;
    public static boolean splitLastThreeStepsAlgorithm = false;
    public static boolean splitLastFourStepsAlgorithm = false;

    public static boolean basicCostFunction = false;
    public static boolean congestionCostFunction = false;
    public static boolean distanceCostFunction = false;

    public static void log(String s) {
        if (logging) {
            System.out.println(s);
        }
    }

    public static void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    public static void printCostPerStep(String s) {
        if (costPerStep) {
            System.out.println(s);
        }
    }

    public static void printMatrices(ArrayList<Integer>[] bitSizeOfNodesPerStepList, ArrayList<Integer>[] congestionOfNodesPerStepList, ArrayList<Integer>[] distanceOfNodesPerStepList) {
        if (matrices) {
            System.out.println("Bytes: " + bitSizeOfNodesPerStepList[0]);
            System.out.println("Congestion : " + congestionOfNodesPerStepList[0]);
            System.out.println("Distance : " + distanceOfNodesPerStepList[0]);
        }
    }
}