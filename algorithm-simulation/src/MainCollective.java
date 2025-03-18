import algorithms.Algorithm;
import algorithms.RecursiveDoubling;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainCollective {

    public static void main(String[] args) throws Exception {
        readParameters();
        ArrayList<Algorithm> algorithms = prepareAlgorithms();
        for (int size: Parameters.networkSizes) {
            System.out.println("Network size: " + (1 << size));
            for (Algorithm a: algorithms) {
                Simulator s = new Simulator(size);
                System.out.println(a.getAlgorithmName() + " is being performed");
                s.perform(a);
                Parameters.log("------------------------------------");

            }
        }
    }

    public static void readParameters() {
        try {
            String file = new String(Files.readAllBytes(Paths.get("algorithm-simulation/config/config.json")));
            JSONObject parameters = new JSONObject(file);

            Parameters.logging = parameters.getBoolean("logging");
            Parameters.debug = parameters.getBoolean("debug");
            JSONArray networkSizes = parameters.getJSONArray("network_sizes");
            for (int i = 0; i < networkSizes.length(); i++) {
                Parameters.networkSizes.add(networkSizes.getInt(i));
            }
            Parameters.recursiveDoubling = parameters.getBoolean("recursive_doubling");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Algorithm> prepareAlgorithms() {
        ArrayList<Algorithm> algorithms = new ArrayList<>();
        if (Parameters.recursiveDoubling)  algorithms.add(new RecursiveDoubling());
        return algorithms;
    }
}
