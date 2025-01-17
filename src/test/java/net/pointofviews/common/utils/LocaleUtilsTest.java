package net.pointofviews.common.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LocaleUtilsTest {

    @Nested
    class getLocale {

        @Nested
        class Success {

            @Test
            void getLocale() {
                // given
                String expectedLocale = "ko-KR";

                // when

                // then
                Assertions.assertThat(LocaleUtils.KOREAN_LANGUAGE_CODE).isEqualTo(expectedLocale);
            }
        }
    }
}
