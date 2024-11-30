package net.pointofviews.member.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.pointofviews.member.domain.MemberFavorGenre;

public interface MemberFavorGenreRepository extends JpaRepository<MemberFavorGenre, Long> {

	List<MemberFavorGenre> findAllByMemberId(UUID memberId);

	void deleteAllByMemberId(UUID member_id);

	@Query(value = """
		SELECT cc.code.code
		  FROM CommonCode cc
		 WHERE cc.description = :codeName
		   AND cc.groupCode.groupCode = '010'
	""")
	String findGenreCodeByGenreName(@Param("codeName") String codeName);

}
