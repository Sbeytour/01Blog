package blog.dto.request;

import blog.entity.ReportReason;
import blog.entity.ReportedEntityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateReportRequestDto {

    @NotNull(message = "Reported entity type is required")
    private ReportedEntityType reportedEntityType;

    @NotNull(message = "Reported entity ID is required")
    private Long reportedEntityId;

    @NotNull(message = "Reason is required")
    private ReportReason reason;

    @NotBlank(message = "Description cannot be blank")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    // Constructors
    public CreateReportRequestDto() {
    }

    public CreateReportRequestDto(ReportedEntityType reportedEntityType, Long reportedEntityId,
                                  ReportReason reason, String description) {
        this.reportedEntityType = reportedEntityType;
        this.reportedEntityId = reportedEntityId;
        this.reason = reason;
        this.description = description;
    }

    // Getters and Setters
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
}
