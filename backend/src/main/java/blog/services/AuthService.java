package blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import blog.dto.RegisterRequestDto;
import blog.entity.User;
import blog.exceptions.UserAlreadyExists;
import blog.repositories.UserRepository;

@Service
public class AuthService {
    @Autowired
    UserRepository userRespository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User saveUser(RegisterRequestDto registerDto) {
        if (userRespository.existsByEmail(registerDto.getUsername())
                || userRespository.existsByEmail(registerDto.getEmail())) {
            throw new UserAlreadyExists("username or email already exists");
        }

        User user = new User();

        user.setUsername(registerDto.getUsername());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setBio(registerDto.getBio());
        user.setProfileImgUrl(registerDto.getProfileImgUrl());

        return userRespository.save(user);
    }
}
