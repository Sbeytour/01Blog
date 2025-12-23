package blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import blog.dto.request.LoginRequestDto;
import blog.dto.request.RegisterRequestDto;
import blog.dto.response.AuthResponseDto;
import blog.dto.response.UserResponseDto;
import blog.entity.User;
import blog.exceptions.InvalidCredentialsException;
import blog.security.JwtUtils;
import blog.services.AuthService;
import blog.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, UserService userService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto requestDto) {
        try {
            User savedUser = authService.saveUser(requestDto);
            String jwt = jwtUtils.generateToken(savedUser);

            UserResponseDto userResponse = UserResponseDto.fromEntity(savedUser);
            AuthResponseDto authResponse = new AuthResponseDto(jwt, userResponse);

            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException(
                    "Registration successful but authentication failed. Please try logging in.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto) {
        User user = authService.login(loginDto);
        String jwt = jwtUtils.generateToken(user);

        UserResponseDto userResponseDto = UserResponseDto.fromEntity(user);
        AuthResponseDto authResponse = new AuthResponseDto(jwt, userResponseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        User user = userService.getCurrentUserEntity();
        UserResponseDto userResponse = UserResponseDto.fromEntity(user);

        return ResponseEntity.ok(userResponse);
    }
}
