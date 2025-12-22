package blog.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import blog.dto.response.AdminReportResponseDto;
import blog.dto.response.AdminStatsResponseDto;
import blog.dto.response.PostResponseDto;
import blog.dto.response.UserResponseDto;
import blog.services.AdminService;
import blog.services.ReportService;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final AdminService adminService;
    private final ReportService reportService;

    public AdminDashboardController(AdminService adminService, ReportService reportService) {
        this.adminService = adminService;
        this.reportService = reportService;
    }

    // ------ Dashboard ----
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponseDto> getDashboardStats() {
        AdminStatsResponseDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    // ----- User ----
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserResponseDto> users = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(users);
    }

    // ----- REPORT -----
    @GetMapping("/reports")
    public ResponseEntity<List<AdminReportResponseDto>> getReports(
            @RequestParam(required = false) String status) {

        List<AdminReportResponseDto> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/reports/{reportId}")
    public ResponseEntity<AdminReportResponseDto> getReportById(@PathVariable Long reportId) {
        AdminReportResponseDto report = reportService.getReportById(reportId);
        return ResponseEntity.ok(report);
    }

    // ---- POST ----
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        List<PostResponseDto> posts = adminService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
}
