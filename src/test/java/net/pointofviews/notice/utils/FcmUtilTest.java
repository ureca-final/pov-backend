package net.pointofviews.notice.utils;

import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import net.pointofviews.notice.domain.FcmErrorCode;
import net.pointofviews.notice.domain.FcmResult;
import net.pointofviews.notice.domain.NoticeSend;
import net.pointofviews.notice.repository.FcmResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FcmUtilTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private FcmResultRepository fcmResultRepository;

    @InjectMocks
    private FcmUtil fcmUtil;

    @Test
    void testRetryMechanism() {
        // 준비
        String mockToken = "test-retry-token";
        NoticeSend mockNoticeSend = mock(NoticeSend.class);
        FcmResult mockResult = FcmResult.builder()
                .token(mockToken)
                .isSuccess(true)
                .noticeSend(mockNoticeSend)
                .build();

        when(fcmResultRepository.save(any(FcmResult.class))).thenReturn(mockResult);

        long startTime = System.currentTimeMillis();

        // 실행
        List<FcmResult> results = fcmUtil.sendMessage(
                List.of(mockToken),
                "재시도 테스트",
                "재시도 메커니즘 검증",
                null,
                "테스트",
                mockNoticeSend
        );

        long endTime = System.currentTimeMillis();

        // 검증
        assertThat(endTime - startTime).isLessThan(3000);
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getToken()).isEqualTo(mockToken);
    }

    @Test
    void testMessageSendingCounts() throws FirebaseMessagingException {
        // Given
        List<String> tokens = Arrays.asList(
                "token1", "token2", "token3", "token4", "token5"
        );
        NoticeSend mockNoticeSend = mock(NoticeSend.class);

        // 성공하는 케이스의 FcmResult 모킹
        FcmResult successResult = FcmResult.builder()
                .token("token1")
                .isSuccess(true)
                .noticeSend(mockNoticeSend)
                .build();

        // 실패하는 케이스의 FcmResult 모킹
        FcmResult failureResult = FcmResult.builder()
                .token("token4")
                .isSuccess(false)
                .errorCode(FcmErrorCode.INVALID_REGISTRATION)
                .noticeSend(mockNoticeSend)
                .build();

        // 재시도 가능한 에러와 불가능한 에러 각각 모킹
        FirebaseMessagingException retryableException = mock(FirebaseMessagingException.class);
        when(retryableException.getErrorCode()).thenReturn(ErrorCode.UNAVAILABLE);
        when(retryableException.getMessage()).thenReturn("Service unavailable");

        FirebaseMessagingException nonRetryableException = mock(FirebaseMessagingException.class);
        when(nonRetryableException.getErrorCode()).thenReturn(ErrorCode.INVALID_ARGUMENT);
        when(nonRetryableException.getMessage()).thenReturn("Invalid token");

        // FirebaseMessaging 동작 모킹
        when(firebaseMessaging.send(any(Message.class)))
                .thenReturn("success")  // token1 성공
                .thenReturn("success")  // token2 성공
                .thenReturn("success")  // token3 성공
                .thenThrow(retryableException)  // token4 첫 번째 시도 실패
                .thenThrow(retryableException)  // token4 두 번째 시도 실패
                .thenThrow(retryableException)  // token4 세 번째 시도 실패
                .thenThrow(nonRetryableException)  // token5 실패
                .thenThrow(nonRetryableException)  // token5 재시도 1
                .thenThrow(nonRetryableException); // token5 재시도 2

        // FcmResultRepository 동작 모킹
        when(fcmResultRepository.save(any(FcmResult.class))).thenAnswer(invocation -> {
            FcmResult result = invocation.getArgument(0);
            return result.isSuccess() ? successResult : failureResult;
        });

        // When
        List<FcmResult> results = fcmUtil.sendMessage(
                tokens,
                "테스트 제목",
                "테스트 내용",
                1L,
                "알림 내용",
                mockNoticeSend
        );

        // Then
        assertThat(results.size()).isEqualTo(tokens.size());

        long successCount = results.stream()
                .filter(FcmResult::isSuccess)
                .count();
        assertThat(successCount).isEqualTo(3);

        // 성공 케이스 3개 + 재시도 케이스 1개 (3번 시도) + 즉시 실패 케이스 3개 = 총 9번의 호출
        verify(firebaseMessaging, times(9)).send(any(Message.class));
    }

    @Test
    void testBatchProcessing() throws FirebaseMessagingException {
        // Given
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            tokens.add("token-" + i);
        }
        NoticeSend mockNoticeSend = mock(NoticeSend.class);

        when(firebaseMessaging.send(any(Message.class)))
                .thenReturn("success");

        // When
        long startTime = System.currentTimeMillis();
        List<FcmResult> results = fcmUtil.sendMessage(
                tokens,
                "대량 발송 테스트",
                "테스트 내용",
                1L,
                "알림 내용",
                mockNoticeSend
        );
        long endTime = System.currentTimeMillis();

        // Then
        assertThat(results.size()).isEqualTo(tokens.size());
        assertThat(endTime - startTime).isLessThan(10000); // 10초 이내 처리

        verify(firebaseMessaging, times(1000)).send(any(Message.class));
        verify(fcmResultRepository, times(1000)).save(any(FcmResult.class));
    }

    @Test
    void testRetryBehavior() throws FirebaseMessagingException {
        // Given
        String token = "retry-test-token";
        NoticeSend mockNoticeSend = mock(NoticeSend.class);
        FcmResult mockResult = FcmResult.builder()
                .token(token)
                .isSuccess(true)
                .noticeSend(mockNoticeSend)
                .build();

        // Firebase 예외 모킹
        FirebaseMessagingException mockException1 = mock(FirebaseMessagingException.class);
        when(mockException1.getErrorCode()).thenReturn(ErrorCode.valueOf("UNAVAILABLE"));

        FirebaseMessagingException mockException2 = mock(FirebaseMessagingException.class);
        when(mockException2.getErrorCode()).thenReturn(ErrorCode.valueOf("UNAVAILABLE"));

        // Firebase 메시징 동작 모킹
        when(firebaseMessaging.send(any(Message.class)))
                .thenThrow(mockException1)
                .thenThrow(mockException2)
                .thenReturn("success"); // 세번째 성공

        when(fcmResultRepository.save(any(FcmResult.class)))
                .thenReturn(mockResult);

        // When
        List<FcmResult> results = fcmUtil.sendMessage(
                List.of(token),
                "재시도 테스트",
                "테스트 내용",
                1L,
                "알림 내용",
                mockNoticeSend
        );

        // Then
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).isSuccess()).isTrue();

        verify(firebaseMessaging, times(3)).send(any(Message.class));
        verify(fcmResultRepository, times(1)).save(any(FcmResult.class));
    }

    @Test
    void testEmptyTokenList() throws FirebaseMessagingException{
        // Given
        List<String> tokens = new ArrayList<>();
        NoticeSend mockNoticeSend = mock(NoticeSend.class);

        // When
        List<FcmResult> results = fcmUtil.sendMessage(
                tokens,
                "테스트",
                "테스트",
                1L,
                "테스트",
                mockNoticeSend
        );

        // Then
        assertThat(results).isNotNull();
        assertThat(results).isEmpty();
        verify(firebaseMessaging, never()).send(any());
    }

}
