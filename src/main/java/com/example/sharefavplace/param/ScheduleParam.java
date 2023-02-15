package com.example.sharefavplace.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleParam {
  @NotNull(groups = UpdateDeleteGroup.class)
  private Integer id;
  @NotBlank(groups = {CreateGroup.class, UpdateDeleteGroup.class})
  private String start;
  @NotBlank(groups = {CreateGroup.class, UpdateDeleteGroup.class})
  private String end;
  @NotNull(groups = {CreateGroup.class, UpdateDeleteGroup.class})
  private Boolean timed;

  public interface CreateGroup{}
  public interface UpdateDeleteGroup{}
}
