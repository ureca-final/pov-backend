package net.pointofviews.common.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum CodeGroupEnum {
    MOVIE_GENRE("010", "영화 장르", "장르", true),
    TV_GENRE("011", "TV 장르", "장르", true),
    REVIEW_KEYWORD("020", "리뷰 키워드", "키워드", true);

    private final String code;
    private final String description;
    private final String name;
    private final boolean isActive;

    CodeGroupEnum(String code, String description, String name, boolean isActive) {
        this.code = code;
        this.description = description;
        this.name = name;
        this.isActive = isActive;
    }

    public static Set<CommonCodeGroup> getAllCodes() {
        return Arrays.stream(CodeGroupEnum.values())
                .map(codeGroupEnum -> CommonCodeGroup.builder()
                        .codeGroupEnum(codeGroupEnum)
                        .build())
                .collect(Collectors.toSet());
    }
}