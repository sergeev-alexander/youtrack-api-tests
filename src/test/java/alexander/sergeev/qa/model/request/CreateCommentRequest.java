package alexander.sergeev.qa.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCommentRequest(
        @JsonProperty("text") String text
) {}