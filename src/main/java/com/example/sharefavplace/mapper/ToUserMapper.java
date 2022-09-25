package com.example.sharefavplace.mapper;

import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UserParam;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ToUserMapper {
  ToUserMapper INSTANCE = Mappers.getMapper(ToUserMapper.class);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "avatarUrl", ignore = true)
  @Mapping(target = "favplaces", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  User userParamToUser(UserParam user);

  @Mapping(target = "password", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  User toResponseUser(User user);
}
