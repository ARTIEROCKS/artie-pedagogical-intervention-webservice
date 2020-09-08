package artie.pedagogicalintervention.webservice.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	 * @param statusCode
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
		
		try {
			result = objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
