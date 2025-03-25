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
    public static boolean splitThreeLastStepsAlgorithm = false;

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
            for (int i = 0; i < bitSizeOfNodesPerStepList.length; i++) {
                System.out.println("Bytes " + i + ": " + bitSizeOfNodesPerStepList[i]);
            }
            for (int i = 0; i < congestionOfNodesPerStepList.length; i++) {
                System.out.println("Congestion " + i + ": " + congestionOfNodesPerStepList[i]);
            }
            for (int i = 0; i < distanceOfNodesPerStepList.length; i++) {
                System.out.println("Distance " + i + ": " + distanceOfNodesPerStepList[i]);
            }
        }
    }
}