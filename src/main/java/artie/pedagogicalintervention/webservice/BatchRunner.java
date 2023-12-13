package artie.pedagogicalintervention.webservice;


import artie.pedagogicalintervention.webservice.batch.BatchDistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BatchRunner implements ApplicationRunner {

    private final BatchDistanceService batchDistanceService;
    private final boolean batchDistanceEnabled;

    @Autowired
    public BatchRunner(BatchDistanceService batchDistanceService, @Value("${BATCH_DISTANCE_ENABLED}") boolean batchDistanceEnabled) {
        this.batchDistanceService = batchDistanceService;
        this.batchDistanceEnabled = batchDistanceEnabled;
    }

    @Override
    public void run(ApplicationArguments args) {
        if(this.batchDistanceEnabled) {
            batchDistanceService.process();
        }
    }
}
