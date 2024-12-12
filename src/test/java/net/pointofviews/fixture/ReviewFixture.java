package net.pointofviews.fixture;

import net.pointofviews.review.dto.response.ReadReviewResponse;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class ReviewFixture {

    private static final Random RANDOM = new Random();

    public static ReadReviewResponse readReviewResponse() {

        return new ReadReviewResponse(
                (long) RANDOM.nextInt(100),
                (long) RANDOM.nextInt(100),
                "영화 제목 " + RANDOM.nextInt(10),
                "리뷰 제목 " + UUID.randomUUID(),
                "리뷰 내용 " + UUID.randomUUID(),
                "작성자 " + RANDOM.nextInt(10),
                "https://example.com/profileImage" + RANDOM.nextInt(10) + ".jpg",
                "https://example.com/thumbnail" + RANDOM.nextInt(10) + ".jpg",
                LocalDateTime.now().minusDays(RANDOM.nextInt(30)),
                (long) RANDOM.nextInt(10),
                RANDOM.nextBoolean(),
                RANDOM.nextBoolean()
        );
    }
}
