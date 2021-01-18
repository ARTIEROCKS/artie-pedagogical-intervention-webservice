package artie.pedagogicalintervention.webservice.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import artie.common.web.enums.ValidSolutionEnum;
import artie.common.web.dto.Exercise;
import artie.pedagogicalintervention.webservice.dto.StudentDTO;

@Document(collection="PedagogicalSoftwareData")
public class PedagogicalSoftwareData {
	
	@Id
	private String id;
	private StudentDTO student;
	private String exerciseId;
	private Exercise exercise;
	private PedagogicalSoftwareDistance solutionDistance = new PedagogicalSoftwareDistance();
	private LocalDateTime dateTime;
	private boolean requestHelp;
	private boolean finishedExercise;
	private int validSolution;
	private String screenShot;
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
	
	public String getExerciseId() {
		return this.exerciseId;
	}
	public void setExerciseId(String exerciseId) {
		this.exerciseId = exerciseId;
	}
	
	public Exercise getExercise() {
		return this.exercise;
	}
	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
		
		if(exercise != null) {
			this.exerciseId = exercise.getId();
		}else{
			this.exerciseId = null;
		}
	}
	
	public PedagogicalSoftwareDistance getSolutionDistance() {
		return solutionDistance;
	}
	public void setSolutionDistance(PedagogicalSoftwareDistance solutionDistance) {
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

	public boolean getFinishedExercise(){return this.finishedExercise;}
	public void setFinishedExercise(boolean finishedExercise){this.finishedExercise = finishedExercise;}

	public int getValidSolution(){return this.validSolution;}
	public void setValidSolution(int validSolution){this.validSolution = validSolution;}

	public String getScreenShot(){return this.screenShot;}
	public void setScreenShot(String screenShot){this.screenShot = screenShot;}

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
	}
	
	/**
	 * Parameterized constructor
	 * @student student
	 * @exercise exercise
	 * @param solutionDistance
	 * @param elements
	 * @param requestHelp
	 * @param finishedExercise
	 * @param validSolution
	 * @param screenShot
	 */
	public PedagogicalSoftwareData(StudentDTO student, Exercise exercise, PedagogicalSoftwareDistance solutionDistance, List<PedagogicalSoftwareElement> elements,
								   boolean requestHelp, boolean finishedExercise, int validSolution, String screenShot) {
		this.student = student;
		this.exercise = exercise;
		this.solutionDistance = solutionDistance;
		this.elements = elements;
		this.dateTime = LocalDateTime.now();
		this.requestHelp = requestHelp;
		this.finishedExercise = finishedExercise;
		this.validSolution = validSolution;
		this.screenShot = screenShot;
		
		if(exercise != null) {
			this.exerciseId = exercise.getId();
		}else{
			this.exerciseId = null;
		}
	}
	
	public void addElement(PedagogicalSoftwareElement element) {
		this.elements.add(element);
	}
}
