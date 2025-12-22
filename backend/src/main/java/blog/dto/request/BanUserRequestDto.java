package blog.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BanUserRequestDto {

    @NotBlank(message = "Reason for ban is required")
    @Size(min = 10, max = 200, message = "Reason must be between 10 and 200 characters")
    private String reason;

    @Min(value = 0, message = "Duration must be 0 or greater (0 means permanent)")
    private Integer durationDays;

    private Boolean permanent;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Boolean getPermanent() {
        return permanent;
    }

    public void setPermanent(Boolean permanent) {
        this.permanent = permanent;
    }
}
