package dot;

import distribute.GraphReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by ferdy on 6/16/14.
 */
public class DotTreeWriter {
    public static void writeTree(String graphFile, String mstFile, Writer to) {
        float[][] graph = GraphReader.readGraph(graphFile);
        float[][] mst = GraphReader.readGraph(mstFile);

        writeDotTree(graph, mst, to);
    }

    private static void writeDotTree(float[][] graph, float[][] mst, Writer to) {
        //write header
        try {
            to.write("graph {\n");

            int n = graph.length;

            for (int y = 0; y < n; y++) {
                for (int x = 0; x < y; x++) {
                    to.write(edgeToDot(x, y, graph[y][x], mst[y][x]));
                }
            }
            System.out.println("writen edges");
            //write footer
            to.write("}\n");
            to.flush();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String edgeToDot(int x, int y, float graphWeight, float mstWeight) {
        if (graphWeight == Float.MAX_VALUE) {
            //not an edge
            return "";
        }

        else {
            //edge exists
            String edge = "\t" + x + " -- " + y + "[label=\"" + Float.toString(mstWeight) + "\"";

            if (graphWeight < mstWeight) {
                //in mst
                edge += ",color=red,penwidth=0.2";
            }

            edge += "];\n";
            return edge;
        }
    }
}
