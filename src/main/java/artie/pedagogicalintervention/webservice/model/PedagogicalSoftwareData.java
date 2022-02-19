package artie.pedagogicalintervention.webservice.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import artie.common.web.dto.SoftwareData;
import artie.common.web.dto.SolutionDistance;
import artie.common.web.dto.Student;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.mongodb.core.mapping.Document;

import artie.common.web.enums.ValidSolutionEnum;
import artie.common.web.dto.Exercise;
import artie.pedagogicalintervention.webservice.dto.StudentDTO;

@Document(collection="PedagogicalSoftwareData")
public class PedagogicalSoftwareData {
	
	@Id
	private String id;
	private StudentDTO student;
	private Exercise exercise;
	private SolutionDistance solutionDistance = new SolutionDistance();
	private LocalDateTime dateTime;
	private boolean requestHelp;
	private boolean predictedNeedHelp;
	private boolean answeredNeedHelp;
	private double secondsHelpOpen;
	private boolean finishedExercise;
	private int validSolution;
	private double grade;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy, hh:mm:ss")
	private Date lastLogin;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy, hh:mm:ss")
    private Date lastExerciseChange;
	private String screenShot;
	private String binary;
	private List<PedagogicalSoftwareElement> elements = new ArrayList<>();
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public StudentDTO getStudent() {
		return this.student;
	}
	public void setStudent(StudentDTO student) {
		this.student = student;
	}

	public Exercise getExercise() {
		return this.exercise;
	}
	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}
	
	public SolutionDistance getSolutionDistance() {
		return solutionDistance;
	}
	public void setSolutionDistance(SolutionDistance solutionDistance) {
		this.solutionDistance = solutionDistance;
	}
	
	public LocalDateTime getDateTime() {
		return this.dateTime;
	}
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	
	public boolean getRequestHelp() {
		return this.requestHelp;
	}
	public void setRequestHelp(boolean requestHelp) {
		this.requestHelp = requestHelp;
	}

	public boolean getPredictedNeedHelp(){return this.predictedNeedHelp;}
	public void setPredictedNeedHelp(boolean predictedNeedHelp){this.predictedNeedHelp = predictedNeedHelp;}

	public boolean getAnsweredNeedHelp(){return this.answeredNeedHelp;}
	public void setAnsweredNeedHelp(boolean answeredNeedHelp){this.answeredNeedHelp = answeredNeedHelp;}

	public double getSecondsHelpOpen(){return this.secondsHelpOpen;}
	public void setSecondsHelpOpen(double secondsHelpOpen){this.secondsHelpOpen = secondsHelpOpen;}

	public boolean getFinishedExercise(){return this.finishedExercise;}
	public void setFinishedExercise(boolean finishedExercise){this.finishedExercise = finishedExercise;}

	public int getValidSolution(){return this.validSolution;}
	public void setValidSolution(int validSolution){this.validSolution = validSolution;}

	public double getGrade(){return this.grade;}
	public void setGrade(double grade){this.grade = grade;}

	public Date getLastLogin(){return this.lastLogin;}
	public void setLastLogin(Date lastLogin){this.lastLogin = lastLogin;}

	public Date getLastExerciseChange(){return this.lastExerciseChange;}
	public void setLastExerciseChange(Date lastExerciseChange){this.lastExerciseChange = lastExerciseChange;}

	public String getScreenShot(){return this.screenShot;}
	public void setScreenShot(String screenShot){this.screenShot = screenShot;}

	public String getBinary(){return this.binary;}
	public void setBinary(String binary){this.binary = binary;}

	public List<PedagogicalSoftwareElement> getElements(){
		return this.elements;
	}
	public void setElements(List<PedagogicalSoftwareElement> elements) {
		this.elements = elements;
	}

	/**
	 * Function to get all the blocks from inside the elements
	 * @return
	 */
	public List<PedagogicalSoftwareBlock> getAllBlocks(){
		List<PedagogicalSoftwareBlock> allBlocks = new ArrayList<>();
		this.elements.forEach(e -> allBlocks.addAll(e.getBlocks()));
		return allBlocks;
	}

	/**
	 * Default Constructor
	 */
	public PedagogicalSoftwareData() {
		this.dateTime = LocalDateTime.now();
		this.requestHelp = false;
		this.finishedExercise = false;
		this.validSolution = ValidSolutionEnum.WAITING_APPROVAL.getValue();
		this.screenShot = null;
		this.predictedNeedHelp = false;
		this.answeredNeedHelp = false;
	}
	
	/**
	 * Parameterized constructor
	 * @student student
	 * @exercise exercise
	 * @param solutionDistance
	 * @param elements
	 * @param requestHelp
	 * @param predictedNeedHelp
	 * @param answeredNeedHelp
	 * @param secondsHelpOpen
	 * @param finishedExercise
	 * @param validSolution
	 * @param grade
	 * @param lastLogin
	 * @param lastExerciseChange
	 * @param screenShot
	 * @param binary
	 */
	public PedagogicalSoftwareData(StudentDTO student, Exercise exercise, SolutionDistance solutionDistance, List<PedagogicalSoftwareElement> elements,
								   boolean requestHelp, boolean predictedNeedHelp, boolean answeredNeedHelp, double secondsHelpOpen, boolean finishedExercise, int validSolution, double grade,
								   Date lastLogin, Date lastExerciseChange, String screenShot, String binary) {
		this.student = student;
		this.exercise = exercise;
		this.solutionDistance = solutionDistance;
		this.elements = elements;
		this.dateTime = LocalDateTime.now();
		this.requestHelp = requestHelp;
		this.predictedNeedHelp = predictedNeedHelp;
		this.answeredNeedHelp = answeredNeedHelp;
		this.secondsHelpOpen = secondsHelpOpen;
		this.finishedExercise = finishedExercise;
		this.validSolution = validSolution;
		this.grade = grade;
		this.lastLogin = lastLogin;
		this.lastExerciseChange = lastExerciseChange;
		this.screenShot = screenShot;
		this.binary = binary;
	}
	
	public void addElement(PedagogicalSoftwareElement element) {
		this.elements.add(element);
	}

	/**
	 * Function to transform the pedagogical software data class into the software data DTO in the common library
	 * @return
	 */
	public SoftwareData toDTO(){
		Student st = this.student;
		return new SoftwareData(st, this.exercise, this.solutionDistance,
								this.secondsHelpOpen, this.finishedExercise, this.validSolution,
								this.grade, this.lastLogin, this.lastExerciseChange);
	}
}
