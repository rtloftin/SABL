
import java.util.ArrayList;
import java.util.Arrays;

public class EMInferenceAgent implements ReactiveAgent {

	//Parameters
	
	int maxIterations = 100;
	
	int numObservations;
	int numActions;
	
	double epsilon;
	
	//Data Structures
	
	ArrayList<int[]> history;
	
	int[] currentHypothesis = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	
	int[] statistics;
	
	//Numerical Integration
	int resolution;
	double du;
	
	double[][][] integrationPoints;
	double[] integrationCoefficients;
	
	//Interface
	
	public EMInferenceAgent(int o, int a, int r, double e){
		numObservations = o;
		numActions = a;
		resolution = r;
		epsilon = e;
		
		initialize();
	}
	
	public int getAction(int observation){
		//return randomAction();
		
		if(epsilon > Math.random())
			return randomAction();
		return currentHypothesis[observation];
	}
	
	public void feedback(int observation, int action, int signal){
		history.add(new int[]{observation, action, signal});
	}
	
	public void learn(){
		findMAPHypothesis();
	}
	
	public int[] getHypothesis(){
		return currentHypothesis;
	}
	
	public void reset(){
		history = new ArrayList<int[]>();
	}
	
	//Learning
		
	private void findMAPHypothesis(){
		
		int[] newHypothesis = new int[numObservations];
		
		for(int i=0; i < numObservations; ++i)
			newHypothesis[i] = randomAction();
		
		int counter = 0;
		
		System.out.println(Arrays.toString(newHypothesis));
		
		do{
			currentHypothesis = newHypothesis;
			newHypothesis = EMUpdate();
			System.out.println(Arrays.toString(newHypothesis) + " " + Arrays.toString(statistics) + " " + Arrays.toString(integrationCoefficients));
			++counter;
		}while(!(Arrays.equals(currentHypothesis, newHypothesis)) && counter < maxIterations);
	}
	
	private int[] EMUpdate(){
		getStatistics();
		getIntegrationCoefficients();
		
		int[] hypothesis  = new int[numObservations];
		
		for(int i=0; i < numObservations; ++i)
			hypothesis[i] = optimize(i);
		
		return hypothesis;
	}
	
	//gets statistics for the current hypothesis.
	private void getStatistics(){
		statistics[0] = statistics[1] = statistics[2] = statistics[3] = 0;
		
		int[] episode;
		
		for(int i=0; i < history.size(); ++i){
			episode = history.get(i);
			
			if(episode[2] == 1)
				++statistics[0];
			else if(episode[2] == -1)
				++statistics[1];
			else if(currentHypothesis[episode[0]] == episode[1])
				++statistics[2];
			else if(currentHypothesis[episode[0]] != episode[1])
				++statistics[3];
		}
	}
	
	//Gets the common coefficients 
	private void getIntegrationCoefficients(){
		double point, t;
		
		integrationCoefficients[0] = integrationCoefficients[1] = 0;
		
		for(int i=0; i < resolution; ++i){
			for(int j=0; j < resolution; ++j){
				point = Math.pow(1-integrationPoints[i][j][0], statistics[0]);
				point *= Math.pow(1-integrationPoints[i][j][1], statistics[1]);
				point *= Math.pow((1-epsilon)*integrationPoints[i][j][0] + epsilon*integrationPoints[i][j][1], statistics[2]);
				point *= Math.pow(epsilon*integrationPoints[i][j][0] + (1-epsilon)*integrationPoints[i][j][1], statistics[3]);
				
				integrationCoefficients[0] += point;
				
				t = (1-epsilon)*integrationPoints[i][j][0] + epsilon*integrationPoints[i][j][1];
				t = t/(epsilon*integrationPoints[i][j][0] + (1-epsilon)*integrationPoints[i][j][1]);
				t = Math.log(t);
				
				integrationCoefficients[1] += t*point;
			}
		}
		
		//MAY need to move within loop for high resolution to avoid overflow
		
		integrationCoefficients[0] *= Math.log((1-epsilon)/epsilon)*du*du;
		integrationCoefficients[1] *= du*du;		
	}
	
	private int optimize(int observation){
		double max = Double.NEGATIVE_INFINITY;
		double value;
		int best = 0;
		
		int[] stats;
		
		for(int i=0; i < numActions; ++i){
			stats = statistics(observation, i);
			
			value = (stats[0] - stats[1])*integrationCoefficients[0] + stats[2]*integrationCoefficients[1];
			
			if(value > max){
				best = i;
				max = value;
			}
		}
		
		return best;
	}
	
	private int[] statistics(int observation, int action){
	
		int[] stats = new int[3];
		
		stats[0] = stats[1] = stats[2] = 0;
		
		int[] episode;
		
		for(int i=0; i < history.size(); ++i){
			episode = history.get(i);
			
			if(episode[0] == observation && episode[1] == action){
				if(episode[2] == 1)
					++stats[0];
				else if(episode[2] == -1)
					++stats[1];
				else if(episode[2] == 0)
					++stats[2];
			}
		}
		
		return stats;
	}
	
	//Utilities
	
	//initialize history list and compute the grid for numerical integration
	private void initialize(){
		history = new ArrayList<int[]>();
		
		statistics = new int[4];
		
		integrationPoints = new double[resolution][resolution][2];
		integrationCoefficients = new double[2];
		du = 1.0/((double)resolution);
		
		double pa = 0;
		double pb = 0;
		
		for(int i=0; i < resolution; ++i){
			pb = 0;
			
			for(int j=0; j < resolution; ++j){
				integrationPoints[i][j][0] = pa;
				integrationPoints[i][j][1] = pb;
				
				pb += du;
			}
			
			pa += du;
		}
		
		integrationPoints[0][0][0] = du*0.1;
		integrationPoints[0][0][1] = du*0.1;
		
		String line;
		
		for(int i=0; i < resolution; ++i){
			line = "";
			
			for(int j=0; j < resolution; ++j)
				line += "(" + integrationPoints[i][j][0] + "|" + integrationPoints[i][j][1] + ")";
			
			System.out.println(line);
		}
	}
	
	private int randomAction(){
		return (int)(Math.floor(numActions*Math.random()));
	}
}
