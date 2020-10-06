package artie.pedagogicalintervention.webservice.dto;

import artie.common.web.dto.Student;

public class StudentDTO extends Student {
	
	private String institutionId;
	private String userId;
	
	
	public String getInstitutionId() {
		return institutionId;
	}
	public void setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * Parameterized constructor
	 * @param id
	 * @param name
	 * @param lastName
	 * @param competence
	 * @param motivation
	 * @param institutionId
	 * @param userId
	 */
	public StudentDTO(String id, String name, String lastName, String competence, String motivation, String institutionId, String userId) {
		super(id, name, lastName, competence, motivation);
		this.institutionId = institutionId;
		this.userId = userId;
	}

	/**
	 * Default constructor
	 */
	public StudentDTO() {

	}

}
