package blog.repositories;

import blog.entity.Report;
import blog.entity.ReportStatus;
import blog.entity.ReportedEntityType;
import blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * Find all reports by status with pagination
     */
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    /**
     * Find all reports by a specific reporter with pagination
     */
    Page<Report> findByReporter(User reporter, Pageable pageable);

    /**
     * Find reports by entity type and entity ID
     */
    @Query("SELECT r FROM Report r WHERE r.reportedEntityType = :entityType AND r.reportedEntityId = :entityId ORDER BY r.createdAt DESC")
    List<Report> findByReportedEntityTypeAndReportedEntityId(
            @Param("entityType") ReportedEntityType entityType,
            @Param("entityId") Long entityId
    );

    /**
     * Check if a user has already reported a specific entity with PENDING or UNDER_REVIEW status
     */
    @Query("SELECT COUNT(r) > 0 FROM Report r WHERE r.reporter = :reporter " +
           "AND r.reportedEntityType = :entityType AND r.reportedEntityId = :entityId " +
           "AND r.status IN ('PENDING', 'UNDER_REVIEW')")
    boolean existsByReporterAndEntityAndActiveStatus(
            @Param("reporter") User reporter,
            @Param("entityType") ReportedEntityType entityType,
            @Param("entityId") Long entityId
    );

    /**
     * Count reports by status
     */
    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = :status")
    Long countByStatus(@Param("status") ReportStatus status);

    /**
     * Count all reports by entity type
     */
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportedEntityType = :entityType")
    Long countByReportedEntityType(@Param("entityType") ReportedEntityType entityType);

    /**
     * Get all reports with pagination (for admin)
     */
    @Query("SELECT r FROM Report r ORDER BY r.createdAt DESC")
    Page<Report> findAllReports(Pageable pageable);

    /**
     * Find reports by entity type with pagination
     */
    Page<Report> findByReportedEntityType(ReportedEntityType entityType, Pageable pageable);

    /**
     * Find reports by multiple statuses with pagination
     */
    @Query("SELECT r FROM Report r WHERE r.status IN :statuses ORDER BY r.createdAt DESC")
    Page<Report> findByStatusIn(@Param("statuses") List<ReportStatus> statuses, Pageable pageable);

    /**
     * Find a report by reporter and entity (including resolved ones)
     */
    Optional<Report> findByReporterAndReportedEntityTypeAndReportedEntityId(
            User reporter,
            ReportedEntityType entityType,
            Long entityId
    );
}
