import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class DataAnalysis {

	private static ArrayList<User> users;
	
	private static int goodUsers;
	
	private static int depth = 1;
	
	private static FileWriter file;
	
	public static void main(String[] args) {
		loadUsers("/home/bamf/study_data/");
		printRData();
		
		//ArrayList<User>[] buckets= allUserBuckets();
		//getANOVAData(buckets);
		
		//userMuHistograms(5, buckets[0], "none");
		//userMuHistograms(5, buckets[1], "some");
		//userMuHistograms(5, buckets[2], "alot");
		//userMuHistograms(5, buckets[3], "expert");
		//userMuHistograms(5, buckets[4], "no-response");
		
		//muStatistics(responseBuckets("gender", new String[]{"male","female"}));
		
		//muStatistics();
		
		//activeUserPerformance(users, 0.1);
		//activeUserPerformance(users, 0.2);
		//activeUserPerformance(users, 0.3);
		//activeUserPerformance(users, 0.4);
		//activeUserPerformance(users, 0.5);
		
		//getMuHistograms(users);
		
		//welchTests();
		
		//ArrayList[] buckets = userBuckets();
		
		//System.out.println("overall ----");
		//getBasicStatistics(users);
		
		//System.out.println("users with training experience ----");
		//getBasicStatistics(buckets[0]);
		//numCorrections(buckets[0]);
		
		//System.out.println("users without training experience ----");
		//getBasicStatistics(buckets[1]);
		//numCorrections(buckets[1]);	
		
		//printStatistics();
		
		//userBuckets();
		
		//getReportedStatistics();
		
		//muBuckets(users);
	}
	
	private static void printRData(){
		//One file has samples for each algorithm, and is unpaired, the other has all data for each user, user is column, data type is row
	
		String[][] lines = {{"", "", "", "", "", "", ""}, {"", "", "", "", "", "", ""}, {"", "", "", "", "", "", ""}};
		
		int[][][] counts = {{{0,0,0},{0,0,0}},{{0,0,0},{0,0,0}},{{0,0,0},{0,0,0}}};
		
		int temp;
		Experiment e;
		
		newFile("/home/bamf/study_data/digest/unpaired_learning_rates");
		
		for(int i=0; i < users.size(); ++i)
			for(int j=0; j < users.get(i).experiments.size() && j < 1; ++j){
				e = users.get(i).experiments.get(j);
				
				if(e.algorithm != 0){
					temp = e.stepsTillRatio(0.5);
					lines[e.algorithm-1][0] += ((-1 == temp) ? 0 : 1) + " ";
				
					if(-1 == temp)
						++counts[0][1][e.algorithm-1];
					else
						++counts[0][0][e.algorithm-1];
				
					if(-1 != temp)
						lines[e.algorithm-1][1] += temp + " ";
				
					temp = e.stepsTillRatio(0.75);
					lines[e.algorithm-1][2] += ((-1 == temp) ? 0 : 1) + " ";
				
					if(-1 == temp)
						++counts[1][1][e.algorithm-1];
					else
						++counts[1][0][e.algorithm-1];
				
					if(-1 != temp)
						lines[e.algorithm-1][3] += temp + " ";
					
					temp = e.stepsTillRatio(1.0);
					lines[e.algorithm-1][4] += ((-1 == temp) ? 0 : 1) + " ";
				
					if(-1 == temp)
						++counts[2][1][e.algorithm-1];
					else
						++counts[2][0][e.algorithm-1];
				
					if(-1 != temp)
						lines[e.algorithm-1][5] += temp + " ";
					
					temp = e.stepsTillComplete();
					lines[e.algorithm-1][6] += temp + " ";
				}
			}
				
		for(int i=0; i < 7; ++i){
			printl(lines[0][i]);
			printl(lines[1][i]);
			printl(lines[2][i]);
		}
		
		save();
		
		newFile("/home/bamf/study_data/digest/chi_tamer_0.5");
		printl(counts[0][0][0] + " " + counts[0][0][1]);
		printl(counts[0][1][0] + " " + counts[0][1][1]);
		save();
		
		newFile("/home/bamf/study_data/digest/chi_tamer_0.75");
		printl(counts[1][0][0] + " " + counts[1][0][1]);
		printl(counts[1][1][0] + " " + counts[1][1][1]);
		save();
		
		newFile("/home/bamf/study_data/digest/chi_tamer_1.0");
		printl(counts[2][0][0] + " " + counts[2][0][1]);
		printl(counts[2][1][0] + " " + counts[2][1][1]);
		save();
		
		newFile("/home/bamf/study_data/digest/chi_cobot_0.5");
		printl(counts[0][0][0] + " " + counts[0][0][2]);
		printl(counts[0][1][0] + " " + counts[0][1][2]);
		save();
		
		newFile("/home/bamf/study_data/digest/chi_cobot_0.75");
		printl(counts[1][0][0] + " " + counts[1][0][2]);
		printl(counts[1][1][0] + " " + counts[1][1][2]);
		save();
		
		newFile("/home/bamf/study_data/digest/chi_cobot_1.0");
		printl(counts[2][0][0] + " " + counts[2][0][2]);
		printl(counts[2][1][0] + " " + counts[2][1][2]);
		save();
	}
	
	private static void printStatistics(){
		double[][][] data = new double[5][][];
		double[][][] datab = new double[5][][];
		String line;
		
		ArrayList[] userGroup = userBuckets();
		
		for(depth = 1; depth <= 4; ++depth){
			
			//data[0] = stepsTillCorrect(users, 0.25);
			data[1] = stepsTillCorrect(users, 0.5);
			data[2] = stepsTillCorrect(users, 0.75);
			data[3] = stepsTillCorrect(users, 1.0);
			data[4] = stepsTillComplete(users);
			
			newFile("/home/bamf/study_data/digest/overall_performance_to_depth_" + depth + "_by_algorithm");
			
			printl("algorithm 50% 75% 100% Terminated");

			line = "Bayesian ";
			for(int i=1; i < 5; ++i)
				line += data[i][1][0] + " ";
			printl(line);
			
			line = "Sim-TAMER ";
			for(int i=1; i < 5; ++i)
				line += data[i][2][0] + " ";
			printl(line);
			
			line = "Sim-COBOT ";
			for(int i=1; i < 5; ++i)
				line += data[i][3][0] + " ";
			printl(line);

			save();
			
			newFile("/home/bamf/study_data/digest/overall_performance_to_depth_" + depth + "_by_criterion");
			
			printl("criterion Bayesian TAMER COBOT");
			
			line = "50% ";
			for(int i=1; i < 4; ++i)
				line += data[1][1][0] + " ";
			printl(line);
			
			line = "75% ";
			for(int i=1; i < 4; ++i)
				line += data[2][i][0] + " ";
			printl(line);
			
			line = "100% ";
			for(int i=1; i < 4; ++i)
				line += data[3][i][0] + " ";
			printl(line);
			
			line = "Terminated ";
			for(int i=1; i < 4; ++i)
				line += data[4][i][0] + " ";
			printl(line);
			
			save();
			
			newFile("/home/bamf/study_data/digest/overall_success_rate_to_depth_" + depth + "_by_criterion");
			
			printl("criterion Bayesian TAMER COBOT");
			
			line = "50% ";
			for(int i=1; i < 4; ++i)
				line += 100*(data[1][i][1]-data[1][i][3])/data[1][i][1]  + " ";
			printl(line);
			
			line = "75% ";
			for(int i=1; i < 4; ++i)
				line += 100*(data[2][i][1]-data[2][i][3])/data[2][i][1]  + " ";
			printl(line);
			
			line = "100% ";
			for(int i=1; i < 4; ++i)
				line += 100*(data[3][i][1]-data[3][i][3])/data[3][i][1]  + " ";
			printl(line);
			
			save();

			data[1] = stepsTillCorrect(userGroup[0], 0.5);
			data[2] = stepsTillCorrect(userGroup[0], 0.75);
			data[3] = stepsTillCorrect(userGroup[0], 1.0);
			data[4] = stepsTillComplete(userGroup[0]);
			
			datab[1] = stepsTillCorrect(userGroup[1], 0.5);
			datab[2] = stepsTillCorrect(userGroup[1], 0.75);
			datab[3] = stepsTillCorrect(userGroup[1], 1.0);
			datab[4] = stepsTillComplete(userGroup[1]);
			
			newFile("/home/bamf/study_data/digest/experience_group_to_depth_" + depth + "_by_algorithm_50");
			
			printl("algorithm none some");
			
			printl("Bayesian " + datab[1][1][0] + " " + data[1][1][0]);
			printl("Sim-TAMER " + datab[1][2][0] + " " + data[1][2][0]);
			printl("Sim-COBOT " + datab[1][3][0] + " " + data[1][3][0]);
		
			save();
			
			newFile("/home/bamf/study_data/digest/experience_group_to_depth_" + depth + "_by_experience_50");
			
			printl("criterion Bayesian TAMER COBOT");
			
			printl("some " + data[1][1][0] + " " + data[1][2][0] + " " + data[1][3][0]);
			printl("none " + datab[1][1][0] + " " + datab[1][2][0] + " " + datab[1][3][0]);
		
			save();
			
			newFile("/home/bamf/study_data/digest/experience_group_to_depth_" + depth + "_by_algorithm_75");
			
			printl("algorithm none some");
			
			printl("Bayesian " + datab[2][1][0] + " " + data[2][1][0]);
			printl("Sim-TAMER " + datab[2][2][0] + " " + data[2][2][0]);
			printl("Sim-COBOT " + datab[2][3][0] + " " + data[2][3][0]);
		
			save();
			
			newFile("/home/bamf/study_data/digest/experience_group_to_depth_" + depth + "_by_experience_75");
			
			printl("criterion Bayesian TAMER COBOT");
			
			printl("some " + data[2][1][0] + " " + data[2][2][0] + " " + data[2][3][0]);
			printl("none " + datab[2][1][0] + " " + datab[2][2][0] + " " + datab[2][3][0]);
		
			save();
			
			newFile("/home/bamf/study_data/digest/experience_group_to_depth_" + depth + "_by_algorithm_100");
			
			printl("algorithm none some");
			
			printl("Bayesian " + datab[3][1][0] + " " + data[3][1][0]);
			printl("Sim-TAMER " + datab[3][2][0] + " " + data[3][2][0]);
			printl("Sim-COBOT " + datab[3][3][0] + " " + data[3][3][0]);
		
			save();
			
			newFile("/home/bamf/study_data/digest/experience_group_to_depth_" + depth + "_by_experience_100");
			
			printl("criterion Bayesian TAMER COBOT");
			
			printl("some " + data[3][1][0] + " " + data[3][2][0] + " " + data[3][3][0]);
			printl("none " + datab[3][1][0] + " " + datab[3][2][0] + " " + datab[3][3][0]);
		
			save();
			
			newFile("/home/bamf/study_data/digest/experience_group_to_depth_" + depth + "_by_algorithm_complete");
			
			printl("algorithm none some");

			printl("Bayesian " + datab[4][1][0] + " " + data[4][1][0]);
			printl("Sim-TAMER " + datab[4][2][0] + " " + data[4][2][0]);
			printl("Sim-COBOT " + datab[4][3][0] + " " + data[4][3][0]);
		
			save();
			
			newFile("/home/bamf/study_data/digest/experience_group_to_depth_" + depth + "_by_experience_complete");
			
			printl("criterion Bayesian TAMER COBOT");
			
			printl("some " + data[4][1][0] + " " + data[4][2][0] + " " + data[4][3][0]);
			printl("none " + datab[4][1][0] + " " + datab[4][2][0] + " " + datab[4][3][0]);
		
			save();
		}
	}
	
	private static void getStatistics(){
		newFile("/home/bamf/study_data/synopsis");
		
		double[][] data;
		
		printl("data format (algorithm, average steps till complete, total experiments, standard deviation, number of failures)");
		
		for(depth = 1; depth < 4; ++depth){
			printl("\nresults for first " + depth + " experiments");
		
			data = stepsTillCorrect(users, 0.25);
			printl("\nsteps till 25% correct:");
			printl("EM " + data[0][0] + " " + data[0][1] + " " + data[0][2] + " " + data[0][3]);
			printl("FIXED " + data[1][0] + " " + data[1][1] + " " + data[1][2] + " " + data[1][3]);
			printl("TAMER " + data[2][0] + " " + data[2][1] + " " + data[2][2] + " " + data[2][3]);
			printl("COBOT " + data[3][0] + " " + data[3][1] + " " + data[3][2] + " " + data[3][3]);
		
			data = stepsTillCorrect(users, 0.5);
			printl("\nsteps till 50% correct:");
			printl("EM " + data[0][0] + " " + data[0][1] + " " + data[0][2] + " " + data[0][3]);
			printl("FIXED " + data[1][0] + " " + data[1][1] + " " + data[1][2] + " " + data[1][3]);
			printl("TAMER " + data[2][0] + " " + data[2][1] + " " + data[2][2] + " " + data[2][3]);
			printl("COBOT " + data[3][0] + " " + data[3][1] + " " + data[3][2] + " " + data[3][3]);
		
			data = stepsTillCorrect(users, 0.75);
			printl("\nsteps till 75% correct:");
			printl("EM " + data[0][0] + " " + data[0][1] + " " + data[0][2] + " " + data[0][3]);
			printl("FIXED " + data[1][0] + " " + data[1][1] + " " + data[1][2] + " " + data[1][3]);
			printl("TAMER " + data[2][0] + " " + data[2][1] + " " + data[2][2] + " " + data[2][3]);
			printl("COBOT " + data[3][0] + " " + data[3][1] + " " + data[3][2] + " " + data[3][3]);
		
			data = stepsTillCorrect(users, 1);
			printl("\nsteps till 100% correct:");
			printl("EM " + data[0][0] + " " + data[0][1] + " " + data[0][2] + " " + data[0][3]);
			printl("FIXED " + data[1][0] + " " + data[1][1] + " " + data[1][2] + " " + data[1][3]);
			printl("TAMER " + data[2][0] + " " + data[2][1] + " " + data[2][2] + " " + data[2][3]);
			printl("COBOT " + data[3][0] + " " + data[3][1] + " " + data[3][2] + " " + data[3][3]);
		
			data = stepsTillComplete(users);
			printl("\nsteps till complete:");
			printl("EM " + data[0][0] + " " + data[0][1] + " " + data[0][2]);
			printl("FIXED " + data[1][0] + " " + data[1][1] + " " + data[1][2]);
			printl("TAMER " + data[2][0] + " " + data[2][1] + " " + data[2][2]);
			printl("COBOT " + data[3][0] + " " + data[3][1] + " " + data[3][2]);
		}
		
		try{
			file.flush();
		}catch(Exception e){System.out.println(e.getMessage());}
	}
	
	private static void loadUsers(String path){
		users = new ArrayList<User>();
		File[] files;
		String[] f;
		
		try{
			files = (new File(path + "/logs")).listFiles();
			
			for(int i=0; i < files.length; ++i){
				f = files[i].getName().split("_");
				
				if(f[0].equals("session")){
					users.add(new User(f[1], path));
				}
			}
		}catch(Exception e){e.getMessage();}
		
		int totalUsers = users.size();
		int oneUsers = 0;
		int twoUsers = 0;
		
		int em = 0;
		int emFirst = 0;
		int bayes = 0;
		int bayesFirst = 0;
		int tamer = 0;
		int tamerFirst = 0;
		int cobot = 0;
		int cobotFirst = 0;
		
		for(int i=0; i < totalUsers; ++i){
			if(users.get(i).experiments.size() > 0)
				++oneUsers;
			if(users.get(i).experiments.size() > 1)
				++twoUsers;
			
			if(users.get(i).experiments.size() > 0){
				if(0 == users.get(i).experiments.get(0).algorithm)
					++emFirst;
				else if(1 == users.get(i).experiments.get(0).algorithm)
					++bayesFirst;
				else if(2 == users.get(i).experiments.get(0).algorithm)
					++tamerFirst;
				else
					++cobotFirst;
			}
			
			for(int j=0; j < 2 && j < users.get(i).experiments.size(); ++j){
				if(0 == users.get(i).experiments.get(0).algorithm)
					++em;
				else if(1 == users.get(i).experiments.get(0).algorithm)
					++bayes;
				else if(2 == users.get(i).experiments.get(0).algorithm)
					++tamer;
				else
					++cobot;
			}
		}
		
		System.out.println("total: " + totalUsers + " one: " + oneUsers + " two: " + twoUsers);
		System.out.println("em: " + em + " first: " + emFirst + " bayes: " + bayes + " first: " + bayesFirst);
		System.out.println("tamer: " + tamer + " first: " + tamerFirst + " cobot: " + cobot + " first: " + cobotFirst);
	}

	private static double[][] stepsTillComplete(ArrayList<User> usrs){
		ArrayList<Integer>[] counts = new ArrayList[4];
		
		for(int i=0; i < 4; ++i)
			counts[i] = new ArrayList<Integer>();
		
		User u;
		Experiment e;
		
		goodUsers = 0;
		
		for(int i=0; i < usrs.size(); ++i){
			u = usrs.get(i);
			
			if(u.experiments.size() > 0)
				++goodUsers;
			
			for(int j=0; j < depth && j < u.experiments.size(); ++j){
				e = u.experiments.get(j);
				
				counts[e.algorithm].add(e.stepsTillComplete());
			}
		}
		
		double[][] data = new double[4][4];
	
		for(int i=1; i < 4; ++i){
			Arrays.fill(data[i], 0);
			
			data[i][1] = counts[i].size();
			
			if(data[i][1] > 0){
				for(int j=0; j < counts[i].size(); ++j)
					data[i][0] += counts[i].get(j);
			
				data[i][0] = data[i][0]/(data[i][1]);
			
				for(int j=0; j < counts[i].size(); ++j)
					data[i][2] += (data[i][0] - counts[i].get(j))*(data[i][0] - counts[i].get(j));
			
				
				data[i][3] = data[i][2]/(data[i][1]-1);
				data[i][2] = Math.sqrt(data[i][2]/data[i][1]);
			}
		}
		
		return data;
	}
	
	private static double[][] stepsTillCorrect(ArrayList<User> usrs, double ratio){
		ArrayList<Integer>[] counts = new ArrayList[4];
		
		for(int i=0; i < 4; ++i)
			counts[i] = new ArrayList<Integer>();
		
		User u;
		Experiment e;
		
		for(int i=0; i < usrs.size(); ++i){
			u = usrs.get(i);
			
			for(int j=0; j < depth && j < u.experiments.size(); ++j){
				e = u.experiments.get(j);
				
				counts[e.algorithm].add(e.stepsTillRatio(ratio));
			}
		}
		
		double[][] data = new double[4][4];
	
		for(int i=1; i < 4; ++i){
			Arrays.fill(data[i], 0);
			
			data[i][1] = counts[i].size();
			
			for(int j=0; j < counts[i].size(); ++j){
				if(counts[i].get(j) == -1)
					data[i][3] += 1;
				else
					data[i][0] += counts[i].get(j);
			}
			
			if(data[i][1] - data[i][3] > 0){
				data[i][0] = data[i][0]/(data[i][1]-data[i][3]);
			
				for(int j=0; j < counts[i].size(); ++j){
					if(counts[i].get(j) != -1)
						data[i][2] += (data[i][0] - counts[i].get(j))*(data[i][0] - counts[i].get(j));
				}
			
				double n = (data[i][1]-data[i][3]);
				//data[i][3] = data[i][2]/(n-1);
				data[i][2] = Math.sqrt(data[i][2]/n);
			}
		}
		
		return data;
	}
	
	private static double[][] stepsTillCorrect(double ratio, ArrayList<Experiment> exps){
		ArrayList<Integer>[] counts = new ArrayList[4];
		
		for(int i=0; i < 4; ++i)
			counts[i] = new ArrayList<Integer>();
		
		Experiment e;
		
		for(int i=0; i < exps.size(); ++i){
			e = exps.get(i);
			counts[e.algorithm].add(e.stepsTillRatio(ratio));
		}
		
		double[][] data = new double[4][4];
	
		for(int i=0; i < 4; ++i){
			Arrays.fill(data[i], 0);
			
			data[i][1] = counts[i].size();
			
			for(int j=0; j < counts[i].size(); ++j){
				if(counts[i].get(j) == -1)
					data[i][3] += 1;
				else
					data[i][0] += counts[i].get(j);
			}
			
			if(data[i][1] - data[i][3] > 0){
				data[i][0] = data[i][0]/(data[i][1]-data[i][3]);
			
				for(int j=0; j < counts[i].size(); ++j){
					if(counts[i].get(j) != -1)
						data[i][2] += (data[i][0] - counts[i].get(j))*(data[i][0] - counts[i].get(j));
				}
			
				data[i][2] = Math.sqrt(data[i][2]/(data[i][1]-data[i][3]));
			}
		}
		
		return data;
	}
	
	private static void muBuckets(ArrayList<User> usrs){
		ArrayList<Experiment> approval = new ArrayList<Experiment>();
		ArrayList<Experiment> disapproval = new ArrayList<Experiment>();
		ArrayList<Experiment> neutral = new ArrayList<Experiment>();
		
		double threshold = 0.1;
		
		User u;
		Experiment e;
		double[] mu;
		
		double mup = 0;
		double mun = 0;
		double dif = 0;
		double c = 0;
		
		for(int i=0; i < users.size(); ++i){
			u = users.get(i);
			
			for(int j=0; j < u.experiments.size(); ++j){
				e = u.experiments.get(j);
				mu = e.getMu();
				
				if(!Double.isInfinite(mu[0]) && !Double.isNaN(mu[0]) && !Double.isInfinite(mu[1]) && !Double.isNaN(mu[1])){
					mup += mu[0];
					mun += mu[1];
					dif += mu[0]-mu[1];
					c += 1;
					
					if(mu[0]-mu[1] <= -1*threshold)
						disapproval.add(e);
					else if(mu[0]-mu[1] >= threshold)
						approval.add(e);
					else
						neutral.add(e);
				}
			}
		}
	
		double[][] data = new double[4][4];
		
		data = stepsTillCorrect(1, approval);
		System.out.println("approval - steps till 75% correct:");
		System.out.println("EM " + data[0][0] + " " + data[0][1] + " " + data[0][2] + " " + data[0][3]);
		System.out.println("FIXED " + data[1][0] + " " + data[1][1] + " " + data[1][2] + " " + data[1][3]);
		System.out.println("TAMER " + data[2][0] + " " + data[2][1] + " " + data[2][2] + " " + data[2][3]);
		System.out.println("COBOT " + data[3][0] + " " + data[3][1] + " " + data[3][2] + " " + data[3][3]);

		data = stepsTillCorrect(1, disapproval);
		System.out.println("disapproval - steps till 75% correct:");
		System.out.println("EM " + data[0][0] + " " + data[0][1] + " " + data[0][2] + " " + data[0][3]);
		System.out.println("FIXED " + data[1][0] + " " + data[1][1] + " " + data[1][2] + " " + data[1][3]);
		System.out.println("TAMER " + data[2][0] + " " + data[2][1] + " " + data[2][2] + " " + data[2][3]);
		System.out.println("COBOT " + data[3][0] + " " + data[3][1] + " " + data[3][2] + " " + data[3][3]);
		
		data = stepsTillCorrect(1, neutral);
		System.out.println("neutral - steps till 75% correct:");
		System.out.println("EM " + data[0][0] + " " + data[0][1] + " " + data[0][2] + " " + data[0][3]);
		System.out.println("FIXED " + data[1][0] + " " + data[1][1] + " " + data[1][2] + " " + data[1][3]);
		System.out.println("TAMER " + data[2][0] + " " + data[2][1] + " " + data[2][2] + " " + data[2][3]);
		System.out.println("COBOT " + data[3][0] + " " + data[3][1] + " " + data[3][2] + " " + data[3][3]);
	
		System.out.println((mup/c) + " " + (mun/c) + " " + (dif/c));
	}
	
	private static void getReportedStatistics(){
		double[][][] data = new double[4][6][6];
		double[][] count = new double[4][6];
		

		
		for(int i=0; i < 4; ++i){
			Arrays.fill(count[i], 0);
			
			for(int j=0; j < 6; ++j)
				Arrays.fill(data[i][j], 0);
		}
			
		User u;
		Experiment e;
		
		for(int i=0; i < users.size(); ++i){
			u = users.get(i);
			
			for(int j=0; j < u.experiments.size() /*&& j < 1 */; ++j){
				e = u.experiments.get(j);
				
				for(int k=0; k < 6; ++k){
					if(e.survey != null){
						count[e.algorithm][k] += 1;
					
						if(e.survey.answers[k].equals("strongly agree"))
							data[e.algorithm][k][0] += 1;
						else if(e.survey.answers[k].equals("agree"))
							data[e.algorithm][k][1] += 1;
						else if(e.survey.answers[k].equals("no opinion"))
							data[e.algorithm][k][2] += 1;
						else if(e.survey.answers[k].equals("disagree"))
							data[e.algorithm][k][3] += 1;
						else if(e.survey.answers[k].equals("strongly disagree"))
							data[e.algorithm][k][4] += 1;
						else
							data[e.algorithm][k][5] += 1;
					}
				}
			}
		}
		
		for(int i=0; i < 4; ++i)
			for(int j=0; j < 6; ++j)
				for(int k=0; k < 6; ++k)
					data[i][j][k] = 100*data[i][j][k]/count[i][j];
		
		int a = 0;
		
		newFile("/home/bamf/study_data/digest/solved_all");
		printl("algorithm EM Bayesian Sim-TAMER Sim-COBOT");
		printl("strongly-agree " + data[0][a][0] + " " + data[1][a][0] + " " + data[2][a][0] + " " + data[3][a][0]);
		printl("agree " + data[0][a][1] + " " + data[1][a][1] + " " + data[2][a][1] + " " + data[3][a][1]);
		printl("no-opinion " + data[0][a][2] + " " + data[1][a][2] + " " + data[2][a][2] + " " + data[3][a][2]);
		printl("disagree " + data[0][a][3] + " " + data[1][a][3] + " " + data[2][a][3] + " " + data[3][a][3]);
		printl("strongly-disagree " + data[0][a][4] + " " + data[1][a][4] + " " + data[2][a][4] + " " + data[3][a][4]);
		
		save();
		
		newFile("/home/bamf/study_data/digest/solved_combined");
		printl("algorithm EM Bayesian Sim-TAMER Sim-COBOT");
		printl("agree " + (data[0][a][0] + data[0][a][1]) + " " + (data[1][a][0] + data[1][a][1]) + " " + (data[2][a][0] + data[2][a][1]) + " " + (data[3][a][0] + data[3][a][1]));
		printl("no-opinion " + data[0][a][2] + " " + data[1][a][2] + " " + data[2][a][2] + " " + data[3][a][2]);
		printl("disagree " + (data[0][a][3] + data[0][a][4]) + " " + (data[1][a][3] + data[1][a][4]) + " " + (data[2][a][3] + data[2][a][4]) + " " + (data[3][a][3] + data[3][a][4]));

		save();

		a = 1;
		
		newFile("/home/bamf/study_data/digest/behaved_all");
		printl("algorithm EM Bayesian Sim-TAMER Sim-COBOT");
		printl("strongly-agree " + data[0][a][0] + " " + data[1][a][0] + " " + data[2][a][0] + " " + data[3][a][0]);
		printl("agree " + data[0][a][1] + " " + data[1][a][1] + " " + data[2][a][1] + " " + data[3][a][1]);
		printl("no-opinion " + data[0][a][2] + " " + data[1][a][2] + " " + data[2][a][2] + " " + data[3][a][2]);
		printl("disagree " + data[0][a][3] + " " + data[1][a][3] + " " + data[2][a][3] + " " + data[3][a][3]);
		printl("strongly-disagree " + data[0][a][4] + " " + data[1][a][4] + " " + data[2][a][4] + " " + data[3][a][4]);

		save();		
		
		newFile("/home/bamf/study_data/digest/behaved_combined");
		printl("algorithm EM Bayesian Sim-TAMER Sim-COBOT");
		printl("agree " + (data[0][a][0] + data[0][a][1]) + " " + (data[1][a][0] + data[1][a][1]) + " " + (data[2][a][0] + data[2][a][1]) + " " + (data[3][a][0] + data[3][a][1]));
		printl("no-opinion " + data[0][a][2] + " " + data[1][a][2] + " " + data[2][a][2] + " " + data[3][a][2]);
		printl("disagree " + (data[0][a][3] + data[0][a][4]) + " " + (data[1][a][3] + data[1][a][4]) + " " + (data[2][a][3] + data[2][a][4]) + " " + (data[3][a][3] + data[3][a][4]));
		
		save();
		
		a = 2;
		
		newFile("/home/bamf/study_data/digest/speed_all");
		printl("algorithm EM Bayesian Sim-TAMER Sim-COBOT");
		printl("strongly-agree " + data[0][a][0] + " " + data[1][a][0] + " " + data[2][a][0] + " " + data[3][a][0]);
		printl("agree " + data[0][a][1] + " " + data[1][a][1] + " " + data[2][a][1] + " " + data[3][a][1]);
		printl("no-opinion " + data[0][a][2] + " " + data[1][a][2] + " " + data[2][a][2] + " " + data[3][a][2]);
		printl("disagree " + data[0][a][3] + " " + data[1][a][3] + " " + data[2][a][3] + " " + data[3][a][3]);
		printl("strongly-disagree " + data[0][a][4] + " " + data[1][a][4] + " " + data[2][a][4] + " " + data[3][a][4]);
		
		save();
		
		newFile("/home/bamf/study_data/digest/speed_combined");
		printl("algorithm EM Bayesian Sim-TAMER Sim-COBOT");
		printl("agree " + (data[0][a][0] + data[0][a][1]) + " " + (data[1][a][0] + data[1][a][1]) + " " + (data[2][a][0] + data[2][a][1]) + " " + (data[3][a][0] + data[3][a][1]));
		printl("no-opinion " + data[0][a][2] + " " + data[1][a][2] + " " + data[2][a][2] + " " + data[3][a][2]);
		printl("disagree " + (data[0][a][3] + data[0][a][4]) + " " + (data[1][a][3] + data[1][a][4]) + " " + (data[2][a][3] + data[2][a][4]) + " " + (data[3][a][3] + data[3][a][4]));
		
		save();
		
		a = 3;
		
		newFile("/home/bamf/study_data/digest/response_all");
		printl("algorithm EM Bayesian Sim-TAMER Sim-COBOT");
		printl("strongly-agree " + data[0][a][0] + " " + data[1][a][0] + " " + data[2][a][0] + " " + data[3][a][0]);
		printl("agree " + data[0][a][1] + " " + data[1][a][1] + " " + data[2][a][1] + " " + data[3][a][1]);
		printl("no-opinion " + data[0][a][2] + " " + data[1][a][2] + " " + data[2][a][2] + " " + data[3][a][2]);
		printl("disagree " + data[0][a][3] + " " + data[1][a][3] + " " + data[2][a][3] + " " + data[3][a][3]);
		printl("strongly-disagree " + data[0][a][4] + " " + data[1][a][4] + " " + data[2][a][4] + " " + data[3][a][4]);
		
		save();
		
		newFile("/home/bamf/study_data/digest/response_combined");
		printl("algorithm EM Bayesian Sim-TAMER Sim-COBOT");
		printl("agree " + (data[0][a][0] + data[0][a][1]) + " " + (data[1][a][0] + data[1][a][1]) + " " + (data[2][a][0] + data[2][a][1]) + " " + (data[3][a][0] + data[3][a][1]));
		printl("no-opinion " + data[0][a][2] + " " + data[1][a][2] + " " + data[2][a][2] + " " + data[3][a][2]);
		printl("disagree " + (data[0][a][3] + data[0][a][4]) + " " + (data[1][a][3] + data[1][a][4]) + " " + (data[2][a][3] + data[2][a][4]) + " " + (data[3][a][3] + data[3][a][4]));
		
		save();
		
		a = 4;
		
		newFile("/home/bamf/study_data/digest/concepts_all");
		printl("algorithm EM Bayesian Sim-TAMER Sim-COBOT");
		printl("strongly-agree " + data[0][a][0] + " " + data[1][a][0] + " " + data[2][a][0] + " " + data[3][a][0]);
		printl("agree " + data[0][a][1] + " " + data[1][a][1] + " " + data[2][a][1] + " " + data[3][a][1]);
		printl("no-opinion " + data[0][a][2] + " " + data[1][a][2] + " " + data[2][a][2] + " " + data[3][a][2]);
		printl("disagree " + data[0][a][3] + " " + data[1][a][3] + " " + data[2][a][3] + " " + data[3][a][3]);
		printl("strongly-disagree " + data[0][a][4] + " " + data[1][a][4] + " " + data[2][a][4] + " " + data[3][a][4]);
		
		save();
		
		newFile("/home/bamf/study_data/digest/concepts_combined");
		printl("algorithm EM Bayesian Sim-TAMER Sim-COBOT");
		printl("agree " + (data[0][a][0] + data[0][a][1]) + " " + (data[1][a][0] + data[1][a][1]) + " " + (data[2][a][0] + data[2][a][1]) + " " + (data[3][a][0] + data[3][a][1]));
		printl("no-opinion " + data[0][a][2] + " " + data[1][a][2] + " " + data[2][a][2] + " " + data[3][a][2]);
		printl("disagree " + (data[0][a][3] + data[0][a][4]) + " " + (data[1][a][3] + data[1][a][4]) + " " + (data[2][a][3] + data[2][a][4]) + " " + (data[3][a][3] + data[3][a][4]));
		
		save();
		
		a = 5;
		
		newFile("/home/bamf/study_data/digest/generalize_all");
		printl("algorithm EM Bayesian Sim-TAMER Sim-COBOT");
		printl("strongly-agree " + data[0][a][0] + " " + data[1][a][0] + " " + data[2][a][0] + " " + data[3][a][0]);
		printl("agree " + data[0][a][1] + " " + data[1][a][1] + " " + data[2][a][1] + " " + data[3][a][1]);
		printl("no-opinion " + data[0][a][2] + " " + data[1][a][2] + " " + data[2][a][2] + " " + data[3][a][2]);
		printl("disagree " + data[0][a][3] + " " + data[1][a][3] + " " + data[2][a][3] + " " + data[3][a][3]);
		printl("strongly-disagree " + data[0][a][4] + " " + data[1][a][4] + " " + data[2][a][4] + " " + data[3][a][4]);
		
		save();
		
		newFile("/home/bamf/study_data/digest/generalize_combined");
		printl("algorithm EM Bayesian Sim-TAMER Sim-COBOT");
		printl("agree " + (data[0][a][0] + data[0][a][1]) + " " + (data[1][a][0] + data[1][a][1]) + " " + (data[2][a][0] + data[2][a][1]) + " " + (data[3][a][0] + data[3][a][1]));
		printl("no-opinion " + data[0][a][2] + " " + data[1][a][2] + " " + data[2][a][2] + " " + data[3][a][2]);
		printl("disagree " + (data[0][a][3] + data[0][a][4]) + " " + (data[1][a][3] + data[1][a][4]) + " " + (data[2][a][3] + data[2][a][4]) + " " + (data[3][a][3] + data[3][a][4]));
	
		save();
	}
	
	private static ArrayList[] userBuckets(){
		double[][] data = new double[4][6];
		
		ArrayList<User> noExperience = new ArrayList<User>();
		ArrayList<User> experience = new ArrayList<User>();
		
		User u;
		BackgroundSurvey s;
		
		for(int i=0; i < users.size(); ++i){
			u = users.get(i);
			s = u.survey;
			
			if(s != null && s.trainingExperience != null){
				System.out.println(s.trainingExperience);
				if(s.trainingExperience.equals("None"))
					noExperience.add(u);
				else if(!s.trainingExperience.equals(""))
					experience.add(u);
			}
		}
		
		return new ArrayList[]{experience, noExperience};
	}
	
	private static ArrayList[] allUserBuckets(){
		double[][] data = new double[4][6];
		
		ArrayList<User> none = new ArrayList<User>();
		ArrayList<User> some = new ArrayList<User>();
		ArrayList<User> alot = new ArrayList<User>();
		ArrayList<User> expert = new ArrayList<User>();
		ArrayList<User> noResponse = new ArrayList<User>();
		
		User u;
		BackgroundSurvey s;
		
		for(int i=0; i < users.size(); ++i){
			u = users.get(i);
			s = u.survey;
			
			if(s != null && s.trainingExperience != null){
				System.out.println(s.trainingExperience);
				if(s.trainingExperience.equals("None"))
					none.add(u);
				else if(s.trainingExperience.equals("Some"))
					some.add(u);
				else if(s.trainingExperience.equals("A lot"))
					alot.add(u);
				else if(s.trainingExperience.equals("I am an expert in dog training"))
					expert.add(u);
				else
					noResponse.add(u);
			}
		}
		
		System.out.println("None: " + none.size() + " Some: " + some.size() + " A lot: " + alot.size() + " I am an expert in dog training: " + expert.size() + " no response: " + noResponse.size());
		
		return new ArrayList[]{none, some, alot, expert, noResponse};
	}
	
	private static ArrayList[] responseBuckets(String fieldName, String[] responses){
		
		ArrayList<User>[] results = new ArrayList[responses.length + 1];
		
		for(int i=0; i <= responses.length; ++i)
			results[i] = new ArrayList<User>();
		
		Field field = null;
		
		try{
			field = BackgroundSurvey.class.getField(fieldName);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
			
		User u;
		BackgroundSurvey s;
		String response;
		boolean noResponse;
		
		for(int i=0; i < users.size(); ++i){
			u = users.get(i);
			s = u.survey;
			
			if(s != null){
				
				try{
					response = (String)field.get(s);
				}catch(Exception e){
					response = null;
				}
					
				noResponse = true;
			
				for(int j=0; j < responses.length; ++j)
					if(responses[j].equals(response)){
						results[j].add(u);
					    noResponse = false;
					}
				
				if(noResponse)
					results[responses.length].add(u);
			}
		}
		
		String summary = "";
		
		for(int i=0; i < responses.length; ++i)
			summary += responses[i] + ": " + results[i].size() + " ";
		
		System.out.println(summary + "No response: " + results[responses.length].size());
		
		return results;
	}
	
	private static void numCorrections(ArrayList<User> usrs){
		ArrayList<Experiment> exps = new ArrayList<Experiment>();
		
		User u;
		Experiment e;
		
		depth = 4;
		
		for(int i=0; i < usrs.size(); ++i){
			u = usrs.get(i);
			
			for(int j=0; j < u.experiments.size() && j < 4; ++j)
				exps.add(u.experiments.get(j));
		}
		
		double episodes = 0;
		double trainedEpisodes = 0;
		double feedbacks = 0;
		double corrections = 0;
		
		int[] d;
		
		for(int i=0; i < exps.size(); ++i){
			e = exps.get(i);
			d = e.getcorrections();
			
			episodes += d[0];
			trainedEpisodes += d[1];
			feedbacks += d[2];
			corrections += d[3];
		}
		
		episodes = episodes/((double)exps.size());
		trainedEpisodes = trainedEpisodes/((double)exps.size());
		feedbacks = feedbacks/((double)exps.size());
		corrections = corrections/((double)exps.size());
		
		System.out.println("experiments: " + exps.size() + ", episodes: " + episodes + ", trained episodes: " + trainedEpisodes + ", feedbacks: " + feedbacks + ", corrections: " + corrections);
	}
	
	private static void getBasicStatistics(ArrayList<User> usrs){
		int[][] stats = new int[4][4];
		
		for(int i=0; i < 4; ++i)
			for(int j=0; j < 4; ++j)
				stats[i][j] = 0;
		
		User u;
		
		for(int i=0; i < usrs.size(); ++i){
			u = usrs.get(i);
			
			for(int j=0; j < 4; ++j){
				if(u.experiments.size() > j)
					++stats[u.experiments.get(j).algorithm][j];
			}
		}
		
		System.out.println("EM - at depth " + stats[0][0] + ", " + stats[0][1] + ", " + stats[0][2] + ", " + stats[0][3]);
		System.out.println("FIXED - at depth " + stats[1][0] + ", " + stats[1][1] + ", " + stats[1][2] + ", " + stats[1][3]);
		System.out.println("TAMER - at depth " + stats[2][0] + ", " + stats[2][1] + ", " + stats[2][2] + ", " + stats[2][3]);
		System.out.println("COBOT - at depth " + stats[3][0] + ", " + stats[3][1] + ", " + stats[3][2] + ", " + stats[3][3]);
	}
	
	private static void welchTests(){
		
		double[][][] data = new double[5][][];
		
		depth = 1;
		
		data[0] = stepsTillCorrect(users, 0.25);
		data[1] = stepsTillCorrect(users, 0.5);
		data[2] = stepsTillCorrect(users, 0.75);
		data[3] = stepsTillCorrect(users, 1.0);
		data[4] = stepsTillComplete(users);
		

		String l;

		for(int i=0; i < 5; ++i){
			
			System.out.println("");
			
			for(int j=0; j < 4; ++j){
				l = "";
				
				for(int k=0; k < 4; ++k){
					if(j == k)
						l += "* ";
					else{
						l += "(" + tStatistic(data[i][j][0],data[i][j][3],data[i][j][1],data[i][k][0],data[i][k][3],data[i][k][1]);
						l += "," + welchEquation(data[i][j][3],data[i][j][1],data[i][k][3],data[i][k][1]) + ") ";
					}
				}
				
				System.out.println(l);
			}
		}
	}
	
	private static double tStatistic(double ma, double va, double na, double mb, double vb, double nb){
		return (ma-mb)/(Math.sqrt(va/na + vb/nb));
	}
	
	private static double welchEquation(double va, double na, double vb, double nb){
		double ra = va/na;
		double rb = vb/nb;
		
		return (ra + rb)*(ra + rb)/(ra*ra/(na-1) + rb*rb/(nb-1));
	}
	
	private static void getMuHistograms(ArrayList<User> usrs){
		User u;
		Experiment e;
		ArrayList<Integer>[] data;
		
		for(int i=0; i < usrs.size(); ++i){
			u = usrs.get(i);
			
			for(int j=0; j < u.experiments.size() && j < 4; ++j){
				data = u.experiments.get(j).neutralHistory();
				
				if(data[0].size() > 0){
					newFile("/home/bamf/study_data/histograms/data/" + i + "_" + j + "_neutral_time");
					printList(data[0]);
					save();
				}
				
				if(data[1].size() > 0){
					newFile("/home/bamf/study_data/histograms/data/" + i + "_" + j + "_correct_time");
					printList(data[1]);
					save();
				}

				if(data[2].size() > 0){
					newFile("/home/bamf/study_data/histograms/data/" + i + "_" + j + "_incorrect_time");
					printList(data[2]);
					save();
				}
				
				if(data[3].size() > 0){
					newFile("/home/bamf/study_data/histograms/data/" + i + "_" + j + "_correct_total");
					printList(data[3]);
					save();
				}
				
				if(data[4].size() > 0){
					newFile("/home/bamf/study_data/histograms/data/" + i + "_" + j + "_incorrect_total");
					printList(data[4]);
					save();
				}
				
				launchCommand("/home/bamf/study_data/user_plots.sh " + i + " " + j);
			}
		}
	}
	
	private static void userMuHistograms(int bins, ArrayList<User> usrs, String userLabel){
		
		User u;
		double[] dat;
		double[] mus = new double[4];
		double mup;
		double mun;
		double interval = 1.0/((double)bins);
		double bin;
		double nbin;
		int[] muPlus = new int[bins];
		int[] muMinus = new int[bins];
		
		Arrays.fill(muPlus, 0);
		Arrays.fill(muMinus, 0);
		
		for(int i=0; i < usrs.size(); ++i){
			u = usrs.get(i);
			Arrays.fill(mus, 0);
			
			for(int j=0; j < u.experiments.size() && j < 4; ++j){
				dat = u.experiments.get(j).getMu();
				
				mus[0] += dat[0];
				mus[1] += dat[1];
				mus[2] += dat[2];
				mus[3] += dat[3];
			}
			
			mup = (mus[0] > 0) ? mus[2]/mus[0] : 0;
			mun = (mus[1] > 0) ? mus[3]/mus[1] : 0;
			
			bin = 0;
			nbin = interval;
			
			for(int k=0; k < bins; ++k){
				if(mup >= bin && mup < nbin)
					++muPlus[k];
				
				if(mun >= bin && mun < nbin)
					++muMinus[k];
				
				bin = nbin;
				nbin += interval;
			}
		}
		
		newFile("/home/bamf/study_data/digest/muHistogram_" + bins + "_" + userLabel);
		
		bin = 0;
		for(int i=0; i < bins; ++i){
			printl(bin + " " + muPlus[i] + " " + muMinus[i]);
			bin += interval;
		}
		
		save();
		
		String term = "set terminal pdf enhanced";
		String ylabel = "set ylabel 'number of users'; set nokey; set style fill";
		String title = "set title '" + userLabel + "'";
		

		String xlabel = "set xlabel 'mu+'";
		String output = "set output '/home/bamf/study_data/figures/mup_histogram_" + bins + "_" + userLabel + ".pdf'";
		String plot = "plot '/home/bamf/study_data/digest/muHistogram_" + bins + "_" + userLabel + "' u 1:2 w boxes";
		
		launchCommand("gnuplot -e \"" + term + "; " + output + "; " + ylabel + "; " + xlabel + "; " + title + "; " + plot + "\"");

		xlabel = "set xlabel 'mu-'";
		output = "set output '/home/bamf/study_data/figures/mun_histogram_" + bins + "_" + userLabel + ".pdf'";
		plot = "plot '/home/bamf/study_data/digest/muHistogram_" + bins + "_" + userLabel + "' u 1:3 w boxes";
		
		launchCommand("gnuplot -e \"" + term + "; " + output + "; " + ylabel + "; " + xlabel + "; " + title + "; " + plot + "\"");
	}
	
	private static void printList(ArrayList<Integer> a){
		for(int i=0; i < a.size(); ++i)
			printl(i + " " + a.get(i));
	}
	
	private static void activeUserPerformance(ArrayList<User> usrs, double ratio){
		
		ArrayList<Experiment> exps = getActiveExperiments(usrs, ratio);
		
		double[][] data;
		String line;
		
		newFile("/home/bamf/study_data/active/digest_" + ratio);
		
		printl("algorithm EM Bayesian TAMER COBOT");
		
		data = stepsTillCorrect(0.25, exps);
		
		line = "0.25 ";
		
		for(int i=0; i < 4; ++i)
			line += data[i][0] + " ";
		
		printl(line);
		
		data = stepsTillCorrect(0.5, exps);
		
		line = "0.5 ";
		
		for(int i=0; i < 4; ++i)
			line += data[i][0] + " ";
		
		printl(line);
		
		data = stepsTillCorrect(0.75, exps);
		
		line = "0.75 ";
		
		for(int i=0; i < 4; ++i)
			line += data[i][0] + " ";
		
		printl(line);
		
		data = stepsTillCorrect(1, exps);
		
		line = "1 ";
		
		for(int i=0; i < 4; ++i)
			line += data[i][0] + " ";
		
		printl(line);
		
		save();
	}
	
	private static ArrayList<Experiment> getActiveExperiments(ArrayList<User> usrs, double threshold){
		
		ArrayList<Experiment> exps = new ArrayList<Experiment>();
		
		User u;
		Experiment e;
		
		for(int i=0; i < usrs.size(); ++i){
			u = usrs.get(i);
			
			for(int j=0; j < u.experiments.size() && j < 4; ++j){
				e = u.experiments.get(j);
				
				if(e.neutralRatio() <= threshold)
					exps.add(e);
			}
		}
		
		return exps;
	}
	
	private static void muStatistics(){
		ArrayList[] buckets = userBuckets();
		
		double experiencePositive = 0;
		double experienceNegative = 0;
		double experienceCorrect = 0;
		double experienceIncorrect = 0;
		double noExperiencePositive = 0;
		double noExperienceNegative = 0;
		double noExperienceCorrect = 0;
		double noExperienceIncorrect = 0;
		
		double count;
		double[] mus;
		
		User u;
		Experiment e;
		
		count = 0;
		for(int i=0; i < buckets[0].size(); ++i){
			u = (User)buckets[0].get(i);
			
			for(int j=0; j < u.experiments.size() && j < 4; ++j){
				e = u.experiments.get(j);
				mus = e.getMu();
				experienceCorrect += mus[0];
				experienceIncorrect += mus[1];
				experiencePositive += mus[2];
				experienceNegative += mus[3];
				count += 1;
			}
		}
		
		double experienceMuPlus = (experienceCorrect > 0) ? experiencePositive/experienceCorrect : 0;
		double experienceMuMinus = (experienceIncorrect > 0) ? experienceNegative/experienceIncorrect : 0;
		double experienceMu = (experienceCorrect + experienceIncorrect > 0) ? (experiencePositive + experienceNegative)/(experienceCorrect + experienceIncorrect) : 0;
		
		count = 0;
		for(int i=0; i < buckets[1].size(); ++i){
			u = (User)buckets[1].get(i);
			
			for(int j=0; j < u.experiments.size() && j < 4; ++j){
				e = u.experiments.get(j);
				mus = e.getMu();
				noExperienceCorrect += mus[0];
				noExperienceIncorrect += mus[1];
				noExperiencePositive += mus[2];
				noExperienceNegative += mus[3];
				count += 1;
			}
		}
		
		double noExperienceMuPlus = (noExperienceCorrect > 0) ? noExperiencePositive/noExperienceCorrect : 0;
		double noExperienceMuMinus = (noExperienceIncorrect > 0) ? noExperienceNegative/noExperienceIncorrect : 0;
		double noExperienceMu = (noExperienceCorrect + noExperienceIncorrect > 0) ? (noExperiencePositive + noExperienceNegative)/(noExperienceCorrect + noExperienceIncorrect) : 0;
		
		System.out.println("Fraction of correct actions recieving neutral feedback (mu+): " + experienceMuPlus + " " + noExperienceMuPlus + " " + (experiencePositive + noExperiencePositive)/(experienceCorrect + noExperienceCorrect));
		System.out.println("Fraction of incorrect actions recieving neutral feedback (mu-): " + experienceMuMinus + " " + noExperienceMuMinus + " " + (experienceNegative + noExperienceNegative)/(experienceIncorrect + noExperienceIncorrect));
		System.out.println("Fraction of all actions recieving neutral feedback: " + experienceMu + " " + noExperienceMu + " " + (experiencePositive + experienceNegative + noExperiencePositive + noExperienceNegative)/(experienceCorrect + experienceIncorrect + noExperienceCorrect + noExperienceIncorrect));
	
		System.out.println("average correct actions per user: " + experienceCorrect + " " + noExperienceCorrect + " " + (experienceCorrect + noExperienceCorrect));
		System.out.println("average incorrect actions per user: " + experienceIncorrect + " " + noExperienceIncorrect + " " + (experienceIncorrect + noExperienceIncorrect));		 
		System.out.println("average actions per user: " + (experienceCorrect + experienceIncorrect) + " " + (noExperienceCorrect + noExperienceIncorrect) + " " + ((experienceCorrect + experienceIncorrect) + (noExperienceCorrect + noExperienceIncorrect)));
	}
	
	private static void muStatistics(ArrayList[] buckets){
		
		double positive = 0;
		double negative = 0;
		double correct = 0;
		double incorrect = 0;
		
		String muPlus = "";
		String muMinus = "";
		String mu = "";
	    String averageCorrect = "";
	    String averageIncorrect = "";
		String average = "";
	    
		double count;
		double[] mus;
		
		ArrayList<User> bucket;
		User u;
		Experiment e;
		
		for(int k=0; k < buckets.length; ++k){
			bucket = buckets[k];
			count = 0;
			
			positive = negative = correct = incorrect = 0;
			
			for(int i=0; i < bucket.size(); ++i){
				u = bucket.get(i);
			
				for(int j=0; j < u.experiments.size() && j < 4; ++j){
					e = u.experiments.get(j);
					mus = e.getMu();
					correct += mus[0];
					incorrect += mus[1];
					positive += mus[2];
					negative += mus[3];
					count += 1;
				}
			}
		
			muPlus += ((correct > 0) ? positive/correct : 0) + " ";
			muMinus += ((incorrect > 0) ? negative/incorrect : 0) + " ";
			mu += ((correct + incorrect > 0) ? (positive + negative)/(correct + incorrect) : 0) + " ";
			
		    averageCorrect += ((count > 0) ? correct/count : 0) + " ";
		    averageIncorrect += ((count > 0) ? incorrect/count : 0) + " ";
			average += ((count > 0) ? (correct + incorrect)/count : 0) + " ";
		}
		
		System.out.println("Fraction of correct actions receiving neutral feedback (mu+): " + muPlus);
		System.out.println("Fraction of incorrect actions receiving neutral feedback (mu-): " + muMinus);
		System.out.println("Fraction of all actions receiving neutral feedback: " + mu);
		System.out.println("average correct actions per experiment: " + averageCorrect);
		System.out.println("average incorrect actions per experiment: " + averageIncorrect);		 
		System.out.println("average actions per experiment: " + average);
	}
	
	private static void getANOVAData(ArrayList[] buckets){
		newFile("/home/bamf/ANOVA_DATA.csv");
		printl("U,C,E,N");
		
		ArrayList<User> bucket;
		String[] levels = new String[]{"none", "some", "alot", "expert"};
		User u;
		double[] parameters;
		
		for(int i=0; i < buckets.length-1; ++i){
			bucket = buckets[i];
			
			for(int j=0; j < bucket.size(); ++j){
				u = bucket.get(j);
				
				if(u.experiments.size() > 0){
					parameters = u.experiments.get(0).getMu();
					
					if(parameters[0] > 0 && parameters[1] > 0){
						printl(u.id + ",correct," + levels[i] + "," + (parameters[2]/parameters[0]));
						printl(u.id + ",incorrect," + levels[i] + "," + (parameters[3]/parameters[1]));
					}
				}
			}
		}
		
		save();
	}
	
	private static void launchCommand(String command){
		System.out.println("comand: " + command);
		try{
			Runtime.getRuntime().exec(new String[]{"bash", "-c", command}).waitFor();
		}catch(Exception e){System.out.println(e.getLocalizedMessage());}
	}
	
	private static void newFile(String path){
		try{
			//File f;
			//int id = 0;
			
			//do{
				//f = new File(path + "_" + id);
				//++id;
			//}while(f.exists());
			
			file = new FileWriter(path);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	private static void printl(String s){
		try{
			file.write(s + "\n");
		}catch(Exception e){System.out.println(e.getMessage());}
		
		//System.out.println(s);
		
	}
	
	private static void save(){
		try{
			file.flush();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}
