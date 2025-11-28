package io.respondnow.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DefaultResponseDTO {
  private String correlationId;
  private String message;
  private String status;
}
