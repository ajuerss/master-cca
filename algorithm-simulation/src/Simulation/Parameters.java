package Simulation;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameters {
    public static boolean logging = false;
    public static ArrayList<Integer> networkSizes = new ArrayList<>();
    public static ArrayList<Integer> networkSizesTrivance = new ArrayList<>();
    public static ArrayList<Integer> messageOverheads = new ArrayList<>();


    public static boolean debug = false;
    public static boolean costPerStep = false;
    public static boolean matrices = false;

    public static boolean ringAlgorithm = false;
    public static boolean ringTwoPortAlgorithm = false;
    public static boolean recursiveDoublingLatencyAlgorithm = false;
    public static boolean recursiveDoublingBandwidthAlgorithm = false;
    public static boolean splitLastTwoStepsAlgorithm = false;
    public static boolean splitLastThreeStepsAlgorithm = false;
    public static boolean splitLastFourStepsAlgorithm = false;
    public static boolean swingLatencyAlgorithm = false;
    public static boolean swingBandwidthAlgorithm = false;
    public static boolean swingLatencyTwoPortAlgorithm = false;
    public static boolean swingBandwidthTwoPortAlgorithm = false;
    public static boolean trivanceLatencyAlgorithm = false;
    public static boolean trivanceBandwidthAlgorithm = false;


    public static boolean basicCostFunction = false;
    public static boolean congestionCostFunction = false;

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

    public static void printMatrices(double[] bitSizeOfNodesPerStepList, int[] congestionOfNodesPerStepList, int[] distanceOfNodesPerStepList) {
        if (matrices) {
            System.out.println("Bytes: " + Arrays.toString(bitSizeOfNodesPerStepList));
            System.out.println("Congestion : " + Arrays.toString(congestionOfNodesPerStepList));
            System.out.println("Distance : " + Arrays.toString(distanceOfNodesPerStepList));
        }
    }
}