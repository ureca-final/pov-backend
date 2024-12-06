package net.pointofviews.common.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.domain.CommonCode;
import net.pointofviews.common.exception.CommonCodeException;
import net.pointofviews.common.service.CommonCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonCodeServiceImpl implements CommonCodeService {

    private final CommonCodeCacheServiceImpl commonCodeCacheServiceImpl;

    @Override
    @Transactional(readOnly = true)
    public String convertCommonCodeNameToName(String numberCode, CodeGroupEnum codeGroupEnum) {
        Map<String, String> genreCodeMap = findAllByCodeGroupEnum(codeGroupEnum).stream()
                .collect(Collectors.toMap(CommonCode::getName, CommonCode::getDescription));

        return Optional.ofNullable(genreCodeMap.get(numberCode))
                .orElseThrow(() -> CommonCodeException.commonCodeNotFound(numberCode));
    }

    @Override
    public String convertNameToCommonCode(String name, CodeGroupEnum codeGroupEnum) {
        Map<String, String> codeMap = findAllByCodeGroupEnum(codeGroupEnum).stream()
                .collect(Collectors.toMap(CommonCode::getDescription, code -> code.getCode().getCode()));

        return Optional.ofNullable(codeMap.get(name))
                .orElseThrow(() -> CommonCodeException.NameNotFound(name));
    }

    @Override
    @Transactional(readOnly = true)
    public String convertCommonCodeToName(String numberCode, CodeGroupEnum codeGroupEnum) {
        Map<String, String> genreCodeMap = findAllByCodeGroupEnum(codeGroupEnum).stream()
                .collect(Collectors.toMap(
                        code -> code.getCode().getCode(),
                        CommonCode::getDescription
                ));

        return Optional.ofNullable(genreCodeMap.get(numberCode))
                .orElseThrow(() -> CommonCodeException.commonCodeNotFound(numberCode));
    }

    @Override
    @Transactional(readOnly = true)
    public String convertCommonCodeNameToCommonCode(String name, CodeGroupEnum codeGroupEnum) {
        Map<String, String> codeMap = findAllByCodeGroupEnum(codeGroupEnum).stream()
                .collect(Collectors.toMap(CommonCode::getName, code -> code.getCode().getCode()
                ));

        return Optional.ofNullable(codeMap.get(name))
                .orElseThrow(() -> CommonCodeException.NameNotFound(name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommonCode> findAllByCodeGroupEnum(CodeGroupEnum codeGroupEnum) {
        List<CommonCode> commonCodeList = commonCodeCacheServiceImpl.findAll();

        return commonCodeList.stream()
                .filter(code -> code.getGroupCode().getGroupCode().equals(codeGroupEnum.getCode()))
                .toList();
    }
}
