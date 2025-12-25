package blog.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import blog.entity.Report;
import blog.entity.ReportStatus;
import blog.entity.ReportedType;
import blog.entity.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Check if a user has already reported a specific entity with a pending status
    @Query("SELECT COUNT(r) > 0 FROM Report r WHERE r.reporter = :reporter "
            + "AND r.reportedType = :reportedType AND r.reportedId = :reportedId "
            + "AND r.status = 'PENDING'")
    boolean existsByReporterAndTypeAndReported(
            @Param("reporter") User reporter,
            @Param("reportedType") ReportedType reportedType,
            @Param("reportedId") Long reportedId);

    // Admin queries for report management
    Page<Report> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(ReportStatus status);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportedType = :reportedType "
            + "AND r.reportedId = :reportedId")
    long countByReportedIdAndReportedType(
            @Param("reportedId") Long reportedId,
            @Param("reportedType") ReportedType reportedType);

    @Query("SELECT r.reportedId, COUNT(r) as reportCount FROM Report r "
            + "WHERE r.reportedType = 'USER' "
            + "GROUP BY r.reportedId "
            + "ORDER BY reportCount DESC")
    List<Object[]> findMostReportedUsers(Pageable pageable);

}
