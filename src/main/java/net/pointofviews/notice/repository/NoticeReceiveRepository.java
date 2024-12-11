package net.pointofviews.notice.repository;

import net.pointofviews.notice.domain.NoticeReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoticeReceiveRepository extends JpaRepository<NoticeReceive, Long> {
    List<NoticeReceive> findByMemberIdOrderByCreatedAtDesc(UUID memberId);
    Optional<NoticeReceive> findByIdAndMemberId(Long id, UUID memberId);
}
