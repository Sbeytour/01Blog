package blog.dto.response;

import blog.entity.ReportReason;
import blog.entity.ReportStatus;
import blog.entity.ReportedEntityType;

import java.time.LocalDateTime;

public class ReportResponseDto {

    private Long id;
    private ReportedEntityType reportedEntityType;
    private Long reportedEntityId;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdAt;

    // Constructors
    public ReportResponseDto() {
    }

    public ReportResponseDto(Long id, ReportedEntityType reportedEntityType, Long reportedEntityId,
                             ReportReason reason, String description, ReportStatus status,
                             LocalDateTime createdAt) {
        this.id = id;
        this.reportedEntityType = reportedEntityType;
        this.reportedEntityId = reportedEntityId;
        this.reason = reason;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
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
}
