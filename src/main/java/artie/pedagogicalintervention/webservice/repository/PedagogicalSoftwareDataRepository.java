package artie.pedagogicalintervention.webservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;

import java.util.List;

public interface PedagogicalSoftwareDataRepository extends MongoRepository<PedagogicalSoftwareData, String>{
    List<PedagogicalSoftwareData> findByFinishedExercise(boolean finishedExercise);
}
