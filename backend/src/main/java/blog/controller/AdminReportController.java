package blog.controller;

import blog.dto.request.ResolveReportRequestDto;
import blog.dto.response.AdminReportResponseDto;
import blog.entity.ReportStatus;
import blog.entity.ReportedEntityType;
import blog.entity.User;
import blog.services.AdminReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    @Autowired
    private AdminReportService adminReportService;

    /**
     * Get all reports with optional filters
     * GET /api/admin/reports?status=PENDING&entityType=POST&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<AdminReportResponseDto>> getAllReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ReportedEntityType entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AdminReportResponseDto> reports = adminReportService.getAllReports(status, entityType, page, size);
        return ResponseEntity.ok(reports);
    }

    /**
     * Get a single report by ID
     * GET /api/admin/reports/{reportId}
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<AdminReportResponseDto> getReportById(@PathVariable Long reportId) {
        AdminReportResponseDto report = adminReportService.getReportById(reportId);
        return ResponseEntity.ok(report);
    }

    /**
     * Resolve a report (update status and add admin notes)
     * PUT /api/admin/reports/{reportId}/resolve
     */
    @PutMapping("/{reportId}/resolve")
    public ResponseEntity<AdminReportResponseDto> resolveReport(
            @PathVariable Long reportId,
            @Valid @RequestBody ResolveReportRequestDto requestDto,
            Authentication authentication) {

        User admin = (User) authentication.getPrincipal();
        AdminReportResponseDto report = adminReportService.resolveReport(reportId, requestDto, admin);

        return ResponseEntity.ok(report);
    }

    /**
     * Get report statistics for dashboard
     * GET /api/admin/reports/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getReportStatistics() {
        Map<String, Object> statistics = adminReportService.getReportStatistics();
        return ResponseEntity.ok(statistics);
    }
}
