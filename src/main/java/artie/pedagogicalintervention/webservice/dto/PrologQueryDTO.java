package artie.pedagogicalintervention.webservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrologQueryDTO {
    private String institutionId;
    private String query;
}
