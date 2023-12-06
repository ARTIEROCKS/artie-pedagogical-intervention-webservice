package artie.pedagogicalintervention.webservice.model;

import artie.common.web.dto.Exercise;
import artie.common.web.dto.SoftwareData;
import artie.common.web.dto.SolutionDistance;
import artie.common.web.dto.Student;
import artie.common.web.enums.ValidSolutionEnum;
import artie.pedagogicalintervention.webservice.dto.StudentDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(collection="PedagogicalSoftwareData")
public class PedagogicalSoftwareData {
	
	@Id
	private String id;
	private StudentDTO student;
	private Exercise exercise;
	private SolutionDistance solutionDistance = new SolutionDistance();
	private double aptedDistance;
	private LocalDateTime dateTime;
	private boolean requestHelp;
	private boolean predictedNeedHelp;
	private boolean answeredNeedHelp;

	private String manualEmotionalState;

	private String predictedEmotionalState;

	private String answeredEmotionalState;
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
	public PedagogicalSoftwareData(StudentDTO student, Exercise exercise, SolutionDistance solutionDistance, double aptedDistance, List<PedagogicalSoftwareElement> elements,
								   boolean requestHelp, boolean predictedNeedHelp, boolean answeredNeedHelp, double secondsHelpOpen, boolean finishedExercise, int validSolution, double grade,
								   Date lastLogin, Date lastExerciseChange, String screenShot, String binary) {
		this.student = student;
		this.exercise = exercise;
		this.solutionDistance = solutionDistance;
		this.aptedDistance = aptedDistance;
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
