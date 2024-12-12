package net.pointofviews.notice.repository;

import net.pointofviews.notice.domain.NoticeSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeSendRepository extends JpaRepository<NoticeSend, Long> {
    List<NoticeSend> findByNoticeId(Long noticeId);
}