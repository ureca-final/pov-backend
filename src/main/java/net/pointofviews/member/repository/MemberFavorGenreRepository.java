package net.pointofviews.member.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.pointofviews.member.domain.MemberFavorGenre;

public interface MemberFavorGenreRepository extends JpaRepository<MemberFavorGenre, Long> {

	@Query(value = """
		SELECT mfg.genreCode
		  FROM MemberFavorGenre mfg
		 WHERE mfg.member.id = :memberId
	""")
	List<String> findGenreCodeByMemberId(UUID memberId);

	void deleteByMemberIdAndGenreCodeIn(UUID memberId, Collection<String> genreCodes);

	@Query(value = """
		SELECT cc.code.code
		  FROM CommonCode cc
		 WHERE cc.description = :genreName
		   AND cc.groupCode.groupCode = :groupCode
	""")
	String findGenreCodeByGenreName(@Param("genreName") String genreName, @Param("groupCode") String groupCode);

}
