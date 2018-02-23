import java.util.Arrays;


public class TestProtocolAlpha {

	static int numObservations = 10;
	static int numActions = 3;
	
	static double epsilon = 0.2;
	static double mup, mun;
	
	static int interval = 1;
	static int terminationWindow = 1;
	static int trials = 50;
	
	static ReactiveAgent agent;
	
	static int[] program;
	
	//data collection
	
	static int steps;
	static double[] history;
	static double[][][] statistics;
	static int currentAgent;
	
	static double[][] params = {
		{0.2,0.2},
		{0.2,0.8},
		{0.8,0.2}
		/*{0.8,0.8}*/
	};
	
	public static void main(String[] args) {

		statistics = new double[1 + params.length][params.length][2];
		history = new double[trials];
		
		program = randomProblem();
		
		agent = new EMInferenceAgent(numObservations, numActions, 10, epsilon);
		currentAgent = 0;
		testAgent(agent);
		
		for(int i=0; i < params.length; ++i){
			agent = new FixedAgent(numObservations, numActions, epsilon, params[i][0], params[i][1]);
			currentAgent = i + 1;
			testAgent(agent);
		}
		
		printStatistics();
	}
	
	private static void testAgent(ReactiveAgent a){
		
		agent = a;
		
		double mean = 0;
		double std;
		
		for(int j=0; j < params.length; ++j){
			
			System.out.println("TESTING WITH PARAMETER SET " + j);
			
			mup = params[j][0];
			mun = params[j][1];
			
			for(int i=0; i < trials; ++i){
				trial();
				history[i] = steps;
				mean += steps;
			}
			
			mean = mean/trials;
			std = 0;
			
			for(int i=0; i < history.length; ++i)
				std += (history[i] - mean)*(history[i] - mean);
			
			statistics[currentAgent][j][0] = mean;
			statistics[currentAgent][j][1] = Math.sqrt(std/trials);
		}
	}
	
	private static void trial(){
		
		System.out.println("NEW TRIAL");
		
		agent.reset();
		
		int correctFor = 0;
		steps = 0;
		
		int[] hypothesis;
		
		while(correctFor < terminationWindow){
			
			episode();
			
			++steps;
			
			if(steps % interval == 0){
				
				System.out.println("LEARNING");
				System.out.println("program: " + Arrays.toString(program));
				
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
		int observation = (int)Math.floor(Math.random()*numObservations);
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
	
	private static int[] randomProblem(){
		int[] problem = new int[numObservations];
		
		for(int i=0; i < numObservations; ++i)
			problem[i] = (int)Math.floor(Math.random()*numActions);
		
		return problem;
	}
	
	private static void printStatistics(){
		String line;
		
		for(int i=0; i < statistics.length; ++i){
			line = "";
			
			for(int j=0; j < statistics[i].length; ++j)
				line += "(" + statistics[i][j][0] + "|" + statistics[i][j][1] + ")";
			
			System.out.println(line);
		}
	}
	
	private static void initParameters(){
		
	}
}
