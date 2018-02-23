
import java.util.ArrayList;

public class FixedAgent implements ReactiveAgent {

	int numObservations;
	int numActions;
	
	double epsilon;
	double mup;
	double mun;
	
	double[][] distro;
	
	int[] hypothesis = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	
	ArrayList<int[]> history;
	
	public FixedAgent(int o, int a, double e, double p, double n){
		numObservations = o;
		numActions = a;
		epsilon = e;
		mup = p;
		mun = n;
		
		reset();
	}
	
	public int getAction(int observation) {
		if(epsilon > Math.random())
			return randomAction();
		return hypothesis[observation];
	}

	public void feedback(int observation, int action, int signal) {
		history.add(new int[]{observation, action, signal});
	}

	public void learn() {
		initialize();
		
		int[] episode;
		
		for(int i=0; i < history.size(); ++i){
			episode = history.get(i);
			
			if(episode[2] == 1){
				for(int j=0; j < numActions; ++j){
					if(j == episode[1])
						distro[episode[0]][j] *= (1-epsilon)*(1-mup);
					else
						distro[episode[0]][j] *= epsilon*(1-mup);
				}
			}
			else if(episode[2] == -1){
				for(int j=0; j < numActions; ++j){
					if(j == episode[1])
						distro[episode[0]][j] *= epsilon*(1-mun);
					else
						distro[episode[0]][j] *= (1-epsilon)*(1-mun);
				}
			}
			else{
				for(int j=0; j < numActions; ++j){
					if(j == episode[1])
						distro[episode[0]][j] *= (1-epsilon)*mup + epsilon*mun;
					else
						distro[episode[0]][j] *= epsilon*mup + (1-epsilon)*mun;
				}
			}
		}
		
		for(int i=0; i < numObservations; ++i)
			normalize(distro[i]);
		
		hypothesis = getHypothesis();
	}

	public int[] getHypothesis() {
		
		int[] hypothesis = new int[numObservations];
		
		int best;
		double max;
		
		for(int i=0; i < numObservations; ++i){
			best = 0;
			max = 0;
			
			for(int j=0; j < numActions; ++j)
				if(distro[i][j] > max){
					max = distro[i][j];
					best = j;
				}
			
			hypothesis[i] = best;
		}
		
		return hypothesis;
	}
	
	public void reset(){
		history = new ArrayList<int[]>();
	}
	
	//utilities
	
	private void initialize(){
		double n = 1.0/numActions;
		
		distro = new double[numObservations][numActions];
		
		for(int i=0; i < numObservations; ++i)
			for(int j=0; j < numActions; ++j)
				distro[i][j] = n;
	}
	
	private void normalize(double[] arr){
		double eta = 0;
		
		for(int i=0; i < arr.length; ++i)
			eta += arr[i];
		
		for(int i=0; i < arr.length; ++i)
			arr[i] = arr[i]/eta;
	}
	
	private int randomAction(){
		return (int)Math.floor(Math.random()*numActions);
	}
}
