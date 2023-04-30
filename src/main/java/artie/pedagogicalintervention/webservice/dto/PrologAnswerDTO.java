package artie.pedagogicalintervention.webservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrologAnswerDTO {
    private String variable;
    private String value;
}
