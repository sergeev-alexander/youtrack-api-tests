package alexander.sergeev.qa.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommentRecord(
        @JsonProperty("id") String id,
        @JsonProperty("text") String text,
        @JsonProperty("$type") String type
) {}