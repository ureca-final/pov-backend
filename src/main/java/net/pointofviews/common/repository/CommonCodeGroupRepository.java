package net.pointofviews.common.repository;

import net.pointofviews.common.domain.CommonCodeGroup;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommonCodeGroupRepository extends JpaRepository<CommonCodeGroup, String> {

    @Cacheable(cacheNames = "commonCodeGroup", cacheManager = "commonCodeCacheManager")
    List<CommonCodeGroup> findAll();
}
