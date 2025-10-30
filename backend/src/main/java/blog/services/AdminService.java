package blog.services;

import blog.dto.request.BanUserRequestDto;
import blog.dto.request.UpdateUserRoleRequestDto;
import blog.dto.response.AdminStatsResponseDto;
import blog.dto.response.UserResponseDto;
import blog.entity.Post;
import blog.entity.ReportStatus;
import blog.entity.ReportedType;
import blog.entity.Role;
import blog.entity.User;
import blog.exceptions.ResourceNotFoundException;
import blog.exceptions.UnauthorizedException;
import blog.repositories.PostRepository;
import blog.repositories.ReportRepository;
import blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReportRepository reportRepository;

    // /**
    // * Get dashboard statistics for admin panel
    // */
    public AdminStatsResponseDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalPosts = postRepository.count();
        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING);

        // Get most reported user
        Pageable topOne = PageRequest.of(0, 1);
        List<Object[]> mostReportedUsers = reportRepository.findMostReportedUsers(topOne);

        UserResponseDto mostReportedUser = null;
        long reportCount = 0;

        if (!mostReportedUsers.isEmpty()) {
            Object[] result = mostReportedUsers.get(0);
            Long userId = (Long) result[0];
            reportCount = (Long) result[1];

            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                mostReportedUser = UserResponseDto.fromEntity(user);
            }
        }

        return new AdminStatsResponseDto(totalUsers, totalPosts, pendingReports,
                mostReportedUser, reportCount);
    }

    /**
     * Get all users with pagination
     */
    public Page<UserResponseDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAllByOrderByIdDesc(pageable);

        return users.map(user -> {
            long reportCount = reportRepository.countByReportedIdAndReportedType(
                    user.getId(), ReportedType.USER);
            return UserResponseDto.forAdminDash(user, reportCount);
        });
    }

    /**
     * Ban a user
     */
    @Transactional
    public void banUser(Long userId, BanUserRequestDto requestDto, Long adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " +
                        userId));

        // Prevent admin from banning themselves
        if (userId.equals(adminId)) {
            throw new UnauthorizedException("You cannot ban yourself");
        }

        // Prevent banning other admins
        if (user.getRole() == Role.ADMIN) {
            throw new UnauthorizedException("You cannot ban other admins");
        }

        user.setBanned(true);
        userRepository.save(user);
    }

    /**
     * Unban a user
     */
    @Transactional
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " +
                        userId));

        user.setBanned(false);
        userRepository.save(user);
    }

    /**
     * Delete a user permanently (cascade will handle related entities)
     */
    @Transactional
    public void deleteUser(Long userId, Long adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " +
                        userId));

        // Prevent admin from deleting themselves
        if (userId.equals(adminId)) {
            throw new UnauthorizedException("You cannot delete yourself");
        }

        // Prevent deleting other admins
        if (user.getRole() == Role.ADMIN) {
            throw new UnauthorizedException("You cannot delete other admins");
        }

        userRepository.delete(user);
    }

    /**
     * Update user role
     */
    @Transactional
    public void updateUserRole(Long userId, UpdateUserRoleRequestDto requestDto,
            Long adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " +
                        userId));

        // Prevent admin from changing their own role
        if (userId.equals(adminId)) {
            throw new UnauthorizedException("You cannot change your own role");
        }

        user.setRole(requestDto.getRole());
        userRepository.save(user);
    }

    /**
     * Delete a post by admin (override owner check)
     */
    @Transactional
    public void deletePostByAdmin(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " +
                        postId));

        postRepository.delete(post);
    }

    /**
     * Get all posts with pagination
     */
    public Page<Post> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAll(pageable);
    }
}
