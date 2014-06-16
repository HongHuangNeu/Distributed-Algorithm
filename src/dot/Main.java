package dot;

import java.io.*;

/**
 * Created by ferdy on 6/16/14.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        String graphIn = args[0];
        String mstIn = args[1];
        String pngName = args[2];

//        Writer writer = new FileWriter(new File(pngName));
        Runtime rt = Runtime.getRuntime();
        Process dotProcess = rt.exec("dot -Kcirco -Tpng -o " + pngName);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                dotProcess.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                dotProcess.getInputStream()));
        DotTreeWriter.writeTree(graphIn, mstIn, writer);
        writer.close();
        dotProcess.waitFor();
        System.out.println(dotProcess.exitValue());
        System.out.println("graph written");
    }
}