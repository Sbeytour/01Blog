package blog.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import blog.dto.UpdateProfileRequestDto;
import blog.dto.UserResponseDto;
import blog.entity.User;
import blog.services.EditProfileService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/profile")
public class EditProgileController {
    @Autowired
    private EditProfileService editService;

    @PutMapping
    public UserResponseDto updateProfile(@Valid @RequestBody UpdateProfileRequestDto updateDto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return editService.updateProfile(user.getId(), updateDto);
    }

    @PostMapping("/picture")
    public UserResponseDto updateProfileImg(@Valid @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        User user = (User) authentication.getPrincipal();
        return editService.updateProfileImg(user.getId(),file);
    }
}
