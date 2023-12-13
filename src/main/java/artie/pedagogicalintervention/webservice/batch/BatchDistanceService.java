package artie.pedagogicalintervention.webservice.batch;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchDistanceService {

    @Autowired
    private PedagogicalSoftwareService pedagogicalSoftwareService;
    public void process(){

        List<PedagogicalSoftwareData> elements = this.pedagogicalSoftwareService.findAll();

        for(PedagogicalSoftwareData element: elements){
            
        }

    }
}
