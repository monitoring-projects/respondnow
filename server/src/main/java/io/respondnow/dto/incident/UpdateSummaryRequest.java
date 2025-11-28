package io.respondnow.dto.incident;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSummaryRequest {

  @JsonProperty("summary")
  private String summary;
}
