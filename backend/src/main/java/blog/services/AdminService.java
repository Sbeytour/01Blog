package blog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import blog.dto.response.AdminStatsResponseDto;
import blog.dto.response.PageResponse;
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

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;

    public AdminService(ReportRepository reportRepository, PostRepository postRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

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
    public PageResponse<UserResponseDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAllByOrderByIdDesc(pageable);

        List<UserResponseDto> userDtos = users.getContent().stream()
                .map(user -> {
                    long reportCount = reportRepository.countByReportedIdAndReportedType(
                            user.getId(), ReportedType.USER);
                    return UserResponseDto.forAdminDash(user, reportCount);
                })
                .collect(Collectors.toList());

        return new PageResponse<>(
                userDtos,
                users.getNumber(),
                users.getTotalPages(),
                users.getTotalElements()
        );
    }

    // Get all posts for admin dashboard with pagination
    public PageResponse<PostResponseDto> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllPosts(pageable);

        List<PostResponseDto> postDtos = postPage.getContent().stream()
                .map(post -> {
                    long reportCount = reportRepository.countByReportedIdAndReportedType(
                            post.getId(), ReportedType.POST);
                    return PostResponseDto.forAdminDash(post, reportCount);
                })
                .collect(Collectors.toList());

        return new PageResponse<>(
                postDtos,
                postPage.getNumber(),
                postPage.getTotalPages(),
                postPage.getTotalElements()
        );
    }

}
