import algorithms.Algorithm;
import algorithms.RecursiveDoubling;

import java.util.List;

public class MainCollective {
    public static void main(String[] args) throws Exception {
        for (int i = 4; i <= 4; i++) {
            System.out.println("Network size: " + (1 << i));
            Simulator s = new Simulator(i);
            Algorithm a = new RecursiveDoubling(1 << i);
            System.out.println(a.getAlgorithmName() + " is being performed");
            List<Node> finalNodes = s.perform(a);
        }
    }
}
