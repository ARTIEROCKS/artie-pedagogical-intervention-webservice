package artie.pedagogicalintervention.webservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import artie.common.web.dto.Exercise;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareSolution;

public interface PedagogicalSoftwareSolutionRepository extends MongoRepository<PedagogicalSoftwareSolution, String>{
	List<PedagogicalSoftwareSolution> findByExercise(String exercise);
	List<PedagogicalSoftwareSolution> findByExerciseIdAndUserId(String exerciseId, String userId);
}
