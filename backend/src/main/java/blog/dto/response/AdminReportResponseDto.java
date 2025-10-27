package blog.dto.response;

import blog.entity.ReportReason;
import blog.entity.ReportStatus;
import blog.entity.ReportedEntityType;

import java.time.LocalDateTime;

public class AdminReportResponseDto {

    private Long id;
    private ReportedEntityType reportedEntityType;
    private Long reportedEntityId;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    // Reporter information
    private Long reporterId;
    private String reporterUsername;
    private String reporterEmail;

    // Resolved by information
    private Long resolvedById;
    private String resolvedByUsername;

    // Admin notes
    private String adminNotes;

    // Additional context (optional fields for reported entity details)
    private String reportedEntityTitle; // For posts
    private String reportedUsername;    // For users

    // Constructors
    public AdminReportResponseDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportedEntityType getReportedEntityType() {
        return reportedEntityType;
    }

    public void setReportedEntityType(ReportedEntityType reportedEntityType) {
        this.reportedEntityType = reportedEntityType;
    }

    public Long getReportedEntityId() {
        return reportedEntityId;
    }

    public void setReportedEntityId(Long reportedEntityId) {
        this.reportedEntityId = reportedEntityId;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public Long getResolvedById() {
        return resolvedById;
    }

    public void setResolvedById(Long resolvedById) {
        this.resolvedById = resolvedById;
    }

    public String getResolvedByUsername() {
        return resolvedByUsername;
    }

    public void setResolvedByUsername(String resolvedByUsername) {
        this.resolvedByUsername = resolvedByUsername;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public String getReportedEntityTitle() {
        return reportedEntityTitle;
    }

    public void setReportedEntityTitle(String reportedEntityTitle) {
        this.reportedEntityTitle = reportedEntityTitle;
    }

    public String getReportedUsername() {
        return reportedUsername;
    }

    public void setReportedUsername(String reportedUsername) {
        this.reportedUsername = reportedUsername;
    }
}
