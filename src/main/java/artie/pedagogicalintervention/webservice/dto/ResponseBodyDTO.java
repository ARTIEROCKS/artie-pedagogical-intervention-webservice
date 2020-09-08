package artie.pedagogicalintervention.webservice.dto;

public class ResponseBodyDTO {
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Default constructor
	 */
	public ResponseBodyDTO() {}

	/**
	 * Parameterized constructor
	 * @param message
	 */
	public ResponseBodyDTO(String message) {
		this.message = message;
	}
}
