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

}
