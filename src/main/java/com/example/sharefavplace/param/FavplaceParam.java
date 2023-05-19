package com.example.sharefavplace.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class FavplaceParam {
  @NotNull(groups = UpdateDeleteGroup.class)
  private Integer id;
  @NotBlank(groups = {CreateGroup.class, UpdateDeleteGroup.class})
  @Length(max = 50, groups = {CreateGroup.class, UpdateDeleteGroup.class})
  private String favplacename;
  @NotBlank(groups = {CreateGroup.class, UpdateDeleteGroup.class})
  private String prefecture;
  @NotBlank(groups = {CreateGroup.class, UpdateDeleteGroup.class})
  private String municipality;
  @NotBlank(groups = {CreateGroup.class, UpdateDeleteGroup.class})
  private String address;
  @NotNull(groups = {CreateGroup.class, UpdateDeleteGroup.class})
  private Integer categoryId;
  private String referenceUrl;
  private String remarks;
  @NotNull(groups = {CreateGroup.class, UpdateDeleteGroup.class})
  private Integer userId;

  public interface CreateGroup{}
  public interface UpdateDeleteGroup{}
}