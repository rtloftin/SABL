
import java.io.*;

public class EpsilonAnalysis {

	static double[] epsilons = {0.01, 0.1, 0.2, 0.3};
	
	public static void main(String[] args) throws Exception {

		int scale = Integer.parseInt(args[0]);
		int action = Integer.parseInt(args[1]);
		
		System.out.println("results for scale: " + scale + " action: " + action);
		
		forAllMu(action, scale);
	}

	public static void forFixedMu(double mu, int action, int scale){
		
	}
	
	public static void forAllMu(int action, int scale) throws Exception {
		double total;
		double count;
		
		BufferedReader inFile;
		BufferedWriter outFile;
		
		//EM results
		outFile = new BufferedWriter(new FileWriter("forAllMu_EM" + scale + "_" + action));
		
		for(int i=0; i < epsilons.length; ++i){
			for(int j=0; j < epsilons.length; ++j){
				inFile = new BufferedReader(new FileReader("uniform_EM_agent_" + epsilons[i] + "_" + epsilons[j] + "_0.2_-1_" + scale + "_" + action + "_0"));
				total = count = 0;
				
				while(inFile.ready()){
					total += Double.parseDouble(inFile.readLine().split(" ")[2]);
					count += 1;
				}
				
				inFile.close();
				outFile.write(epsilons[i] + " " + epsilons[j] + " " + ((count > 0) ? (total/count) : 0) + "\n");
			}
			
			outFile.newLine();
			outFile.flush();
		}
		
		outFile.close();
		
		//Fixed results
		outFile = new BufferedWriter(new FileWriter("forAllMu_Fixed" + scale + "_" + action));
				
		for(int i=0; i < epsilons.length; ++i){
			for(int j=0; j < epsilons.length; ++j){
				inFile = new BufferedReader(new FileReader("fixed_agent_" + epsilons[i] + "_" + epsilons[j] + "_0.2_0.1_0.1_0.2_" + scale + "_" + action + "_0"));
				total = count = 0;
						
				while(inFile.ready()){
					total += Double.parseDouble(inFile.readLine().split(" ")[2]);
					count += 1;
				}
						
				inFile.close();
				outFile.write(epsilons[i] + " " + epsilons[j] + " " + ((count > 0) ? (total/count) : 0) + "\n");
			}
					
			outFile.newLine();
			outFile.flush();
		}
				
		outFile.close();
	}
}
