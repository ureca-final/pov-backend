package net.pointofviews.club.utils;

import net.pointofviews.club.exception.InviteCodeException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class InviteCodeGeneratorTest {

    @Nested
    class GenerateInviteCode {

        @Nested
        class Success {

            @Test
            void 랜덤_초대_코드_유효한_문자만_포함() {
                // given
                int length = 8;
                String validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

                // when
                String inviteCode = InviteCodeGenerator.generateInviteCode(length);

                // then
                assertThat(inviteCode).isNotNull();
                for (char c : inviteCode.toCharArray()) {
                    assertThat(validCharacters).contains(String.valueOf(c));
                }
            }
        }

        @Nested
        class Fail {

            @Test
            void 코드_길이가_음수일_때_예외_발생() {
                // given
                int invalidLength = -1;

                // when
                Throwable thrown = catchThrowable(() ->
                        InviteCodeGenerator.generateInviteCode(invalidLength)
                );

                // then
                assertThat(thrown).isInstanceOf(InviteCodeException.class)
                        .hasMessage(InviteCodeException.invalidLength(invalidLength).getMessage());
            }

            @Test
            void 코드_길이가_0일_때_예외_발생() {
                // given
                int invalidLength = 0;

                // when
                Throwable thrown = catchThrowable(() ->
                        InviteCodeGenerator.generateInviteCode(invalidLength)
                );

                // then
                assertThat(thrown).isInstanceOf(InviteCodeException.class)
                        .hasMessage(InviteCodeException.invalidLength(invalidLength).getMessage());
            }
        }
    }
}
