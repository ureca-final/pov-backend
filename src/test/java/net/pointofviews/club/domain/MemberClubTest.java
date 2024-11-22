package net.pointofviews.club.domain;

import net.pointofviews.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MemberClubTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void MemberClub_객체_생성() {
                // given
                Member member = mock(Member.class);
                Club club = mock(Club.class);
                boolean isLeader = true;

                // when
                MemberClub memberClub = MemberClub.builder()
                        .member(member)
                        .club(club)
                        .isLeader(isLeader)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(memberClub.getMember()).isEqualTo(member);
                    softly.assertThat(memberClub.getClub()).isEqualTo(club);
                    softly.assertThat(memberClub.isLeader()).isEqualTo(isLeader);
                });

            }
        }
    }
}