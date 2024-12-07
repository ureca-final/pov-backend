package net.pointofviews.club.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.pointofviews.club.domain.Club;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ClubRepository extends JpaRepository<Club, UUID> {
    Optional<Club> findById(UUID clubId);

    @Query("""
    SELECT c
    FROM Club c
    LEFT JOIN FETCH c.memberClubs mc
    LEFT JOIN FETCH mc.member m
    WHERE c.id = :clubId
""")
    Optional<Club> findByIdWithMemberClubs(@Param("clubId") UUID clubId);
}
