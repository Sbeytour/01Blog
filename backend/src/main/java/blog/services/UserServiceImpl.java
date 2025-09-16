package blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blog.dto.CreateUserDto;
import blog.dto.UserResponseDto;
import blog.entity.User;
import blog.exceptions.UserAlreadyExists;
import blog.repositories.UserRepository;
import jakarta.validation.Valid;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    

    @Override
    public UserResponseDto saveUser(@Valid CreateUserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExists("Email '" + userDto.getEmail() + "' is already registered.");
        }
        
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setBio(userDto.getBio());
        user.setPassword(userDto.getPassword());

        return UserResponseDto.fromEntity(userRepository.save(user));
    }
}
