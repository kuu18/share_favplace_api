package com.example.sharefavplace.param;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class FavplaceParam {
  private Integer id;
  @NotBlank
  @Length(max = 50)
  private String favplacename;
  @NotBlank
  private String prefecture;
  @NotBlank
  private String municipality;
  @NotBlank
  private String address;
  @NotNull
  private List<Integer> categoryIds;
  private String referenceUrl;
  private String remarks;
  @NotNull
  private Integer userId;
}