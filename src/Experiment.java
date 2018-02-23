
import java.util.ArrayList;

public class Experiment {

	//statistics
	/*int numEpisodes;
	
	int numCorrectRewards;
	int numCorrectPunishments;
	int numCorrectNeutral;

	int numIncorrectRewards;
	int numIncorrectPunishments;
	int numIncorrectNeutral;*/
	
	int algorithm;
	
	ArrayList<int[]> history;
	
	ArrayList<String[]> lines;
	
	ExitSurvey survey = null;
	
	boolean complete = true;
	
	int[] correctPolicy = {0,0,0,1,1,1,2,2,2,3,3,3};
	
	public Experiment(ArrayList<String[]> log){
		lines = log;
		
		if(lines.get(0)[5].equals("EM"))
			algorithm = 0;
		else if(lines.get(0)[5].equals("Bayesian"))
			algorithm = 1;
		else if(lines.get(0)[5].equals("TAMER"))
			algorithm = 2;
		else
			algorithm = 3;
	}
	
	public int stepsTillRatio(double ratio){
		
		int count = 0;
		int[] policy;
		int c;
		int threshold = (int)Math.ceil(12*ratio);
		
		for(int i=0; i < lines.size(); ++i){
			if(lines.get(i)[1].equals("STATE")){
				++count;
				
				policy = parsePolicy(lines.get(i)[2]);
				
				c = 0;
				
				for(int j=0; j < policy.length; ++j)
					if(policy[j] == correctPolicy[j])
						++c;
				
				if(c >= threshold)
					return count;
			}
		}
		
		return -1;
	}
	
	public int stepsTillComplete(){
		int count = 0;
		
		for(int i=0; i < lines.size(); ++i)
			if(lines.get(i)[1].equals("STATE"))
				++count;
		
		return count;
	}
	
	public void loadExitSurvey(String id, int s, String path){
		survey = new ExitSurvey(id, s, path);
	}
	
	public double[] getMu(){
		double pc = 0;
		double nc = 0;
		double cc = 0;
		double ic = 0;
		
		boolean correct = false;
		boolean neutral = false;
		
		String[] line;
		
		for(int i=0; i < lines.size(); ++i){
			line = lines.get(i);
			
			if(line[1].equals("EPISODE")){
				if(neutral){
					if(correct)
						pc += 1;
					else
						nc += 1;
				}

				neutral = true;
				
				correct = (Integer.parseInt(line[3]) == correctPolicy[Integer.parseInt(line[2])]);
				
				if(correct)
					cc += 1;
				else
					ic += 1;
			}
			else if(line[1].equals("COMMAND") && (line[2].equals("reward") || line[2].equals("punish")))
				neutral = false;
		}
		
		double[] mus = new double[4];
		
		mus[0] = cc;
		mus[1] = ic;
		mus[2] = pc;
		mus[3] = nc;
		
		return mus;
	}
	
	public double[] getDeltaMu(int win){
		
		double pc = 0;
		double nc = 0;
		double cc = 0;
		double ic = 0;
		
		String[] line;
		
		int c = 0;
		
		for(int i=0; i < lines.size(); ++i){
			line = lines.get(i);
			
			if(line[1].equals("EPISODE"))
				++c;
		}
		
		if(c < win)
			return new double[]{-1};
		
		boolean[][] h = new boolean[c][2];
		double[][] m = new double[c-win][2];
		c = 0;
		
		for(int i=0; i < lines.size(); ++i){
			line = lines.get(i);
			
			if(line[1].equals("EPISODE")){
				h[c][1] = (line[2].equals(line[3])); //WRONG!!!
				++c;
				
				if(c < h.length)
					h[c][0] = true;
			}
			else if(line[1].equals("COMMAND") && (line[2].equals("reward") || line[2].equals("punish")) && c < h.length)
				h[c][0] = false;
		}
		
		double mup = 0;
		double mun = 0;
		double cp = 0;
		double cn = 0;
		
		for(int i=0; i < h.length-win; ++i){
			pc = nc = cc = ic = 0;
			
			for(int j=0; j < win; ++j){
				
				if(h[i+j][1]){
					cc += 1;
					
					if(h[i+j][0])
						pc += 1;
				}
				else{
					ic += 1;
					
					if(h[i+j][0])
						nc += 1;
				}
			}
			
			m[i][0] = pc/cc;
			m[i][1] = nc/ic;
			
			if(cc != 0){
				mup += m[i][0];
				cp += 1;
			}
			
			if(ic != 0){
				mun += m[i][1];
				cn += 1;
			}
		}
		
		//System.out.println(mup + " " + mun);
		
		mup = mup/cp;
		mun = mun/cn;

		//System.out.println(mup + " " + mun);		
		
		double[] mdiff = new double[2];
		
		for(int i=0; i < m.length-win; ++i){
			if(!Double.isInfinite(m[i][0]) && !Double.isNaN(m[i][0]))
				mdiff[0] += (mup-m[i][0])*(mup-m[i][0]);
				
			if(!Double.isInfinite(m[i][1]) && !Double.isNaN(m[i][1]))
				mdiff[1] += (mun-m[i][1])*(mun-m[i][1]);
		}
		
		mdiff[0] = Math.sqrt(mdiff[0]/cp);
		mdiff[1] = Math.sqrt(mdiff[1]/cn);
		
		return mdiff;
	}
	
	public int[] getcorrections(){
		int episodes = 0;
		int trainedEpisodes = 0;
		int feedbacks = 0;
		int corrections = 0;
		int cumulative = 0;
		int last = 0; 
		
		String[] line;
		
		for(int i=0; i < lines.size(); ++i){
			line = lines.get(i);
			
			if(line[1].equals("EPISODE")){
				++episodes;
				
				if(last != 0)
					++trainedEpisodes;
				
				if((last == 1 && cumulative <= 0) || (last == -1 && cumulative >= 0))
					++corrections;
				
				last = cumulative = 0;
			}
			else if(line[1].equals("COMMAND")){
				if(line[2].equals("reward")){
					++cumulative;
					last = 1;
					++feedbacks;
				}
				else if(line[2].equals("punish")){
					--cumulative;
					last = -1;
					++feedbacks;
				}
			}
		}
		
		return new int[]{episodes, trainedEpisodes, feedbacks, corrections};
	}
	
	private int[] parsePolicy(String p){
		
		int[] policy;
		
		p = p.substring(1, p.length()-1);
		
		String[] as = p.split(",");
		
		policy = new int[as.length];
		
		for(int i=0; i < as.length; ++i)
			policy[i] = Integer.valueOf(as[i]);
		
		return policy;
	}
	
	public ArrayList<Integer>[] neutralHistory(){
		
		ArrayList<Integer> neutralOverTime = new ArrayList<Integer>();
		ArrayList<Integer> correctNeutralOverTime = new ArrayList<Integer>();
		ArrayList<Integer> incorrectNeutralOverTime = new ArrayList<Integer>();
		ArrayList<Integer> correctNeutralOverTotal = new ArrayList<Integer>();
		ArrayList<Integer> incorrectNeutralOverTotal = new ArrayList<Integer>();
		
		int totalNeutralFeedback = 0;
		int totalCorrectNeutralFeedback = 0;
		int totalIncorrectNeutralFeedback = 0;
		
		boolean isNeutral = false;
		boolean isCorrect = false;
		boolean isFirst = true;
		
		String[] line;
		
		for(int i=0; i < lines.size(); ++i){
			line = lines.get(i);
			
			if(line[1].equals("EPISODE")){
				
				if(!isFirst){
					if(isNeutral){
						
						++totalNeutralFeedback;
						
						if(isCorrect)
							++totalCorrectNeutralFeedback;
						else
							++totalIncorrectNeutralFeedback;
						
						correctNeutralOverTotal.add(totalCorrectNeutralFeedback);
						incorrectNeutralOverTotal.add(totalIncorrectNeutralFeedback);
					}
					
					neutralOverTime.add(totalNeutralFeedback);
					correctNeutralOverTime.add(totalCorrectNeutralFeedback);
					incorrectNeutralOverTime.add(totalIncorrectNeutralFeedback);
				}
				
				isFirst = false;
				
				isCorrect = (correctPolicy[Integer.parseInt(line[2])] == Integer.parseInt(line[3]));
				
				isNeutral = true;
				
			}
			else if(line[1].equals("COMMAND") && (line[2].equals("reward") || line[2].equals("punish"))){
				isNeutral = false;
			}
		}
		
		ArrayList<Integer>[] data = new ArrayList[5];
		
		data[0] = neutralOverTime;
		data[1] = correctNeutralOverTime;
		data[2] = incorrectNeutralOverTime;
		data[3] = correctNeutralOverTotal;
		data[4] = incorrectNeutralOverTotal;
		
		return data;
	}
	
	public double neutralRatio(){
		
		int totalEpisodes = 0;
		int totalNeutralFeedback = 0;

		boolean isNeutral = false;
		boolean isFirst = true;
		
		String[] line;
		
		for(int i=0; i < lines.size(); ++i){
			line = lines.get(i);
			
			if(line[1].equals("EPISODE")){
				
				if(!isFirst){
					++totalEpisodes;
					
					if(isNeutral)
						++totalNeutralFeedback;
				}
				
				isFirst = false;
				isNeutral = true;
				
			}
			else if(line[1].equals("COMMAND") && (line[2].equals("reward") || line[2].equals("punish"))){
				isNeutral = false;
			}
		}
		
		return ((double)totalNeutralFeedback)/((double)totalEpisodes);
	}
}
