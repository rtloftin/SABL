
public interface ReactiveAgent {

	public int getAction(int observation);
	
	public void feedback(int observation, int action, int signal);
	
	public void learn();
	
	public int[] getHypothesis();
	
	public void reset();
}
