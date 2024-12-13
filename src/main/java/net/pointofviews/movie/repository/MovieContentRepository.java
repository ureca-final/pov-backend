package net.pointofviews.movie.repository;

import net.pointofviews.movie.domain.MovieContent;
import net.pointofviews.movie.domain.MovieContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieContentRepository extends JpaRepository<MovieContent, Long> {

    @Query("SELECT mc FROM MovieContent mc WHERE mc.id IN :ids AND mc.contentType = :type")
    List<MovieContent> findAllByIdAndType(@Param("ids") List<Long> ids, @Param("type") MovieContentType type);

    @Modifying
    @Query("DELETE FROM MovieContent mc WHERE mc.id IN :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);

    List<MovieContent> findAllByMovieId(Long movieId);
}
