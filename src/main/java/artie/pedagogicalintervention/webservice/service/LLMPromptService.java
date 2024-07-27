package artie.pedagogicalintervention.webservice.service;

import artie.pedagogicalintervention.webservice.model.LLMPrompt;
import artie.pedagogicalintervention.webservice.repository.LLMPromptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LLMPromptService {

    @Autowired
    private LLMPromptRepository pedagogicalSentenceRepository;
    public List<LLMPrompt> findByInstitutionIdAndPromptKey(String institutionId, String promptKey){
        return pedagogicalSentenceRepository.findByInstitutionIdAndPromptKey(institutionId, promptKey);
    }
}
