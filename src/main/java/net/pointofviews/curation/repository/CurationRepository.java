package net.pointofviews.curation.repository;

import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.response.ReadUserCurationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CurationRepository extends JpaRepository<Curation, Long> {

    @Query("SELECT c FROM Curation c " +
            "WHERE (:theme IS NULL OR c.theme LIKE %:theme%) " +
            "AND (:category IS NULL OR c.category = :category)")
    List<Curation> searchCurations(
            @Param("theme") String theme,
            @Param("category") CurationCategory category
    );

    boolean existsById(Long id);

//    @Query("SELECT c FROM Curation c WHERE c.startTime <= CURRENT_TIMESTAMP")
//    Page<Curation> findScheduledCurations(Pageable pageable);
}
