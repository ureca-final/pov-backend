package net.pointofviews.notice.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class NoticeSendTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void NoticeSend_객체_생성() {
                // given
                boolean isSucceed = true;
                Notice notice = mock(Notice.class);
                String noticeContent = "details";

                // when
                NoticeSend noticeSend = NoticeSend.builder()
                        .isSucceed(isSucceed)
                        .notice(notice)
                        .noticeContentDetail(noticeContent)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(noticeSend.isSucceed()).isEqualTo(isSucceed);
                    softly.assertThat(noticeSend.getNotice()).isEqualTo(notice);
                    softly.assertThat(noticeSend.getNoticeContentDetail()).isEqualTo(noticeContent);
                    softly.assertThat(noticeSend).isNotNull();
                    softly.assertThat(noticeSend.getId()).isNull();
                });
            }
        }
    }
}