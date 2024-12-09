package net.pointofviews.fixture;

import net.pointofviews.premiere.domain.Premiere;

import java.time.LocalDateTime;
import java.util.Random;

public class PremiereFixture {

    private static final Random RANDOM = new Random();

    public static Premiere createPremiere() {

        LocalDateTime startAt = LocalDateTime.now().minusDays(RANDOM.nextInt(30));
        LocalDateTime endAt = startAt.plusDays(RANDOM.nextInt(10));

        return Premiere.builder()
                .title("시사회 제목" + RANDOM.nextInt(10))
                .eventImage("https://example.com/images/update-premiere" + RANDOM.nextInt(10) + ".jpg")
                .isPaymentRequired(RANDOM.nextBoolean())
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}
