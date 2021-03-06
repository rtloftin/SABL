import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

public class TestProtocolGamma {

	//Parameters
	static int numProblems = 5;
	static int trials = 25;
	static int interval = 1;
	static int terminationWindow = 1;
	static int failpoint = 5000;
	
	static int[] numObservations = {2, 5, 10, 15, 20};
	static int[] numActions = {2, 3, 4};
	static double[][] agents = {
		{0.1, 0.1},
		{0.2, 0.8},
		{0.4, 0.6},
		{0.6, 0.4},
		{0.8, 0.2}
	};
	
	static double[][] params = {
		{0.0, 0.0},
		{0.2, 0.0},
		{0.4, 0.0},
		{0.6, 0.0},
		{0.8, 0.0},
		{0.0, 0.2},
		{0.2, 0.2},
		{0.4, 0.2},
		{0.6, 0.2},
		{0.8, 0.2},
		{0.0, 0.4},
		{0.2, 0.4},
		{0.4, 0.4},
		{0.6, 0.4},
		{0.0, 0.6},
		{0.2, 0.6},
		{0.4, 0.6},
		{0.0, 0.8},
		{0.2, 0.8}
	};
	
	static int[][][][] problemSet = {
		{
			{{1, 0}, {1, 0}, {0, 0}, {0, 1}, {1, 0}},
			{{0, 2}, {0, 1}, {2, 1}, {0, 0}, {1, 1}},
			{{3, 2}, {3, 3}, {1, 0}, {1, 0}, {0, 2}}
		},
		{
			{{0, 1, 0, 1, 0}, {0, 1, 1, 1, 1}, {1, 1, 0, 0, 1}, {1, 1, 1, 0, 0}, {1, 0, 1, 0, 1}},
			{{2, 0, 1, 0, 2}, {1, 2, 1, 2, 0}, {2, 1, 1, 1, 0}, {0, 0, 2, 2, 0}, {1, 2, 2, 1, 2}},
			{{0, 1, 3, 3, 2}, {1, 0, 0, 2, 0}, {1, 2, 1, 3, 1}, {2, 2, 1, 2, 1}, {0, 2, 2, 1, 2}}
		},
		{
			{
				{1, 1, 1, 0, 1, 0, 0, 1, 0, 0},
				{1, 0, 1, 0, 0, 1, 1, 0, 1, 0},
				{0, 0, 1, 1, 0, 1, 1, 0, 0, 1},
				{0, 0, 0, 0, 1, 1, 1, 1, 0, 1},
				{0, 1, 1, 0, 0, 1, 1, 1, 0, 1}
			},
			{
				{1, 2, 2, 1, 0, 2, 0, 1, 1, 2},
				{2, 1, 2, 2, 1, 0, 0, 1, 0, 1},
				{2, 2, 1, 0, 1, 0, 0, 2, 2, 0},
				{0, 2, 0, 2, 0, 2, 2, 2, 1, 1},
				{2, 0, 2, 0, 1, 2, 1, 0, 0, 0}
			},
			{
				{0, 2, 2, 2, 1, 2, 3, 0, 1, 3},
				{1, 3, 1, 1, 1, 2, 3, 0, 2, 2},
				{2, 3, 1, 1, 2, 1, 2, 3, 2, 1},
				{3, 3, 3, 3, 0, 0, 1, 3, 2, 0},
				{2, 0, 3, 3, 0, 2, 0, 2, 3, 3}
			}
		},
		{
			{
				{1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0},
				{1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1},
				{1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1},
				{1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1},
				{1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1}
			},
			{
				{0, 2, 1, 0, 1, 2, 1, 2, 0, 1, 0, 0, 0, 2, 2},
				{2, 1, 2, 2, 2, 1, 2, 0, 1, 1, 1, 1, 0, 1, 2},
				{1, 2, 0, 1, 0, 2, 1, 0, 0, 1, 1, 1, 2, 2, 1},
				{1, 2, 2, 1, 0, 2, 0, 0, 0, 1, 0, 2, 0, 1, 1},
				{2, 2, 2, 0, 2, 0, 1, 2, 1, 0, 0, 1, 0, 2, 0}
			},
			{
				{1, 2, 1, 2, 0, 3, 0, 2, 1, 0, 0, 3, 3, 0, 2},
				{1, 0, 2, 2, 0, 0, 1, 0, 2, 2, 2, 0, 3, 2, 3},
				{1, 0, 0, 3, 3, 1, 2, 0, 0, 1, 3, 1, 0, 0, 0},
				{1, 2, 2, 2, 1, 3, 3, 2, 3, 2, 0, 0, 2, 1, 2},
				{2, 2, 0, 3, 3, 1, 0, 0, 2, 1, 0, 2, 3, 1, 1}
			}
		},
		{
			{
				{1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0},
				{1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1},
				{0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1},
				{1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0},
				{1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0}
			},
			{
				{0, 1, 2, 2, 1, 2, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 2, 1, 1},
				{0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 2, 0, 2, 1, 2, 0, 2, 1, 1, 2},
				{2, 2, 1, 1, 1, 2, 2, 2, 2, 0, 2, 0, 0, 0, 0, 1, 1, 0, 2, 0},
				{2, 0, 2, 2, 1, 1, 1, 1, 1, 2, 0, 0, 0, 2, 1, 0, 0, 0, 2, 1},
				{2, 0, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 0, 0, 0, 2, 2, 0, 2, 2}
			},
			{
				{0, 2, 0, 2, 0, 2, 3, 0, 1, 2, 2, 1, 3, 3, 2, 1, 2, 3, 1, 1},
				{0, 2, 3, 2, 1, 1, 0, 0, 0, 0, 3, 1, 3, 3, 1, 1, 3, 1, 3, 2},
				{1, 0, 1, 3, 1, 3, 2, 2, 1, 2, 2, 1, 3, 1, 0, 0, 2, 2, 2, 1},
				{0, 0, 1, 2, 1, 2, 2, 2, 1, 0, 0, 2, 3, 0, 0, 3, 1, 3, 2, 0},
				{3, 0, 3, 3, 0, 0, 3, 0, 1, 1, 2, 3, 3, 3, 2, 2, 0, 2, 0, 0}
			}
		}
	};
	
	static String path = "";
	
	static int[][] problems;
	
	static int scale;
	static int window = -1;
	static int flip = -1;
	static double eps = 0.2;
	
	static double[] epsilons = {0.01, 0.1, 0.2, 0.3};
	static double epsilon;
	static double mup, mun;
	static int problem;
	static int point;
	
	static ReactiveAgent agent;
	
	static int[] program;
	
	//data collection
	
	static int steps;
	static double[][] history;
	static FileWriter file;
	
	public static void main(String[] args) {		
		
		path = args[0] + "/";
		scale = Integer.parseInt(args[1]);
		//window = Integer.parseInt(args[2]);
		//eps = Double.parseDouble(args[3]);
		
		/*if(args.length > 4)
			flip = Integer.parseInt(args[4]);*/
		
		history = new double[numProblems][trials];
		
		for(int i=0; i < epsilons.length; ++i){
			epsilon = epsilons[i];
			
			for(int l=0; l < epsilons.length; ++l){
				for(int j=0; j < numActions.length; ++j){
	
					problems = problemSet[scale][j];
				
					agent = new GammaEMInferenceAgent(numObservations[scale], numActions[j], 10, epsilons[l], eps, window);
					loadFile("uniform_EM_agent_" + epsilon + "_" + epsilons[l] + "_" + eps + "_" + window, numObservations[scale], numActions[j]);
					testAgent(agent);

					try{
						file.close();
					}catch(Exception e){System.out.println(e.getMessage());}
		
					for(int k=0; k < agents.length; ++k){
						agent = new GammaFixedAgent(numObservations[scale], numActions[j], epsilons[l], agents[k][0], agents[k][1], eps);
						loadFile("fixed_agent_" + epsilon + "_" + epsilons[l] + "_" + eps + "_" + agents[k][0] + "_" + agents[k][1] + "_" + eps, numObservations[scale], numActions[j]);
						testAgent(agent);
				
						try{
							file.close();
						}catch(Exception e){System.out.println(e.getMessage());}
					}
				}
			}
		}
	}
	
	private static void testAgent(ReactiveAgent a){
		
		agent = a;
		
		double mean;
		double std;
		
		for(point=0; point < params.length; ++point){
			
			mean = std = 0;
			
			for(problem = 0; problem < numProblems; ++problem){
				
				//System.out.println("PROBLEM " + problem);
				
				program = problems[problem];
				
				for(int i=0; i < trials; ++i){
					trial();
					
					history[problem][i] = steps;
					mean += steps;
				}
			}
			
			mean = mean/(trials*numProblems);
			
			for(int i=0; i < history.length; ++i)
				for(int j=0; j < history[i].length; ++j)
				std += (history[i][j] - mean)*(history[i][j] - mean);
			
			printTrial(params[point][0], params[point][1], mean, Math.sqrt(std/(trials*numProblems)));
		}
	}
	
	private static void trial(){
		
		agent.reset();
		
		mup = params[point][0];
		mun = params[point][1];
		
		int correctFor = 0;
		steps = 0;
		
		int[] hypothesis = null;
		
		while(correctFor < terminationWindow && steps < failpoint){
			
			/*if(steps == flip){
				mup = params[point][1];
				mun = params[point][0];
			}*/
			
			episode();
			
			++steps;			
			
			if(steps % interval == 0){
				
				agent.learn();
				
				hypothesis = agent.getHypothesis();
				
				if(Arrays.equals(hypothesis, program))
					++correctFor;
				else
					correctFor = 0;
			}
		}
	}

	private static void episode(){
		int observation = (int)Math.floor(Math.random()*numObservations[scale]);
		int action = agent.getAction(observation);
		int signal;
		
		boolean correct = (action == program[observation]); 
			
		if(Math.random() < epsilon)
			correct = !correct;
		
		if(correct)
			signal = (Math.random() < mup) ? 0 : 1;
		else
			signal = (Math.random() < mun) ? 0 : -1;
		
		agent.feedback(observation, action, signal);
	}
	
	private static void loadFile(String agentName, int observations, int actions){
		try{
			File f;
			int id = 0;
			
			do{
				f = new File(path + agentName + "_" + observations + "_" + actions + "_" + id);
				++id;
			}while(f.exists());
			
			file = new FileWriter(f);
		}catch(Exception e){
			System.out.println("Help Me Landru! I cannot create data file: " + e.getMessage());
		}
	}
	
	private static void printTrial(double mp, double mn, double time, double sd){
		try{
			file.write(mp + " " + mn + " " + time + " " + sd + "\n");
			file.flush();
		}catch(Exception e){
			System.out.println("Help Me Landru! I cannot write to data file: " + e.getMessage());
		}
	}
		
	private static void randomProblems(int o, int a){
		problems = new int[numProblems][o];
		
		for(int j=0; j < numProblems; ++j)
			for(int i=0; i < o; ++i)
				problems[j][i] = (int)Math.floor(Math.random()*a);
	}
}
