package blog.controller;

import blog.dto.request.CreateReportRequestDto;
import blog.dto.response.ReportResponseDto;
import blog.entity.User;
import blog.services.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * Create a new report
     * POST /api/reports
     */
    @PostMapping
    public ResponseEntity<ReportResponseDto> createReport(
            @Valid @RequestBody CreateReportRequestDto requestDto,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        ReportResponseDto response = reportService.createReport(requestDto, currentUser);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get current user's submitted reports
     * GET /api/reports/my-reports?page=0&size=10
     */
    @GetMapping("/my-reports")
    public ResponseEntity<Page<ReportResponseDto>> getMyReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        Page<ReportResponseDto> reports = reportService.getUserReports(currentUser, page, size);

        return ResponseEntity.ok(reports);
    }
}
