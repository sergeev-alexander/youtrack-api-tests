package alexander.sergeev.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EntityRef(
        @JsonProperty("id") String id
) {}
