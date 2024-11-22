package net.pointofviews.notice.domain;

import net.pointofviews.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class NoticeReceiveTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void NoticeReceive_객체_생성() {
                // given
                String noticeContent = "noticeContent";
                Long noticeSendId = 1L;
                String noticeTitle = "noticeTitle";
                NoticeType noticeType = NoticeType.CLUB;
                Member member = mock(Member.class);

                // when
                NoticeReceive noticeReceive = NoticeReceive.builder()
                        .noticeContent(noticeContent)
                        .noticeSendId(noticeSendId)
                        .noticeTitle(noticeTitle)
                        .noticeType(noticeType)
                        .member(member)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(noticeReceive).isNotNull();
                    softly.assertThat(noticeReceive.getNoticeContent()).isEqualTo(noticeContent);
                    softly.assertThat(noticeReceive.getNoticeSendId()).isEqualTo(noticeSendId);
                    softly.assertThat(noticeReceive.getNoticeTitle()).isEqualTo(noticeTitle);
                    softly.assertThat(noticeReceive.getNoticeType()).isEqualTo(noticeType);
                    softly.assertThat(noticeReceive.getMember()).isEqualTo(member);
                    softly.assertThat(noticeReceive.isRead()).isFalse();
                });
            }
        }
    }
}