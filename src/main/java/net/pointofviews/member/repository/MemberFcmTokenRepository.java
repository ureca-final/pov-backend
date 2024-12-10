package net.pointofviews.member.repository;

import net.pointofviews.member.domain.Member;
import net.pointofviews.member.domain.MemberFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberFcmTokenRepository extends JpaRepository<MemberFcmToken, Long> {
    Optional<MemberFcmToken> findByMemberAndIsActiveTrue(Member member);

    @Query("SELECT mt FROM MemberFcmToken mt WHERE mt.member IN :members AND mt.isActive = true")
    List<MemberFcmToken> findActiveFcmTokensByMembers(List<Member> members);

    @Query("SELECT mt FROM MemberFcmToken mt WHERE mt.member.id IN :memberIds AND mt.isActive = true")
    List<MemberFcmToken> findActiveTokensByMemberIds(@Param("memberIds") List<UUID> memberIds);
}