package blog.services;

import blog.dto.request.CreateReportRequestDto;
import blog.dto.response.ReportResponseDto;
import blog.entity.*;
import blog.exceptions.DuplicateReportException;
import blog.exceptions.UserNotFoundException;
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

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    /**
     * Create a new report
     */
    @Transactional
    public ReportResponseDto createReport(CreateReportRequestDto requestDto, User reporter) {
        // Validate that the reported entity exists
        validateReportedEntity(requestDto.getReportedEntityType(), requestDto.getReportedEntityId());

        // Check if user is trying to report themselves
        if (requestDto.getReportedEntityType() == ReportedEntityType.USER &&
            requestDto.getReportedEntityId().equals(reporter.getId())) {
            throw new IllegalArgumentException("You cannot report yourself");
        }

        // Check if user has already reported this entity with active status
        boolean alreadyReported = reportRepository.existsByReporterAndEntityAndActiveStatus(
                reporter,
                requestDto.getReportedEntityType(),
                requestDto.getReportedEntityId()
        );

        if (alreadyReported) {
            throw new DuplicateReportException();
        }

        // Create and save the report
        Report report = new Report(
                requestDto.getReportedEntityType(),
                requestDto.getReportedEntityId(),
                reporter,
                requestDto.getReason(),
                requestDto.getDescription()
        );

        Report savedReport = reportRepository.save(report);

        return mapToReportResponseDto(savedReport);
    }

    /**
     * Get reports submitted by the current user
     */
    @Transactional(readOnly = true)
    public Page<ReportResponseDto> getUserReports(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Report> reports = reportRepository.findByReporter(user, pageable);

        return ReportResponseDto.fromEntity(null, null, null, null, null, null, null);
    }

    /**
     * Validate that the reported entity exists
     */
    private void validateReportedEntity(ReportedEntityType entityType, Long entityId) {
        switch (entityType) {
            case USER:
                if (!userRepository.existsById(entityId)) {
                    throw new UserNotFoundException("User not found with ID: " + entityId);
                }
                break;
            case POST:
                if (!postRepository.existsById(entityId)) {
                    throw new IllegalArgumentException("Post not found with ID: " + entityId);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid entity type: " + entityType);
        }
    }

}
