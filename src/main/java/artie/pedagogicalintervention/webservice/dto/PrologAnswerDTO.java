package artie.pedagogicalintervention.webservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrologAnswerDTO {
    private String variable;
    private String value;
}
