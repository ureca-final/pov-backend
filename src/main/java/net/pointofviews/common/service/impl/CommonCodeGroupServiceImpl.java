package net.pointofviews.common.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.domain.CommonCodeGroup;
import net.pointofviews.common.exception.CommonCodeGroupException;
import net.pointofviews.common.repository.CommonCodeGroupRepository;
import net.pointofviews.common.service.CommonCodeGroupService;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonCodeGroupServiceImpl implements CommonCodeGroupService, SmartInitializingSingleton {
    private final CommonCodeGroupRepository commonCodeGroupRepository;

    @Override
    public List<CommonCodeGroup> findAll() {
        List<CommonCodeGroup> groupList = commonCodeGroupRepository.findAll();
        validateCodeGroups(groupList);
        return groupList;
    }

    private void validateCodeGroups(List<CommonCodeGroup> groupList) {
        Set<CommonCodeGroup> codeGroupEnumSet = CodeGroupEnum.getAllCodes();

        if (groupList.size() != codeGroupEnumSet.size()) {
            throw CommonCodeGroupException.commonCodeGroupSizeSyncError();
        }

        for (CommonCodeGroup codeGroup : groupList) {
            if (!codeGroupEnumSet.contains(codeGroup)) {
                throw CommonCodeGroupException.enumAndDbCodeGroupMismatch(codeGroup);
            }
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        List<CommonCodeGroup> groupList = commonCodeGroupRepository.findAll();
        log.info("CommonCodeGroup 캐시 활성화 {}", groupList.toString());
    }
}