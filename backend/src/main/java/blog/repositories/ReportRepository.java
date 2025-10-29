package blog.repositories;

import blog.entity.Report;
import blog.entity.ReportedType;
import blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

        // Check if a user has already reported a specific entity with PENDING status
        @Query("SELECT COUNT(r) > 0 FROM Report r WHERE r.reporter = :reporter " +
                        "AND r.reportedType = :reportedType AND r.reportedId = :reportedId " +
                        "AND r.status IN ('PENDING')")
        boolean existsByReporterAndEntityAndActiveStatus(
                        @Param("reporter") User reporter,
                        @Param("reportedType") ReportedType reportedType,
                        @Param("reportedId") Long reportedId);

}
