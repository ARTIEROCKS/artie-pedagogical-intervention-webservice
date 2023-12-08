package artie.pedagogicalintervention.webservice.repository;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareSolution;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PedagogicalSoftwareSolutionRepository extends MongoRepository<PedagogicalSoftwareSolution, String>{
	List<PedagogicalSoftwareSolution> findByUserId(String userId);
	List<PedagogicalSoftwareSolution> findByExercise_IdAndUserId(String exerciseId, String userId);
	List<PedagogicalSoftwareSolution> findByPedagogicalSoftwareDataId(String pedagogicalSoftwareDataId);
}
