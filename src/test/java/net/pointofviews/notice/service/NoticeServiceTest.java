package net.pointofviews.notice.service;

import net.pointofviews.notice.domain.FcmResult;
import net.pointofviews.notice.domain.NoticeSend;
import net.pointofviews.notice.utils.FcmUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private FcmUtil fcmUtil;

    @Test
    void testBulkNotificationPerformance() {
        // NoticeSend 객체 mock으로 생성
        NoticeSend mockNoticeSend = Mockito.mock(NoticeSend.class);

        // 대량 알림 성능 테스트 구현
        List<String> tokens = generateMockTokens(1000);

        // FcmUtil의 sendMessage 메서드에 대한 모킹
        Mockito.when(fcmUtil.sendMessage(
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyString(),
                Mockito.any()
        )).thenReturn(
                tokens.stream()
                        .map(token -> FcmResult.builder()
                                .token(token)
                                .isSuccess(true)
                                .noticeSend(mockNoticeSend)
                                .build())
                        .collect(Collectors.toList())
        );

        // 성능 테스트 로직
        long startTime = System.currentTimeMillis();

        // 모든 토큰을 한 번에 처리
        List<FcmResult> results = fcmUtil.sendMessage(
                tokens,
                "성능 테스트 알림",
                "대량 알림 테스트",
                null,
                "테스트 콘텐츠",
                mockNoticeSend
        );

        long endTime = System.currentTimeMillis();

        // 성능 검증
        assertThat(results).hasSize(1000);
        assertThat(endTime - startTime).isLessThan(5000); // 5초 이내 완료
    }

    private List<String> generateMockTokens(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> "mock-token-" + i)
                .collect(Collectors.toList());
    }
}