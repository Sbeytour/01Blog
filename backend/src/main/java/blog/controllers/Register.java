package blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import blog.dto.CreateUserDto;
import blog.dto.UserResponseDto;
import blog.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class Register {
    @Autowired
    UserService userService;
    
    @PostMapping
    public UserResponseDto createUser(@RequestBody @Valid CreateUserDto userDto) {
        return userService.saveUser(userDto);
    }
}
