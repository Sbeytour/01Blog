package blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import blog.dto.request.RegisterRequestDto;
import blog.entity.User;
import blog.exceptions.SuccessException;
import blog.exceptions.UserAlreadyExistsException;
import blog.repositories.UserRepository;

@Service
public class AuthService {
    @Autowired
    UserRepository userRespository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User saveUser(RegisterRequestDto registerDto) {
        if (userRespository.existsByUsername(registerDto.getUsername())
                || userRespository.existsByEmail(registerDto.getEmail())) {
            throw new UserAlreadyExistsException("username or email already exists");
        }
        try {
            User user = new User();

            user.setUsername(registerDto.getUsername());
            user.setFirstName(registerDto.getFirstName());
            user.setLastName(registerDto.getLastName());
            user.setEmail(registerDto.getEmail());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

            if (registerDto.getBio() != null && !registerDto.getBio().trim().isEmpty()) {
                user.setBio(registerDto.getBio());
            }

            if (registerDto.getProfileImgUrl() != null && !registerDto.getProfileImgUrl().trim().isEmpty()) {
                user.setProfileImgUrl(registerDto.getProfileImgUrl());
            }

            return userRespository.save(user);
        } catch (Exception e) {
            throw new SuccessException("Failed to register user. Please check your input and try again");
        }
    }
}
