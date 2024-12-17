package net.pointofviews.notice.utils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.notice.domain.FcmErrorCode;
import net.pointofviews.notice.domain.FcmResult;
import net.pointofviews.notice.domain.NoticeSend;
import net.pointofviews.notice.repository.FcmResultRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FcmUtil {
    private final FirebaseMessaging firebaseMessaging;
    private final FcmResultRepository fcmResultRepository;
    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 1000;

    public List<FcmResult> sendMessage(List<String> tokens, String title, String body, Long reviewId, String noticeContent, NoticeSend noticeSend) {
        List<FcmResult> results = new ArrayList<>();

        for (String token : tokens) {
            FcmResult result = sendWithRetry(token, title, body, reviewId, noticeContent, noticeSend);
            results.add(result);
            fcmResultRepository.save(result);
        }
        return results;
    }

    private FcmResult sendWithRetry(String token, String title, String body, Long reviewId, String noticeContent,  NoticeSend noticeSend) {
        int retryCount = 0;
        Exception lastException = null;
        FcmErrorCode lastErrorCode = null;

        while (retryCount < MAX_RETRY_COUNT) {
            try {
                Message message = Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .putData("notice_content", noticeContent)
                        .putData("review_id", reviewId != null ? String.valueOf(reviewId) : "")
                        .setToken(token)
                        .build();

                firebaseMessaging.send(message);
                log.info("Successfully sent message to token: {}", token);

                return FcmResult.builder()
                        .token(token)
                        .isSuccess(true)
                        .noticeSend(noticeSend)
                        .build();
            } catch (FirebaseMessagingException e) {
                lastException = e;
                lastErrorCode = FcmErrorCode.fromCode(e.getErrorCode().toString());

                if (isNonRetryableError(lastErrorCode)) {
                    return FcmResult.builder()
                            .token(token)
                            .isSuccess(false)
                            .errorCode(lastErrorCode)
                            .noticeSend(noticeSend)
                            .build();
                }

                retryCount++;
                if (retryCount < MAX_RETRY_COUNT) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * retryCount);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        FcmErrorCode errorCode = lastException instanceof FirebaseMessagingException ?
                FcmErrorCode.fromCode(((FirebaseMessagingException) lastException).getErrorCode().toString()) :
                FcmErrorCode.UNKNOWN;

        log.error("Failed to send message to token {} after {} retries: {}",
                token, MAX_RETRY_COUNT, lastException.getMessage());

        return FcmResult.builder()
                .token(token)
                .isSuccess(false)
                .errorCode(lastErrorCode != null ? lastErrorCode : FcmErrorCode.UNKNOWN)
                .noticeSend(noticeSend)
                .build();
    }

    private boolean isNonRetryableError(FcmErrorCode errorCode) {
        return errorCode == FcmErrorCode.INVALID_REGISTRATION ||
                errorCode == FcmErrorCode.NOT_REGISTERED ||
                errorCode == FcmErrorCode.INVALID_ARGUMENT;
    }
}