package blog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import blog.dto.response.AdminStatsResponseDto;
import blog.dto.response.PostResponseDto;
import blog.dto.response.UserResponseDto;
import blog.entity.Post;
import blog.entity.ReportStatus;
import blog.entity.ReportedType;
import blog.entity.User;
import blog.repositories.PostRepository;
import blog.repositories.ReportRepository;
import blog.repositories.UserRepository;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReportRepository reportRepository;

    // Get dashboard statistics for admin panel
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

    // Get all users
    public Page<UserResponseDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAllByOrderByIdDesc(pageable);

        return users.map(user -> {
            long reportCount = reportRepository.countByReportedIdAndReportedType(
                    user.getId(), ReportedType.USER);
            return UserResponseDto.forAdminDash(user, reportCount);
        });
    }

    
    // Get all posts for admin dashboard
    public List<PostResponseDto> getAllPosts() {
        List<Post> posts = postRepository.findAllPosts();
        return posts.stream()
                .map(PostResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

}
