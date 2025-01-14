package net.pointofviews.common.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.domain.CommonCode;
import net.pointofviews.common.domain.CommonCodeGroup;
import net.pointofviews.common.exception.CommonCodeException;
import net.pointofviews.common.repository.CommonCodeRepository;
import net.pointofviews.common.service.CommonCodeCacheService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommonCodeCacheServiceImpl implements CommonCodeCacheService {

    private final CommonCodeRepository commonCodeRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "commonCode", cacheManager = "cacheManagerWithTTL")
    public List<CommonCode> findAll() {
        List<CommonCode> commonCodeList = commonCodeRepository.findAllByIsActiveTrue();

        validateCommonCode(commonCodeList);

        return commonCodeList;
    }

    private void validateCommonCode(List<CommonCode> commonCodeList) {
        Set<CommonCodeGroup> codeGroupSet = CodeGroupEnum.getAllCodes();

        if (commonCodeList.isEmpty()) {
            throw CommonCodeException.commonCodeEmptyError();
        }

        commonCodeList.stream()
                .filter(commonCode -> !codeGroupSet.contains(commonCode.getGroupCode()))
                .findFirst()
                .ifPresent(invalidCode -> {
                    throw CommonCodeException.commonCodeIdError(invalidCode.getCode());
                });
    }
}
