package artie.pedagogicalintervention.webservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrologQueryDTO {
    private String institutionId;
    private String query;
}
