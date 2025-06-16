package Simulation;

import Algorithms.*;

import Algorithms.RecursiveDoubling.RecursiveDoublingBandwidth;
import Algorithms.RecursiveDoubling.RecursiveDoublingLatency;
import Algorithms.Ring.Ring;
import Algorithms.Ring.RingTwoPort;
import Algorithms.Split.SplitLastFourSteps;
import Algorithms.Split.SplitLastThreeSteps;
import Algorithms.Split.SplitLastTwoSteps;
import Algorithms.Swing.SwingBandwidth;
import Algorithms.Swing.SwingBandwidthTwoPort;
import Algorithms.Swing.SwingLatency;
import Algorithms.Swing.SwingLatencyTwoPort;
import Algorithms.Trivance.TrivanceBandwidth;
import Algorithms.Trivance.TrivanceLatency;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MainCollective {

    public static void main(String[] args) throws Exception {
        readParameters();
        List<Map.Entry<Integer, CostFunction>> results = new ArrayList<>();
        for (int size: Parameters.networkSizes) {
            ArrayList<Algorithm> algorithms = prepareAlgorithms();
            System.out.println("> Network size: " + (size));
            for (int messageOverheads: Parameters.messageOverheads) {
                //System.out.println(">> Overhead: " + messageOverheads);
                for (Algorithm a: algorithms) {
                    if ((size % 2 == 0 && (a.getAlgorithmType() == AlgorithmType.TRIVANCE_BANDWIDTH || a.getAlgorithmType() == AlgorithmType.TRIVANCE_LATENCY)) || (size % 3 == 0 && !(a.getAlgorithmType() == AlgorithmType.TRIVANCE_BANDWIDTH || a.getAlgorithmType() == AlgorithmType.TRIVANCE_LATENCY))) {
                        continue;
                    }
                    System.out.println(">>>>> " + a.getAlgorithmType());
                    ArrayList<CostFunction> costFunctions = prepareCostFunctions(messageOverheads);
                    if (costFunctions.size() == 0) Parameters.log("No Cost Function found");
                    for (CostFunction c: costFunctions) {
                        Simulator s = new Simulator(size);
                        Parameters.log(c.getFunctionName() + " is applied");
                        s.perform(a, c);
                        c.computeCost();
                        results.add(new AbstractMap.SimpleEntry<>(size, c));
                        System.out.println("Alpha: " + c.getAlphaCost() + " Beta: " + c.getBetaCost());
                        Parameters.log("------------------------------------");

                    }
                }
            }
            System.out.println("------------------------------------------------------------------------------------------------------------");
        }
        writeCostsToJSON(results);
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
            JSONArray messageOverheads = parameters.getJSONArray("message_overheads");
            for (int i = 0; i < messageOverheads.length(); i++) {
                Parameters.messageOverheads.add(messageOverheads.getInt(i));
            }

            JSONObject algorithms = parameters.getJSONObject("algorithms");
            Parameters.ringAlgorithm = algorithms.getBoolean("ring");
            Parameters.ringTwoPortAlgorithm = algorithms.getBoolean("ring_twoPort");
            Parameters.recursiveDoublingLatencyAlgorithm = algorithms.getBoolean("recursiveDoubling_latency");
            Parameters.recursiveDoublingBandwidthAlgorithm = algorithms.getBoolean("recursiveDoubling_bandwidth");
            Parameters.splitLastTwoStepsAlgorithm = algorithms.getBoolean("split_last_two_steps");
            Parameters.splitLastThreeStepsAlgorithm = algorithms.getBoolean("split_last_three_steps");
            Parameters.splitLastFourStepsAlgorithm = algorithms.getBoolean("split_last_four_steps");
            Parameters.swingLatencyAlgorithm = algorithms.getBoolean("swing_latency");
            Parameters.swingBandwidthAlgorithm = algorithms.getBoolean("swing_bandwidth");
            Parameters.swingLatencyTwoPortAlgorithm = algorithms.getBoolean("swing_latency_twoPort");
            Parameters.swingBandwidthTwoPortAlgorithm = algorithms.getBoolean("swing_bandwidth_twoPort");
            Parameters.trivanceLatencyAlgorithm = algorithms.getBoolean("trivance_latency");
            Parameters.trivanceBandwidthAlgorithm = algorithms.getBoolean("trivance_bandwidth");

            JSONObject costFunctions = parameters.getJSONObject("cost_functions");
            Parameters.basicCostFunction = costFunctions.getBoolean("basic");
            Parameters.congestionCostFunction = costFunctions.getBoolean("congestion");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Algorithm> prepareAlgorithms() {
        ArrayList<Algorithm> algorithms = new ArrayList<>();
        if (Parameters.ringAlgorithm)  algorithms.add(new Ring());
        if (Parameters.ringTwoPortAlgorithm)  algorithms.add(new RingTwoPort());
        if (Parameters.recursiveDoublingLatencyAlgorithm)  algorithms.add(new RecursiveDoublingLatency());
        if (Parameters.recursiveDoublingBandwidthAlgorithm)  algorithms.add(new RecursiveDoublingBandwidth());
        if (Parameters.splitLastTwoStepsAlgorithm)  algorithms.add(new SplitLastTwoSteps());
        if (Parameters.splitLastThreeStepsAlgorithm)  algorithms.add(new SplitLastThreeSteps());
        if (Parameters.splitLastFourStepsAlgorithm)  algorithms.add(new SplitLastFourSteps());
        if (Parameters.swingLatencyAlgorithm)  algorithms.add(new SwingLatency());
        if (Parameters.swingBandwidthAlgorithm)  algorithms.add(new SwingBandwidth());
        if (Parameters.swingLatencyTwoPortAlgorithm)  algorithms.add(new SwingLatencyTwoPort());
        if (Parameters.swingBandwidthTwoPortAlgorithm)  algorithms.add(new SwingBandwidthTwoPort());
        if (Parameters.trivanceLatencyAlgorithm)  algorithms.add(new TrivanceLatency());
        if (Parameters.trivanceBandwidthAlgorithm)  algorithms.add(new TrivanceBandwidth());

        return algorithms;
    }

    public static ArrayList<CostFunction> prepareCostFunctions(int messageOverheads) {
        ArrayList<CostFunction> costFunctions = new ArrayList<>();
        if (Parameters.basicCostFunction)  costFunctions.add(new CostFunction(messageOverheads, false));
        if (Parameters.congestionCostFunction)  costFunctions.add(new CostFunction(messageOverheads, true));
        return costFunctions;
    }

    public static void writeCostsToJSON(List<Map.Entry<Integer, CostFunction>> results) throws IOException {
        Map<AlgorithmType, Map<Integer, List<CostFunction>>> grouped = new HashMap<>();

        for (Map.Entry<Integer, CostFunction> entry : results) {
            int size = entry.getKey();
            CostFunction cf = entry.getValue();
            AlgorithmType alg = cf.getAlgorithmType();

            grouped
                    .computeIfAbsent(alg, k -> new TreeMap<>())
                    .computeIfAbsent(size, k -> new ArrayList<>())
                    .add(cf);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        int algCount = 0;
        for (Map.Entry<AlgorithmType, Map<Integer, List<CostFunction>>> algEntry : grouped.entrySet()) {
            AlgorithmType alg = algEntry.getKey();
            Map<Integer, List<CostFunction>> sizeMap = algEntry.getValue();

            List<Integer> sortedSizes = new ArrayList<>(sizeMap.keySet());
            Collections.sort(sortedSizes);

            List<Integer> finalSizes = new ArrayList<>();
            List<Double> latencyList = new ArrayList<>();
            List<Double> bandwidthList = new ArrayList<>();

            for (Integer size : sortedSizes) {
                List<CostFunction> cfs = sizeMap.get(size);
                for (CostFunction cf : cfs) {
                    finalSizes.add(size);
                    latencyList.add(cf.getAlphaCost());
                    bandwidthList.add(Math.round((cf.getBetaCost() / size) * 100.0) / 100.0);
                }
            }

            sb.append("  {\n");
            sb.append("    \"name\": \"").append(alg.toString()).append("\",\n");
            sb.append("    \"network_sizes\": ").append(finalSizes.toString()).append(",\n");
            sb.append("    \"cost_latency\": ").append(latencyList.toString()).append(",\n");
            sb.append("    \"cost_bandwidth\": ").append(bandwidthList.toString()).append("\n");
            sb.append("  }");

            if (++algCount < grouped.size()) sb.append(",");
            sb.append("\n");
        }

        sb.append("]\n");

        try (FileWriter fw = new FileWriter("./algorithm-simulation/results/results.json")) {
            fw.write(sb.toString());
        }
    }
}
