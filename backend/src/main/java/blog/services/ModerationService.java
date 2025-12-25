package blog.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import blog.dto.request.BanUserRequestDto;
import blog.dto.request.UpdateUserRoleRequestDto;
import blog.entity.Post;
import blog.entity.User;
import blog.exceptions.ResourceNotFoundException;
import blog.exceptions.UnauthorizedException;
import blog.repositories.PostRepository;
import blog.repositories.ReportRepository;
import blog.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class ModerationService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public ModerationService(UserRepository userRepository, ReportRepository reportRepository, PostRepository postRepository, UserService userService) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    //-----------------------------User Moderation--------------------

    // Ban a user
    @Transactional
    public void banUser(Long userId, BanUserRequestDto requestDto) {
        User admin = userService.getCurrentUserEntity();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isAdmin()) {
            throw new UnauthorizedException("Cannot ban admin users");
        }

        if (user.getIsBanned()) {
            throw new UnauthorizedException("This user is already banned");
        }

        // Prevent banning yourself
        if (user.getId() == admin.getId()) {
            throw new UnauthorizedException("You cannot ban yourself");
        }

        user.setIsBanned(true);
        user.setBanReason(requestDto.getReason());

        if (Boolean.TRUE.equals(requestDto.getPermanent())) {
            user.setBannedUntil(null);
        } else if (requestDto.getDurationDays() != null && requestDto.getDurationDays() > 0) {
            user.setBannedUntil(LocalDateTime.now().plusDays(requestDto.getDurationDays()));
        } else {
            user.setBannedUntil(null);
        }

        userRepository.save(user);
    }

    // Unban a user
    @Transactional
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setIsBanned(false);
        userRepository.save(user);
    }

    // Delete a user permanently
    @Transactional
    public void deleteUser(Long userId) {
        User admin = userService.getCurrentUserEntity();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Prevent admin from deleting themselves
        if (userId.equals(admin.getId())) {
            throw new UnauthorizedException("You cannot delete yourself");
        }

        // Prevent deleting other admins
        if (user.isAdmin()) {
            throw new UnauthorizedException("You cannot delete other admins");
        }

        reportRepository.findAll().stream()
                .filter(report -> report.getResolvedBy() != null && report.getResolvedBy().getId() == userId)
                .forEach(report -> {
                    report.setResolvedBy(null);
                    reportRepository.save(report);
                });

        userRepository.delete(user);
    }

    // Update user role
    @Transactional
    public void updateUserRole(Long userId, UpdateUserRoleRequestDto requestDto,
            Long adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: "
                + userId));

        // Prevent admin from changing their own role
        if (userId.equals(adminId)) {
            throw new UnauthorizedException("You cannot change your own role");
        }

        user.setRole(requestDto.getRole());
        userRepository.save(user);
    }

    //--------------------Post Moderation-----------------------------
    
    //Delete Post
    @Transactional
    public void deletePostByAdmin(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: "
                + postId));

        postRepository.delete(post);
    }
}
