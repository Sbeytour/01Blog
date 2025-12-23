package blog.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import blog.dto.request.LoginRequestDto;
import blog.dto.request.RegisterRequestDto;
import blog.entity.User;
import blog.exceptions.InvalidCredentialsException;
import blog.exceptions.SuccessException;
import blog.exceptions.UserAlreadyExistsException;
import blog.exceptions.UserisBannedException;
import blog.repositories.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRespository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRespository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {
        this.userRespository = userRespository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User saveUser(RegisterRequestDto registerRequest) {
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            throw new SuccessException("Username is required");
        }
        if (registerRequest.getFirstName() == null || registerRequest.getFirstName().trim().isEmpty()) {
            throw new SuccessException("FirstName is required");
        }
        if (registerRequest.getLastName() == null || registerRequest.getLastName().trim().isEmpty()) {
            throw new SuccessException("LastName is required");
        }
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            throw new SuccessException("Email is required");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            throw new SuccessException("Password is required");
        }

        if (userRespository.existsByUsername(registerRequest.getUsername())
                || userRespository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("username or email already exists");
        }

        User user = new User();

        user.setUsername(registerRequest.getUsername().trim());
        user.setFirstName(registerRequest.getFirstName().trim());
        user.setLastName(registerRequest.getLastName().trim());
        user.setEmail(registerRequest.getEmail().trim());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        if (registerRequest.getBio() != null && !registerRequest.getBio().trim().isEmpty()) {
            user.setBio(registerRequest.getBio());
        }

        return userRespository.save(user);
    }

    public User login(LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return (User) authentication.getPrincipal();
        } catch (UserisBannedException e) {
            throw new UserisBannedException("User account is banned");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username/email or password");
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Authentication failed. Please check your credentials");
        }
    }
}
