package net.pointofviews.notice.utils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.notice.exception.NoticeException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FcmUtil {
    private final FirebaseMessaging firebaseMessaging;

    public FcmUtil(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public void sendMessage(String token, String title, String body, Long reviewId, String noticeContent) {
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
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .putAllData(data)
                    .build();

            String response = firebaseMessaging.send(message);
            log.info("FCM message sent successfully: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send firebase message.", e);
            throw new NoticeException.NoticeSendFailedException();
        }
    }
}