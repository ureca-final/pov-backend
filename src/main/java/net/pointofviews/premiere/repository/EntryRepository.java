package net.pointofviews.premiere.repository;

import net.pointofviews.premiere.domain.Entry;
import net.pointofviews.premiere.dto.response.ReadEntryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    Long countEntriesByPremiereId(Long premiereId);

    boolean existsEntryByMemberIdAndPremiereId(UUID memberId, Long premiereId);

    Optional<Entry> findEntryByOrderId(String orderId);

    void deleteByOrderId(String orderId);

    @Query(value = """
                    SELECT new net.pointofviews.premiere.dto.response.ReadEntryResponse(
                        p.id,
                        p.title,
                        py.approvedAt,
                        e.amount
                    )
                    FROM Entry e
                    lEFT JOIN e.member m
                    lEFT JOIN e.premiere p
                    LEFT JOIN Payment py ON py.orderId = e.orderId
                    WHERE m.id = :memberId AND py.approvedAt IS NOT NULL
                    ORDER BY py.approvedAt DESC
            """)
    List<ReadEntryResponse> findAllByMemberId(UUID memberId);
}
