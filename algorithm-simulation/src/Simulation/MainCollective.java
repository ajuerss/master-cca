package Simulation;

import Algorithms.*;

import CostModels.Basic;
import CostModels.Congestion;
import CostModels.CostFunction;
import CostModels.Distance;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainCollective {

    public static void main(String[] args) throws Exception {
        readParameters();
        for (int size: Parameters.networkSizes) {
            ArrayList<Algorithm> algorithms = prepareAlgorithms();
            System.out.println("Network size: " + (1 << size));
            for (Algorithm a: algorithms) {
                System.out.println(a.getAlgorithmName() + " is being performed");
                ArrayList<CostFunction> costFunctions = prepareCostFunctions();
                if (costFunctions.size() == 0) Parameters.log("No Cost Function found");
                for (CostFunction c: costFunctions) {
                    Simulator s = new Simulator(size);
                    System.out.println(c.getFunctionName() + " is applied");
                    s.perform(a, c);
                    c.computeCost();
                    System.out.println("Alpha: " + c.getAlphaCost() + " Beta: " + c.getBetaCost());
                    Parameters.log("------------------------------------");

                }
            }
        }
        System.out.println("Simulation completed!");
    }

    public static void readParameters() {
        try {
            String file = new String(Files.readAllBytes(Paths.get("algorithm-simulation/config/config.json")));
            JSONObject parameters = new JSONObject(file);

            Parameters.logging = parameters.getBoolean("logging");
            JSONObject debug = parameters.getJSONObject("debug");
            Parameters.debug = debug.getBoolean("general");
            Parameters.costPerStep = debug.getBoolean("cost_per_step");
            Parameters.matrices = debug.getBoolean("matrices");

            JSONArray networkSizes = parameters.getJSONArray("network_sizes");
            for (int i = 0; i < networkSizes.length(); i++) {
                Parameters.networkSizes.add(networkSizes.getInt(i));
            }

            JSONObject algorithms = parameters.getJSONObject("algorithms");
            Parameters.ringAlgorithm = algorithms.getBoolean("ring");
            Parameters.recursiveDoublingAlgorithm = algorithms.getBoolean("recursive_doubling");
            Parameters.swingAlgorithm = algorithms.getBoolean("swing");
            Parameters.splitLastTwoStepsAlgorithm = algorithms.getBoolean("split_last_two_steps");
            Parameters.splitLastThreeStepsAlgorithm = algorithms.getBoolean("split_last_three_steps");
            Parameters.splitLastFourStepsAlgorithm = algorithms.getBoolean("split_last_four_steps");

            JSONObject costFunctions = parameters.getJSONObject("cost_functions");
            Parameters.basicCostFunction = costFunctions.getBoolean("basic");
            Parameters.congestionCostFunction = costFunctions.getBoolean("congestion");
            Parameters.distanceCostFunction = costFunctions.getBoolean("distance");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Algorithm> prepareAlgorithms() {
        ArrayList<Algorithm> algorithms = new ArrayList<>();
        if (Parameters.ringAlgorithm)  algorithms.add(new Ring());
        if (Parameters.recursiveDoublingAlgorithm)  algorithms.add(new RecursiveDoubling());
        if (Parameters.swingAlgorithm)  algorithms.add(new Swing());
        if (Parameters.splitLastTwoStepsAlgorithm)  algorithms.add(new SplitLastTwoSteps());
        if (Parameters.splitLastThreeStepsAlgorithm)  algorithms.add(new SplitLastThreeSteps());
        if (Parameters.splitLastFourStepsAlgorithm)  algorithms.add(new SplitLastFourSteps());
        return algorithms;
    }

    public static ArrayList<CostFunction> prepareCostFunctions() {
        ArrayList<CostFunction> costFunctions = new ArrayList<>();
        if (Parameters.basicCostFunction)  costFunctions.add(new Basic());
        if (Parameters.congestionCostFunction)  costFunctions.add(new Congestion());
        if (Parameters.distanceCostFunction)  costFunctions.add(new Distance());
        return costFunctions;
    }
}
