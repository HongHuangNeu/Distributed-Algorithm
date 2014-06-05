package ghs.util;

import java.io.File;
import java.util.Scanner;

public class GraphReader {

    public static double[][] readGraph(String fileName) {
        try {
            //read file
            File file = new File(fileName);
            //into a scanner
            Scanner s = new Scanner(file);

            //number of nodes
            int n = s.nextInt();
            //of the graph
            double[][] graph = new double[n][n];

            for (int row = 0; row < n; row++) {
                graph[row][row] = dist("x");
                for (int col = 0; col < row; col++) {
                    double weight = dist(s.next());
                    graph[row][col] = weight;
                    graph[col][row] = weight;
                }
            }

            return graph;

        } catch (Exception e) {
            return new double[0][0];
        }
    }

    //translate a distance string representation to a float x = max
    public static double dist(String distRep) {
        if (distRep.equals("x")) {
            return Double.MAX_VALUE;
        } else {
            return Double.parseDouble(distRep);
        }
    }
}
