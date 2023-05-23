package artie.pedagogicalintervention.webservice.model;

import artie.sensor.common.dto.SensorObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmotionalStateMessage extends SensorObject {
    private String externalId;

    public EmotionalStateMessage(SensorObject sensorObject, String externalId) throws JsonProcessingException {
        ObjectMapper obj = new ObjectMapper();
        obj.registerModule(new JavaTimeModule());
        this.date = sensorObject.getDate();
        this.milliseconds = sensorObject.getMilliseconds();
        this.sensorObjectType = sensorObject.getSensorObjectType();
        this.sensorName = sensorObject.getSensorName();
        this.data = obj.writeValueAsString(sensorObject.getData());
        this.fromDate = sensorObject.getFromDate();
        this.toDate = sensorObject.getToDate();
        this.externalId = externalId;
    }
}
