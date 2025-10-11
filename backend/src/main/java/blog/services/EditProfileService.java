package blog.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import blog.dto.UpdateProfileRequestDto;
import blog.dto.UserResponseDto;
import blog.entity.User;
import blog.exceptions.UserAlreadyExistsException;
import blog.exceptions.UserNotFoundException;
import blog.repositories.UserRepository;

@Service
public class EditProfileService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponseDto updateProfile(Long userId, UpdateProfileRequestDto updateDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update firstName
        if (updateDto.getFirstName() != null && !updateDto.getFirstName().trim().isEmpty()) {
            user.setFirstName(updateDto.getFirstName().trim());
        }

        // Update lastName
        if (updateDto.getLastName() != null && !updateDto.getLastName().trim().isEmpty()) {
            user.setLastName(updateDto.getLastName().trim());
        }

        // Update email (check if already exists)
        if (updateDto.getEmail() != null && !updateDto.getEmail().trim().isEmpty()) {
            String newEmail = updateDto.getEmail().trim();
            if (!user.getEmail().equals(newEmail)) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new UserAlreadyExistsException("Email already exists");
                }
                user.setEmail(newEmail);
            }
        }

        // Update password
        if (updateDto.getPassword() != null && !updateDto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        // Update bio
        if (updateDto.getBio() != null) {
            user.setBio(updateDto.getBio().trim().isEmpty() ? null : updateDto.getBio().trim());
        }

        User updatedUser = userRepository.save(user);
        return UserResponseDto.fromEntity(updatedUser);
    }

    public UserResponseDto updateProfileImg(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String uploadsDir = "uploads";
        File dir = new File(uploadsDir);

        if (!dir.exists())
            dir.mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadsDir, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        user.setProfileImgUrl("/files/" + fileName);
        User updatedUser = userRepository.save(user);

        return UserResponseDto.fromEntity(updatedUser);
    }
}
