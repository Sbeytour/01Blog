package blog.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
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
import blog.exceptions.UserAlreadyExistsException;
import blog.exceptions.UserisBannedException;
import blog.exceptions.ValidationException;
import blog.repositories.UserRepository;
import blog.util.BanMessageFormatter;

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
            throw new ValidationException("Username is required");
        }
        if (registerRequest.getFirstName() == null || registerRequest.getFirstName().trim().isEmpty()) {
            throw new ValidationException("FirstName is required");
        }
        if (registerRequest.getLastName() == null || registerRequest.getLastName().trim().isEmpty()) {
            throw new ValidationException("LastName is required");
        }
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            throw new ValidationException("Password is required");
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
        // First, check if user exists and is banned
        User user = userRespository.findByUsernameOrEmail(loginRequest.getIdentifier());
        if (user != null && user.isActiveBan()) {
            String banMessage = BanMessageFormatter.formatBanMessage(user.getBannedUntil(), user.getBanReason());
            throw new UserisBannedException(banMessage);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return (User) authentication.getPrincipal();
        } catch (LockedException | DisabledException e) {
            // User account is locked/disabled (banned)
            throw new UserisBannedException(e.getMessage());
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username/email or password");
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Authentication failed. Please check your credentials");
        }
    }
}
