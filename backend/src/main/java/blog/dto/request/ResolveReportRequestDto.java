package blog.dto.request;

import blog.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResolveReportRequestDto {

    @NotNull(message = "Status is required")
    private ReportStatus status;

    @Size(max = 2000, message = "Admin notes cannot exceed 2000 characters")
    private String adminNotes;

    public ResolveReportRequestDto(ReportStatus status, String adminNotes) {
        this.status = status;
        this.adminNotes = adminNotes;
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
}
