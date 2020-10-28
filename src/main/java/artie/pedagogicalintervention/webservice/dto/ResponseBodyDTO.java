package artie.pedagogicalintervention.webservice.dto;

public class ResponseBodyDTO {
	
	private String message;
	private Object object;

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
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

	/**
	 * Parameterized constructor
	 * @param object
	 */
	public ResponseBodyDTO(Object object) {
		this.object = object;
	}

	/**
	 * Parameterized constructor
	 * @param message
	 * @param object
	 */
	public ResponseBodyDTO(String message, Object object) {
		this.message = message;
		this.object = object;
	}
}
