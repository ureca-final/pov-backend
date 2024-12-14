package net.pointofviews.notice.utils;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.notice.exception.NoticeException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FcmUtil {
    private final FirebaseMessaging firebaseMessaging;

    public FcmUtil(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public void sendMessage(List<String> tokens, String title, String body, Long reviewId, String noticeContent) {
        try {
            // 메시지 리스트 생성
            List<Message> messages = tokens.stream()
                    .map(token -> Message.builder()
                            .setNotification(Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build())
                            .putData("notice_content", noticeContent)
                            .putData("review_id", reviewId != null ? String.valueOf(reviewId) : "")
                            .setToken(token)
                            .build())
                    .collect(Collectors.toList());

            log.info("Sending FCM messages for {} tokens", messages.size());

            BatchResponse response = firebaseMessaging.sendAll(messages);
            log.info("FCM messages sent successfully: {} successful and {} failed",
                    response.getSuccessCount(), response.getFailureCount());

            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        log.error("Failed to send message to token {}: {}",
                                tokens.get(i), responses.get(i).getException());
                    }
                }
                if (response.getSuccessCount() == 0) {
                    throw new NoticeException.NoticeSendFailedException();
                }
            }

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send firebase message.", e);
            throw new NoticeException.NoticeSendFailedException();
        }
    }
}