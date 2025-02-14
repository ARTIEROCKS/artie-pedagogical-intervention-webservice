package artie.pedagogicalintervention.webservice.dto;

import artie.common.web.dto.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO extends Student {
	
	private String institutionId;
	private String userId;

	
	/**
	 * Parameterized constructor
	 * @param id
	 * @param name
	 * @param lastName
	 * @param studentNumber
	 * @param gender
	 * @param motherTongue
	 * @param age
	 * @param competence
	 * @param motivation
	 * @param institutionId
	 * @param userId
	 */
	public StudentDTO(String id, String name, String lastName, String studentNumber,int gender, int motherTongue,
					  int age, int competence, int motivation, boolean recordFace, boolean recordInteractions,
					  boolean interactsWithRobot, String institutionId, String userId) {
		super(id, name, lastName, studentNumber, gender, motherTongue, age, competence, motivation, recordFace,
				recordInteractions, interactsWithRobot);

		this.institutionId = institutionId;
		this.userId = userId;
	}

}
