package blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import blog.dto.response.AdminReportResponseDto;
import blog.dto.response.AdminStatsResponseDto;
import blog.dto.response.PageResponse;
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
    public ResponseEntity<PageResponse<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<UserResponseDto> users = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(users);
    }

    // ----- REPORT -----
    @GetMapping("/reports")
    public ResponseEntity<PageResponse<AdminReportResponseDto>> getReports(
            @RequestParam(required = false) String status, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<AdminReportResponseDto> reports = reportService.getAllReportsPaged(page, size);

        return ResponseEntity.ok(reports);
    }

    // @GetMapping("/reports/paged")
    // public ResponseEntity<PageResponse<AdminReportResponseDto>> getReportsPaged(
    // @RequestParam(defaultValue = "0") int page,
    // @RequestParam(defaultValue = "20") int size) {

    // PageResponse<AdminReportResponseDto> reports =
    // reportService.getAllReportsPaged(page, size);
    // return ResponseEntity.ok(reports);
    // }

    @GetMapping("/reports/{reportId}")
    public ResponseEntity<AdminReportResponseDto> getReportById(@PathVariable Long reportId) {
        AdminReportResponseDto report = reportService.getReportById(reportId);
        return ResponseEntity.ok(report);
    }

    // ---- POST ----
    // @GetMapping("/posts")
    // public ResponseEntity<List<PostResponseDto>> getAllPosts() {
    //     List<PostResponseDto> posts = adminService.getAllPosts();
    //     return ResponseEntity.ok(posts);
    // }

    @GetMapping("/posts")
    public ResponseEntity<PageResponse<PostResponseDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<PostResponseDto> posts = adminService.getAllPosts(page, size);
        return ResponseEntity.ok(posts);
    }
}
