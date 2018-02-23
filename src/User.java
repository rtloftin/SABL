
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

public class User {

	ArrayList<String[]> lines;

	ArrayList<Experiment> experiments;
	
	String path;
	String id;
	
	BackgroundSurvey survey;
	
	public User(String i, String p){
		id = i;
		path = p;
		
		loadLines();
		
		parseExperiments();
		
		loadBackgroundSurvey();
	}
	
	private void loadLines(){
		try{
			BufferedReader file = new BufferedReader(new FileReader(path + "/logs/session_" + id + "_log"));
			
			lines = new ArrayList<String[]>();
			
			String line = file.readLine();
			
			while(null != line){
				lines.add(line.split(" "));
				line = file.readLine();
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	private void loadBackgroundSurvey(){
		survey = new BackgroundSurvey(id, path);
	}
	
	private void parseExperiments(){
		experiments = new ArrayList<Experiment>();
		
		String[] line;
		Experiment current = null;
		ArrayList<String[]> expLog = null;
		int exp = 1;
		
		for(int i=0; i < lines.size(); ++i){
			line = lines.get(i);
			
			if(line[1].equals("EVENT")){
				//System.out.println("EVENT");
				if(line[2].equals("agent")){
					expLog = new ArrayList<String[]>();
				}
				else if(line[2].equals("finish") && expLog != null){
					//System.out.println("Experiment loaded, " + expLog.size() + " lines");
					current = new Experiment(expLog);
					experiments.add(current);
					expLog = null;
				}
				else if(line[2].equals("exit") && current != null && exp < 5){
					current.loadExitSurvey(id, exp, path);
					++exp;
				}
			}
			
			if(expLog != null)
				expLog.add(lines.get(i));
		}
	}
}
