package net.pointofviews.notice.utils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FcmUtil {
    private final FirebaseMessaging firebaseMessaging;

    public void sendMessage(List<String> tokens, String title, String body, Long reviewId, String noticeContent) {
        tokens.forEach(token -> {
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
            } catch (FirebaseMessagingException e) {
                log.error("Failed to send message to token {}: {}", token, e.getMessage());
            }
        });
    }
}