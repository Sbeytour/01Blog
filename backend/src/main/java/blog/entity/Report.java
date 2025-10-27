package blog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Reported entity type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "reported_entity_type", nullable = false)
    private ReportedEntityType reportedEntityType;

    @NotNull(message = "Reported entity ID is required")
    @Column(name = "reported_entity_id", nullable = false)
    private Long reportedEntityId;

    @NotNull(message = "Reporter is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @NotNull(message = "Reason is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    @NotBlank(message = "Description cannot be blank")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id")
    private User resolvedBy;

    @Size(max = 2000, message = "Admin notes cannot exceed 2000 characters")
    @Column(name = "admin_notes", length = 2000)
    private String adminNotes;

    // Constructors
    public Report() {
    }

    public Report(ReportedEntityType reportedEntityType, Long reportedEntityId, User reporter,
                  ReportReason reason, String description) {
        this.reportedEntityType = reportedEntityType;
        this.reportedEntityId = reportedEntityId;
        this.reporter = reporter;
        this.reason = reason;
        this.description = description;
        this.status = ReportStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportedEntityType getReportedEntityType() {
        return reportedEntityType;
    }

    public void setReportedEntityType(ReportedEntityType reportedEntityType) {
        this.reportedEntityType = reportedEntityType;
    }

    public Long getReportedEntityId() {
        return reportedEntityId;
    }

    public void setReportedEntityId(Long reportedEntityId) {
        this.reportedEntityId = reportedEntityId;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public User getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(User resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
}
