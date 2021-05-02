package artie.pedagogicalintervention.webservice.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ResponseDTO {
	
	private ResponseBodyDTO body;
	
	
	
	public ResponseBodyDTO getBody() {
		return body;
	}

	public void setBody(ResponseBodyDTO body) {
		this.body = body;
	}

	/**
	 * Default Constructor
	 */
	public ResponseDTO() {}
	
	/**
	 * Parameterized constructor
	 * @param body
	 */
	public ResponseDTO(ResponseBodyDTO body) {
		this.body = body;
	}

	
	/**
	 * Function to transform this object in a JSON
	 * @return
	 */
	public String toJSON() {
		
		String result = "";
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		
		try {
			result = objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
