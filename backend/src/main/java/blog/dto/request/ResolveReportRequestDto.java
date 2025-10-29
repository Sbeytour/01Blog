package blog.dto.request;

import blog.entity.ReportAction;
import blog.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResolveReportRequestDto {

    @NotNull(message = "Status is required")
    private ReportStatus status;

    @Size(max = 2000, message = "Admin notes cannot exceed 2000 characters")
    private String adminNotes;

    @NotNull(message = "Action is required")
    private ReportAction action;

    public ResolveReportRequestDto() {
    }

    public ResolveReportRequestDto(ReportStatus status, String adminNotes, ReportAction action) {
        this.status = status;
        this.adminNotes = adminNotes;
        this.action = action;
    }

    // Getters and Setters
    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public ReportAction getAction() {
        return action;
    }

    public void setAction(ReportAction action) {
        this.action = action;
    }
}
