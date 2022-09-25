package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.CycleAvoidingMappingContext;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Qualifier("userServiceJpa")
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToUserEntity(userDto, new CycleAvoidingMappingContext());
        log.info("Mapped user: {}", user);
        Person createdUser = userRepository.save(user);
        log.info("Created user: {}", createdUser);
        return userMapper.userEntityToUserDto(createdUser, new CycleAvoidingMappingContext());
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person user = userMapper.userDtoToUserEntity(userDto, new CycleAvoidingMappingContext());
        log.info("Mapped user: {}", user);
        Person updatedUser = userRepository.save(user);
        log.info("Updated user: {}", updatedUser);
        return userMapper.userEntityToUserDto(updatedUser, new CycleAvoidingMappingContext());
    }

    @Override
    public UserDto getUserById(Long id) {
        Person extractedUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id '" + id + "' is not found"));
        log.info("Extracted user: {}", extractedUser);
        return userMapper.userEntityToUserDto(extractedUser, new CycleAvoidingMappingContext());
    }

    @Override
    public void deleteUserById(Long id) {
        getUserById(id);
        userRepository.deleteById(id);
    }
}
