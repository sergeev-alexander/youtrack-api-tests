package alexander.sergeev.qa.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IssueRecord(
        @JsonProperty("id") String id,
        @JsonProperty("idReadable") String idReadable,
        @JsonProperty("summary") String summary,
        @JsonProperty("description") String description,
        @JsonProperty("project") ProjectRecord project,
        @JsonProperty("$type") String type
) {}