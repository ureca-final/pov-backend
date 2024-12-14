package net.pointofviews.notice.utils;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.notice.exception.NoticeException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class FcmUtil {
    private final FirebaseMessaging firebaseMessaging;

    public FcmUtil(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public void sendMessage(List<String> tokens, String title, String body, Long reviewId, String noticeContent) {
        try {
            // notification 데이터
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // data payload 구성
            Map<String, String> data = new HashMap<>();
            data.put("notice_content", noticeContent);
            data.put("review_id", reviewId != null ? String.valueOf(reviewId) : "");

            log.info("Sending FCM message with data: {}", data);

            // Message 구성
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(notification)
                    .putAllData(data)
                    .addAllTokens(tokens)
                    .build();

            BatchResponse response = firebaseMessaging.sendMulticast(message);
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