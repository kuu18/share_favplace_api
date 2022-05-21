package com.example.sharefavplace.mapper;

import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UserParam;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserParamToUserMapper {
  UserParamToUserMapper INSTANCE = Mappers.getMapper(UserParamToUserMapper.class);

  User userParamToUser(UserParam user);
}
