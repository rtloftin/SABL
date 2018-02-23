
import java.util.ArrayList;

public class GammaFixedAgent implements ReactiveAgent {

	int numObservations;
	int numActions;
	double eps;
	
	double epsilon;
	double mup;
	double mun;
	
	double[][] distro;
	
	int[] episode;
	
	public GammaFixedAgent(int o, int a, double e, double p, double n, double ep){
		numObservations = o;
		numActions = a;
		epsilon = e;
		mup = p;
		mun = n;
		eps = ep;
		
		reset();
	}
	
	public int getAction(int observation) {
		if(eps > Math.random())
			return randomAction();
		return mapAction(observation);
	}

	public void feedback(int observation, int action, int signal) {
		episode = new int[]{observation, action, signal};
	}

	public void learn() {		
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
		
		normalize(distro[episode[0]]);
	}

	public int[] getHypothesis() {
		
		int[] hypothesis = new int[numObservations];

		for(int i=0; i < numObservations; ++i)
			hypothesis[i] = mapAction(i);
		
		return hypothesis;
	}
	
	public void reset(){
		double n = 1.0/numActions;
		
		distro = new double[numObservations][numActions];
		
		for(int i=0; i < numObservations; ++i)
			for(int j=0; j < numActions; ++j)
				distro[i][j] = n;
	}
	
	//utilities
	
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
	
	private int mapAction(int o){
		int best = randomAction();
		double max = distro[o][best];
		
		for(int a=0; a < numActions; ++a){
			if(distro[o][a] > max){
				max = distro[o][a];
				best = a;
			}
		}
		
		return best;
	}
}
