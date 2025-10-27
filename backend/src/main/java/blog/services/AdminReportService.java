package blog.services;

import blog.dto.request.ResolveReportRequestDto;
import blog.dto.response.AdminReportResponseDto;
import blog.entity.*;
import blog.exceptions.ReportNotFoundException;
import blog.repositories.PostRepository;
import blog.repositories.ReportRepository;
import blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    /**
     * Get all reports with pagination and optional filters
     */
    @Transactional(readOnly = true)
    public Page<AdminReportResponseDto> getAllReports(ReportStatus status, ReportedEntityType entityType,
                                                      int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Report> reports;

        if (status != null && entityType != null) {
            // Filter by both status and entity type
            reports = reportRepository.findByStatusIn(List.of(status), pageable)
                    .map(report -> report.getReportedEntityType() == entityType ? report : null)
                    .map(report -> report);
        } else if (status != null) {
            // Filter by status only
            reports = reportRepository.findByStatus(status, pageable);
        } else if (entityType != null) {
            // Filter by entity type only
            reports = reportRepository.findByReportedEntityType(entityType, pageable);
        } else {
            // No filters - get all reports
            reports = reportRepository.findAllReports(pageable);
        }

        return reports.map(this::mapToAdminReportResponseDto);
    }

    /**
     * Get a single report by ID with full details
     */
    @Transactional(readOnly = true)
    public AdminReportResponseDto getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        return mapToAdminReportResponseDto(report);
    }

    /**
     * Resolve a report (update status and add admin notes)
     */
    @Transactional
    public AdminReportResponseDto resolveReport(Long reportId, ResolveReportRequestDto requestDto, User admin) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        // Validate that the new status is a resolution status
        if (requestDto.getStatus() != ReportStatus.RESOLVED &&
            requestDto.getStatus() != ReportStatus.DISMISSED &&
            requestDto.getStatus() != ReportStatus.UNDER_REVIEW) {
            throw new IllegalArgumentException("Invalid status. Must be RESOLVED, DISMISSED, or UNDER_REVIEW");
        }

        // Update report status
        report.setStatus(requestDto.getStatus());
        report.setAdminNotes(requestDto.getAdminNotes());

        // If status is RESOLVED or DISMISSED, set resolution details
        if (requestDto.getStatus() == ReportStatus.RESOLVED ||
            requestDto.getStatus() == ReportStatus.DISMISSED) {
            report.setResolvedAt(LocalDateTime.now());
            report.setResolvedBy(admin);
        }

        Report updatedReport = reportRepository.save(report);

        return mapToAdminReportResponseDto(updatedReport);
    }

    /**
     * Get report statistics for admin dashboard
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getReportStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // Count by status
        Long pendingCount = reportRepository.countByStatus(ReportStatus.PENDING);
        Long underReviewCount = reportRepository.countByStatus(ReportStatus.UNDER_REVIEW);
        Long resolvedCount = reportRepository.countByStatus(ReportStatus.RESOLVED);
        Long dismissedCount = reportRepository.countByStatus(ReportStatus.DISMISSED);

        statistics.put("pendingCount", pendingCount);
        statistics.put("underReviewCount", underReviewCount);
        statistics.put("resolvedCount", resolvedCount);
        statistics.put("dismissedCount", dismissedCount);
        statistics.put("totalCount", pendingCount + underReviewCount + resolvedCount + dismissedCount);

        // Count by entity type
        Long userReportsCount = reportRepository.countByReportedEntityType(ReportedEntityType.USER);
        Long postReportsCount = reportRepository.countByReportedEntityType(ReportedEntityType.POST);

        statistics.put("userReportsCount", userReportsCount);
        statistics.put("postReportsCount", postReportsCount);

        return statistics;
    }

    /**
     * Map Report entity to AdminReportResponseDto with full details
     */
    private AdminReportResponseDto mapToAdminReportResponseDto(Report report) {
        AdminReportResponseDto dto = new AdminReportResponseDto();

        // Basic report info
        dto.setId(report.getId());
        dto.setReportedEntityType(report.getReportedEntityType());
        dto.setReportedEntityId(report.getReportedEntityId());
        dto.setReason(report.getReason());
        dto.setDescription(report.getDescription());
        dto.setStatus(report.getStatus());
        dto.setCreatedAt(report.getCreatedAt());
        dto.setUpdatedAt(report.getUpdatedAt());
        dto.setResolvedAt(report.getResolvedAt());
        dto.setAdminNotes(report.getAdminNotes());

        // Reporter information
        User reporter = report.getReporter();
        dto.setReporterId(reporter.getId());
        dto.setReporterUsername(reporter.getUsername());
        dto.setReporterEmail(reporter.getEmail());

        // Resolved by information
        if (report.getResolvedBy() != null) {
            dto.setResolvedById(report.getResolvedBy().getId());
            dto.setResolvedByUsername(report.getResolvedBy().getUsername());
        }

        // Add context about the reported entity
        if (report.getReportedEntityType() == ReportedEntityType.POST) {
            postRepository.findById(report.getReportedEntityId()).ifPresent(post -> {
                dto.setReportedEntityTitle(post.getTitle());
            });
        } else if (report.getReportedEntityType() == ReportedEntityType.USER) {
            userRepository.findById(report.getReportedEntityId()).ifPresent(user -> {
                dto.setReportedUsername(user.getUsername());
            });
        }

        return dto;
    }
}
