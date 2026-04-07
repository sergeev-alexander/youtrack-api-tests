package alexander.sergeev.qa.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ErrorRecord(
        @JsonProperty("error") String error,
        @JsonProperty("error_description") String errorDescription
) {}