package artie.pedagogicalintervention.webservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private String userId;
    private String contextId;
    private String userPrompt;
    private String systemPrompt;
}
