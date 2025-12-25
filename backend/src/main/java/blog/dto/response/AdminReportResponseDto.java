package blog.dto.response;

import java.time.LocalDateTime;

import blog.entity.Report;
import blog.entity.ReportReason;
import blog.entity.ReportStatus;
import blog.entity.ReportedType;

public class AdminReportResponseDto {
    private Long id;
    private Long reportedId;
    private Long reporterId;
    private ReportedType reportedType;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private String reporterUsername;
    private String reportedName;
    private String reportedStatus;
    private String adminNotes;
    private String resolvedByUsername;
    private LocalDateTime resolvedAt;

    public AdminReportResponseDto() {
    }

    public static AdminReportResponseDto fromEntity(Report report) {
        AdminReportResponseDto dto = new AdminReportResponseDto();
        dto.id = report.getId();
        dto.reportedType = report.getReportedType();
        dto.reason = report.getReason();
        dto.description = report.getDescription();
        dto.status = report.getStatus();
        dto.reporterUsername = report.getReporter() != null ? report.getReporter().getUsername() : null;
        return dto;
    }

    public static AdminReportResponseDto forDetails(Report report, String reportedName, String status) {
        AdminReportResponseDto dto = new AdminReportResponseDto();
        dto.id = report.getId();
        dto.reportedId = report.getReportedId();
        dto.reporterId = report.getReporter() != null ? report.getReporter().getId() : null;
        dto.reportedType = report.getReportedType();
        dto.reason = report.getReason();
        dto.description = report.getDescription();
        dto.status = report.getStatus();
        dto.createdAt = report.getCreatedAt();
        dto.reporterUsername = report.getReporter() != null ? report.getReporter().getUsername() : null;
        dto.reportedName = reportedName;
        dto.reportedStatus = status;
        dto.adminNotes = report.getAdminNotes();
        dto.resolvedByUsername = report.getResolvedBy() != null ? report.getResolvedBy().getUsername() : null;
        dto.resolvedAt = report.getResolvedAt();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportedId() {
        return reportedId;
    }

    public void setReportedId(Long reportedId) {
        this.reportedId = reportedId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public ReportedType getReportedType() {
        return reportedType;
    }

    public void setReportedType(ReportedType reportedType) {
        this.reportedType = reportedType;
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

    public String getReportedName() {
        return reportedName;
    }

    public void setReportedName(String reportedName) {
        this.reportedName = reportedName;
    }

    public String getReportedStatus() {
        return reportedStatus;
    }

    public void setReportedStatus(String reportedStatus) {
        this.reportedStatus = reportedStatus;
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
