package artie.pedagogicalintervention.webservice.model;

import javax.persistence.Id;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import artie.common.web.dto.Student;
import artie.sensor.common.dto.SensorObject;

@Document(collection="SensorData")
public class SensorData extends SensorObject {
	
	//Attributes
	@Id
	private String id;
	private int competence;
	private int motivation;
	private String predictedEmotionalState;
	private String answeredEmotionalState;
	
	
	//Properties
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public int getCompetence() {
		return competence;
	}
	public void setCompetence(int competence) {
		this.competence = competence;
	}
	
	public int getMotivation() {
		return motivation;
	}
	public void setMotivation(int motivation) {
		this.motivation = motivation;
	}

	public String getPredictedEmotionalState() { return this.predictedEmotionalState; }
	public void setPredictedEmotionalState(String predictedEmotionalState) { this.predictedEmotionalState = predictedEmotionalState; }

	public String getAnsweredEmotionalState() { return this.answeredEmotionalState; }
	public void setAnsweredEmotionalState(String answeredEmotionalState){ this.answeredEmotionalState = answeredEmotionalState; }


	/**
	 * Default constructor
	 */
	public SensorData() {
	}
	
	/**
	 * Parameterized constructor
	 * @param sensorObject
	 * @param student
	 * @throws JsonProcessingException
	 */
	public SensorData(SensorObject sensorObject, Student student) throws JsonProcessingException {
		ObjectMapper obj = new ObjectMapper();
		obj.registerModule(new JavaTimeModule());
		this.date = sensorObject.getDate();
		this.milliseconds = sensorObject.getMilliseconds();
		this.sensorObjectType = sensorObject.getSensorObjectType();
		this.sensorName = sensorObject.getSensorName();
		this.data = obj.writeValueAsString(sensorObject.getData());
		this.fromDate = sensorObject.getFromDate();
		this.toDate = sensorObject.getToDate();
		this.competence = student.getCompetence();
		this.motivation = student.getMotivation();
	}
	
}
