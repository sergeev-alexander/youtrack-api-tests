package alexander.sergeev.qa.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserRecord(
        @JsonProperty("id") String id,
        @JsonProperty("login") String login,
        @JsonProperty("$type") String type
) {}