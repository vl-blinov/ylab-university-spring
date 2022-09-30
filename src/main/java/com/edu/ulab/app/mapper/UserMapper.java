package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.web.request.UserRequest;
import org.mapstruct.Context;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userRequestToUserDto(UserRequest userRequest);

    UserRequest userDtoToUserRequest(UserDto userDto);

    @Mapping(source = "books", target = "books")
    Person userDtoToUserEntity(UserDto userDto, @Context CycleAvoidingMappingContext context);

    @InheritInverseConfiguration
    UserDto userEntityToUserDto(Person userEntity, @Context CycleAvoidingMappingContext context);
}
