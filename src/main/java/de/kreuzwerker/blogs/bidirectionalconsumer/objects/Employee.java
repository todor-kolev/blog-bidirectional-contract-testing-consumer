package de.kreuzwerker.blogs.bidirectionalconsumer.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class Employee {

  @NotNull private String firstName;
  private String lastName;
  @NotNull private String email;
  @Schema(type = "string", format = "uuid")
  private String employeeId = "";
}
