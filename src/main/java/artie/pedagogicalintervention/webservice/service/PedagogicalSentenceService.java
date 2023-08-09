package artie.pedagogicalintervention.webservice.service;

import artie.pedagogicalintervention.webservice.model.PedagogicalSentence;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSentenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedagogicalSentenceService {

    @Autowired
    private PedagogicalSentenceRepository pedagogicalSentenceRepository;
    public List<PedagogicalSentence> findByInstitutionIdAndSentenceKey(String institutionId, String sentenceKey){
        return pedagogicalSentenceRepository.findByInstitutionIdAndSentenceKey(institutionId, sentenceKey);
    }
}
