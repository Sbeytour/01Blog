package blog.dto.request;

import blog.entity.AdminAction;
import blog.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResolveReportRequestDto {

    @NotNull(message = "Status is required")
    private ReportStatus status;

    @Size(max = 2000, message = "Admin notes cannot exceed 2000 characters")
    private String adminNotes;

    @NotNull(message = "Action is required")
    private AdminAction action;

    private Integer banDurationDays;

    private Boolean banPermanent;

    public ResolveReportRequestDto() {
    }

    public ResolveReportRequestDto(ReportStatus status, String adminNotes, AdminAction action) {
        this.status = status;
        this.adminNotes = adminNotes;
        this.action = action;
    }

    public ResolveReportRequestDto(ReportStatus status, String adminNotes, AdminAction action, Integer banDurationDays, Boolean banPermanent) {
        this.status = status;
        this.adminNotes = adminNotes;
        this.action = action;
        this.banDurationDays = banDurationDays;
        this.banPermanent = banPermanent;
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

    public AdminAction getAction() {
        return action;
    }

    public void setAction(AdminAction action) {
        this.action = action;
    }

    public Integer getBanDurationDays() {
        return banDurationDays;
    }

    public void setBanDurationDays(Integer banDurationDays) {
        this.banDurationDays = banDurationDays;
    }

    public Boolean getBanPermanent() {
        return banPermanent;
    }

    public void setBanPermanent(Boolean banPermanent) {
        this.banPermanent = banPermanent;
    }
}
