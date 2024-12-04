package net.pointofviews.common.utils;

import java.util.Locale;

public class ISOCodeToKoreanConverter {
    private ISOCodeToKoreanConverter() {
        throw new UnsupportedOperationException("Utility class");
    }

    public final static String KOREAN_LANGUAGE_CODE = new Locale("ko", "KR").toLanguageTag();

    /**
     * ISO 3166-1 Alpha-2 국가 코드를 한국어 국가 이름으로 변환
     * @param countryCode ISO 3166-1 Alpha-2 코드 (예: "KR", "US")
     * @return 한국어 국가 이름 (예: "대한민국", "미국")
     */
    public static String convertCountryCodeToKorean(String countryCode) {
        Locale locale = new Locale("", countryCode);
        return locale.getDisplayCountry(Locale.KOREAN);
    }

    /**
     * ISO 639-1 언어 코드를 한국어 언어 이름으로 변환
     * @param languageCode ISO 639-1 코드 (예: "ko", "en")
     * @return 한국어 언어 이름 (예: "한국어", "영어")
     */
    public static String convertLanguageCodeToKorean(String languageCode) {
        Locale locale = new Locale(languageCode);
        return locale.getDisplayLanguage(Locale.KOREAN);
    }
}
