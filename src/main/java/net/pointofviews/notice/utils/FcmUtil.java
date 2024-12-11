package net.pointofviews.notice.utils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.notice.exception.NoticeException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FcmUtil {
    private final FirebaseMessaging firebaseMessaging;

    public FcmUtil(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public void sendMessage(String token, String title, String body) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send firebase message.", e);
            throw new NoticeException.NoticeSendFailedException();
        }
    }
}