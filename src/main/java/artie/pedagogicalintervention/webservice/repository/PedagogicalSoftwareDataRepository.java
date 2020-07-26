package artie.pedagogicalintervention.webservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;

public interface PedagogicalSoftwareDataRepository extends MongoRepository<PedagogicalSoftwareData, String>{

}
