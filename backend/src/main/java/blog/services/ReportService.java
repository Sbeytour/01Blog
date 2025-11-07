package blog.services;

import blog.dto.request.CreateReportRequestDto;
import blog.dto.request.ResolveReportRequestDto;
import blog.dto.response.AdminReportResponseDto;

import blog.dto.response.ReportResponseDto;
import blog.entity.*;
import blog.exceptions.DuplicateReportException;
import blog.exceptions.ReportNotFoundException;
import blog.exceptions.ResourceNotFoundException;
import blog.exceptions.UserNotFoundException;
import blog.repositories.PostRepository;
import blog.repositories.ReportRepository;
import blog.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Transactional
    public ReportResponseDto createReport(CreateReportRequestDto requestDto, User reporter) {
        // Validate that the reported entity exists
        validateReportedEntity(requestDto.getReportedType(), requestDto.getReportedId());

        // prevent users to report themselves
        if (requestDto.getReportedType() == ReportedType.USER &&
                requestDto.getReportedId().equals(reporter.getId())) {
            throw new IllegalArgumentException("You cannot report yourself");
        }

        // Check if user has already reported this user/post
        boolean alreadyReported = reportRepository.existsByReporterAndTypeAndReported(
                reporter,
                requestDto.getReportedType(),
                requestDto.getReportedId());

        if (alreadyReported) {
            throw new DuplicateReportException();
        }

        // Create and save the report
        Report report = new Report();

        report.setReportedType(requestDto.getReportedType());
        report.setReportedId(requestDto.getReportedId());
        report.setReporter(reporter);
        report.setReason(requestDto.getReason());
        report.setDescription(requestDto.getDescription());

        Report savedReport = reportRepository.save(report);

        return ReportResponseDto.fromEntity(savedReport);
    }

    // validate the existence of the type reported
    private void validateReportedEntity(ReportedType reportedType, Long reportedId) {
        switch (reportedType) {
            case USER:
                if (!userRepository.existsById(reportedId)) {
                    throw new UserNotFoundException("User not found");
                }
                break;
            case POST:
                if (!postRepository.existsById(reportedId)) {
                    throw new IllegalArgumentException("Post not found");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid entity type: " + reportedType);
        }
    }

    // // ===== ADMIN METHODS =====

    // Get all reports
    public List<AdminReportResponseDto> getAllReports() {
        List<Report> reports = reportRepository.findAllByOrderByCreatedAtDesc();

        return reports.stream().map(report -> AdminReportResponseDto.fromEntity(report)).toList();
    }

    // Get a single report by ID with full details
    @Transactional(readOnly = true)
    public AdminReportResponseDto getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));

        Map<String, String> entityInfo = getReportedEntityInfo(report.getReportedType(), report.getReportedId());
        return AdminReportResponseDto.forDetails(report, entityInfo.get("name"), entityInfo.get("status"));
    }

    // Get the name and status of the reported entity
    private Map<String, String> getReportedEntityInfo(ReportedType reportedType, Long reportedId) {
        switch (reportedType) {
            case USER:
                return userRepository.findById(reportedId)
                        .map(user -> {
                            String name = user.getUsername();
                            String status;
                            if (user.getBanned() != null && user.getBanned()) {
                                status = "BANNED";
                            } else {
                                status = "ACTIVE";
                            }
                            return Map.of("name", name, "status", status);
                        })
                        .orElseGet(() -> {
                            System.out.println("User not found - returning DELETED status");
                            return Map.of("name", "This User was Deleted", "status", "DELETED");
                        });

            case POST:
                return postRepository.findById(reportedId)
                        .map(post -> {
                            String name = post.getTitle();
                            String status;
                            if (post.getHidden()) {
                                status = "HIDDEN";
                            } else {
                                status = "ACTIVE";
                            }
                            return Map.of("name", name, "status", status);
                        })
                        .orElseGet(() -> {
                            System.out.println("Post not found - returning DELETED status");
                            return Map.of("name", "This Post was Deleted", "status", "DELETED");
                        });

            default:
                return Map.of("name", "Unknown", "status", "UNKNOWN");
        }
    }

    // /**
    // * Resolve a report and take action on the reported entity
    // */
    @Transactional
    public void resolveReport(Long reportId, ResolveReportRequestDto requestDto, User admin) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found with id: " + reportId));

        // Update report status
        report.setStatus(requestDto.getStatus());
        report.setAdminNotes(requestDto.getAdminNotes());
        report.setResolvedBy(admin);
        report.setResolvedAt(LocalDateTime.now());

        // Take action based on the request
        if (requestDto.getStatus() == ReportStatus.RESOLVED) {
            executeReportAction(report, requestDto.getAction());
        }

        reportRepository.save(report);
    }

    // /**
    // * Execute the action on the reported entity
    // */
    private void executeReportAction(Report report, ReportAction action) {
        switch (action) {
            case BAN_USER:
                if (report.getReportedType() == ReportedType.USER) {
                    User user = userRepository.findById(report.getReportedId())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    user.setBanned(true);
                    userRepository.save(user);
                }
                break;
                case HIDE_POST:
                    if (report.getReportedType() == ReportedType)
            case DELETE_USER:
                if (report.getReportedType() == ReportedType.USER) {
                    userRepository.deleteById(report.getReportedId());
                }
                break;
            case DELETE_POST:
                if (report.getReportedType() == ReportedType.POST) {
                    postRepository.deleteById(report.getReportedId());
                }
                break;
            case NONE:
                // No action taken
                break;
        }
    }
}