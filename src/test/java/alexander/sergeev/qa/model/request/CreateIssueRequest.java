package alexander.sergeev.qa.model.request;

import alexander.sergeev.qa.model.EntityRef;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateIssueRequest(
        @JsonProperty("summary") String summary,
        @JsonProperty("description") String description,
        @JsonProperty("project") EntityRef project
) {}