package blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import blog.dto.AuthResponseDto;
import blog.dto.LoginRequestDto;
import blog.dto.RegisterRequestDto;
import blog.dto.UserResponseDto;
import blog.entity.User;
import blog.exceptions.InvalidCredentialsException;
import blog.security.JwtUtils;
import blog.services.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/register")
    public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto requestDto) {
        try {
            User savedUser = authService.saveUser(requestDto);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateToken(authentication);

            UserResponseDto userResponse = UserResponseDto.fromEntity(savedUser);
            AuthResponseDto authResponse = new AuthResponseDto(jwt, userResponse);

            return authResponse;
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException(
                    "Registration successful but authentication failed. Please try logging in.");
        }
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto loginDto) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getIdentifier(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateToken(authentication);

            User user = (User) authentication.getPrincipal();
            UserResponseDto userResponseDto = UserResponseDto.fromEntity(user);
            AuthResponseDto authResponse = new AuthResponseDto(jwt, userResponseDto);

            return authResponse;
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username/email or password");
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Authentication failed. Please check your credentials");
        }

    }

    @GetMapping("/me")
    public UserResponseDto getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return UserResponseDto.fromEntity(user);
    }
}
