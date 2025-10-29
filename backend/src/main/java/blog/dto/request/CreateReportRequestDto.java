package blog.dto.request;

import blog.entity.ReportReason;
import blog.entity.ReportedType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateReportRequestDto {

    @NotNull(message = "Reported  type is required")
    private ReportedType reportedType;

    @NotNull(message = "Reported  ID is required")
    private Long reportedId;

    @NotNull(message = "Reason is required")
    private ReportReason reason;

    @NotBlank(message = "Description cannot be blank")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    // Constructors
    public CreateReportRequestDto() {
    }

    public CreateReportRequestDto(ReportedType reportedType, Long reportedId,
                                  ReportReason reason, String description) {
        this.reportedType = reportedType;
        this.reportedId = reportedId;
        this.reason = reason;
        this.description = description;
    }

    // Getters and Setters
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
}
