package io.respondnow.dto.incident;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.respondnow.dto.DefaultResponseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeleteResponseDTO extends DefaultResponseDTO {

  @JsonProperty("data")
  private DeleteResponse data;

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class DeleteResponse {
    private String incidentId;
    private Boolean deleted;
    private String correlationID;
  }
}
