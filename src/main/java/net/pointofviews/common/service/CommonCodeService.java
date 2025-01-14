package net.pointofviews.common.service;

import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.domain.CommonCode;

import java.util.List;

public interface CommonCodeService {
    String convertCommonCodeNameToName(String numberCode, CodeGroupEnum codeGroupEnum);

    String convertNameToCommonCode(String name, CodeGroupEnum codeGroupEnum);

    String convertCommonCodeToName(String numberCode, CodeGroupEnum codeGroupEnum);

    String convertCommonCodeNameToCommonCode(String name, CodeGroupEnum codeGroupEnum);

    String convertCommonCodeDescriptionToCode(String description, CodeGroupEnum codeGroupEnum);

    List<CommonCode> findAllByCodeGroupEnum(CodeGroupEnum codeGroupEnum);
}
