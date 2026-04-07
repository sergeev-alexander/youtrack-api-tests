package alexander.sergeev.qa.model.request;

import alexander.sergeev.qa.model.EntityRef;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateProjectRequest(
        @JsonProperty("name") String name,
        @JsonProperty("shortName") String shortName,
        @JsonProperty("leader") EntityRef leader
) {}
