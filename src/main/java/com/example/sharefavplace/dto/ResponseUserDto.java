package com.example.sharefavplace.dto;

import java.util.Collection;
import java.util.Date;

import com.example.sharefavplace.model.Role;

import lombok.Data;

@Data
public class ResponseUserDto {
  private Integer id;
  private String username;
  private String email;
  private Date createdAt;
  private String avatarUrl;
  private Collection<Role> roles;
}
