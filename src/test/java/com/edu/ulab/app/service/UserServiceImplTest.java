package com.edu.ulab.app.service;

import com.edu.ulab.app.config.UnitTest;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.UniqueViolationException;
import com.edu.ulab.app.mapper.CycleAvoidingMappingContext;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Functionality tests {@link UserServiceImpl}.
 */
@UnitTest
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserServiceImpl underTest;

    @Test
    @DisplayName("Create a new user. Should be successful.")
    void createUser_Test_Success() {

        //Given
        UserDto userDto = new UserDto();
        userDto.setFullName("test name");
        userDto.setTitle("test title");
        userDto.setAge(100);

        Person user  = new Person();
        user.setFullName("test name");
        user.setTitle("test title");
        user.setAge(100);

        Person createdUser  = new Person();
        createdUser.setId(1L);
        createdUser.setFullName("test name");
        createdUser.setTitle("test title");
        createdUser.setAge(100);

        UserDto result = new UserDto();
        result.setId(1L);
        result.setFullName("test name");
        result.setTitle("test title");
        result.setAge(100);

        //When
        when(userRepository.existsByTitle(userDto.getTitle())).thenReturn(false);
        when(userMapper.userDtoToUserEntity(any(UserDto.class), any(CycleAvoidingMappingContext.class)))
                .thenReturn(user);
        when(userRepository.save(user)).thenReturn(createdUser);
        when(userMapper.userEntityToUserDto(any(Person.class), any(CycleAvoidingMappingContext.class)))
                .thenReturn(result);

        UserDto userDtoResult = underTest.createUser(userDto);

        //Then
        assertEquals(1L, userDtoResult.getId());
    }

    @Test
    @DisplayName("Create a new user. Should throw UniqueViolationException.")
    void createUser_Test_Failure() {

        //Given
        String title = "test title";

        UserDto userDto = new UserDto();
        userDto.setFullName("test name");
        userDto.setTitle(title);
        userDto.setAge(100);

        String message = "User with title '" + title + "' already exists.";

        //When
        when(userRepository.existsByTitle(title)).thenReturn(true);

        //Then
        assertThrows(UniqueViolationException.class, () -> underTest.createUser(userDto), message);
    }

    @Test
    @DisplayName("Update an existing user. Should be successful.")
    void updateUser_Test() {

        //Given
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFullName("test name");
        userDto.setTitle("test title");
        userDto.setAge(100);

        Person user  = new Person();
        user.setId(1L);
        user.setFullName("test name");
        user.setTitle("test title");
        user.setAge(100);

        Person updatedUser  = new Person();
        updatedUser.setId(1L);
        updatedUser.setFullName("test name");
        updatedUser.setTitle("test title");
        updatedUser.setAge(100);

        UserDto result = new UserDto();
        result.setId(1L);
        result.setFullName("test name");
        result.setTitle("test title");
        result.setAge(100);

        //When
        when(userMapper.userDtoToUserEntity(any(UserDto.class), any(CycleAvoidingMappingContext.class)))
                .thenReturn(user);
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.userEntityToUserDto(any(Person.class), any(CycleAvoidingMappingContext.class)))
                .thenReturn(result);

        UserDto userDtoResult = underTest.updateUser(userDto);

        //Then
        assertEquals(1L, userDtoResult.getId());
    }

    @Test
    @DisplayName("Get a user by the given ID. Should be successful.")
    void getUserById_Test_Success() {

        //Given
        Long userId = 1L;

        Person extractedUser  = new Person();
        extractedUser.setId(userId);
        extractedUser.setFullName("test name");
        extractedUser.setTitle("test title");
        extractedUser.setAge(100);

        UserDto result = new UserDto();
        result.setId(userId);
        result.setFullName("test name");
        result.setTitle("test title");
        result.setAge(100);

        //When
        when(userRepository.findById(userId)).thenReturn(Optional.of(extractedUser));
        when(userMapper.userEntityToUserDto(any(Person.class), any(CycleAvoidingMappingContext.class)))
                .thenReturn(result);

        UserDto userDtoResult = underTest.getUserById(userId);

        //Then
        assertEquals(1L, userDtoResult.getId());
    }

    @Test
    @DisplayName("Get a user by the given ID. Should throw NotFoundException.")
    void getUserById_Test_Failure() {

        //Given
        Long userId = 1L;
        String message = "User with id '" + userId + "' is not found";

        //When
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //Then
        assertThrows(NotFoundException.class, () -> underTest.getUserById(userId), message);
    }

    @Test
    @DisplayName("Delete a user by the given ID. Should be successful.")
    void deleteUserById_Test_Success() {

        //Given
        Long userId = 1L;

        Person extractedUser  = new Person();
        extractedUser.setId(userId);
        extractedUser.setFullName("test name");
        extractedUser.setTitle("test title");
        extractedUser.setAge(100);

        //When
        when(userRepository.findById(userId)).thenReturn(Optional.of(extractedUser));
        underTest.deleteUserById(userId);

        //Then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).deleteById(captor.capture());
        assertEquals(userId, captor.getValue());
    }

    @Test
    @DisplayName("Delete a user by the given ID. Should throw NotFoundException.")
    void deleteUserById_Test_Failure() {

        //Given
        Long userId = 1L;
        String message = "User with id '" + userId + "' is not found";

        //When
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //Then
        assertThrows(NotFoundException.class, () -> underTest.deleteUserById(userId), message);
    }
}
