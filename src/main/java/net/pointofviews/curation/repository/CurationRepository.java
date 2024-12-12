package net.pointofviews.curation.repository;

import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.response.ReadAdminAllCurationResponse;
import net.pointofviews.curation.dto.response.ReadAdminCurationMovieResponse;
import net.pointofviews.curation.dto.response.ReadAdminCurationResponse;
import net.pointofviews.curation.dto.response.ReadUserCurationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CurationRepository extends JpaRepository<Curation, Long> {

    @Query("SELECT c FROM Curation c " +
            "WHERE (:theme IS NULL OR c.theme LIKE %:theme%) " +
            "AND (:category IS NULL OR c.category = :category)")
    List<Curation> searchCurations(
            @Param("theme") String theme,
            @Param("category") CurationCategory category
    );


    @Query("""
       SELECT new net.pointofviews.curation.dto.response.ReadAdminAllCurationResponse(
           c.id,
           c.title,
           c.startTime
       )
       FROM Curation c
       """)
    List<ReadAdminAllCurationResponse> findAllCurations();


    @Query("""
       SELECT new net.pointofviews.curation.dto.response.ReadAdminCurationResponse(
           c.id,
           c.theme,
           c.category,
           c.title,
           c.description,
           c.startTime
       )
       FROM Curation c
       WHERE c.id = :curationId
       """)
    Optional<ReadAdminCurationResponse> findCurationDetailById(@Param("curationId") Long curationId);

    @Query("""
       SELECT new net.pointofviews.curation.dto.response.ReadAdminCurationMovieResponse(
           m.title,
           m.released
       )
       FROM Movie m
       WHERE m.id IN :movieIds
       """)
    List<ReadAdminCurationMovieResponse> findMoviesByIds(@Param("movieIds") Set<Long> movieIds);

    boolean existsById(Long id);

//    @Query("SELECT c FROM Curation c WHERE c.startTime <= CURRENT_TIMESTAMP")
//    Page<Curation> findScheduledCurations(Pageable pageable);
}
