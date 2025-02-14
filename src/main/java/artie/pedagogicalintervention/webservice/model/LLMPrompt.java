package artie.pedagogicalintervention.webservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@Document(collection="LLMPrompt")
public class LLMPrompt {
    @Id
    private String id;
    private String institutionId;
    private String promptKey;
    private String prompt;
    private String role;
}
