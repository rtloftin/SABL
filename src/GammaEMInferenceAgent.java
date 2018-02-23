
import java.util.ArrayList;
import java.util.Arrays;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class GammaEMInferenceAgent implements ReactiveAgent {

	//Parameters
	
	int maxIterations = 100;
	
	int numObservations;
	int numActions;
	
	int window;
	double eps;
	
	double epsilon;
	
	//Data Structures
	
	ArrayList<int[]> history;
	
	int[] currentHypothesis;
	
	int[] statistics;
	
	//Numerical Integration
	int resolution;
	double du;
	
	double[][][] integrationPoints;
	BigDecimal[][] t;
	BigDecimal[] integrationCoefficients;
	
	MathContext mc;
	
	//Interface
	
	public GammaEMInferenceAgent(int o, int a, int r, double e, double ep, int w){
		numObservations = o;
		numActions = a;
		resolution = r;
		epsilon = e;
		eps = ep;
		window = w;
		
		mc = MathContext.DECIMAL128;
		
		currentHypothesis = new int[numObservations];
		
		initialize();
	}
	
	public int getAction(int observation){
		if(eps > Math.random())
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
		
		do{
			currentHypothesis = newHypothesis;
			newHypothesis = EMUpdate();
			//System.out.println("-----" + Arrays.toString(newHypothesis));
			//System.out.println("-----" + integrationCoefficients[0] + " " + integrationCoefficients[1]);
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
		
		int s = history.size();
		int win = (window > 0) ? window : s;
		
		for(int i=1; i <= s; ++i){
			episode = history.get(s-i);
			
			if(episode[2] == 1)
				++statistics[0];
			else if(episode[2] == -1)
				++statistics[1];
			else if(i <= win){
				if(currentHypothesis[episode[0]] == episode[1])
					++statistics[2];
				else
					++statistics[3];
			}
		}
	}
	
	//Gets the common coefficients 
	private void getIntegrationCoefficients(){
		BigDecimal point;
		
		integrationCoefficients[0] = BigDecimal.ZERO;
		integrationCoefficients[1] = BigDecimal.ZERO;
		
		for(int i=0; i < resolution; ++i){
			for(int j=0; j < resolution; ++j){
				point = (new BigDecimal(1-integrationPoints[i][j][0])).pow(statistics[0], mc);
				point = point.multiply((new BigDecimal(1-integrationPoints[i][j][1])).pow(statistics[1], mc), mc);
				point = point.multiply((new BigDecimal((1-epsilon)*integrationPoints[i][j][0] + epsilon*integrationPoints[i][j][1])).pow(statistics[2], mc), mc);
				point = point.multiply((new BigDecimal(epsilon*integrationPoints[i][j][0] + (1-epsilon)*integrationPoints[i][j][1])).pow(statistics[3], mc), mc);
				
				integrationCoefficients[0] = integrationCoefficients[0].add(point, mc);
				integrationCoefficients[1] = integrationCoefficients[1].add(t[i][j].multiply(point, mc), mc);
			}
		}
		
		//MAY need to move within loop for high resolution to avoid overflow
		
		integrationCoefficients[0] = integrationCoefficients[0].multiply(new BigDecimal(Math.log((1-epsilon)/epsilon)*du*du), mc);
		integrationCoefficients[1] = integrationCoefficients[1].multiply(new BigDecimal(du*du), mc);		
	}
	
	private int optimize(int observation){
		int best = randomAction();
		
		int[] stats;
		stats = statistics(observation, best);
		
		BigDecimal max = integrationCoefficients[0].multiply(new BigDecimal(stats[0] - stats[1]), mc).add(integrationCoefficients[1].multiply(new BigDecimal(stats[2]), mc), mc);
		BigDecimal value;
		
		for(int i=0; i < numActions; ++i){
			stats = statistics(observation, i);
			
			value = integrationCoefficients[0].multiply(new BigDecimal(stats[0] - stats[1]), mc).add(integrationCoefficients[1].multiply(new BigDecimal(stats[2]), mc), mc);
			
			if(value.compareTo(max) > 0){
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
		
		int s = history.size();
		int win = (window > 0) ? window : s;
		
		for(int i=1; i <= s; ++i){
			episode = history.get(s-i);
			
			if(episode[0] == observation && episode[1] == action){
				if(episode[2] == 1)
					++stats[0];
				else if(episode[2] == -1)
					++stats[1];
				else if(i <= win && episode[2] == 0) //Only consider recent neutral feedback
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
		integrationCoefficients = new BigDecimal[2];
		du = 1.0/((double)resolution);
		
		double pa = 0;
		double pb = 0;
		
		t = new BigDecimal[resolution][resolution];
		
		for(int i=0; i < resolution; ++i){
			pb = 0;
			
			for(int j=0; j < resolution; ++j){
				if(i == 0 && j == 0){
					integrationPoints[0][0][0] = du*0.1;
					integrationPoints[0][0][1] = du*0.1;
				}
				else{
					integrationPoints[i][j][0] = pa;
					integrationPoints[i][j][1] = pb;
				}
				
				t[i][j] = new BigDecimal((1-epsilon)*integrationPoints[i][j][0] + epsilon*integrationPoints[i][j][1]);
				t[i][j] = t[i][j].divide(new BigDecimal(epsilon*integrationPoints[i][j][0] + (1-epsilon)*integrationPoints[i][j][1]), mc);
				t[i][j] = ln(t[i][j]);
				
				pb += du;
			}
			
			pa += du;
		}
	}
	
	private int randomAction(){
		return (int)(Math.floor(numActions*Math.random()));
	}
	
	private BigDecimal ln(BigDecimal d){
		//System.out.println(d.scale());
		//System.out.println(d.unscaledValue().doubleValue());
		return new BigDecimal(Math.log(d.unscaledValue().doubleValue()) - d.scale()*Math.log(10.0));
	}
}
