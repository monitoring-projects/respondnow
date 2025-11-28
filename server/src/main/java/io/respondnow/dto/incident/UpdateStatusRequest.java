package io.respondnow.dto.incident;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.respondnow.model.incident.Status;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

  @JsonProperty("status")
  private Status status;
}
