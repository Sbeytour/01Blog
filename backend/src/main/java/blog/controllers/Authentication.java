package blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import blog.dto.RegisterUserDto;
import blog.dto.UserResponseDto;
import blog.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class Authentication {
    @Autowired
    UserService userService;
    
    @PostMapping("/register")
    public UserResponseDto createUser(@RequestBody @Valid RegisterUserDto userDto) {
        return userService.saveUser(userDto);
    }

    @GetMapping
    public String Hello() {
        return "Hello safae";
    }
}
