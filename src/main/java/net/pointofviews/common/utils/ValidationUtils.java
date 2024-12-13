package net.pointofviews.common.utils;
import java.util.regex.Pattern;

public class ValidationUtils {

    // 유튜브 URL 검사용 정규식 패턴
    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile(
            "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.be)/.+$"
    );

    /**
     * 유효한 유튜브 URL인지 확인
     * @param url 검사할 URL
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public static boolean isValidYouTubeUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        return YOUTUBE_URL_PATTERN.matcher(url).matches();
    }
}