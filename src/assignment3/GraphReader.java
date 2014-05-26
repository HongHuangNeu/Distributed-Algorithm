package assignment3;

import java.io.File;
import java.util.Scanner;

public class GraphReader {
	
	public static float[][] readGraph(String fileName) {
		try {
			//read file
			File file = new File(fileName);
			//into a scanner
			Scanner s = new Scanner(file);
			
			//number of nodes
			int n = s.nextInt();
			//of the graph
			float[][] graph = new float[n][n];
			
			for(int row = 0; row < n; row++) {
				for(int col = 0; col < n; col++) {
					graph[row][col] = dist(s.next());
				}
			}
			
			return graph;
			
		}
		catch (Exception e){
			e.printStackTrace();
			return new float[0][0];
		}
	}
	
	//translate a distance string representation to a float x = max
	public static float dist(String distRep) {
		if(distRep.equals("x")) {
			return Float.MAX_VALUE;
		}
		else {
			return Float.parseFloat(distRep);
		}
	}
}
