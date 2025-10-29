package blog.dto.response;

import blog.entity.Report;
import blog.entity.ReportReason;
import blog.entity.ReportedType;

import java.time.LocalDateTime;

public class ReportResponseDto {

    private Long id;
    private ReportedType reportedType;
    private Long reportedId;
    private ReportReason reason;
    private String description;
    private LocalDateTime createdAt;

    // Constructor
    public ReportResponseDto() {
    }

    // method to convert entity to dto
    public static ReportResponseDto fromEntity(Report report) {
        ReportResponseDto reportResp = new ReportResponseDto();
        reportResp.id = report.getReportedId();
        reportResp.reportedType = report.getReportedType();
        reportResp.reportedId = report.getReportedId();
        reportResp.reason = report.getReason();
        reportResp.description = report.getDescription();
        reportResp.createdAt = report.getCreatedAt();
        return reportResp;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
