package net.pointofviews.member.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MemberFavorGenreTest {

    @Nested
    class Constructor{

        @Nested
        class Success{

            @Test
            void MemberFavorGenre_객체_생성(){
                // given
                Member member = mock(Member.class);

                // when
                MemberFavorGenre memberFavorGenre = MemberFavorGenre.builder()
                        .genreCode("00")
                        .member(member)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(memberFavorGenre.getGenreCode()).isEqualTo("00");
                    softly.assertThat(memberFavorGenre.getMember()).isEqualTo(member);
                    softly.assertThat(memberFavorGenre.getId()).isNull();
                });
            }
        }
    }
}