package blog.controller;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import blog.dto.request.UpdateProfileRequestDto;
import blog.dto.response.UserResponseDto;
import blog.entity.User;
import blog.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping
    public UserResponseDto updateProfile(@Valid @RequestBody UpdateProfileRequestDto updateDto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.updateProfile(user.getId(), updateDto);
    }

    @GetMapping("/{username}")
    public UserResponseDto getUserProfile(@PathVariable String username, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return userService.getUserProfile(username, currentUser.getId());
    }

    @PostMapping("/picture")
    public UserResponseDto updateProfileImg(@Valid @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        User user = (User) authentication.getPrincipal();
        return userService.updateProfileImg(user.getId(), file);
    }

    @DeleteMapping("/picture")
    public UserResponseDto deleteProfileImg(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        return userService.deleteProfileImg(user.getId());
    }
}
