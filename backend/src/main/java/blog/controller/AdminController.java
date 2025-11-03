package blog.controller;

import blog.dto.response.AdminStatsResponseDto;
import blog.dto.response.UserResponseDto;
import blog.services.AdminService;
// import blog.services.ReportService;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // @Autowired
    // private ReportService reportService;

    // ===== DASHBOARD STATS =====

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponseDto> getDashboardStats() {
        AdminStatsResponseDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    // // ===== USER MANAGEMENT =====

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size) {
    Page<UserResponseDto> users = adminService.getAllUsers(page, size);
    return ResponseEntity.ok(users);
    }

    // @PutMapping("/users/{userId}/ban")
    // public ResponseEntity<Void> banUser(
    // @PathVariable Long userId,
    // @Valid @RequestBody BanUserRequestDto requestDto,
    // Authentication authentication) {
    // User admin = (User) authentication.getPrincipal();
    // adminService.banUser(userId, requestDto, admin.getId());
    // return ResponseEntity.ok().build();
    // }

    // @PutMapping("/users/{userId}/unban")
    // public ResponseEntity<Void> unbanUser(@PathVariable Long userId) {
    // adminService.unbanUser(userId);
    // return ResponseEntity.ok().build();
    // }

    // @DeleteMapping("/users/{userId}")
    // public ResponseEntity<Void> deleteUser(
    // @PathVariable Long userId,
    // Authentication authentication) {
    // User admin = (User) authentication.getPrincipal();
    // adminService.deleteUser(userId, admin.getId());
    // return ResponseEntity.noContent().build();
    // }

    // @PutMapping("/users/{userId}/role")
    // public ResponseEntity<Void> updateUserRole(
    // @PathVariable Long userId,
    // @Valid @RequestBody UpdateUserRoleRequestDto requestDto,
    // Authentication authentication) {
    // User admin = (User) authentication.getPrincipal();
    // adminService.updateUserRole(userId, requestDto, admin.getId());
    // return ResponseEntity.ok().build();
    // }

    // // ===== REPORT MANAGEMENT =====

    // @GetMapping("/reports")
    // public ResponseEntity<Page<AdminReportResponseDto>> getReports(
    // @RequestParam(required = false) String status,
    // @RequestParam(defaultValue = "0") int page,
    // @RequestParam(defaultValue = "20") int size) {

    // Page<AdminReportResponseDto> reports;

    // if (status != null && !status.isEmpty()) {
    // ReportStatus reportStatus = ReportStatus.valueOf(status.toUpperCase());
    // reports = reportService.getReportsByStatus(reportStatus, page, size);
    // } else {
    // reports = reportService.getAllReports(page, size);
    // }

    // return ResponseEntity.ok(reports);
    // }

    // @GetMapping("/reports/{reportId}")
    // public ResponseEntity<AdminReportResponseDto> getReportById(@PathVariable
    // Long reportId) {
    // AdminReportResponseDto report = reportService.getReportById(reportId);
    // return ResponseEntity.ok(report);
    // }

    // @PutMapping("/reports/{reportId}/resolve")
    // public ResponseEntity<Void> resolveReport(
    // @PathVariable Long reportId,
    // @Valid @RequestBody ResolveReportRequestDto requestDto,
    // Authentication authentication) {
    // User admin = (User) authentication.getPrincipal();
    // reportService.resolveReport(reportId, requestDto, admin);
    // return ResponseEntity.ok().build();
    // }

    // // ===== POST MANAGEMENT =====

    // @GetMapping("/posts")
    // public ResponseEntity<Page<Post>> getAllPosts(
    // @RequestParam(defaultValue = "0") int page,
    // @RequestParam(defaultValue = "20") int size) {
    // Page<Post> posts = adminService.getAllPosts(page, size);
    // return ResponseEntity.ok(posts);
    // }

    // @DeleteMapping("/posts/{postId}")
    // public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
    // adminService.deletePostByAdmin(postId);
    // return ResponseEntity.noContent().build();
    // }
}
