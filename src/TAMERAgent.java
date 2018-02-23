
public class TAMERAgent implements ReactiveAgent {

	double[][] h;

	int lastObservation;
	int lastAction;
	int lastSignal;
	
	//PARAMETERS
	
	int numActions;
	int numObservations;
	
	double epsilon;
	double alpha;
	
	boolean cobot;

	public TAMERAgent(int o, int a, double e, double l, boolean c){
		numObservations = o;
		numActions = a;
		
		epsilon = e;
		alpha = l;
		
		cobot = c;
		
		reset();
	}
	
	public void feedback(int observation, int action, int signal){
		lastObservation = observation;
		lastAction = action;
		lastSignal = signal;
	}
	
	public void learn(){
		if(lastSignal == 1)
			h[lastObservation][lastAction] += alpha*(1.0 - h[lastObservation][lastAction]);
		else if(lastSignal == -1)
			h[lastObservation][lastAction] += alpha*(-1.0 - h[lastObservation][lastAction]);
		else if(cobot && lastSignal == 0)
			h[lastObservation][lastAction] -= alpha*h[lastObservation][lastAction];
	}
	
	public int[] getHypothesis(){
		int[] hypothesis = new int[numObservations];
		
		for(int i=0; i < numObservations; ++i)
			hypothesis[i] = maxAction(i);
		
		return hypothesis;
	}
	
	public void reset(){
		h = new double[numObservations][numActions];
		
		for(int i=0; i < numObservations; ++i)
			for(int j=0; j < numActions; ++j)
				h[i][j] = 0;
		
		lastAction = 0;
		lastObservation = 0;
		lastSignal = 0;
	}
	
	public int getAction(int observation) {
		if(epsilon > Math.random())
			return randomAction();
		return maxAction(observation);
	}
	
	private int randomAction(){
		return (int)Math.floor(Math.random()*numActions);
	}
	
	private int maxAction(int o){
		int best = randomAction();
		double max = h[o][best];
		
		for(int a=0; a < numActions; ++a){
			if(h[o][a] > max){
				max = h[o][a];
				best = a;
			}
		}
		
		return best;
	}
}
