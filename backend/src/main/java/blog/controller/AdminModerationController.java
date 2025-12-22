package blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import blog.dto.request.BanUserRequestDto;
import blog.dto.request.ResolveReportRequestDto;
import blog.dto.request.UpdateUserRoleRequestDto;
import blog.entity.User;
import blog.services.ModerationService;
import blog.services.ReportService;
import jakarta.validation.Valid;

public class AdminModerationController {

    private final ModerationService moderationService;
    private final ReportService reportService;

    public AdminModerationController(ModerationService moderationService, ReportService reportService) {
        this.moderationService = moderationService;
        this.reportService = reportService;
    }

    @PutMapping("/users/{userId}/ban")
    public ResponseEntity<Void> banUser(
            @PathVariable Long userId,
            @Valid @RequestBody BanUserRequestDto requestDto) {
        moderationService.banUser(userId, requestDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable Long userId) {
        moderationService.unbanUser(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId,
            Authentication authentication) {
        moderationService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequestDto requestDto,
            Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        moderationService.updateUserRole(userId, requestDto, admin.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reports/{reportId}/resolve")
    public ResponseEntity<Void> resolveReport(
            @PathVariable Long reportId,
            @Valid @RequestBody ResolveReportRequestDto requestDto,
            Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        reportService.resolveReport(reportId, requestDto, admin);
        return ResponseEntity.ok().build();
    }

}
