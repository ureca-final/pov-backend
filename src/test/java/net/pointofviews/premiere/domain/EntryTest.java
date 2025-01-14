package net.pointofviews.premiere.domain;

import net.pointofviews.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class EntryTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void Entry_객체_생성() {
                // given
                Integer amount = 1;
                Integer quantity = 1;
                Member member = mock(Member.class);
                Premiere premiere = mock(Premiere.class);

                // when
                Entry entry = Entry.builder()
                        .amount(amount)
                        .quantity(quantity)
                        .member(member)
                        .premiere(premiere)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(entry).isNotNull();
                    softly.assertThat(entry.getAmount()).isEqualTo(amount);
                    softly.assertThat(entry.getQuantity()).isEqualTo(quantity);
                    softly.assertThat(entry.getMember()).isEqualTo(member);
                    softly.assertThat(entry.getPremiere()).isEqualTo(premiere);
                });
            }
        }

        @Nested
        class Failure {

            @Test
            void 수량_없음_IllegalArgumentException_예외발생() {
                // given
                Member member = mock(Member.class);
                Premiere premiere = mock(Premiere.class);

                // when & then
                assertThatThrownBy(() -> Entry.builder()
                        .amount(1)
                        .member(member)
                        .premiere(premiere)
                        .build())
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }
}