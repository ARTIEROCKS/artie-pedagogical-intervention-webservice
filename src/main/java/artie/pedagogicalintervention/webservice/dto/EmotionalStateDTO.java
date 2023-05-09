package artie.pedagogicalintervention.webservice.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmotionalStateDTO {
    private String _id;
    private String emotionalState;
    private String externalId;
}
