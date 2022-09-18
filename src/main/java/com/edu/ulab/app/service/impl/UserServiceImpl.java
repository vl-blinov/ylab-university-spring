package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dao.impl.UserDao;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUserEntity = userMapper.userDtoToUserEntity(userDto);
        User createdUserEntity = userDao.add(newUserEntity);
        return userMapper.userEntityToUserDto(createdUserEntity);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User updatingUserEntity = userMapper.userDtoToUserEntity(userDto);
        User updatedUserEntity = userDao.update(updatingUserEntity);
        return userMapper.userEntityToUserDto(updatedUserEntity);
    }

    @Override
    public UserDto getUserById(Long id) {
        User extractedUserEntity = userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id '" + id + "' is not found"));
        return userMapper.userEntityToUserDto(extractedUserEntity);
    }

    @Override
    public void deleteUserById(Long id) {
        getUserById(id);
        userDao.deleteById(id);
    }
}
