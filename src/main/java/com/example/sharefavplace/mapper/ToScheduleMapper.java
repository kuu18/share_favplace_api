package com.example.sharefavplace.mapper;

import com.example.sharefavplace.model.Schedule;
import com.example.sharefavplace.param.ScheduleParam;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ToScheduleMapper {
  ToScheduleMapper INSTANCE = Mappers.getMapper(ToScheduleMapper.class);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "df", ignore = true)
  @Mapping(target = "dfwithTime", ignore = true)
  @Mapping(target = "favplace", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "start", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "end", dateFormat = "yyyy-MM-dd")
  Schedule scheduleParamToschedule(ScheduleParam scheduleParam);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "df", ignore = true)
  @Mapping(target = "dfwithTime", ignore = true)
  @Mapping(target = "favplace", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "start", dateFormat = "yyyy-MM-dd HH:mm")
  @Mapping(target = "end", dateFormat = "yyyy-MM-dd HH:mm")
  Schedule scheduleParamToscheduleWithTime(ScheduleParam scheduleParam);

}
