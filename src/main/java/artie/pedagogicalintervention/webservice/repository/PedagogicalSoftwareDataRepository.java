package artie.pedagogicalintervention.webservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedagogicalSoftwareDataRepository extends MongoRepository<PedagogicalSoftwareData, String>{
    List<PedagogicalSoftwareData> findByFinishedExercise(boolean finishedExercise);
    List<PedagogicalSoftwareData> findByStudent_IdAndExerciseId(String studentId, String exerciseId);
    List<PedagogicalSoftwareData> findByStudent_Id(String studentId);
}
