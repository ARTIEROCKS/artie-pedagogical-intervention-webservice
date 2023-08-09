package artie.pedagogicalintervention.webservice.repository;

import artie.pedagogicalintervention.webservice.model.PedagogicalSentence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedagogicalSentenceRepository extends MongoRepository<PedagogicalSentence, String> {
    List<PedagogicalSentence> findByInstitutionIdAndSentenceKey(String institutionId, String sentenceKey);
}
