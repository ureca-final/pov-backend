package net.pointofviews.fixture;

import net.pointofviews.premiere.domain.Premiere;

import java.time.LocalDateTime;
import java.util.Random;

public class PremiereFixture {

    private static final Random RANDOM = new Random();

    public static Premiere createPremiere() {

        LocalDateTime startAt = LocalDateTime.now().minusDays(1);
        LocalDateTime endAt = startAt.plusDays(2);

        return Premiere.builder()
                .title("시사회 제목" + RANDOM.nextInt(10))
                .thumbnail("https://example.com/premieres/1/thumbnail/" + RANDOM.nextInt(10) + ".jpg")
                .eventImage("https://example.com/premieres/1/event/" + RANDOM.nextInt(10) + ".jpg")
                .amount(50000)
                .maxQuantity(RANDOM.nextInt(100))
                .isPaymentRequired(RANDOM.nextBoolean())
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }

    public static Premiere createPremiere(int maxQuantity) {

        LocalDateTime startAt = LocalDateTime.now().minusDays(1);
        LocalDateTime endAt = startAt.plusDays(2);

        return Premiere.builder()
                .title("시사회 제목" + RANDOM.nextInt(10))
                .thumbnail("https://example.com/premieres/1/thumbnail/" + RANDOM.nextInt(10) + ".jpg")
                .eventImage("https://example.com/premieres/1/event/" + RANDOM.nextInt(10) + ".jpg")
                .amount(50000)
                .isPaymentRequired(RANDOM.nextBoolean())
                .maxQuantity(maxQuantity)
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}
