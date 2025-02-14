package artie.pedagogicalintervention.webservice.service;

import artie.pedagogicalintervention.webservice.dto.MessageDTO;
import artie.pedagogicalintervention.webservice.dto.TeacherHelpRequestDTO;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareSolution;
import artie.pedagogicalintervention.webservice.repository.PedagogicalSoftwareSolutionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TeacherHelpRequestListenerService implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(TeacherHelpRequestListenerService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PedagogicalSoftwareService pedagogicalSoftwareService;

    @Autowired
    private InterventionService interventionService;

    @Override
    public void onMessage(Message message) {
        try {
            String strMessage = new String(message.getBody());
            logger.info("Consuming Message - {}", strMessage);

            // Parse the message content
            TeacherHelpRequestDTO messageDTO = this.objectMapper.readValue(strMessage, TeacherHelpRequestDTO.class);
            PedagogicalSoftwareData psd = pedagogicalSoftwareService.getLastRecordByStudentId(messageDTO.getStudentId());

            // Now we have the PSD we also should have the next steps to generate the help
            interventionService.buildAndSendIntervention(psd, null);

        } catch (Exception e) {
            // Handle exception
            logger.error("Failed to process message: {}", e.getMessage());
        }
    }
}
