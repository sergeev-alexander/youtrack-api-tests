package alexander.sergeev.qa.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateIssueRequest(
        @JsonProperty("summary") String summary
) {}
