import java.io.*;

public class BackgroundSurvey {

	public String age;
	public String gender;
	public String education;
	public String otherEducation;
	public String hadDog;
	public String ifHadDog;
	public String hasDog;
	public String ifHasDog;
	public String trainedDogs;
	public String trainingExperience;
	public String groupClass;
	public String privateClass;
	public String usedTechniques;
	public String otherUsedTechniques;
	public String familiarTechniques;
	public String otherFamiliarTechniques;
	
	public BackgroundSurvey(String id, String path){
		
		try{
			BufferedReader file = new BufferedReader(new FileReader(path + "/forms/background_survey_" + id + "_log"));
			
			/*age = file.readLine();
			gender = file.readLine();
			education = file.readLine();
			otherEducation = file.readLine();
			hadDog = yesNo(file.readLine());
			ifHadDog = file.readLine();
			hasDog = yesNo(file.readLine());
			ifHasDog = file.readLine();
			trainedDogs = yesNo(file.readLine());
			trainingExperience = file.readLine();
			groupClass = yesNo(file.readLine());
			privateClass = yesNo(file.readLine());
			usedTechniques = file.readLine().split("|");
			otherUsedTechniques = file.readLine();
			familiarTechniques = file.readLine().split("|");
			otherFamiliarTechniques = file.readLine();*/
			
			age = file.readLine();
			gender = file.readLine();
			education = file.readLine();
			otherEducation = file.readLine();
			hadDog = file.readLine();
			ifHadDog = file.readLine();
			hasDog = file.readLine();
			ifHasDog = file.readLine();
			trainedDogs = file.readLine();
			trainingExperience = file.readLine();
			groupClass = file.readLine();
			privateClass = file.readLine();
			usedTechniques = file.readLine();
			otherUsedTechniques = file.readLine();
			familiarTechniques = file.readLine();
			otherFamiliarTechniques = file.readLine();
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	private int yesNo(String a){
		if(a == "no")
			return 1;
		if(a == "yes")
			return 2;
		return 0;
	}
}
