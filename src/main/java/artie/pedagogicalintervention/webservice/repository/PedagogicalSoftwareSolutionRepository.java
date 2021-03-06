package artie.pedagogicalintervention.webservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import artie.common.web.dto.Exercise;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareSolution;

public interface PedagogicalSoftwareSolutionRepository extends MongoRepository<PedagogicalSoftwareSolution, String>{
	List<PedagogicalSoftwareSolution> findByUserId(String userId);
	List<PedagogicalSoftwareSolution> findByExercise_IdAndUserId(String exerciseId, String userId);
	List<PedagogicalSoftwareSolution> findByPedagogicalSoftwareDataId(String pedagogicalSoftwareDataId);
}
