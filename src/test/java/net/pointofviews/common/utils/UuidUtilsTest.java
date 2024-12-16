package net.pointofviews.common.utils;

import net.pointofviews.common.exception.UuidException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UuidUtilsTest {

    @Nested
    class FromString {

        @Nested
        class Success {

            @Test
            void UUID_문자열을_UUID_객체로_변환() {
                // given
                String validUuid = "123e4567-e89b-12d3-a456-426614174000";

                // when
                UUID result = UuidUtils.fromString(validUuid);

                // then
                assertThat(result).isNotNull();
                assertThat(result.toString()).isEqualTo(validUuid);
            }
        }

        @Nested
        class Failure {

            @Test
            void 잘못된_UUID_문자열로_객체_변환_시도() {
                // given
                String invalidUuid = "invalid-uuid-string";

                // when & then
                assertThatThrownBy(() -> UuidUtils.fromString(invalidUuid))
                        .isInstanceOf(UuidException.class)
                        .hasMessageContaining(UuidException.invalidUuid(invalidUuid).getMessage());
            }

            @Test
            void Null_입력하여_UUID_변환_시도() {
                // given
                String nullUuid = null;

                // when & then
                assertThatThrownBy(() -> UuidUtils.fromString(nullUuid))
                        .isInstanceOf(UuidException.class)
                        .hasMessageContaining(UuidException.invalidUuid(nullUuid).getMessage());
            }
        }
    }
}
