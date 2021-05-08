package artie.pedagogicalintervention.webservice.repository;

import artie.common.web.dto.Exercise;
import artie.pedagogicalintervention.webservice.dto.StudentDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;

import java.util.List;

public interface PedagogicalSoftwareDataRepository extends MongoRepository<PedagogicalSoftwareData, String>{
    List<PedagogicalSoftwareData> findByFinishedExercise(boolean finishedExercise);
    List<PedagogicalSoftwareData> findByStudent_IdAndExerciseId(String studentId, String exerciseId);
}
