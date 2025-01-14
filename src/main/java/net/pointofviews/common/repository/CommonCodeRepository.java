package net.pointofviews.common.repository;

import net.pointofviews.common.domain.CommonCode;
import net.pointofviews.common.domain.CommonCodeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommonCodeRepository extends JpaRepository<CommonCode, CommonCodeId> {

    @Query("""
            select c from CommonCode c
            left join fetch c.groupCode
            where c.isActive = true
            """)
    List<CommonCode> findAllByIsActiveTrue();
}
