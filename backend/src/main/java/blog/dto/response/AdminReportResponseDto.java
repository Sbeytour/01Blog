package blog.dto.response;

import blog.entity.Report;
import blog.entity.ReportReason;
import blog.entity.ReportStatus;
import blog.entity.ReportedType;

import java.time.LocalDateTime;

public class AdminReportResponseDto {

    private Long id;
    private ReportedType reportedType;
    private Long reportedId;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private String reporterUsername;
    private Long reporterId;
    private String reportedEntityName;  // Username or post title
    private String adminNotes;
    private String resolvedByUsername;
    private LocalDateTime resolvedAt;

    public AdminReportResponseDto() {
    }

    public static AdminReportResponseDto fromEntity(Report report, String reportedEntityName) {
        AdminReportResponseDto dto = new AdminReportResponseDto();
        dto.id = report.getId();
        dto.reportedType = report.getReportedType();
        dto.reportedId = report.getReportedId();
        dto.reason = report.getReason();
        dto.description = report.getDescription();
        dto.status = report.getStatus();
        dto.createdAt = report.getCreatedAt();
        dto.reporterUsername = report.getReporter() != null ? report.getReporter().getUsername() : null;
        dto.reporterId = report.getReporter() != null ? report.getReporter().getId() : null;
        dto.reportedEntityName = reportedEntityName;
        dto.adminNotes = report.getAdminNotes();
        dto.resolvedByUsername = report.getResolvedBy() != null ? report.getResolvedBy().getUsername() : null;
        dto.resolvedAt = report.getResolvedAt();
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportedType getReportedType() {
        return reportedType;
    }

    public void setReportedType(ReportedType reportedType) {
        this.reportedType = reportedType;
    }

    public Long getReportedId() {
        return reportedId;
    }

    public void setReportedId(Long reportedId) {
        this.reportedId = reportedId;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getReportedEntityName() {
        return reportedEntityName;
    }

    public void setReportedEntityName(String reportedEntityName) {
        this.reportedEntityName = reportedEntityName;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public String getResolvedByUsername() {
        return resolvedByUsername;
    }

    public void setResolvedByUsername(String resolvedByUsername) {
        this.resolvedByUsername = resolvedByUsername;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
