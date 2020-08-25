package artie.pedagogicalintervention.webservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareSolution;

public interface PedagogicalSoftwareSolutionRepository extends MongoRepository<PedagogicalSoftwareSolution, String>{

}
