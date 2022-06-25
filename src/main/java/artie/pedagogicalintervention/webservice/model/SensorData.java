package artie.pedagogicalintervention.webservice.model;

import javax.persistence.Id;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import artie.common.web.dto.Student;
import artie.sensor.common.dto.SensorObject;
@Data
@NoArgsConstructor
@Document(collection="SensorData")
public class SensorData extends SensorObject {
	
	//Attributes
	@Id
	private String id;
	private String externalId;
	private int competence;
	private int motivation;
	private String predictedEmotionalState;
	private String answeredEmotionalState;

	
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
		this.externalId = student.getId();
	}
	
}
