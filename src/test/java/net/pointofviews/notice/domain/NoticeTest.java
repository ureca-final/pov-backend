package net.pointofviews.notice.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class NoticeTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void Notice_객체_생성() {
                // given
                String noticeTitle = "title";
                String noticeContent = "content";
                UUID memberId = UUID.randomUUID();
                String description = "description";
                NoticeType noticeType = NoticeType.CLUB;

                // when
                Notice notice = Notice.builder()
                        .noticeTitle(noticeTitle)
                        .memberId(memberId)
                        .description(description)
                        .noticeType(noticeType)
                        .noticeContent(noticeContent)
                        .build();

                // then
                SoftAssertions.assertSoftly(
                        softly -> {
                            softly.assertThat(notice).isNotNull();
                            softly.assertThat(notice.getNoticeContent()).isEqualTo(noticeContent);
                            softly.assertThat(notice.getNoticeTitle()).isEqualTo(noticeTitle);
                            softly.assertThat(notice.getNoticeType()).isEqualTo(noticeType);
                            softly.assertThat(notice.getMemberId()).isEqualTo(memberId);
                            softly.assertThat(notice.getDescription()).isEqualTo(description);
                            softly.assertThat(notice.isActive()).isTrue();
                            softly.assertThat(notice.getId()).isNull();
                        }
                );
            }
        }
    }
}