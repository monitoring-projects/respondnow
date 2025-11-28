package io.respondnow.dto.incident;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.respondnow.dto.DefaultResponseDTO;
import io.respondnow.model.incident.Incident;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
public class GetResponseDTO extends DefaultResponseDTO {

  @JsonProperty("data")
  private Incident incident;
}
