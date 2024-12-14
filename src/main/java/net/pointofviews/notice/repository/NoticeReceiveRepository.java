package net.pointofviews.notice.repository;

import net.pointofviews.notice.domain.NoticeReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoticeReceiveRepository extends JpaRepository<NoticeReceive, Long> {
    List<NoticeReceive> findByMemberIdOrderByCreatedAtDesc(UUID memberId);
    Optional<NoticeReceive> findByIdAndMemberId(Long id, UUID memberId);

    @Query("SELECT DISTINCT nr FROM NoticeReceive nr " +
            "LEFT JOIN Review r ON r.id = nr.reviewId " +
            "LEFT JOIN Movie m ON m.id = r.movie.id " +
            "WHERE nr.member.id = :memberId " +
            "ORDER BY nr.createdAt DESC")
    List<NoticeReceive> findByMemberIdWithReviewAndMovieOrderByCreatedAtDesc(@Param("memberId") UUID memberId);
}
