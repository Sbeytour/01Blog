package blog.services;

import blog.dto.RegisterUserDto;
import blog.dto.UserResponseDto;

public interface UserService {
    public UserResponseDto saveUser(RegisterUserDto createUserDto);
}
