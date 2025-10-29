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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

        // Check if user has already reported this entity with active status
        boolean alreadyReported = reportRepository.existsByReporterAndEntityAndActiveStatus(
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

    // ===== ADMIN METHODS =====

    /**
     * Get all reports with pagination
     */
    public Page<AdminReportResponseDto> getAllReports(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Report> reports = reportRepository.findAllByOrderByCreatedAtDesc(pageable);

        return reports.map(report -> {
            String entityName = getReportedEntityName(report.getReportedType(), report.getReportedId());
            return AdminReportResponseDto.fromEntity(report, entityName);
        });
    }

    /**
     * Get reports filtered by status
     */
    public Page<AdminReportResponseDto> getReportsByStatus(ReportStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Report> reports = reportRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable);

        return reports.map(report -> {
            String entityName = getReportedEntityName(report.getReportedType(), report.getReportedId());
            return AdminReportResponseDto.fromEntity(report, entityName);
        });
    }

    /**
     * Get a single report by ID with full details
     */
    public AdminReportResponseDto getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found with id: " + reportId));

        String entityName = getReportedEntityName(report.getReportedType(), report.getReportedId());
        return AdminReportResponseDto.fromEntity(report, entityName);
    }

    /**
     * Resolve a report and take action on the reported entity
     */
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

    /**
     * Execute the action on the reported entity
     */
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

    /**
     * Get the name of the reported entity (username or post title)
     */
    private String getReportedEntityName(ReportedType reportedType, Long reportedId) {
        switch (reportedType) {
            case USER:
                return userRepository.findById(reportedId)
                        .map(User::getUsername)
                        .orElse("Unknown User");
            case POST:
                return postRepository.findById(reportedId)
                        .map(Post::getTitle)
                        .orElse("Unknown Post");
            default:
                return "Unknown";
        }
    }

}
