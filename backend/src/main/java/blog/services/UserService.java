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

import blog.dto.request.UpdateProfileRequestDto;
import blog.dto.response.UserResponseDto;
import blog.entity.User;
import blog.exceptions.UserAlreadyExistsException;
import blog.exceptions.UserNotFoundException;
import blog.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponseDto getUserProfile(String username) {
        User user = userRepository.findByUsernameOrEmail(username);

        return UserResponseDto.fromEntity(user);
    }

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

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";

        String fileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(uploadsDir, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // ✅ Delete old profile picture if exists
        if (user.getProfileImgUrl() != null && !user.getProfileImgUrl().isEmpty()) {
            String oldFileName = user.getProfileImgUrl().replace("/files/", "");
            Path oldFilePath = Paths.get(uploadsDir, oldFileName);
            Files.deleteIfExists(oldFilePath);
        }

        user.setProfileImgUrl("/files/" + fileName);
        User updatedUser = userRepository.save(user);

        return UserResponseDto.fromEntity(updatedUser);
    }

    public UserResponseDto deleteProfileImg(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String uploadsDir = "uploads";
        String fileUrl = user.getProfileImgUrl();
        if (fileUrl != null && !fileUrl.isEmpty()) {
            try {
                String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
                System.out.println("filename: " + fileName);
                Path filePath = Paths.get(uploadsDir).resolve(fileName).normalize();
                Files.deleteIfExists(filePath);
                user.setProfileImgUrl(null);
            } catch (IOException e) {
                System.err.println("Could not delete file: " + fileUrl);
            }
        }

        User updatedUser = userRepository.save(user);

        return UserResponseDto.fromEntity(updatedUser);
    }
}
