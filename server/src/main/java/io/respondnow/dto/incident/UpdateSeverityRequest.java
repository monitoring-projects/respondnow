package io.respondnow.dto.incident;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.respondnow.model.incident.Severity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSeverityRequest {

  @JsonProperty("severity")
  private Severity severity;
}
