package artie.pedagogicalintervention.webservice.repository;

import artie.pedagogicalintervention.webservice.model.LLMPrompt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LLMPromptRepository extends MongoRepository<LLMPrompt, String> {
    List<LLMPrompt> findByInstitutionIdAndPromptKey(String institutionId, String promptKey);
}
