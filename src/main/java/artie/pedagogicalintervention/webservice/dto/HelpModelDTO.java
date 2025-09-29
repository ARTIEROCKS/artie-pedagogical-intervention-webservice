package artie.pedagogicalintervention.webservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelpModelDTO {

    private Double threshold;

    @JsonAlias({"help_needed", "need_help"})
    private Boolean helpNeeded;

    @JsonProperty("last_probability")
    private Double lastProbability;

    @JsonProperty("sequence_probabilities")
    private List<Double> sequenceProbabilities;

    private Attention attention;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attention {
        private Boolean available;
        @JsonProperty("top_k")
        private List<TopK> topK;
        @JsonProperty("seq_len")
        private Integer seqLen;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopK {
        private Integer t;
        private Double w;
    }
}

