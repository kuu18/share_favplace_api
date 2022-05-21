package com.example.sharefavplace.mapper;

import com.example.sharefavplace.dto.ResponseUserDto;
import com.example.sharefavplace.model.User;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserToUserDtoMapper {
  UserToUserDtoMapper INSTANCE = Mappers.getMapper(UserToUserDtoMapper.class);

  ResponseUserDto userToUserDto(User user);
}
