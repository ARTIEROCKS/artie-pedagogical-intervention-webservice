package artie.pedagogicalintervention.webservice.model;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import artie.common.web.dto.Student;
import artie.sensor.common.dto.SensorObject;

@Document(collection="SensorData")
public class SensorData {
	
	//Attributes
	@Id
	private String id;
	private Date date;
	private long milliseconds;
	private String sensorObjectType;
	private String sensorName;
	private String competence;
	private String motivation;
	private String data;
	
	
	//Properties
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public long getMilliseconds() {
		return this.milliseconds;
	}
	public void setMilliseconds(long milliseconds) {
		this.milliseconds = milliseconds;
	}
	
	public String getSensorObjectType() {
		return this.sensorObjectType;
	}
	public void setSensorObjectType(String sensorObjectType) {
		this.sensorObjectType = sensorObjectType;
	}
	
	public String getSensorName() {
		return this.sensorName;
	}
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	
	public String getCompetence() {
		return competence;
	}
	public void setCompetence(String competence) {
		this.competence = competence;
	}
	
	public String getMotivation() {
		return motivation;
	}
	public void setMotivation(String motivation) {
		this.motivation = motivation;
	}
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
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
		this.date = sensorObject.getDate();
		this.milliseconds = sensorObject.getMilliseconds();
		this.sensorObjectType = sensorObject.getSensorObjectType().toString();
		this.sensorName = sensorObject.getSensorName();
		this.data = obj.writeValueAsString(sensorObject.getData());
		this.competence = student.getCompetence();
		this.motivation = student.getMotivation();
	}
	
}
