package blog.controller;

import blog.dto.request.CreateReportRequestDto;
import blog.dto.response.ReportResponseDto;
import blog.entity.User;
import blog.services.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // create new report
    @PostMapping
    public ResponseEntity<ReportResponseDto> createReport(
            @Valid @RequestBody CreateReportRequestDto requestDto,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        ReportResponseDto response = reportService.createReport(requestDto, currentUser);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
