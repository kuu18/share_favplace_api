package com.example.sharefavplace.mapper;

import com.example.sharefavplace.model.Favplace;
import com.example.sharefavplace.param.FavplaceParam;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ToFavplaceMapper {
  ToFavplaceMapper INSTANCE = Mappers.getMapper(ToFavplaceMapper.class);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "categories", ignore = true)
  @Mapping(target = "imageUrl", ignore = true)
  @Mapping(target = "user", ignore = true)
  Favplace favplaceParamToFavplace(FavplaceParam param);
}
