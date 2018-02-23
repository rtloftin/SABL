import java.io.BufferedReader;
import java.io.FileReader;


public class ExitSurvey {

	public String strategy;
	public String solved;
	public String behaved;
	public String speed;
	public String response;
	public String concepts;
	public String generalize;
	
	public String[] answers = new String[6];
	
	public ExitSurvey(String id, int stage, String path){
		
		try{
			BufferedReader file = new BufferedReader(new FileReader(path + "/forms/exit_survey_" + id + "_" + stage + "_log"));
			
			String line = file.readLine();
			strategy = "";
			
			while(!line.equals("MULTIPLE CHOICE")){
				strategy += line;
				line = file.readLine();
			}
				
			file.readLine();
			
			answers[0] = solved = file.readLine();
			answers[1] = behaved = file.readLine();
			answers[2] = speed = file.readLine();
			answers[3] = response = file.readLine();
			answers[4] = concepts = file.readLine();
			answers[5] = generalize = file.readLine();
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
}
