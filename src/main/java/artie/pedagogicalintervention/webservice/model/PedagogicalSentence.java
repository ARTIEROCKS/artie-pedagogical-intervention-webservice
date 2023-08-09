package artie.pedagogicalintervention.webservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@Document(collection="PedagogicalSentence")
public class PedagogicalSentence {
    @Id
    private String id;
    private String institutionId;
    private String sentenceKey;
    private String sentence;
}
