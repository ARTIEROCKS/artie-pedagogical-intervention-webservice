package artie.pedagogicalintervention.webservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import artie.pedagogicalintervention.webservice.model.SensorData;

public interface SensorDataRepository extends MongoRepository<SensorData, String> {

}
