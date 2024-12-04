package net.pointofviews.club.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.pointofviews.club.domain.MemberClub;

public interface MemberClubRepository extends JpaRepository<MemberClub, Long> {

	@Query(value = """
		SELECT mcr
		  FROM MemberClub mcr
		  JOIN FETCH mcr.member m
		  JOIN FETCH mcr.club c
		 WHERE m.id = :memberId
	""")
	List<MemberClub> findClubsByMemberId(@Param("memberId") UUID memberId);
}
