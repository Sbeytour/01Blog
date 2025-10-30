package blog.dto.request;

import blog.entity.Role;
import jakarta.validation.constraints.NotNull;

public class UpdateUserRoleRequestDto {

    @NotNull(message = "Role is required")
    private Role role;
    
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
