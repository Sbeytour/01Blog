package blog.services;

import blog.dto.CreateUserDto;
import blog.dto.UserResponseDto;

public interface UserService {
    public UserResponseDto saveUser(CreateUserDto createUserDto);
}
