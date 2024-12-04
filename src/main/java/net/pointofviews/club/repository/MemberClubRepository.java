package net.pointofviews.club.repository;

import net.pointofviews.club.domain.MemberClub;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MemberClubRepository extends JpaRepository<MemberClub, Long> {

    @Query(value = """
            SELECT mcr
              FROM MemberClub mcr
              JOIN FETCH mcr.member m
              JOIN FETCH mcr.club c
             WHERE m.id = :memberId
            """)
    List<MemberClub> findClubsByMemberId(@Param("memberId") UUID memberId);

    @Query(value = """
            SELECT new net.pointofviews.review.dto.response.ReadReviewResponse(
                    r.id,
                    mv.title,
                    r.title,
                    r.contents,
                    m.nickname,
                    m.profileImage,
                    mv.poster,
                    r.createdAt,
                    (SELECT rlc.reviewLikeCount FROM ReviewLikeCount rlc WHERE rlc.review.id = r.id),
                    CASE WHEN EXISTS (SELECT 1 FROM ReviewLike rl WHERE rl.review.id = r.id AND rl.isLiked = true) THEN true ELSE false END,
                    r.isSpoiler
             )
             FROM MemberClub mc
             JOIN mc.member m
             JOIN mc.club c
             JOIN Review r ON r.member.id = m.id
             JOIN r.movie mv
            WHERE c.id = :clubId
            ORDER BY r.createdAt DESC
            """)
    Slice<ReadReviewResponse> findReviewsWithLikesByClubId(@Param("clubId") UUID clubId, Pageable pageable);

}
